# JOPA-Spring-transaction

Integration of JOPA transactions into the Spring declarative transactions - the `@Transactional` annotation.

### Notes

- Isolation level configuration is currently not supported, because it is not supported by JOPA either.
- When a method is not transactional, the library with create an `EntityManager` instance, which will
be automatically closed after one operation/query execution.

## Usage

Usage of the library is fairly simple. It is only necessary to instantiate the `JopaTransactionManager` 
and `DelegatingEntityManager` Spring beans.
- `JopaTransactionManager` implements Spring's `TransactionManager` interface and Spring calls it at significant
moments of the transaction lifecycle (begin, commit etc.),
- `DelegatingEntityManager` implements JOPA's `EntityManager` interface and is injected into transactional Spring beans,
where it delegates calls to the `EntityManager` instance bound to the current transaction. This is the only class with
which the end users directly interact.

Do not forget to `EnableTransactionManagement`.

Assuming there is an `EntityManagerFactory` Spring bean, Java-based configuration of the aforementioned beans looks 
for example as follows:

```java
@Configuration
@EnableTransactionManagement
@Import(PersistenceFactory.class)
public class PersistenceConfig {

    @Bean
    public DelegatingEntityManager entityManager() {
        return new DelegatingEntityManager();
    }

    @Bean(name = "txManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf, 
                                                         DelegatingEntityManager emProxy) {
        return new JopaTransactionManager(emf, emProxy);
    }
}
```

`EntityManager` is then injected into beans using `Autowired` or `Inject` (`PersistenceContext` is currently not supported,
as it is closely tied to JPA):

```
@Autowired
private EntityManager em;
```

The tests contain a complete setup. For a fully operational example, see 
[JOPA Example 04 - JOPA + Spring](https://github.com/kbss-cvut/jopa-examples/tree/master/example04-jopa-spring) and the
[JSON-LD demo](https://github.com/kbss-cvut/jopa-examples/tree/master/jsonld).

## Getting the Library

The library is now available in [Maven central](http://search.maven.org/#search%7Cga%7C1%7Ccom.github.ledsoft),
so getting it is just a matter of adding a Maven dependency:

```xml
<dependency>
    <groupId>com.github.ledsoft</groupId>
    <artifactId>jopa-spring-transaction</artifactId>
</dependency>
```

## JOPA

See [https://github.com/kbss-cvut/jopa](https://github.com/kbss-cvut/jopa).

## License

MIT
