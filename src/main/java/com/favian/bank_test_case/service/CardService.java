package com.favian.bank_test_case.service;

import com.favian.bank_test_case.dto.BalanceResponse;
import com.favian.bank_test_case.dto.CardResponse;
import com.favian.bank_test_case.dto.CreateCardRequest;
import com.favian.bank_test_case.entity.Card;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.entity.enums.CardStatus;
import com.favian.bank_test_case.exception.exceptions.CardBlockedException;
import com.favian.bank_test_case.exception.exceptions.CardNotFoundException;
import com.favian.bank_test_case.exception.exceptions.UserNotFoundException;
import com.favian.bank_test_case.repository.CardRepository;
import com.favian.bank_test_case.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    public CardService(CardRepository cardRepository, UserRepository userRepository, EncryptionService encryptionService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getUserCards(User user, Pageable pageable) {
        return cardRepository.findByUser(user, pageable)
                .map(this::mapToCardResponse);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getUserCardsByStatus(User user, CardStatus status, Pageable pageable) {
        return cardRepository.findByUserAndStatus(user, status, pageable)
                .map(this::mapToCardResponse);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> searchUserCards(User user, String searchTerm, Pageable pageable) {
        return cardRepository.findByUserAndMaskedNumberContainingIgnoreCase(user, searchTerm, pageable)
                .map(this::mapToCardResponse);
    }

    @Transactional(readOnly = true)
    public CardResponse getCardById(Long cardId, User user) {
        Card card = findCardByIdAndUser(cardId, user);
        return mapToCardResponse(card);
    }

    @Transactional(readOnly = true)
    public BalanceResponse getCardBalance(Long cardId, User user) {
        Card card = findCardByIdAndUser(cardId, user);
        return new BalanceResponse(
                card.getId(),
                card.getMaskedNumber(),
                card.getBalance()
        );
    }

    @Transactional
    public CardResponse blockCard(Long cardId, User user) {
        Card card = findCardByIdAndUser(cardId, user);
        
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("Card is already blocked");
        }
        
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return mapToCardResponse(savedCard);
    }

    @Transactional(readOnly = true)
    public Card findCardByIdAndUser(Long cardId, User user) {
        return cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new CardNotFoundException("Card not found or does not belong to user"));
    }

    //admin
    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("id", request.userId().toString()));
        
        Card card = new Card();
        card.setUser(user);
        
        String cardNumber = generateCardNumber();
        card.setEncryptedNumber(encryptionService.encrypt(cardNumber));
        card.setMaskedNumber(maskCardNumber(cardNumber));
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        
        Card savedCard = cardRepository.save(card);
        return mapToCardResponse(savedCard);
    }
    
    @Transactional(readOnly = true)
    public Page<CardResponse> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(this::mapToCardResponse);
    }
    
    @Transactional
    public CardResponse adminBlockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));
        
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("Card is already blocked");
        }
        
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return mapToCardResponse(savedCard);
    }
    
    @Transactional
    public CardResponse activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));
        
        card.setStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);
        return mapToCardResponse(savedCard);
    }
    
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));
        
        cardRepository.delete(card);
    }

    private CardResponse mapToCardResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getMaskedNumber(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }
    
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        
        return cardNumber.toString();
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() != 16) {
            return cardNumber;
        }
        
        return cardNumber.substring(0, 4) + " **** **** " + cardNumber.substring(12);
    }
}
