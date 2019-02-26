package com.github.ledsoft.jopa.spring.transaction;

import com.github.ledsoft.jopa.spring.transaction.config.PersistenceConfig;
import com.github.ledsoft.jopa.spring.transaction.model.Person;
import com.github.ledsoft.jopa.spring.transaction.model.Phone;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.transactions.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceConfig.class})
@EnableTransactionManagement
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionalBehaviorTest {

    @Autowired
    private EntityManager em;

    @Test
    @Transactional("txManager")
    public void transactionalMethodPersistsData() {
        final Person p = new Person("Catherine Halsey");
        p.addPhone(new Phone("123 456 789"));
        p.addPhone(new Phone("987 654 321"));
        persist(p);
        verifyData(p);
    }

    private void persist(Person data) {
        em.persist(data);
    }

    private void verifyData(Person p) {
        final Person result = em.find(Person.class, p.getUri());
        assertNotNull(result);
        assertEquals(p.getName(), result.getName());
        assertEquals(p.getPhones(), result.getPhones());
    }

    @Test
    @Transactional("txManager")
    public void transactionPropagatesToAnotherTransactionalMethod() {
        final Person p = new Person("Catherine Halsey");
        em.persist(p);
        final EntityTransaction currentTransaction = em.getTransaction();
        final Person p2 = transactionalPersist(currentTransaction);
        verifyData(p);
        verifyData(p2);
    }

    @Transactional("txManager")
    public Person transactionalPersist(EntityTransaction transactionObject) {
        assertSame(transactionObject, em.getTransaction());
        final Person p2 = new Person("Thomas Lasky");
        em.persist(p2);
        return p2;
    }
}
