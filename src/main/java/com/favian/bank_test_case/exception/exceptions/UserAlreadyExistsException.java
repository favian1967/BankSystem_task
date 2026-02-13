package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

public class UserAlreadyExistsException extends BankException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException forEmail(String email) {
        return new UserAlreadyExistsException(
                "Пользователь с email " + email + " уже существует"
        );
    }
}