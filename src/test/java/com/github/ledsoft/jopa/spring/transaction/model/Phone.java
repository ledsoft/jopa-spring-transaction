package com.github.ledsoft.jopa.spring.transaction.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.Namespace;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

import java.net.URI;

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

        if (uri != null ? !uri.equals(phone.uri) : phone.uri != null) return false;
        return number != null ? number.equals(phone.number) : phone.number == null;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "uri=" + uri +
                ", number='" + number + '\'' +
                '}';
    }
}
