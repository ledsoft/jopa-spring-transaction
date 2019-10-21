package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * Query result spliterator which closes the corresponding entity manager once the query stream is processed.
 *
 * @param <X> Result row type
 */
class EntityManagerClosingResultSpliterator<X> extends Spliterators.AbstractSpliterator<X> {

    private final Spliterator<X> wrappedSpliterator;
    private final EntityManager em;

    EntityManagerClosingResultSpliterator(Spliterator<X> wrappedSpliterator,
                                                 EntityManager em) {
        super(Long.MAX_VALUE, Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL);
        this.wrappedSpliterator = wrappedSpliterator;
        this.em = em;
    }

    @Override
    public boolean tryAdvance(Consumer<? super X> action) {
        try {
            final boolean result = wrappedSpliterator.tryAdvance(action);
            if (!result) {
                closeEntityManager();
            }
            return result;
        } catch (RuntimeException e) {
            closeEntityManager();
            throw e;
        }
    }

    private void closeEntityManager() {
        em.close();
    }

    @Override
    public void forEachRemaining(Consumer<? super X> action) {
        try {
            wrappedSpliterator.forEachRemaining(action);
        } finally {
            closeEntityManager();
        }
    }
}
