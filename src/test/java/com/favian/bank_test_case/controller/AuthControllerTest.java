package com.favian.bank_test_case.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favian.bank_test_case.dto.AuthResponse;
import com.favian.bank_test_case.dto.LoginRequest;
import com.favian.bank_test_case.dto.RegisterRequest;
import com.favian.bank_test_case.service.AuthService;
import com.favian.bank_test_case.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthService authService;

    @Test
    public void register_shouldRegisterNewUser() throws Exception {
        // ARRANGE
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "Test", "User", "+1234567890");
        AuthResponse response = new AuthResponse("access-token", "refresh-token");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    public void login_shouldAuthenticateUser() throws Exception {
        // ARRANGE
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AuthResponse response = new AuthResponse("access-token", "refresh-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }
}
