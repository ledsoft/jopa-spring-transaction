package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class EntityManagerClosingQueryProxyTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private Query delegate;

    private EntityManagerClosingQueryProxy sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new EntityManagerClosingQueryProxy(delegate, emMock);
    }

    @Test
    void executeUpdateClosesEntityManagerAfterExecution() {
        sut.executeUpdate();
        final InOrder inOrder = Mockito.inOrder(delegate, emMock);
        inOrder.verify(delegate).executeUpdate();
        inOrder.verify(emMock).close();
    }

    @Test
    void getResultListClosesEntityManager() {
        sut.getResultList();
        final InOrder inOrder = Mockito.inOrder(delegate, emMock);
        inOrder.verify(delegate).getResultList();
        inOrder.verify(emMock).close();
    }

    @Test
    void getSingleResultClosesEntityManager() {
        sut.getSingleResult();
        final InOrder inOrder = Mockito.inOrder(delegate, emMock);
        inOrder.verify(delegate).getSingleResult();
        inOrder.verify(emMock).close();
    }

    @Test
    void getResultStreamClosesEntityManagerAfterStreamProcessingIsDone() {
        final Stream<Integer> resultStream = Stream.of(1, 2, 3, 4, 5);
        final Consumer<?> consumer = mock(Consumer.class);
        when(delegate.getResultStream()).thenReturn(resultStream);
        sut.getResultStream().forEach(consumer);
        final InOrder inOrder = Mockito.inOrder(delegate, consumer, emMock);
        inOrder.verify(delegate).getResultStream();
        inOrder.verify(consumer, times(5)).accept(any());
        inOrder.verify(emMock).close();
    }
}
