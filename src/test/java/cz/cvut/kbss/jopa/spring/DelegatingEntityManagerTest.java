package cz.cvut.kbss.jopa.spring;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.spring.model.Person;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DelegatingEntityManagerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DelegatingEntityManager sut = new DelegatingEntityManager();

    @Test
    public void delegatesCallsToCurrentEntityManager() {
        final EntityManager em = mock(EntityManager.class);
        sut.setLocalTransaction(new JopaTransactionDefinition(em));

        final Person instance = new Person();
        sut.persist(instance);
        verify(em).persist(instance);
    }

    @Test
    public void throwsTransactionMissingExceptionWhenTransactionIsNotAssociatedWithThread() {
        thrown.expect(TransactionMissingException.class);
        sut.persist(new Person());
    }

    @Test
    public void twoThreadsWorkWithDifferentTransactionManagers() throws Exception {
        final EntityManager emOne = mock(EntityManager.class);
        final EntityManager emTwo = mock(EntityManager.class);
        final CountDownLatch latch = new CountDownLatch(2);
        final Thread tOne = new Thread(new TestTransaction(emOne, latch));
        final Thread tTwo = new Thread(new TestTransaction(emTwo, latch));
        tOne.start();
        tTwo.start();
        latch.await();

        verify(emOne).persist(any());
        verify(emOne).contains(any());
        verify(emTwo).persist(any());
        verify(emTwo).contains(any());
    }

    private class TestTransaction implements Runnable {

        private EntityManager em;
        private CountDownLatch countDown;

        TestTransaction(EntityManager em, CountDownLatch countDown) {
            this.em = em;
            this.countDown = countDown;
        }

        @Override
        public void run() {
            DelegatingEntityManagerTest.this.sut.setLocalTransaction(new JopaTransactionDefinition(em));
            DelegatingEntityManagerTest.this.sut.persist(new Person());
            DelegatingEntityManagerTest.this.sut.contains("Test");
            countDown.countDown();
        }
    }
}