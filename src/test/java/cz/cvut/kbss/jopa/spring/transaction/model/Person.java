package cz.cvut.kbss.jopa.spring.transaction.model;

import cz.cvut.kbss.jopa.model.annotations.*;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Namespace(prefix = "ex", namespace = "http://www.example.org/")
@OWLClass(iri = "ex:Person")
public class Person {

    @Id
    private URI uri;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = "ex:name")
    private String name;

    @OWLObjectProperty(iri = "ex:hasPhone", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Phone> phones;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
        this.uri = URI.create("http://www.example.org/" + name.replace(" ", "+"));
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Phone> getPhones() {
        return phones;
    }

    public void setPhones(Set<Phone> phones) {
        this.phones = phones;
    }

    public void addPhone(Phone phone) {
        if (phones == null) {
            this.phones = new HashSet<>();
        }
        phones.add(phone);
    }

    @Override
    public String toString() {
        return "Person{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", phones=" + phones +
                '}';
    }
}
