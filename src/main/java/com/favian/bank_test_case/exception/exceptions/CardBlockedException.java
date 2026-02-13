package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

public class CardBlockedException extends BankException {
    public CardBlockedException(String message) {
        super(message);
    }

    public CardBlockedException(Long cardId) {
        super("Карта с ID " + cardId + " заблокирована");
    }
}
