package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class EntityManagerClosingTypedQueryProxyTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<?> delegate;

    private EntityManagerClosingTypedQueryProxy<?> sut;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.sut = new EntityManagerClosingTypedQueryProxy<>(delegate, emMock);
    }

    @Test
    public void executeUpdateClosesEntityManagerAfterExecution() {
        sut.executeUpdate();
        final InOrder inOrder = Mockito.inOrder(delegate, emMock);
        inOrder.verify(delegate).executeUpdate();
        inOrder.verify(emMock).close();
    }

    @Test
    public void getResultListClosesEntityManager() {
        sut.getResultList();
        final InOrder inOrder = Mockito.inOrder(delegate, emMock);
        inOrder.verify(delegate).getResultList();
        inOrder.verify(emMock).close();
    }

    @Test
    public void getSingleResultClosesEntityManager() {
        sut.getSingleResult();
        final InOrder inOrder = Mockito.inOrder(delegate, emMock);
        inOrder.verify(delegate).getSingleResult();
        inOrder.verify(emMock).close();
    }
}