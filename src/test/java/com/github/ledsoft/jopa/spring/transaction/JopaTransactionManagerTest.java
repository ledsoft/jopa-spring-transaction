package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.transactions.EntityTransaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.support.DefaultTransactionStatus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JopaTransactionManagerTest {

    @Mock
    private EntityManagerFactory emfMock;

    @Mock
    private EntityManager emMock;

    @Mock
    private EntityTransaction etMock;

    private DelegatingEntityManager emProxy;

    private JopaTransactionManager txManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(emfMock.createEntityManager()).thenReturn(emMock);
        when(emMock.getTransaction()).thenReturn(etMock);
        this.emProxy = new DelegatingEntityManager();
        this.txManager = new JopaTransactionManager(emfMock, emProxy);
    }

    @Test
    public void doGetTransactionReturnsNewTransactionObjectWhenNoneExistsForCurrentThread() {
        final JopaTransactionDefinition tx = txManager.doGetTransaction();
        assertNotNull(tx);
        assertFalse(tx.isExisting());
    }

    @Test
    public void doBeginCreatesTransactionalEntityManagerAndSetsItOnCurrentThread() {
        final JopaTransactionDefinition txObject = txManager.doGetTransaction();
        txManager.doBegin(txObject, txObject);
        assertNotNull(emProxy.getLocalTransaction());
        assertEquals(emMock, emProxy.getLocalTransaction().getTransactionEntityManager());
    }

    @Test
    public void doBeginStartsTransactionInTheTransactionalEntityManager() {
        final JopaTransactionDefinition txObject = txManager.doGetTransaction();
        txManager.doBegin(txObject, txObject);
        verify(etMock).begin();
    }

    @Test
    public void doGetTransactionReturnsTransactionObjectWithExistingEntityManagerWhenItIsAlreadyBoundToCurrentThread() {
        final JopaTransactionDefinition tx = txManager.doGetTransaction();
        txManager.doBegin(tx, tx);
        assertNotNull(tx);
        final JopaTransactionDefinition txTwo = txManager.doGetTransaction();
        assertTrue(txTwo.isExisting());
        assertSame(emMock, txTwo.getTransactionEntityManager());
    }

    @Test
    public void doBeginStartsTransactionOnExistingThreadBoundEntityManager() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        emProxy.setLocalTransaction(txOne);
        txOne.setTransactionEntityManager(emMock);
        final JopaTransactionDefinition txTwo = txManager.doGetTransaction();
        txManager.doBegin(txTwo, txTwo);
        verify(etMock).begin();
        verify(emfMock, never()).createEntityManager();
    }

    @Test
    public void doCommitCommitsTransactionBoundToCurrentThread() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        txManager.doCommit(new DefaultTransactionStatus(txOne, false, false, false, false, null));
        verify(etMock).commit();
    }

    @Test
    public void doRollbackRollsBackTransactionBoundToCurrentThread() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        txManager.doRollback(new DefaultTransactionStatus(txOne, false, false, false, false, null));
        verify(etMock).rollback();
    }

    @Test
    public void doCleanupAfterCompletionUnbindsEntityManagerAndClosesIt() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        emProxy.setLocalTransaction(txOne);
        txManager.doCleanupAfterCompletion(txOne);
        assertNull(emProxy.getLocalTransaction());
        verify(emMock).close();
    }

    @Test
    public void isExistingReturnsTrueWhenTransactionIsRunning() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        assertTrue(txManager.isExistingTransaction(txOne));
    }

    @Test
    public void doSetRollbackOnlySetsRollbackOnlyStatusOnCurrentTransaction() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        emProxy.setLocalTransaction(txOne);
        txManager.doSetRollbackOnly(new DefaultTransactionStatus(txOne, false, false, false, false, null));
        verify(etMock).setRollbackOnly();
    }
}