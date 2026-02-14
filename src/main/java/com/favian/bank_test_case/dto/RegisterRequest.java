package com.favian.bank_test_case.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,
        
        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "First name can only contain letters, spaces and hyphens")
        String firstName,
        
        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "Last name can only contain letters, spaces and hyphens")
        String lastName,
        
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid phone number format (e.g., +1234567890)")
        String phone
) {
}
