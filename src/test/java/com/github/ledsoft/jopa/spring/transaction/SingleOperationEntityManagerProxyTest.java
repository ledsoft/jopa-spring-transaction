package com.github.ledsoft.jopa.spring.transaction;

import com.github.ledsoft.jopa.spring.transaction.model.Person;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class SingleOperationEntityManagerProxyTest {

    private static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("find", Arrays.asList(Class.class, Object.class),
                        Arrays.asList(Person.class, new Object())),
                Arguments.of("find", Arrays.asList(Class.class, Object.class, Descriptor.class),
                        Arrays.asList(Person.class, new Object(), new EntityDescriptor())),
                Arguments.of("getReference", Arrays.asList(Class.class, Object.class),
                        Arrays.asList(Person.class, new Object())),
                Arguments.of("getReference", Arrays.asList(Class.class, Object.class, Descriptor.class),
                        Arrays.asList(Person.class, new Object(), new EntityDescriptor())),
                Arguments.of("clear", Collections.emptyList(), Collections.emptyList()),
                Arguments
                        .of("detach", Collections.singletonList(Object.class), Collections.singletonList(new Person())),
                Arguments.of("contains", Collections.singletonList(Object.class),
                        Collections.singletonList(new Person())),
                Arguments.of("isConsistent", Collections.singletonList(URI.class),
                        Collections.singletonList(URI.create("http://example.org"))),
                Arguments.of("getTransaction", Collections.emptyList(), Collections.emptyList()),
                Arguments.of("getContexts", Collections.emptyList(), Collections.emptyList())
        );
    }

    @Mock
    private EntityManager delegate;

    private SingleOperationEntityManagerProxy sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.sut = new SingleOperationEntityManagerProxy(delegate);
    }

    @ParameterizedTest
    @MethodSource("data")
    void callsDelegateMethodAndClosesIt(String methodName, List<Class<?>> paramTypes, List<Object> arguments)
            throws Exception {
        final Method m = SingleOperationEntityManagerProxy.class
                .getDeclaredMethod(methodName, paramTypes.toArray(new Class[0]));
        m.invoke(sut, arguments.toArray());
        final MockingDetails details = Mockito.mockingDetails(delegate);
        final List<String> methodsInvoked =
                details.getInvocations().stream().map(inv -> inv.getMethod().getName()).collect(
                        Collectors.toList());
        assertTrue(methodsInvoked.contains(m.getName()));
        verify(delegate).close();
    }
}