package cz.cvut.kbss.jopa.spring;

import cz.cvut.kbss.jopa.model.EntityManager;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class TransactionProxyFactoryTest {

    private TransactionProxyFactory factory = new TransactionProxyFactory();

    @Test
    public void createTransactionProxyCreatesTransactionDefinitionWithSpecifiedDelegate() {
        final EntityManager em = mock(EntityManager.class);
        final JopaTransactionDefinition transaction = factory.createTransactionProxy(em);
        assertNotNull(transaction);
        assertSame(em, transaction.getDelegate());
    }
}