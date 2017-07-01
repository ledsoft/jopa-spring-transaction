package cz.cvut.kbss.jopa.spring;

/**
 * Thrown when transaction was expected to be running but was not.
 */
public class TransactionMissingException extends RuntimeException {

    TransactionMissingException(String message) {
        super(message);
    }
}
