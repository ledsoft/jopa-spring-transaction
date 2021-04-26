package com.github.ledsoft.jopa.spring.transaction;

import cz.cvut.kbss.jopa.exceptions.OWLPersistenceException;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class EntityManagerClosingResultSpliteratorTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private Consumer<Integer> consumerMock;

    private EntityManagerClosingResultSpliterator<Integer> sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void constructorCreatesSpliteratorWithOrderedNonNullAndImmutableCharacteristics() {
        this.sut = new EntityManagerClosingResultSpliterator<>(Stream.of(1, 2, 3, 4, 5).spliterator(), emMock);
        assertTrue(sut.hasCharacteristics(Spliterator.IMMUTABLE));
        assertTrue(sut.hasCharacteristics(Spliterator.ORDERED));
        assertTrue(sut.hasCharacteristics(Spliterator.NONNULL));
        assertFalse(sut.hasCharacteristics(Spliterator.CONCURRENT));
        assertFalse(sut.hasCharacteristics(Spliterator.DISTINCT));
        assertFalse(sut.hasCharacteristics(Spliterator.SIZED));
        assertFalse(sut.hasCharacteristics(Spliterator.SUBSIZED));
        assertFalse(sut.hasCharacteristics(Spliterator.SORTED));
    }

    @Test
    void tryAdvancePassesWrappedSpliteratorItemToConsumer() {
        final Spliterator<Integer> wrapped = Stream.of(1).spliterator();
        this.sut = new EntityManagerClosingResultSpliterator<>(wrapped, emMock);
        assertTrue(sut.tryAdvance(consumerMock));
        verify(consumerMock).accept(1);
    }

    @Test
    void tryAdvanceReturnsTrueWhenWrappedSpliteratorReturnsTrue() {
        final Spliterator<Integer> wrapped = Stream.of(1, 2, 3, 4, 5).spliterator();
        this.sut = new EntityManagerClosingResultSpliterator<>(wrapped, emMock);
        assertTrue(sut.tryAdvance(consumerMock));
    }

    @Test
    void tryAdvanceReturnsFalseWhenWrappedSpliteratorReturnsFalse() {
        final Spliterator<Integer> wrapped = new ArrayList<Integer>().spliterator();
        this.sut = new EntityManagerClosingResultSpliterator<>(wrapped, emMock);
        assertFalse(sut.tryAdvance(consumerMock));
    }

    @Test
    void tryAdvanceInvokesClosingProcedureWhenWrappedSpliteratorReturnsFalse() {
        final Spliterator<Integer> wrapped = new ArrayList<Integer>().spliterator();
        this.sut = new EntityManagerClosingResultSpliterator<>(wrapped, emMock);
        assertFalse(sut.tryAdvance(consumerMock));
        verify(emMock).close();
    }

    @Test
    void tryAdvanceInvokesClosingProcedureWhenConsumerThrowsException() {
        final Spliterator<Integer> wrapped = Stream.of(1, 2, 3, 4, 5).spliterator();
        this.sut = new EntityManagerClosingResultSpliterator<>(wrapped, emMock);
        doThrow(OWLPersistenceException.class).when(consumerMock).accept(any());
        assertThrows(OWLPersistenceException.class, () -> sut.tryAdvance(consumerMock));
        verify(emMock).close();
    }

    @Test
    void forEachRemainingInvokesMapperAndConsumerForAllRemainingResultSetItems() {
        final Spliterator<Integer> wrapped = Stream.of(1, 2, 3, 4, 5).spliterator();
        this.sut = new EntityManagerClosingResultSpliterator<>(wrapped, emMock);
        sut.forEachRemaining(consumerMock);
        for (int i = 0; i < 5; i++) {
            verify(consumerMock).accept(i + 1);
        }
    }

    @Test
    void forEachRemainingInvokesClosingProcedureAfterAllItemsHaveBeenProcessed() {
        final Spliterator<Integer> wrapped = Stream.of(1, 2, 3, 4, 5).spliterator();
        this.sut = new EntityManagerClosingResultSpliterator<>(wrapped, emMock);
        sut.forEachRemaining(consumerMock);
        verify(emMock).close();
    }

    @Test
    void forEachRemainingInvokesClosingProcedureWhenProcessingThrowsException() {
        final Spliterator<Integer> wrapped = Stream.of(1, 2, 3, 4, 5).spliterator();
        this.sut = new EntityManagerClosingResultSpliterator<>(wrapped, emMock);
        doThrow(OWLPersistenceException.class).when(consumerMock).accept(any());
        assertThrows(OWLPersistenceException.class, () -> sut.forEachRemaining(consumerMock));
        verify(emMock).close();
    }
}
