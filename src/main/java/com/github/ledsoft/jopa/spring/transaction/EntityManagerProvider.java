package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;

/**
 * Provides access to JOPA persistence unit to transactional delegators.
 */
public interface EntityManagerProvider {

    /**
     * Creates a fresh {@link EntityManager} instance.
     *
     * @return {@code EntityManager}
     */
    EntityManager createEntityManager();

    /**
     * Returns the {@link EntityManagerFactory} representing the underlying persistence unit.
     *
     * @return
     */
    EntityManagerFactory getEntityManagerFactory();
}
