package com.favian.bank_test_case.service;

import com.favian.bank_test_case.dto.CardResponse;
import com.favian.bank_test_case.dto.CreateCardRequest;
import com.favian.bank_test_case.entity.Card;
import com.favian.bank_test_case.entity.Role;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.entity.enums.CardStatus;
import com.favian.bank_test_case.exception.exceptions.CardNotFoundException;
import com.favian.bank_test_case.repository.CardRepository;
import com.favian.bank_test_case.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    private CardService cardService;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EncryptionService encryptionService;

    private User user1;
    private Card card1;

    @BeforeEach
    public void setUp() {
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("user@test.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPhone("+1234567890");
        user1.setPasswordHash("hashedPassword");
        user1.setRoles(new HashSet<>(Set.of(userRole)));

        card1 = new Card();
        card1.setId(1L);
        card1.setUser(user1);
        card1.setEncryptedNumber("encryptedNumber1");
        card1.setMaskedNumber("1234 **** **** 5678");
        card1.setExpiryDate(LocalDate.now().plusYears(3));
        card1.setStatus(CardStatus.ACTIVE);
        card1.setBalance(BigDecimal.valueOf(10000));
    }

    @Test
    public void createCard_shouldCreateCardSuccessfully() {
        // ARRANGE
        CreateCardRequest request = new CreateCardRequest(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(encryptionService.encrypt(any())).thenReturn("encryptedCardNumber");

        Card savedCard = new Card();
        savedCard.setId(10L);
        savedCard.setUser(user1);
        savedCard.setEncryptedNumber("encryptedCardNumber");
        savedCard.setMaskedNumber("9876 **** **** 5432");
        savedCard.setExpiryDate(LocalDate.now().plusYears(3));
        savedCard.setStatus(CardStatus.ACTIVE);
        savedCard.setBalance(BigDecimal.ZERO);

        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // ACT
        CardResponse result = cardService.createCard(request);

        // ASSERT
        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(CardStatus.ACTIVE, result.status());
        assertEquals(BigDecimal.ZERO, result.balance());

        verify(userRepository, times(1)).findById(1L);
        verify(encryptionService, times(1)).encrypt(any());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    public void blockCard_shouldBlockActiveCard() {
        // ARRANGE
        when(cardRepository.findByIdAndUser(1L, user1)).thenReturn(Optional.of(card1));
        when(cardRepository.save(any(Card.class))).thenReturn(card1);

        // ACT
        CardResponse result = cardService.blockCard(1L, user1);

        // ASSERT
        assertEquals(CardStatus.BLOCKED, card1.getStatus());

        verify(cardRepository, times(1)).findByIdAndUser(1L, user1);
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    public void getCardById_shouldThrowWhenCardNotFound() {
        // ARRANGE
        when(cardRepository.findByIdAndUser(99L, user1)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(99L, user1));

        verify(cardRepository, times(1)).findByIdAndUser(99L, user1);
    }
}
