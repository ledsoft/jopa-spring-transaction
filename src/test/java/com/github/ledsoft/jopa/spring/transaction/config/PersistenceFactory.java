package com.github.ledsoft.jopa.spring.transaction.config;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.rdf4j.Rdf4jDataSource;
import cz.cvut.kbss.ontodriver.rdf4j.config.Rdf4jOntoDriverProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class PersistenceFactory {

    private EntityManagerFactory emf;

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return emf;
    }

    @PostConstruct
    private void init() {
        final Map<String, String> properties = new HashMap<>();
        properties.put(JOPAPersistenceProperties.LANG, "en");
        properties.put(JOPAPersistenceProperties.SCAN_PACKAGE, "com.github.ledsoft.jopa.spring.transaction.model");
        properties.put(JOPAPersistenceProperties.CACHE_ENABLED, Boolean.FALSE.toString());
        properties.put(Rdf4jOntoDriverProperties.USE_VOLATILE_STORAGE, Boolean.TRUE.toString());
        properties.put(JOPAPersistenceProperties.JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());
        properties.put(JOPAPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY, "test-repository");
        properties.put(JOPAPersistenceProperties.DATA_SOURCE_CLASS, Rdf4jDataSource.class.getCanonicalName());
        this.emf = Persistence.createEntityManagerFactory("testPU", properties);
    }

    @PreDestroy
    private void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
