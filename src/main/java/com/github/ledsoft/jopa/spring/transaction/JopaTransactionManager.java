package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * Integrates JOPA transactions into the Spring transactional management.
 * <p>
 * This class manages instances representing transactions and propagates important events in the transaction lifecycle
 * to JOPA, while the transaction lifecycle itself is managed by Spring.
 */
public class JopaTransactionManager extends AbstractPlatformTransactionManager {

    private static final Logger LOG = LoggerFactory.getLogger(JopaTransactionManager.class);

    private final EntityManagerFactory emf;

    private final DelegatingEntityManager emProxy;

    private final TransactionObjectFactory txFactory = new TransactionObjectFactory();

    public JopaTransactionManager(EntityManagerFactory emf, DelegatingEntityManager emProxy) {
        this.emf = emf;
        this.emProxy = emProxy;
    }

    @Override
    protected JopaTransactionDefinition doGetTransaction() throws TransactionException {
        final JopaTransactionDefinition tx = txFactory.createTransactionObject();
        if (emProxy.getLocalTransaction() != null) {
            tx.setTransactionEntityManager(emProxy.getLocalTransaction().getTransactionEntityManager());
        }
        return tx;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition transactionDefinition) throws
                                                                                            TransactionException {
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) transaction;
        if (!txObject.isExisting()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Creating new transactional EntityManager.");
            }
            final EntityManager em = emf.createEntityManager();
            txObject.setTransactionEntityManager(em);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Starting transaction and binding EntityManager to the current thread.");
        }
        final EntityManager em = txObject.getTransactionEntityManager();
        em.getTransaction().begin();
        // Bind the transactional EM to the current thread
        emProxy.setLocalTransaction(txObject);
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Commencing transaction commit.");
        }
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) status.getTransaction();
        final EntityManager em = txObject.getTransactionEntityManager();
        em.getTransaction().commit();
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Commencing transaction rollback.");
        }
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) status.getTransaction();
        final EntityManager em = txObject.getTransactionEntityManager();
        em.getTransaction().rollback();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Closing transactional EntityManager.");
        }
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) transaction;
        emProxy.clearLocalTransaction();
        txObject.getTransactionEntityManager().close();
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        return ((JopaTransactionDefinition) transaction).isExisting();
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        final JopaTransactionDefinition txObject = (JopaTransactionDefinition) status.getTransaction();
        final EntityManager em = txObject.getTransactionEntityManager();
        em.getTransaction().setRollbackOnly();
    }
}
