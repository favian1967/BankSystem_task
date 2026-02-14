package com.favian.bank_test_case.dto;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        @Email(message = "Email should be valid")
        String email,
        
        String firstName,
        
        String lastName,
        
        String phone
) {
}
