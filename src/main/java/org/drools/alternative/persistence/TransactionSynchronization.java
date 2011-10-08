package org.drools.alternative.persistence;


public interface TransactionSynchronization {    
    
    void beforeCompletion();
    
    void afterCompletion(int status);
}