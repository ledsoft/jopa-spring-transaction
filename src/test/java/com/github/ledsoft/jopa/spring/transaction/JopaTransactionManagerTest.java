package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.exceptions.RollbackException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.transactions.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.DefaultTransactionStatus;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JopaTransactionManagerTest {

    @Mock
    private EntityManagerFactory emfMock;

    @Mock
    private EntityManager emMock;

    @Mock
    private EntityTransaction etMock;

    private DelegatingEntityManager emProxy;

    private JopaTransactionManager txManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(emfMock.createEntityManager()).thenReturn(emMock);
        when(emMock.getTransaction()).thenReturn(etMock);
        this.emProxy = new DelegatingEntityManager();
        this.txManager = new JopaTransactionManager(emfMock, emProxy);
    }

    @Test
    void doGetTransactionReturnsNewTransactionObjectWhenNoneExistsForCurrentThread() {
        final JopaTransactionDefinition tx = txManager.doGetTransaction();
        assertNotNull(tx);
        assertFalse(tx.isExisting());
    }

    @Test
    void doBeginCreatesTransactionalEntityManagerAndSetsItOnCurrentThread() {
        final JopaTransactionDefinition txObject = txManager.doGetTransaction();
        txManager.doBegin(txObject, txObject);
        assertNotNull(emProxy.getLocalTransaction());
        assertEquals(emMock, emProxy.getLocalTransaction().getTransactionEntityManager());
    }

    @Test
    void doBeginStartsTransactionInTheTransactionalEntityManager() {
        final JopaTransactionDefinition txObject = txManager.doGetTransaction();
        txManager.doBegin(txObject, txObject);
        verify(etMock).begin();
    }

    @Test
    void doGetTransactionReturnsTransactionObjectWithExistingEntityManagerWhenItIsAlreadyBoundToCurrentThread() {
        final JopaTransactionDefinition tx = txManager.doGetTransaction();
        txManager.doBegin(tx, tx);
        assertNotNull(tx);
        final JopaTransactionDefinition txTwo = txManager.doGetTransaction();
        assertTrue(txTwo.isExisting());
        assertSame(emMock, txTwo.getTransactionEntityManager());
    }

    @Test
    void doBeginStartsTransactionOnExistingThreadBoundEntityManager() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        emProxy.setLocalTransaction(txOne);
        txOne.setTransactionEntityManager(emMock);
        final JopaTransactionDefinition txTwo = txManager.doGetTransaction();
        txManager.doBegin(txTwo, txTwo);
        verify(etMock).begin();
        verify(emfMock, never()).createEntityManager();
    }

    @Test
    void doCommitCommitsTransactionBoundToCurrentThread() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        txManager.doCommit(new DefaultTransactionStatus(txOne, false, false, false, false, null));
        verify(etMock).commit();
    }

    @Test
    void doRollbackRollsBackTransactionBoundToCurrentThread() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        txManager.doRollback(new DefaultTransactionStatus(txOne, false, false, false, false, null));
        verify(etMock).rollback();
    }

    @Test
    void doCleanupAfterCompletionUnbindsEntityManagerAndClosesIt() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        emProxy.setLocalTransaction(txOne);
        txManager.doCleanupAfterCompletion(txOne);
        assertNull(emProxy.getLocalTransaction());
        verify(emMock).close();
    }

    @Test
    void isExistingReturnsTrueWhenTransactionIsRunning() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        assertTrue(txManager.isExistingTransaction(txOne));
    }

    @Test
    void doSetRollbackOnlySetsRollbackOnlyStatusOnCurrentTransaction() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        emProxy.setLocalTransaction(txOne);
        txManager.doSetRollbackOnly(new DefaultTransactionStatus(txOne, false, false, false, false, null));
        verify(etMock).setRollbackOnly();
    }

    @Test
    void exceptionOnCommitClearsTransactionStatusAndThrowsTransactionException() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        doThrow(new RollbackException("Error!")).when(etMock).commit();
        final TransactionSystemException ex = assertThrows(TransactionSystemException.class,
                () -> txManager.doCommit(new DefaultTransactionStatus(txOne, false, false, false, false, null)));
        assertThat(ex.getCause(), instanceOf(RollbackException.class));
        assertThat(ex.getMessage(), containsString("Unable to commit JOPA entity transaction!"));
        verify(etMock, never()).rollback();
    }
}