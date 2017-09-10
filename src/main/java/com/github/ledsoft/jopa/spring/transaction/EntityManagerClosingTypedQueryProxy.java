package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.query.Parameter;
import cz.cvut.kbss.jopa.model.query.TypedQuery;

import java.util.List;
import java.util.Set;

class EntityManagerClosingTypedQueryProxy<X> implements TypedQuery<X> {

    private final TypedQuery<X> delegate;
    private final EntityManager em;

    EntityManagerClosingTypedQueryProxy(TypedQuery<X> delegate, EntityManager em) {
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
    public List<X> getResultList() {
        try {
            return delegate.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public X getSingleResult() {
        try {
            return delegate.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public TypedQuery<X> setMaxResults(int max) {
        return delegate.setMaxResults(max);
    }

    @Override
    public int getMaxResults() {
        return delegate.getMaxResults();
    }

    @Override
    public Parameter<?> getParameter(int index) {
        return delegate.getParameter(index);
    }

    @Override
    public Parameter<?> getParameter(String paramName) {
        return delegate.getParameter(paramName);
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
    public Object getParameterValue(int index) {
        return delegate.getParameterValue(index);
    }

    @Override
    public Object getParameterValue(String paramName) {
        return delegate.getParameterValue(paramName);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> parameter) {
        return delegate.getParameterValue(parameter);
    }

    @Override
    public TypedQuery<X> setParameter(int index, Object value) {
        return delegate.setParameter(index, value);
    }

    @Override
    public TypedQuery<X> setParameter(int index, String value, String language) {
        return delegate.setParameter(index, value, language);
    }

    @Override
    public TypedQuery<X> setParameter(String paramName, Object value) {
        return delegate.setParameter(paramName, value);
    }

    @Override
    public TypedQuery<X> setParameter(String paramName, String value, String language) {
        return delegate.setParameter(paramName, value, language);
    }

    @Override
    public <T> TypedQuery<X> setParameter(Parameter<T> parameter, T value) {
        return delegate.setParameter(parameter, value);
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<String> parameter, String value, String language) {
        return delegate.setParameter(parameter, value, language);
    }

    @Override
    public TypedQuery<X> setDescriptor(Descriptor descriptor) {
        return delegate.setDescriptor(descriptor);
    }
}
