package com.favian.bank_test_case.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favian.bank_test_case.dto.CardResponse;
import com.favian.bank_test_case.dto.TransferRequest;
import com.favian.bank_test_case.dto.TransactionResponse;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.entity.enums.CardStatus;
import com.favian.bank_test_case.entity.enums.TransactionStatus;
import com.favian.bank_test_case.entity.enums.TransactionType;
import com.favian.bank_test_case.service.CardService;
import com.favian.bank_test_case.service.JwtService;
import com.favian.bank_test_case.service.TransactionService;
import com.favian.bank_test_case.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private CardService cardService;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private UserService userService;

    private User user;
    private CardResponse cardResponse;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        cardResponse = new CardResponse(
                1L, "1234 **** **** 5678", LocalDate.now().plusYears(3),
                CardStatus.ACTIVE, BigDecimal.valueOf(10000)
        );

        when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllCards_shouldReturnPageOfCards() throws Exception {
        // ARRANGE
        Page<CardResponse> page = new PageImpl<>(List.of(cardResponse));
        when(cardService.getUserCards(eq(user), any(Pageable.class))).thenReturn(page);

        // ACT & ASSERT
        mockMvc.perform(get("/api/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void blockCard_shouldBlockCard() throws Exception {
        // ARRANGE
        CardResponse blockedCard = new CardResponse(
                1L, "1234 **** **** 5678", LocalDate.now().plusYears(3),
                CardStatus.BLOCKED, BigDecimal.valueOf(10000)
        );
        when(cardService.blockCard(1L, user)).thenReturn(blockedCard);

        // ACT & ASSERT
        mockMvc.perform(post("/api/cards/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void transferBetweenOwnCards_shouldTransferSuccessfully() throws Exception {
        // ARRANGE
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(1000), "Test");
        TransactionResponse response = new TransactionResponse(
                1L, 1L, "1234 **** **** 5678", 2L, "4321 **** **** 8765",
                TransactionType.TRANSFER, BigDecimal.valueOf(1000), "Test",
                TransactionStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now()
        );
        when(transactionService.transferBetweenOwnCards(any(TransferRequest.class), eq(user)))
                .thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
