package cz.cvut.kbss.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.metamodel.Metamodel;
import cz.cvut.kbss.jopa.model.query.Query;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import cz.cvut.kbss.jopa.spring.exception.TransactionMissingException;
import cz.cvut.kbss.jopa.transactions.EntityTransaction;

import java.net.URI;
import java.util.List;

/**
 * Delegates calls to an {@link EntityManager} instance associated with the current thread.
 */
public class DelegatingEntityManager implements EntityManager {

    private final ThreadLocal<JopaTransactionDefinition> localTransaction = new ThreadLocal<>();

    @Override
    public void persist(Object o) {
        getTransactionalDelegate().persist(o);
    }

    private EntityManager getTransactionalDelegate() {
        final JopaTransactionDefinition currentTransaction = localTransaction.get();
        if (currentTransaction == null) {
            throw new TransactionMissingException("Expected transaction, but found none.");
        }
        return currentTransaction.getTransactionEntityManager();
    }

    @Override
    public void persist(Object o, Descriptor descriptor) {
        getTransactionalDelegate().persist(o, descriptor);
    }

    @Override
    public <T> T merge(T t) {
        return getTransactionalDelegate().merge(t);
    }

    @Override
    public <T> T merge(T t, Descriptor descriptor) {
        return getTransactionalDelegate().merge(t, descriptor);
    }

    @Override
    public void remove(Object o) {
        getTransactionalDelegate().remove(o);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o) {
        return getTransactionalDelegate().find(aClass, o);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o, Descriptor descriptor) {
        return getTransactionalDelegate().find(aClass, o, descriptor);
    }

    @Override
    public void flush() {
        getTransactionalDelegate().flush();
    }

    @Override
    public void refresh(Object o) {
        getTransactionalDelegate().refresh(o);
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
    public Query createQuery(String s) {
        return getTransactionalDelegate().createQuery(s);
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
        return getTransactionalDelegate().getEntityManagerFactory();
    }

    @Override
    public List<URI> getContexts() {
        return getTransactionalDelegate().getContexts();
    }

    @Override
    public Metamodel getMetamodel() {
        return getTransactionalDelegate().getMetamodel();
    }

    @Override
    public void setUseTransactionalOntologyForQueryProcessing() {
        getTransactionalDelegate().setUseTransactionalOntologyForQueryProcessing();
    }

    @Override
    public boolean useTransactionalOntologyForQueryProcessing() {
        return getTransactionalDelegate().useTransactionalOntologyForQueryProcessing();
    }

    @Override
    public void setUseBackupOntologyForQueryProcessing() {
        getTransactionalDelegate().setUseBackupOntologyForQueryProcessing();
    }

    @Override
    public boolean useBackupOntologyForQueryProcessing() {
        return getTransactionalDelegate().useBackupOntologyForQueryProcessing();
    }

    void setLocalTransaction(JopaTransactionDefinition transaction) {
        localTransaction.set(transaction);
    }

    JopaTransactionDefinition getLocalTransaction() {
        return localTransaction.get();
    }

    /**
     * Removes the transaction object bound to the current thread.
     */
    void clearLocalTransaction() {
        localTransaction.remove();
    }
}
