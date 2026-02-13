package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

import java.math.BigDecimal;

public class InsufficientFundsException extends BankException {
    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(BigDecimal required, BigDecimal available) {
        super("Недостаточно средств. Требуется: " + required + ", доступно: " + available);
    }
}
