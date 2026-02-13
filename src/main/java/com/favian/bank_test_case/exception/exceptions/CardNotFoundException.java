package com.favian.bank_test_case.exception.exceptions;

import com.favian.bank_test_case.exception.BankException;

public class CardNotFoundException extends BankException {
    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(Long cardId) {
        super("Карта с ID " + cardId + " не найдена");
    }
}
