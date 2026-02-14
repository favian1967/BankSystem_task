package com.favian.bank_test_case.controller;

import com.favian.bank_test_case.dto.UserResponse;
import com.favian.bank_test_case.service.JwtService;
import com.favian.bank_test_case.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllUsers_shouldReturnPageOfUsers() throws Exception {
        // ARRANGE
        UserResponse userResponse = new UserResponse(
                1L, "test@example.com", "Test", "User", "+1234567890", Set.of("USER"), LocalDateTime.now()
        );
        Page<UserResponse> page = new PageImpl<>(List.of(userResponse));
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        // ACT & ASSERT
        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteUser_shouldDeleteUser() throws Exception {
        // ARRANGE
        doNothing().when(userService).deleteUser(1L);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
}
