package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;

/**
 * Provides non-transactional entity manager instances.
 */
class SinglePUEntityManagerProvider implements EntityManagerProvider {

    private final EntityManagerFactory emf;

    SinglePUEntityManagerProvider(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public EntityManager createEntityManager() {
        return new SingleOperationEntityManagerProxy(emf.createEntityManager());
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
