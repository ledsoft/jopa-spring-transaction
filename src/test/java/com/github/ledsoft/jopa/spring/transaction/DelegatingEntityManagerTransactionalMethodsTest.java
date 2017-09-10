package com.github.ledsoft.jopa.spring.transaction;

import com.github.ledsoft.jopa.spring.transaction.model.Person;
import cz.cvut.kbss.jopa.exceptions.TransactionRequiredException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.CustomMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class DelegatingEntityManagerTransactionalMethodsTest {

    @Parameterized.Parameters(name = "method: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"persist", new Person()},
                {"merge", new Person()},
                {"remove", new Person()},
                {"refresh", new Person()},
                {"flush", null}
        });
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DelegatingEntityManager sut = new DelegatingEntityManager();

    @Parameterized.Parameter
    public String methodName;

    @Parameterized.Parameter(1)
    public Object argument;

    @Test
    public void transactionalMethodThrowsTransactionRequiredExceptionWhenInvokedWithoutTransaction() throws Exception {
        thrown.expectCause(CoreMatchers.allOf(CoreMatchers.isA(TransactionRequiredException.class),
                new CauseMessageMatcher(
                        "Transaction required when calling " + methodName + " on container-managed entity manager.")));
        if (argument != null) {
            final Method m = sut.getClass().getDeclaredMethod(methodName, Object.class);
            m.invoke(sut, argument);
        } else {
            final Method m = sut.getClass().getDeclaredMethod(methodName);
            m.invoke(sut);
        }
    }

    private static final class CauseMessageMatcher extends CustomMatcher<TransactionRequiredException> {

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
