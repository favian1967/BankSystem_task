package com.favian.bank_test_case.service;

import com.favian.bank_test_case.dto.AuthResponse;
import com.favian.bank_test_case.dto.LoginRequest;
import com.favian.bank_test_case.dto.RegisterRequest;
import com.favian.bank_test_case.entity.Role;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.exception.exceptions.InvalidCredentialsException;
import com.favian.bank_test_case.repository.RoleRepository;
import com.favian.bank_test_case.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private RoleRepository roleRepository;

    private User user;
    private Role userRole;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPasswordHash("encodedPassword");

        userDetails = new org.springframework.security.core.userdetails.User(
                "test@test.com", "encodedPassword", Collections.emptyList()
        );
    }

    @Test
    public void register_shouldRegisterSuccessfully() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest(
                "test@test.com", "password123", "John", "Doe", "+1234567890"
        );

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(jwtService.generateAccessToken(userDetails)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        // ACT
        AuthResponse result = authService.register(request);

        // ASSERT
        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void login_shouldLoginSuccessfully() {
        // ARRANGE
        LoginRequest request = new LoginRequest("test@test.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("test@test.com", "password123"));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(jwtService.generateAccessToken(userDetails)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn("refresh-token");

        // ACT
        AuthResponse result = authService.login(request);

        // ASSERT
        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void login_shouldThrowWhenBadCredentials() {
        // ARRANGE
        LoginRequest request = new LoginRequest("test@test.com", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // ACT & ASSERT
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}
