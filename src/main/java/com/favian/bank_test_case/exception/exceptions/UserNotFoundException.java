package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

public class UserNotFoundException extends BankException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super("Пользователь с ID " + userId + " не найден");
    }

    public UserNotFoundException(String field, String value) {
        super("Пользователь с " + field + " = " + value + " не найден");
    }
}
