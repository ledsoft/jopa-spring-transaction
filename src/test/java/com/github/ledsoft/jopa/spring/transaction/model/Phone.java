package com.github.ledsoft.jopa.spring.transaction.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

import java.net.URI;
import java.util.Objects;

@Namespace(prefix = "ex", namespace = "http://www.example.org/")
@OWLClass(iri = "ex:Phone")
public class Phone {

    @Id(generated = true)
    private URI uri;

    @OWLDataProperty(iri = "ex:number")
    private String number;

    public Phone() {
    }

    public Phone(String number) {
        this.number = number;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(uri, phone.uri) && Objects.equals(number, phone.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, number);
    }

    @Override
    public String toString() {
        return "Phone{" +
                "uri=" + uri +
                ", number='" + number + '\'' +
                '}';
    }
}
