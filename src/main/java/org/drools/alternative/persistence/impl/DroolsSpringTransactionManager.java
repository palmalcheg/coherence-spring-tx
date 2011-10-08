package org.drools.alternative.persistence.impl;


import org.drools.alternative.persistence.TransactionManager;
import org.drools.alternative.persistence.TransactionSynchronization;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adopted from drools-jpa module
 * @author Mark Proctor
 * @author Esteban Aliverti
 */
public class DroolsSpringTransactionManager
    implements
    TransactionManager {

    Logger                               logger                                            = LoggerFactory.getLogger( getClass() );
    private AbstractPlatformTransactionManager ptm;

    TransactionDefinition                      td                 = new DefaultTransactionDefinition();
    TransactionStatus                          currentTransaction = null;

    public DroolsSpringTransactionManager(AbstractPlatformTransactionManager ptm) {
        this.ptm = ptm;
    }

    private boolean localTransaction;

    public void begin() {
        if ( getStatus() == TransactionManager.STATUS_NO_TRANSACTION ) {
            // If there is no transaction then start one, we will commit within the same Command
            // it seems in spring calling getTransaction is enough to begin a new transaction
            currentTransaction = this.ptm.getTransaction( td );
            localTransaction = true;
        } else {
            localTransaction = false;
        }
    }

    public void commit() {
        if ( this.localTransaction ) {
            // if we didn't begin this transaction, then do nothing
            this.localTransaction = false;
            this.ptm.commit( currentTransaction );
            currentTransaction = null;
        }
    }

    public void rollback() {
	if ( this.localTransaction ) {
        	this.localTransaction = false;
        	this.ptm.rollback( currentTransaction );
		currentTransaction = null;
	}
    }

    /**
     * Borrowed from Seam
     * @author Michael Youngstrom
     */
    public int getStatus() {
        if ( ptm == null ) {
            return TransactionManager.STATUS_NO_TRANSACTION;
        }

        logger.debug("Current TX name (According to TransactionSynchronizationManager) : "+TransactionSynchronizationManager.getCurrentTransactionName());
        if ( TransactionSynchronizationManager.isActualTransactionActive() ) {
            TransactionStatus transaction = null;
            try {
                if ( currentTransaction == null ) {
                    transaction = ptm.getTransaction( td );
                    if ( transaction.isNewTransaction() ) {
                        return TransactionManager.STATUS_COMMITTED;
                    }
                } else {
                    transaction = currentTransaction;
                }
                logger.debug("Current TX: "+transaction);
                // If SynchronizationManager thinks it has an active transaction but
                // our transaction is a new one
                // then we must be in the middle of committing
                if ( transaction.isCompleted() ) {
                    if ( transaction.isRollbackOnly() ) {
                        return TransactionManager.STATUS_ROLLEDBACK;
                    }
                    return TransactionManager.STATUS_COMMITTED;
                } else {
                    if ( transaction.isRollbackOnly() ) {
                        return TransactionManager.STATUS_ROLLEDBACK;
                    }
                    return TransactionManager.STATUS_ACTIVE;
                }
            } finally {
                if ( currentTransaction == null ) {
                    ptm.commit( transaction );
                }
            }
        }
        return TransactionManager.STATUS_NO_TRANSACTION;
    }

    public void registerTransactionSynchronization(TransactionSynchronization ts) {
        TransactionSynchronizationManager.registerSynchronization( new SpringTransactionSynchronizationAdapter( ts ) );
    }
}