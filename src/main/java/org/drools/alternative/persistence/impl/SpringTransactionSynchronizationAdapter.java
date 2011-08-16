package org.drools.alternative.persistence.impl;

import org.drools.alternative.persistence.TransactionManager;
import org.drools.alternative.persistence.TransactionSynchronization;


public class SpringTransactionSynchronizationAdapter
    implements
    org.springframework.transaction.support.TransactionSynchronization {
    private TransactionSynchronization ts;
    
    public SpringTransactionSynchronizationAdapter(TransactionSynchronization ts) {
        this.ts = ts;
    }

    public void afterCommit() {
    }

    public void afterCompletion(int status) {
        switch ( status ) {
            case org.springframework.transaction.support.TransactionSynchronization.STATUS_COMMITTED : {
                this.ts.afterCompletion( TransactionManager.STATUS_COMMITTED );
                break;
            }
            case org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK : {
                this.ts.afterCompletion( TransactionManager.STATUS_ROLLEDBACK );
                break;
            }
            default : {
                this.ts.afterCompletion( TransactionManager.STATUS_UNKNOWN );
            }
        }
    }

    public void beforeCommit(boolean readOnly) {
    }

    public void beforeCompletion() {
        this.ts.beforeCompletion();
    }

    public void resume() {
    }

    public void suspend() {
    }

    @Override
    public void flush() {        
    }

}