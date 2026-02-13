package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

import java.math.BigDecimal;

public class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }

    public InvalidAmountException(BigDecimal amount) {
        super("Недопустимая сумма: " + amount);
    }
}
