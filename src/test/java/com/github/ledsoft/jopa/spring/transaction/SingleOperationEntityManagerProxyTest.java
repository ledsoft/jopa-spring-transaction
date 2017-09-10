package com.github.ledsoft.jopa.spring.transaction;

import com.github.ledsoft.jopa.spring.transaction.model.Person;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class SingleOperationEntityManagerProxyTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"find", Arrays.asList(Class.class, Object.class), Arrays.asList(Person.class, new Object())},
                {"find", Arrays.asList(Class.class, Object.class, Descriptor.class),
                 Arrays.asList(Person.class, new Object(), new EntityDescriptor())},
                {"clear", Collections.emptyList(), Collections.emptyList()},
                {"detach", Collections.singletonList(Object.class), Collections.singletonList(new Person())},
                {"contains", Collections.singletonList(Object.class), Collections.singletonList(new Person())},
                {"isConsistent", Collections.singletonList(URI.class),
                 Collections.singletonList(URI.create("http://example.org"))},
                {"getTransaction", Collections.emptyList(), Collections.emptyList()},
                {"getContexts", Collections.emptyList(), Collections.emptyList()}
        });
    }

    @Parameterized.Parameter
    public String methodName;

    @Parameterized.Parameter(1)
    public List<Class<?>> paramTypes;

    @Parameterized.Parameter(2)
    public List<Object> arguments;

    @Mock
    private EntityManager delegate;

    private SingleOperationEntityManagerProxy sut;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.sut = new SingleOperationEntityManagerProxy(delegate);
    }

    @Test
    public void callsDelegateMethodAndClosesIt() throws Exception {
        final Method m = SingleOperationEntityManagerProxy.class
                .getDeclaredMethod(methodName, paramTypes.toArray(new Class[paramTypes.size()]));
        m.invoke(sut, arguments.toArray());
        final MockingDetails details = Mockito.mockingDetails(delegate);
        final List<String> methodsInvoked =
                details.getInvocations().stream().map(inv -> inv.getMethod().getName()).collect(
                        Collectors.toList());
        assertTrue(methodsInvoked.contains(m.getName()));
        verify(delegate).close();
    }
}