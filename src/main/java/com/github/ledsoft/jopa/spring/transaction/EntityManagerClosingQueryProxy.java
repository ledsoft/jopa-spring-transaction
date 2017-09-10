package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.query.Parameter;
import cz.cvut.kbss.jopa.model.query.Query;

import java.util.List;
import java.util.Set;

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
        return delegate.setMaxResults(i);
    }

    @Override
    public int getMaxResults() {
        return delegate.getMaxResults();
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
        return delegate.setParameter(index, paramName, language);
    }

    @Override
    public Query setParameter(String paramName, Object paramValue) {
        return delegate.setParameter(paramName, paramValue);
    }

    @Override
    public Query setParameter(String paramName, String paramValue, String language) {
        return delegate.setParameter(paramName, paramValue, language);
    }

    @Override
    public <T> Query setParameter(Parameter<T> parameter, T value) {
        return delegate.setParameter(parameter, value);
    }

    @Override
    public Query setParameter(Parameter<String> parameter, String value, String language) {
        return delegate.setParameter(parameter, value, language);
    }
}
