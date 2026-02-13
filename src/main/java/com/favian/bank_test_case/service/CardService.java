package com.favian.bank_test_case.service;

import com.favian.bank_test_case.dto.BalanceResponse;
import com.favian.bank_test_case.dto.CardResponse;
import com.favian.bank_test_case.entity.Card;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.entity.enums.CardStatus;
import com.favian.bank_test_case.exception.exceptions.CardBlockedException;
import com.favian.bank_test_case.exception.exceptions.CardNotFoundException;
import com.favian.bank_test_case.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

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

    private CardResponse mapToCardResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getMaskedNumber(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }
}
