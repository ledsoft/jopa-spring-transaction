package cz.cvut.kbss.jopa.spring;

import cz.cvut.kbss.jopa.model.EntityManager;

class TransactionProxyFactory {

    /**
     * Creates a new transaction object wrapping the specified transactional {@link EntityManager}.
     *
     * @return Instance representing the new transaction
     */
    JopaTransactionDefinition createTransactionProxy(EntityManager delegate) {
        return new JopaTransactionDefinition(delegate);
    }
}
