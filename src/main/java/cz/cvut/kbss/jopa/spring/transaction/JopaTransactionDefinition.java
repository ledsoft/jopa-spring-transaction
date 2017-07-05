package cz.cvut.kbss.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Simple object representing the transaction.
 * <p>
 * It inherits default transaction attributes from the {@link DefaultTransactionDefinition}.
 * <p>
 * Wraps the transactional {@link EntityManager} (if representing an existing transaction).
 */
public class JopaTransactionDefinition extends DefaultTransactionDefinition {

    private EntityManager transactionEntityManager;

    public JopaTransactionDefinition() {
    }

    public JopaTransactionDefinition(EntityManager delegate) {
        this.transactionEntityManager = delegate;
    }

    public EntityManager getTransactionEntityManager() {
        return transactionEntityManager;
    }

    public void setTransactionEntityManager(EntityManager transactionEntityManager) {
        this.transactionEntityManager = transactionEntityManager;
    }

    public boolean isExisting() {
        return transactionEntityManager != null;
    }
}
