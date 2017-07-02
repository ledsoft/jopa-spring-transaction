package cz.cvut.kbss.jopa.spring.transaction;

class TransactionObjectFactory {


    /**
     * Creates a new (default) transaction object.
     *
     * @return Instance representing a transaction
     */
    JopaTransactionDefinition createTransactionObject() {
        return new JopaTransactionDefinition();
    }
}
