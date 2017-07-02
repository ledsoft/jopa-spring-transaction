package cz.cvut.kbss.jopa.spring.transaction.model;

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
    public String toString() {
        return "Phone{" +
                "uri=" + uri +
                ", number='" + number + '\'' +
                '}';
    }
}
