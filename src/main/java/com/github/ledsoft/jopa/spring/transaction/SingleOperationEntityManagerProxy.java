package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.metamodel.FieldSpecification;
import cz.cvut.kbss.jopa.model.metamodel.Metamodel;
import cz.cvut.kbss.jopa.model.query.Query;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import cz.cvut.kbss.jopa.model.query.criteria.CriteriaQuery;
import cz.cvut.kbss.jopa.model.query.criteria.CriteriaBuilder;
import cz.cvut.kbss.jopa.transactions.EntityTransaction;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Entity manager proxy which closes the delegate after each operation.
 * <p>
 * Operations requiring a transaction are not supported.
 */
public class SingleOperationEntityManagerProxy implements EntityManager {

    private final EntityManager delegate;

    public SingleOperationEntityManagerProxy(EntityManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public void persist(Object o) {
        throw transactionNotSupported();
    }

    private static UnsupportedOperationException transactionNotSupported() {
        return new UnsupportedOperationException("Transactional operations are not supported by this proxy.");
    }

    @Override
    public void persist(Object o, Descriptor descriptor) {
        throw transactionNotSupported();
    }

    @Override
    public <T> T merge(T t) {
        throw transactionNotSupported();
    }

    @Override
    public <T> T merge(T t, Descriptor descriptor) {
        throw transactionNotSupported();
    }

    @Override
    public void remove(Object o) {
        throw transactionNotSupported();
    }

    @Override
    public <T> T find(Class<T> aClass, Object id) {
        try {
            return delegate.find(aClass, id);
        } finally {
            delegate.close();
        }
    }

    @Override
    public <T> T find(Class<T> aClass, Object id, Descriptor descriptor) {
        try {
            return delegate.find(aClass, id, descriptor);
        } finally {
            delegate.close();
        }
    }

    @Override
    public <T> T getReference(Class<T> aClass, Object identifier) {
        try {
            return delegate.getReference(aClass, identifier);
        } finally {
            delegate.close();
        }
    }

    @Override
    public <T> T getReference(Class<T> aClass, Object identifier, Descriptor descriptor) {
        try {
            return delegate.getReference(aClass, identifier, descriptor);
        } finally {
            delegate.close();
        }
    }

    @Override
    public void flush() {
        throw transactionNotSupported();
    }

    @Override
    public void refresh(Object o) {
        throw transactionNotSupported();
    }

    @Override
    public void clear() {
        try {
            delegate.clear();
        } finally {
            delegate.close();
        }
    }

    @Override
    public void detach(Object instance) {
        try {
            delegate.detach(instance);
        } finally {
            delegate.close();
        }
    }

    @Override
    public boolean contains(Object instance) {
        try {
            return delegate.contains(instance);
        } finally {
            delegate.close();
        }
    }

    @Override
    public boolean isConsistent(URI context) {
        try {
            return delegate.isConsistent(context);
        } finally {
            delegate.close();
        }
    }

    @Override
    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public void setProperty(String property, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Query createQuery(String query) {
        final Query instance = delegate.createQuery(query);
        return new EntityManagerClosingQueryProxy(instance, delegate);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        final TypedQuery<T> instance = delegate.createQuery(criteriaQuery);
        return new EntityManagerClosingTypedQueryProxy<>(instance, delegate);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String query, Class<T> resultType) {
        final TypedQuery<T> instance = delegate.createQuery(query, resultType);
        return new EntityManagerClosingTypedQueryProxy<>(instance, delegate);
    }

    @Override
    public Query createNamedQuery(String queryName) {
        final Query instance = delegate.createNamedQuery(queryName);
        return new EntityManagerClosingQueryProxy(instance, delegate);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String queryName, Class<T> resultType) {
        final TypedQuery<T> instance = delegate.createNamedQuery(queryName, resultType);
        return new EntityManagerClosingTypedQueryProxy<>(instance, delegate);
    }

    @Override
    public Query createNativeQuery(String query) {
        final Query instance = delegate.createNativeQuery(query);
        return new EntityManagerClosingQueryProxy(instance, delegate);
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String query, Class<T> resultType) {
        final TypedQuery<T> instance = delegate.createNativeQuery(query, resultType);
        return new EntityManagerClosingTypedQueryProxy<>(instance, delegate);
    }

    @Override
    public Query createNativeQuery(String sparqlString, String resultSetMapping) {
        final Query instance = delegate.createNativeQuery(sparqlString, resultSetMapping);
        return new EntityManagerClosingQueryProxy(instance, delegate);
    }

    @Override
    public <T> boolean isInferred(T t, FieldSpecification<? super T, ?> fieldSpecification, Object o) {
        try {
            return delegate.isInferred(t, fieldSpecification, o);
        } finally {
            delegate.close();
        }
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        if (aClass.equals(this.getClass())) {
            return aClass.cast(this);
        }
        throw new IllegalStateException("No transactional EntityManager available.");
    }

    @Override
    public Object getDelegate() {
        return delegate.getDelegate();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public EntityTransaction getTransaction() {
        try {
            return delegate.getTransaction();
        } finally {
            delegate.close();
        }
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<URI> getContexts() {
        try {
            return delegate.getContexts();
        } finally {
            delegate.close();
        }
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        throw new UnsupportedOperationException();
    }
}
