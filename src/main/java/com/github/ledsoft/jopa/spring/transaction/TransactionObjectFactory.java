package com.github.ledsoft.jopa.spring.transaction;

/**
 * Builds transaction representing objects.
 */
public class TransactionObjectFactory {

    /**
     * Creates a new (default) transaction object.
     *
     * @return Instance representing a transaction
     */
    JopaTransactionDefinition createTransactionObject() {
        return new JopaTransactionDefinition();
    }
}
