package com.favian.bank_test_case.service;

import com.favian.bank_test_case.dto.UpdateUserRequest;
import com.favian.bank_test_case.dto.UserResponse;
import com.favian.bank_test_case.entity.Role;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.exception.exceptions.UserNotFoundException;
import com.favian.bank_test_case.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private User user1;

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
    }

    @Test
    public void getAllUsers_shouldReturnAllUsersWithPagination() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user1));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // ACT
        Page<UserResponse> result = userService.getAllUsers(pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("user@test.com", result.getContent().get(0).email());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void updateUser_shouldUpdateAllFields() {
        // ARRANGE
        UpdateUserRequest request = new UpdateUserRequest("new@test.com", "NewFirst", "NewLast", "+1111111111");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        // ACT
        UserResponse result = userService.updateUser(1L, request);

        // ASSERT
        assertNotNull(result);
        assertEquals("new@test.com", user1.getEmail());
        assertEquals("NewFirst", user1.getFirstName());

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    public void deleteUser_shouldDeleteExistingUser() {
        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // ACT
        userService.deleteUser(1L);

        // ASSERT
        verify(userRepository, times(1)).delete(user1);
    }
}
