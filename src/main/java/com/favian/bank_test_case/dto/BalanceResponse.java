package com.favian.bank_test_case.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        Long cardId,
        String maskedNumber,
        BigDecimal balance
) {
}
