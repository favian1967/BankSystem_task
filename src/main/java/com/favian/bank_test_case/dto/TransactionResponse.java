package com.favian.bank_test_case.dto;

import com.favian.bank_test_case.entity.enums.TransactionStatus;
import com.favian.bank_test_case.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long fromCardId,
        String fromCardMasked,
        Long toCardId,
        String toCardMasked,
        TransactionType type,
        BigDecimal amount,
        String description,
        TransactionStatus status,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {
}
