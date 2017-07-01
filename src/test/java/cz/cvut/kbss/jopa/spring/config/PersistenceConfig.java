package cz.cvut.kbss.jopa.spring.config;

import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.spring.JopaTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Import(PersistenceFactory.class)
public class PersistenceConfig {

    @Bean(name = "txManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        // TODO
        return new JopaTransactionManager();
    }
}
