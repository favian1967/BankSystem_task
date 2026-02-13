package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

public class AccessDeniedException extends BankException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException() {
        super("Доступ запрещен");
    }
}
