package com.favian.bank_test_case.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        Set<String> roles,
        LocalDateTime createdAt
) {
}
