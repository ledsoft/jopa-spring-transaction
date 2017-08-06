package com.github.ledsoft.jopa.spring.exception;

/**
 * Thrown when transaction was expected to be running but was not.
 */
public class TransactionMissingException extends RuntimeException {

    public TransactionMissingException(String message) {
        super(message);
    }
}
