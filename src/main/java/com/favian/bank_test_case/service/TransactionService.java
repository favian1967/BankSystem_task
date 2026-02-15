package com.favian.bank_test_case.service;

import com.favian.bank_test_case.dto.TransactionResponse;
import com.favian.bank_test_case.dto.TransferRequest;
import com.favian.bank_test_case.entity.Card;
import com.favian.bank_test_case.entity.Transaction;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.entity.enums.CardStatus;
import com.favian.bank_test_case.entity.enums.TransactionStatus;
import com.favian.bank_test_case.entity.enums.TransactionType;
import com.favian.bank_test_case.exception.exceptions.CardBlockedException;
import com.favian.bank_test_case.exception.exceptions.InsufficientFundsException;
import com.favian.bank_test_case.exception.exceptions.InvalidAmountException;
import com.favian.bank_test_case.repository.CardRepository;
import com.favian.bank_test_case.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;

    @Transactional
    public TransactionResponse transferBetweenOwnCards(TransferRequest request, User user) {
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than 0");
        }

        Card fromCard = cardService.findCardByIdAndUser(request.fromCardId(), user);
        Card toCard = cardService.findCardByIdAndUser(request.toCardId(), user);

        if (fromCard.getId().equals(toCard.getId())) {
            throw new InvalidAmountException("Cannot transfer to the same card");
        }

        if (fromCard.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("Source card is blocked");
        }
        if (toCard.getStatus() == CardStatus.BLOCKED) {
            throw new CardBlockedException("Destination card is blocked");
        }

        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds on source card");
        }

        Transaction transaction = new Transaction();
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setStatus(TransactionStatus.PENDING);

        try {
            fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
            toCard.setBalance(toCard.getBalance().add(request.amount()));

            cardRepository.save(fromCard);
            cardRepository.save(toCard);

            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            
            log.info("Transfer completed: amount={}, from={}, to={}, user={}", 
                    request.amount(), fromCard.getMaskedNumber(), toCard.getMaskedNumber(), user.getEmail());

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            log.error("Transfer failed: from={}, to={}, amount={}, error={}", 
                    fromCard.getId(), toCard.getId(), request.amount(), e.getMessage());
            throw e;
        } finally {
            transaction = transactionRepository.save(transaction);
        }

        return mapToTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactions(User user, Pageable pageable) {
        return transactionRepository.findByUserId(user.getId(), pageable)
                .map(this::mapToTransactionResponse);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getCardTransactions(Long cardId, User user, Pageable pageable) {
        Card card = cardService.findCardByIdAndUser(cardId, user);
        return transactionRepository.findByCard(card, pageable)
                .map(this::mapToTransactionResponse);
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getFromCard() != null ? transaction.getFromCard().getId() : null,
                transaction.getFromCard() != null ? transaction.getFromCard().getMaskedNumber() : null,
                transaction.getToCard() != null ? transaction.getToCard().getId() : null,
                transaction.getToCard() != null ? transaction.getToCard().getMaskedNumber() : null,
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                transaction.getCompletedAt()
        );
    }
}
