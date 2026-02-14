package com.favian.bank_test_case.service;

import com.favian.bank_test_case.dto.TransactionResponse;
import com.favian.bank_test_case.dto.TransferRequest;
import com.favian.bank_test_case.entity.Card;
import com.favian.bank_test_case.entity.Transaction;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.entity.enums.CardStatus;
import com.favian.bank_test_case.entity.enums.TransactionStatus;
import com.favian.bank_test_case.entity.enums.TransactionType;
import com.favian.bank_test_case.exception.exceptions.InsufficientFundsException;
import com.favian.bank_test_case.repository.CardRepository;
import com.favian.bank_test_case.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardService cardService;

    private User user;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setEncryptedNumber("encrypted1");
        fromCard.setMaskedNumber("1234 **** **** 5678");
        fromCard.setExpiryDate(LocalDate.now().plusYears(3));
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(BigDecimal.valueOf(10000));

        toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setEncryptedNumber("encrypted2");
        toCard.setMaskedNumber("4321 **** **** 8765");
        toCard.setExpiryDate(LocalDate.now().plusYears(3));
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(BigDecimal.valueOf(5000));
    }

    @Test
    public void transferBetweenOwnCards_shouldTransferSuccessfully() {
        // ARRANGE
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(3000), "Test transfer");

        when(cardService.findCardByIdAndUser(1L, user)).thenReturn(fromCard);
        when(cardService.findCardByIdAndUser(2L, user)).thenReturn(toCard);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setFromCard(fromCard);
        savedTransaction.setToCard(toCard);
        savedTransaction.setType(TransactionType.TRANSFER);
        savedTransaction.setAmount(BigDecimal.valueOf(3000));
        savedTransaction.setStatus(TransactionStatus.COMPLETED);
        savedTransaction.setCompletedAt(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // ACT
        TransactionResponse result = transactionService.transferBetweenOwnCards(request, user);

        // ASSERT
        assertNotNull(result);
        assertEquals(TransactionType.TRANSFER, result.type());
        assertEquals(TransactionStatus.COMPLETED, result.status());
        assertEquals(BigDecimal.valueOf(7000), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(8000), toCard.getBalance());

        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void transferBetweenOwnCards_shouldThrowWhenInsufficientFunds() {
        // ARRANGE
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(50000), "Too much");

        when(cardService.findCardByIdAndUser(1L, user)).thenReturn(fromCard);
        when(cardService.findCardByIdAndUser(2L, user)).thenReturn(toCard);

        // ACT & ASSERT
        assertThrows(InsufficientFundsException.class,
                () -> transactionService.transferBetweenOwnCards(request, user));

        verify(cardRepository, never()).save(any(Card.class));
    }
}
