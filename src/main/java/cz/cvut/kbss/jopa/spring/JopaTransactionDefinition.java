package cz.cvut.kbss.jopa.spring;

import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Simple object representing the transaction.
 * <p>
 * Wraps the transactional {@link EntityManager}.
 */
public class JopaTransactionDefinition extends DefaultTransactionDefinition {

    private final EntityManager delegate;

    private boolean existing;

    public JopaTransactionDefinition(EntityManager delegate) {
        this.delegate = delegate;
    }

    public EntityManager getDelegate() {
        return delegate;
    }

    public boolean isExisting() {
        return existing;
    }

    public void setExisting(boolean existing) {
        this.existing = existing;
    }
}
