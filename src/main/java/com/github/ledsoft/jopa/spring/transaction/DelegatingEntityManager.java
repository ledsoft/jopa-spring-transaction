package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.exceptions.TransactionRequiredException;
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
import org.springframework.beans.factory.DisposableBean;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Delegates calls to an {@link EntityManager} instance associated with the current thread.
 */
public class DelegatingEntityManager implements DisposableBean, EntityManager {

    private final ThreadLocal<JopaTransactionDefinition> localTransaction = new ThreadLocal<>();

    private EntityManagerProvider emProvider;

    @Override
    public void persist(Object o) {
        getRequiredTransactionalDelegate("persist").persist(o);
    }

    protected EntityManager getTransactionalDelegate() {
        if (hasTransactionalDelegate()) {
            return localTransaction.get().getTransactionEntityManager();
        }
        return emProvider.createEntityManager();
    }

    protected boolean hasTransactionalDelegate() {
        return localTransaction.get() != null;
    }

    protected EntityManager getRequiredTransactionalDelegate(String methodName) {
        if (!hasTransactionalDelegate()) {
            throw new TransactionRequiredException(
                    "Transaction required when calling " + methodName + " on container-managed entity manager.");
        }
        return localTransaction.get().getTransactionEntityManager();
    }

    @Override
    public void persist(Object o, Descriptor descriptor) {
        getRequiredTransactionalDelegate("persist").persist(o, descriptor);
    }

    @Override
    public <T> T merge(T t) {
        return getRequiredTransactionalDelegate("merge").merge(t);
    }

    @Override
    public <T> T merge(T t, Descriptor descriptor) {
        return getRequiredTransactionalDelegate("merge").merge(t, descriptor);
    }

    @Override
    public void remove(Object o) {
        getRequiredTransactionalDelegate("remove").remove(o);
    }

    @Override
    public <T> T find(Class<T> aClass, Object identifier) {
        return getTransactionalDelegate().find(aClass, identifier);
    }

    @Override
    public <T> T find(Class<T> aClass, Object identifier, Descriptor descriptor) {
        return getTransactionalDelegate().find(aClass, identifier, descriptor);
    }

    @Override
    public <T> T getReference(Class<T> targetClass, Object identifier) {
        return getTransactionalDelegate().getReference(targetClass, identifier);
    }

    @Override
    public <T> T getReference(Class<T> targetClass, Object identifier, Descriptor descriptor) {
        return getTransactionalDelegate().getReference(targetClass, identifier, descriptor);
    }

    @Override
    public void flush() {
        getRequiredTransactionalDelegate("flush").flush();
    }

    @Override
    public void refresh(Object o) {
        getRequiredTransactionalDelegate("refresh").refresh(o);
    }

    @Override
    public void clear() {
        getTransactionalDelegate().clear();
    }

    @Override
    public void detach(Object o) {
        getTransactionalDelegate().detach(o);
    }

    @Override
    public boolean contains(Object o) {
        return getTransactionalDelegate().contains(o);
    }

    @Override
    public boolean isConsistent(URI uri) {
        return getTransactionalDelegate().isConsistent(uri);
    }

    @Override
    public <T> boolean isInferred(T t, FieldSpecification<? super T, ?> fieldSpecification, Object o) {
        return getRequiredTransactionalDelegate("isInferred").isInferred(t, fieldSpecification, o);
    }

    @Override
    public Map<String, Object> getProperties() {
        return getTransactionalDelegate().getProperties();
    }

    @Override
    public void setProperty(String property, Object value) {
        getTransactionalDelegate().setProperty(property, value);
    }

    @Override
    public Query createQuery(String s) {
        return getTransactionalDelegate().createQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return getTransactionalDelegate().createQuery(criteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String s, Class<T> aClass) {
        return getTransactionalDelegate().createQuery(s, aClass);
    }

    @Override
    public Query createNamedQuery(String s) {
        return getTransactionalDelegate().createNamedQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String s, Class<T> aClass) {
        return getTransactionalDelegate().createNamedQuery(s, aClass);
    }

    @Override
    public Query createNativeQuery(String s) {
        return getTransactionalDelegate().createNativeQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createNativeQuery(String s, Class<T> aClass) {
        return getTransactionalDelegate().createNativeQuery(s, aClass);
    }

    @Override
    public Query createNativeQuery(String sparqlString, String resultSetMapping) {
        return getTransactionalDelegate().createNativeQuery(sparqlString, resultSetMapping);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return getTransactionalDelegate().unwrap(aClass);
    }

    @Override
    public Object getDelegate() {
        return getTransactionalDelegate().getDelegate();
    }

    @Override
    public void close() {
        getTransactionalDelegate().close();
    }

    @Override
    public boolean isOpen() {
        return getTransactionalDelegate().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return getTransactionalDelegate().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        if (hasTransactionalDelegate()) {
            localTransaction.get().getTransactionEntityManager().getEntityManagerFactory();
        }
        return emProvider.getEntityManagerFactory();
    }

    @Override
    public List<URI> getContexts() {
        return getTransactionalDelegate().getContexts();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return getTransactionalDelegate().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        if (hasTransactionalDelegate()) {
            localTransaction.get().getTransactionEntityManager().getMetamodel();
        }
        return emProvider.getEntityManagerFactory().getMetamodel();
    }

    public void setLocalTransaction(JopaTransactionDefinition transaction) {
        localTransaction.set(transaction);
    }

    public JopaTransactionDefinition getLocalTransaction() {
        return localTransaction.get();
    }

    /**
     * Removes the transaction object bound to the current thread.
     */
    public void clearLocalTransaction() {
        localTransaction.remove();
    }

    public void setEntityManagerProvider(EntityManagerProvider emProvider) {
        this.emProvider = emProvider;
    }

    @Override
    public void destroy() {
        if (localTransaction.get() != null) {
            localTransaction.get().getTransactionEntityManager().close();
        }
    }
}
