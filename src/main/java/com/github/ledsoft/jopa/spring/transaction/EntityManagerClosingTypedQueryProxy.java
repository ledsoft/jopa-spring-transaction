package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.query.Parameter;
import cz.cvut.kbss.jopa.model.query.TypedQuery;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        delegate.setMaxResults(max);
        return this;
    }

    @Override
    public TypedQuery<X> setFirstResult(int index) {
        delegate.setFirstResult(index);
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
        delegate.setParameter(index, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(int index, String value, String language) {
        delegate.setParameter(index, value, language);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String paramName, Object value) {
        delegate.setParameter(paramName, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String paramName, String value, String language) {
        delegate.setParameter(paramName, value, language);
        return this;
    }

    @Override
    public <T> TypedQuery<X> setParameter(Parameter<T> parameter, T value) {
        delegate.setParameter(parameter, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<String> parameter, String value, String language) {
        delegate.setParameter(parameter, value, language);
        return this;
    }

    @Override
    public TypedQuery<X> setUntypedParameter(int paramIndex, Object paramValue) {
        delegate.setUntypedParameter(paramIndex, paramValue);
        return this;
    }

    @Override
    public TypedQuery<X> setUntypedParameter(String paramName, Object paramValue) {
        delegate.setUntypedParameter(paramName, paramValue);
        return this;
    }

    @Override
    public <T> TypedQuery<X> setUntypedParameter(Parameter<T> parameter, T paramValue) {
        delegate.setUntypedParameter(parameter, paramValue);
        return this;
    }

    @Override
    public TypedQuery<X> setDescriptor(Descriptor descriptor) {
        delegate.setDescriptor(descriptor);
        return this;
    }

    @Override
    public Stream<X> getResultStream() {
        final Stream<X> wrappedStream = delegate.getResultStream();
        return StreamSupport.stream(new EntityManagerClosingResultSpliterator<>(wrappedStream.spliterator(), em), false);
    }
}
