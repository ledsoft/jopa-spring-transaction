package com.github.ledsoft.jopa.spring.transaction;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionObjectFactoryTest {

    private final TransactionObjectFactory factory = new TransactionObjectFactory();

    @Test
    void createTransactionProxyCreatesEmptyTransactionDefinition() {
        final JopaTransactionDefinition transaction = factory.createTransactionObject();
        assertNotNull(transaction);
        assertFalse(transaction.isExisting());
        assertNull(transaction.getTransactionEntityManager());
    }
}