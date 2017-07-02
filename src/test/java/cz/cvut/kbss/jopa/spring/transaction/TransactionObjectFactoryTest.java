package cz.cvut.kbss.jopa.spring.transaction;

import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionObjectFactoryTest {

    private TransactionObjectFactory factory = new TransactionObjectFactory();

    @Test
    public void createTransactionProxyCreatesEmptyTransactionDefinition() {
        final JopaTransactionDefinition transaction = factory.createTransactionObject();
        assertNotNull(transaction);
        assertFalse(transaction.isExisting());
        assertNull(transaction.getTransactionEntityManager());
    }
}