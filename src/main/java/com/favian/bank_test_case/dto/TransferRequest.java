package com.favian.bank_test_case.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "Source card ID is required")
        @Positive(message = "Source card ID must be positive")
        Long fromCardId,
        
        @NotNull(message = "Destination card ID is required")
        @Positive(message = "Destination card ID must be positive")
        Long toCardId,
        
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        @DecimalMax(value = "1000000.00", message = "Amount must not exceed 1,000,000.00")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
        BigDecimal amount,
        
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description
) {
}
