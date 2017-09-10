package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;

/**
 * Provides non-transactional entity manager instances.
 */
class EntityManagerProvider {

    private final EntityManagerFactory emf;

    EntityManagerProvider(EntityManagerFactory emf) {
        this.emf = emf;
    }

    EntityManager createEntityManager() {
        return new SingleOperationEntityManagerProxy(emf.createEntityManager());
    }

    EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
