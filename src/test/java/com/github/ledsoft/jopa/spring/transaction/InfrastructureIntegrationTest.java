package com.github.ledsoft.jopa.spring.transaction;

import com.github.ledsoft.jopa.spring.transaction.config.PersistenceConfig;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersistenceConfig.class})
@EnableTransactionManagement
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InfrastructureIntegrationTest {

    @Autowired
    private EntityManager em;

    @Test
    public void injectsEntityManagerDelegate() {
        assertTrue(em instanceof DelegatingEntityManager);
    }

    @Test
    @Transactional("txManager")
    public void activeTransactionIsAvailableInTransactionalMethod() {
        assertNotNull(em);
        assertTrue(em.getTransaction().isActive());
    }
}
