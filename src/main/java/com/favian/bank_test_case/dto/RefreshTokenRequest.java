package com.favian.bank_test_case.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        @Size(min = 10, max = 500, message = "Invalid refresh token")
        String refreshToken
) {
}