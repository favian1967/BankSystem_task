package com.favian.bank_test_case.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
