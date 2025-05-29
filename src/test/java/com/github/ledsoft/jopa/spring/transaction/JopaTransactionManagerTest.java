package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.exceptions.RollbackException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.transactions.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        when(emfMock.createEntityManager(any())).thenReturn(emMock);
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition txObject = txManager.doGetTransaction();
        txManager.doBegin(txObject, txObject);
        assertNotNull(emProxy.getLocalTransaction());
        assertEquals(emMock, emProxy.getLocalTransaction().getTransactionEntityManager());
    }

    @Test
    void doBeginStartsTransactionInTheTransactionalEntityManager() {
        when(emfMock.createEntityManager(any())).thenReturn(emMock);
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition txObject = txManager.doGetTransaction();
        txManager.doBegin(txObject, txObject);
        verify(etMock).begin();
    }

    @Test
    void doGetTransactionReturnsTransactionObjectWithExistingEntityManagerWhenItIsAlreadyBoundToCurrentThread() {
        when(emfMock.createEntityManager(any())).thenReturn(emMock);
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition tx = txManager.doGetTransaction();
        txManager.doBegin(tx, tx);
        assertNotNull(tx);
        final JopaTransactionDefinition txTwo = txManager.doGetTransaction();
        assertTrue(txTwo.isExisting());
        assertSame(emMock, txTwo.getTransactionEntityManager());
    }

    @Test
    void doBeginStartsTransactionOnExistingThreadBoundEntityManager() {
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        emProxy.setLocalTransaction(txOne);
        txOne.setTransactionEntityManager(emMock);
        final JopaTransactionDefinition txTwo = txManager.doGetTransaction();
        txManager.doBegin(txTwo, txTwo);
        verify(etMock).begin();
        verify(emfMock, never()).createEntityManager();
    }

    @Test
    void doBeginRequestsReadOnlyEntityManagerWhenTransactionIsReadOnly() {
        when(emfMock.createEntityManager(any())).thenReturn(emMock);
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition tx = txManager.doGetTransaction();
        tx.setReadOnly(true);
        txManager.doBegin(tx, tx);
        verify(emfMock).createEntityManager(Map.of("cz.cvut.kbss.jopa.transactionMode", "read_only"));
    }

    @Test
    void doCommitCommitsTransactionBoundToCurrentThread() {
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        txManager.doCommit(getTransactionStatus(txOne));
        verify(etMock).commit();
    }

    private static DefaultTransactionStatus getTransactionStatus(JopaTransactionDefinition txOne) {
        return new DefaultTransactionStatus("test", txOne, false, false, false, false, false, null);
    }

    @Test
    void doRollbackRollsBackTransactionBoundToCurrentThread() {
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        txManager.doRollback(getTransactionStatus(txOne));
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
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        emProxy.setLocalTransaction(txOne);
        txManager.doSetRollbackOnly(getTransactionStatus(txOne));
        verify(etMock).setRollbackOnly();
    }

    @Test
    void exceptionOnCommitClearsTransactionStatusAndThrowsTransactionException() {
        when(emMock.getTransaction()).thenReturn(etMock);
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        doThrow(new RollbackException("Error!")).when(etMock).commit();
        final TransactionSystemException ex = assertThrows(TransactionSystemException.class,
                                                           () -> txManager.doCommit(
                                                                   getTransactionStatus(txOne)));
        assertThat(ex.getCause(), instanceOf(RollbackException.class));
        assertThat(ex.getMessage(), containsString("Unable to commit JOPA entity transaction!"));
        verify(etMock, never()).rollback();
    }

    @Test
    void doSuspendRemovesEntityManagerFromCurrentTransactionDefinition() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txOne.setTransactionEntityManager(emMock);
        final Object result = txManager.doSuspend(txOne);
        assertNull(txOne.getTransactionEntityManager());
        assertSame(emMock, result);
    }

    @Test
    void doResumeSetsProvidedSuspendedEntityManagerOnSpecifiedTransactionObject() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        txManager.doResume(txOne, emMock);
        assertSame(emMock, txOne.getTransactionEntityManager());
    }

    @Test
    void doResumeEnsuresEntityManagerProxyContainsTransactionDefinition() {
        final JopaTransactionDefinition txOne = txManager.doGetTransaction();
        emProxy.clearLocalTransaction();
        txManager.doResume(txOne, emMock);
        assertTrue(emProxy.hasTransactionalDelegate());
        assertEquals(txOne, emProxy.getLocalTransaction());
    }
}
