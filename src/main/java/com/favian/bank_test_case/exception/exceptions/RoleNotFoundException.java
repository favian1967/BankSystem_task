package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

public class RoleNotFoundException extends BankException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
