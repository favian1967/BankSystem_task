package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

public class RefreshTokenNotFoundException extends BankException {
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }

    public RefreshTokenNotFoundException() {
        super("Refresh токен не найден");
    }
}
