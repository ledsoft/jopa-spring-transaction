package com.github.ledsoft.jopa.spring.transaction;

import com.github.ledsoft.jopa.spring.transaction.model.Person;
import cz.cvut.kbss.jopa.exceptions.TransactionRequiredException;
import org.hamcrest.CustomMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DelegatingEntityManagerTransactionalMethodsTest {

    private static Stream<Arguments> data() {
        return Stream.of(Arguments.of("persist", new Person()),
                Arguments.of("merge", new Person()),
                Arguments.of("remove", new Person()),
                Arguments.of("refresh", new Person()),
                Arguments.of("flush", null));
    }

    private DelegatingEntityManager sut = new DelegatingEntityManager();

    @ParameterizedTest
    @MethodSource("data")
    void transactionalMethodThrowsTransactionRequiredExceptionWhenInvokedWithoutTransaction(String methodName,
                                                                                            Object argument)
            throws Exception {
        final InvocationTargetException result;
        if (argument != null) {
            final Method m = sut.getClass().getDeclaredMethod(methodName, Object.class);
            result = assertThrows(InvocationTargetException.class, () -> m.invoke(sut, argument));
        } else {
            final Method m = sut.getClass().getDeclaredMethod(methodName);
            result = assertThrows(InvocationTargetException.class, () -> m.invoke(sut));
        }
        assertThat(result.getCause(), allOf(instanceOf(TransactionRequiredException.class), new CauseMessageMatcher(
                "Transaction required when calling " + methodName + " on container-managed entity manager.")));
    }

    private static final class CauseMessageMatcher extends CustomMatcher<Throwable> {

        private final String message;

        private CauseMessageMatcher(String message) {
            super("cause message");
            this.message = message;
        }

        @Override
        public boolean matches(Object item) {
            final TransactionRequiredException e = (TransactionRequiredException) item;
            return e.getMessage().contains(message);
        }
    }
}
