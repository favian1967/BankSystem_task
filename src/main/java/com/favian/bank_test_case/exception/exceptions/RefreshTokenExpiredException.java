package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

public class RefreshTokenExpiredException extends BankException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }

    public RefreshTokenExpiredException() {
        super("Refresh токен истек");
    }
}
