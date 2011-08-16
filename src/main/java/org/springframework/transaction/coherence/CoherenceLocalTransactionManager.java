package org.springframework.transaction.coherence;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.tangosol.coherence.transaction.Connection;
import com.tangosol.coherence.transaction.ConnectionFactory;
import com.tangosol.coherence.transaction.Isolation;
import com.tangosol.coherence.transaction.TransactionState;

/**
 * {@link org.springframework.transaction.PlatformTransactionManager} implementation
 * that manages local transactions for a single Coherence ConnectionFactory.
 * Binds a Coherence Connection from the specified ConnectionFactory to the thread,
 * potentially allowing for one thread-bound Connection per ConnectionFactory.
 *
 * <p>Application code is required to retrieve the Coherence Connection via
 * {@link ConnectionFactoryUtils#doGetConnection(ConnectionFactory)}*
 *
 * @author Ivan Evdokimov
 */
@SuppressWarnings("serial")
public class CoherenceLocalTransactionManager extends AbstractPlatformTransactionManager
		implements ResourceTransactionManager, InitializingBean {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

	private ConnectionFactory connectionFactory;
	
	/**
	 * Create a new CoherenceLocalTransactionManager instance.
	 * @param connectionFactory Coherence ConnectionFactory to manage local transactions for
	 * @throws IOException 
	 */
	public CoherenceLocalTransactionManager(ConnectionFactory cf) throws IOException {
		setConnectionFactory(cf);				
		afterPropertiesSet();
	}


	/**
	 * Set the Coherence ConnectionFactory that this instance should manage local
	 * transactions for.
	 */
	public void setConnectionFactory(ConnectionFactory cf) {		
			this.connectionFactory = cf;
	}

	/**
	 * Return the Coherence ConnectionFactory that this instance manages local
	 * transactions for.
	 */
	public ConnectionFactory getConnectionFactory() {
		return this.connectionFactory;
	}

	public void afterPropertiesSet() {
		if (getConnectionFactory() == null) {
			throw new IllegalArgumentException("Property 'connectionFactory' is required");
		}
	}


	public Object getResourceFactory() {
		return getConnectionFactory();
	}

	@Override
	protected Object doGetTransaction() {
		CoherenceLocalTransactionObject txObject = new CoherenceLocalTransactionObject();
		ConnectionHolder conHolder =
		    (ConnectionHolder) TransactionSynchronizationManager.getResource(getConnectionFactory());
		txObject.setConnectionHolder(conHolder);
		return txObject;
	}

	@Override
	protected boolean isExistingTransaction(Object transaction) {
		CoherenceLocalTransactionObject txObject = (CoherenceLocalTransactionObject) transaction;
		return (txObject.getConnectionHolder() != null);
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		CoherenceLocalTransactionObject txObject = (CoherenceLocalTransactionObject) transaction;

		Connection con = null;		
		try {		    			
            con = getConnectionFactory().createConnection(getClass().getClassLoader());			
			if (logger.isDebugEnabled()) {
	            logger.debug("Aquired connection : {} ",
	                    new Object[] { con });
			}
			con.setAutoCommit(false);
			con.setEager(false);
			
			if (definition.getIsolationLevel()==TransactionDefinition.ISOLATION_REPEATABLE_READ)
			    con.setIsolationLevel(Isolation.STMT_MONOTONIC_CONSISTENT_READ);
			
			if (definition.getIsolationLevel()==TransactionDefinition.ISOLATION_SERIALIZABLE)
			    con.setIsolationLevel(Isolation.TX_MONOTONIC_CONSISTENT_READ);
			
			txObject.setConnectionHolder(new ConnectionHolder(con));
			txObject.getConnectionHolder().setSynchronizedWithTransaction(true);
			
			int timeout = determineTimeout(definition);
			if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
				txObject.getConnectionHolder().setTimeoutInSeconds(timeout);
			}
			TransactionSynchronizationManager.bindResource(getConnectionFactory(), txObject.getConnectionHolder());
		}		
		catch (Exception ex) {
			ConnectionFactoryUtils.releaseConnection(con, getConnectionFactory());
			throw new TransactionSystemException("Unexpected failure on begin of Coherence local transaction", ex);
		}		
	}

	@Override
	protected Object doSuspend(Object transaction) {
		CoherenceLocalTransactionObject txObject = (CoherenceLocalTransactionObject) transaction;
		txObject.setConnectionHolder(null);
		return TransactionSynchronizationManager.unbindResource(getConnectionFactory());
	}

	@Override
	protected void doResume(Object transaction, Object suspendedResources) {
		ConnectionHolder conHolder = (ConnectionHolder) suspendedResources;
		TransactionSynchronizationManager.bindResource(getConnectionFactory(), conHolder);
	}

	protected boolean isRollbackOnly(Object transaction) throws TransactionException {
		CoherenceLocalTransactionObject txObject = (CoherenceLocalTransactionObject) transaction;
		return txObject.getConnectionHolder().isRollbackOnly();
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		CoherenceLocalTransactionObject txObject = (CoherenceLocalTransactionObject) status.getTransaction();
		Connection con = txObject.getConnectionHolder().getConnection();
		if (logger.isDebugEnabled()) {
		    TransactionState transactionState = con.getTransactionState();
            logger.debug("Committing Coherence local transaction on Connection : xid={}, isolation={} , status={}",
                    new Object[] { transactionState.getXid(), transactionState.getIsolation().name(),
                            transactionState.getStatus().name() });
		}
		try {
			con.commit();
		}		
		catch (Exception ex) {
			throw new TransactionSystemException("Unexpected failure on commit of Coherence local transaction", ex);
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		CoherenceLocalTransactionObject txObject = (CoherenceLocalTransactionObject) status.getTransaction();
		Connection con = txObject.getConnectionHolder().getConnection();
		if (logger.isDebugEnabled()) {
		    TransactionState transactionState = con.getTransactionState();
            logger.debug("Rolling back Coherence local transaction on Connection : xid={}, isolation={} , status={}",
                    new Object[] { transactionState.getXid(), transactionState.getIsolation().name(),
                            transactionState.getStatus().name() });
		}
		try {
			con.rollback();
		}		
		catch (Exception ex) {
			throw new TransactionSystemException("Unexpected failure on rollback of Coherence local transaction", ex);
		}
	}

	@Override
	protected void doSetRollbackOnly(DefaultTransactionStatus status) {
		CoherenceLocalTransactionObject txObject = (CoherenceLocalTransactionObject) status.getTransaction();
		if (logger.isDebugEnabled()) {			
			TransactionState transactionState = txObject.getConnectionHolder().getConnection().getTransactionState();
            logger.debug("Setting Coherence local transaction : xid={}, isolation={} , status={} rollback-only",
                    new Object[] { transactionState.getXid(), transactionState.getIsolation().name(),
                            transactionState.getStatus().name() });
		}
		txObject.getConnectionHolder().setRollbackOnly();
	}

	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		CoherenceLocalTransactionObject txObject = (CoherenceLocalTransactionObject) transaction;

		// Remove the connection holder from the thread.
		TransactionSynchronizationManager.unbindResource(getConnectionFactory());
		txObject.getConnectionHolder().clear();

		Connection con = txObject.getConnectionHolder().getConnection();
		if (logger.isDebugEnabled()) {
            logger.debug("Releasing Coherence Connection {} after transaction",
                    new Object[] { con.toString() });
		}
		ConnectionFactoryUtils.releaseConnection(con, getConnectionFactory());
	}


	/**
	 * Coherence local transaction object, representing a ConnectionHolder.
	 * Used as transaction object by CoherenceLocalTransactionManager.
	 * @see ConnectionHolder
	 */
	private static class CoherenceLocalTransactionObject {

		private ConnectionHolder connectionHolder;

		public void setConnectionHolder(ConnectionHolder connectionHolder) {
			this.connectionHolder = connectionHolder;
		}

		public ConnectionHolder getConnectionHolder() {
			return connectionHolder;
		}
	}

}
