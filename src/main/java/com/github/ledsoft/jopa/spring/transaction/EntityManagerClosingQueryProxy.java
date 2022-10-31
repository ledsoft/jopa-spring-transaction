package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.query.Parameter;
import cz.cvut.kbss.jopa.model.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Closes the EntityManager instance which created the delegate when query is finished.
 */
public class EntityManagerClosingQueryProxy implements Query {

    private final Query delegate;
    private final EntityManager em;

    EntityManagerClosingQueryProxy(Query delegate, EntityManager em) {
        this.delegate = delegate;
        this.em = em;
    }

    @Override
    public void executeUpdate() {
        try {
            delegate.executeUpdate();
        } finally {
            em.close();
        }
    }

    @Override
    public List getResultList() {
        try {
            return delegate.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Object getSingleResult() {
        try {
            return delegate.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Query setMaxResults(int i) {
        delegate.setMaxResults(i);
        return this;
    }

    @Override
    public int getMaxResults() {
        return delegate.getMaxResults();
    }

    @Override
    public Query setFirstResult(int i) {
        delegate.setFirstResult(i);
        return this;
    }

    @Override
    public int getFirstResult() {
        try {
            return delegate.getFirstResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Parameter<?> getParameter(int i) {
        return delegate.getParameter(i);
    }

    @Override
    public Parameter<?> getParameter(String s) {
        return delegate.getParameter(s);
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return delegate.getParameters();
    }

    @Override
    public boolean isBound(Parameter<?> parameter) {
        return delegate.isBound(parameter);
    }

    @Override
    public Object getParameterValue(int i) {
        return delegate.getParameterValue(i);
    }

    @Override
    public Object getParameterValue(String s) {
        return delegate.getParameterValue(s);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> parameter) {
        return delegate.getParameterValue(parameter);
    }

    @Override
    public Query setParameter(int index, Object paramValue) {
        return delegate.setParameter(index, paramValue);
    }

    @Override
    public Query setParameter(int index, String paramName, String language) {
        delegate.setParameter(index, paramName, language);
        return this;
    }

    @Override
    public Query setParameter(String paramName, Object paramValue) {
        delegate.setParameter(paramName, paramValue);
        return this;
    }

    @Override
    public Query setParameter(String paramName, String paramValue, String language) {
        delegate.setParameter(paramName, paramValue, language);
        return this;
    }

    @Override
    public <T> Query setParameter(Parameter<T> parameter, T value) {
        delegate.setParameter(parameter, value);
        return this;
    }

    @Override
    public Query setParameter(Parameter<String> parameter, String value, String language) {
        delegate.setParameter(parameter, value, language);
        return this;
    }

    @Override
    public Query setUntypedParameter(int index, Object paramValue) {
        delegate.setUntypedParameter(index, paramValue);
        return this;
    }

    @Override
    public Query setUntypedParameter(String paramName, Object paramValue) {
        delegate.setUntypedParameter(paramName, paramValue);
        return this;
    }

    @Override
    public <T> Query setUntypedParameter(Parameter<T> parameter, T paramValue) {
        delegate.setUntypedParameter(parameter, paramValue);
        return this;
    }

    @Override
    public Query setHint(String hint, Object value) {
        delegate.setHint(hint, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return delegate.getHints();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream getResultStream() {
        final Stream wrappedStream = delegate.getResultStream();
        return StreamSupport.stream(new EntityManagerClosingResultSpliterator(wrappedStream.spliterator(), em), false);
    }
}
