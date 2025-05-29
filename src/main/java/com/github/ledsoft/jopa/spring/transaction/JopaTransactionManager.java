package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.exceptions.RollbackException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.transactions.EntityTransaction;
import org.springframework.lang.NonNull;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.util.Map;

/**
 * Integrates JOPA transactions into the Spring transactional management.
 * <p>
 * This class manages instances representing transactions and propagates important events in the transaction lifecycle
 * to JOPA, while the transaction lifecycle itself is managed by Spring.
 */
public class JopaTransactionManager extends AbstractPlatformTransactionManager {

    private final EntityManagerFactory emf;

    private final DelegatingEntityManager emProxy;

    private final TransactionObjectFactory txFactory = new TransactionObjectFactory();

    public JopaTransactionManager(EntityManagerFactory emf, DelegatingEntityManager emProxy) {
        this.emf = emf;
        this.emProxy = emProxy;
        emProxy.setEntityManagerProvider(new SinglePUEntityManagerProvider(emf));
    }

    @NonNull
    @Override
    protected JopaTransactionDefinition doGetTransaction() throws TransactionException {
        final JopaTransactionDefinition tx = txFactory.createTransactionObject();
        if (emProxy.getLocalTransaction() != null) {
            tx.setTransactionEntityManager(emProxy.getLocalTransaction().getTransactionEntityManager());
        }
        return tx;
    }

    @Override
    protected void doBegin(@NonNull Object transaction, @NonNull TransactionDefinition transactionDefinition) throws
            TransactionException {
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) transaction;
        if (!txObject.isExisting()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Creating new transactional EntityManager.");
            }
            final EntityManager em = emf.createEntityManager(
                    txObject.isReadOnly() ? Map.of("cz.cvut.kbss.jopa.transactionMode", "read_only") : Map.of());
            txObject.setTransactionEntityManager(em);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Starting transaction and binding EntityManager to the current thread.");
        }
        final EntityManager em = txObject.getTransactionEntityManager();
        em.getTransaction().begin();
        // Bind the transactional EM to the current thread
        emProxy.setLocalTransaction(txObject);
    }

    @Override
    protected void doCommit(@NonNull DefaultTransactionStatus status) throws TransactionException {
        if (logger.isDebugEnabled()) {
            logger.debug("Commencing transaction commit.");
        }
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) status.getTransaction();
        try {
            final EntityTransaction tx = txObject.getTransactionEntityManager().getTransaction();
            tx.commit();
        } catch (RollbackException e) {
            throw new TransactionSystemException("Unable to commit JOPA entity transaction!", e);
        }
    }

    @Override
    protected void doRollback(@NonNull DefaultTransactionStatus status) throws TransactionException {
        if (logger.isDebugEnabled()) {
            logger.debug("Commencing transaction rollback.");
        }
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) status.getTransaction();
        final EntityManager em = txObject.getTransactionEntityManager();
        em.getTransaction().rollback();
    }

    @Override
    protected void doCleanupAfterCompletion(@NonNull Object transaction) {
        if (logger.isTraceEnabled()) {
            logger.trace("Closing transactional EntityManager.");
        }
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) transaction;
        emProxy.clearLocalTransaction();
        txObject.getTransactionEntityManager().close();
    }

    @Override
    protected boolean isExistingTransaction(@NonNull Object transaction) throws TransactionException {
        return ((JopaTransactionDefinition) transaction).isExisting();
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) status.getTransaction();
        final EntityManager em = txObject.getTransactionEntityManager();
        em.getTransaction().setRollbackOnly();
    }

    @NonNull
    @Override
    protected Object doSuspend(@NonNull Object transaction) throws TransactionException {
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) transaction;
        final EntityManager em = txObject.getTransactionEntityManager();
        txObject.setTransactionEntityManager(null);
        return em;
    }

    @Override
    protected void doResume(Object transaction, @NonNull Object suspendedResources) throws TransactionException {
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) transaction;
        final EntityManager suspendedEm = (EntityManager) suspendedResources;
        txObject.setTransactionEntityManager(suspendedEm);
        emProxy.setLocalTransaction(txObject);
    }
}
