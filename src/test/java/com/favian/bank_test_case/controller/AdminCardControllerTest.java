package com.favian.bank_test_case.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favian.bank_test_case.dto.CardResponse;
import com.favian.bank_test_case.dto.CreateCardRequest;
import com.favian.bank_test_case.entity.enums.CardStatus;
import com.favian.bank_test_case.service.CardService;
import com.favian.bank_test_case.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createCard_shouldCreateNewCard() throws Exception {
        // ARRANGE
        CreateCardRequest request = new CreateCardRequest(1L);
        CardResponse response = new CardResponse(
                1L, "1234 **** **** 5678", LocalDate.now().plusYears(3),
                CardStatus.ACTIVE, BigDecimal.ZERO
        );
        when(cardService.createCard(any(CreateCardRequest.class))).thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void blockCard_shouldBlockCardByAdmin() throws Exception {
        // ARRANGE
        CardResponse response = new CardResponse(
                1L, "1234 **** **** 5678", LocalDate.now().plusYears(3),
                CardStatus.BLOCKED, BigDecimal.valueOf(5000)
        );
        when(cardService.adminBlockCard(1L)).thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(post("/api/admin/cards/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }
}
