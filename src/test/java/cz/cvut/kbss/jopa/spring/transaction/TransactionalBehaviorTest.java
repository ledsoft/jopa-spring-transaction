package cz.cvut.kbss.jopa.spring.transaction;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.spring.transaction.config.PersistenceConfig;
import cz.cvut.kbss.jopa.spring.transaction.model.Person;
import cz.cvut.kbss.jopa.spring.transaction.model.Phone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class})
@EnableTransactionManagement
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionalBehaviorTest {

    @Autowired
    private EntityManager em;

    @Test
    public void transactionalMethodPersistsData() throws Exception {
        final Person p = new Person("Catherine Halsey");
        p.addPhone(new Phone("123 456 789"));
        p.addPhone(new Phone("987 654 321"));
        persist(p);
        verifyData(p);
    }

    @Transactional("txManager")
    private void persist(Person data) {
        em.persist(data);
    }

    private void verifyData(Person p) {
        final Person result = em.find(Person.class, p.getUri());
        assertNotNull(result);
        assertEquals(p.getName(), result.getName());
        assertEquals(p.getPhones(), result.getPhones());
    }
}
