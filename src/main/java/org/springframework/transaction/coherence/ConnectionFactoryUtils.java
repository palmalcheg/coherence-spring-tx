package org.springframework.transaction.coherence;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import com.tangosol.coherence.transaction.Connection;
import com.tangosol.coherence.transaction.ConnectionFactory;
import com.tangosol.coherence.transaction.DefaultConnectionFactory;

/**
 * Helper class that provides static methods for obtaining Coherence Connections
 * from a {@link com.tangosol.coherence.transaction.ConnectionFactory}. Includes
 * special support for Spring-managed transactional Connections, e.g. managed by
 * {@link CoherenceLocalTransactionManager}.
 * 
 * 
 * @author Ivan Evdokimov
 * @see #releaseConnection
 * @see CoherenceLocalTransactionManager
 * @see org.springframework.transaction.support.TransactionSynchronizationManager
 */
public abstract class ConnectionFactoryUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionFactoryUtils.class);

    /**
     * Actually obtain a Coherence Connection from the given ConnectionFactory.
     * Same as {@link #getConnection}, but throwing the original
     * ResourceException.
     * <p>
     * Is aware of a corresponding Connection bound to the current thread, for
     * example when using {@link CoherenceLocalTransactionManager}. 
     * 
     * @param cf
     *            the ConnectionFactory to obtain Connection from
     * @return a Coherence Connection from the given ConnectionFactory
     * @throws ResourceException
     *             if thrown by Coherence API methods
     * @see #doReleaseConnection
     */
    public static Connection doGetConnection(ConnectionFactory cf) {
        
        Assert.notNull(cf, "No ConnectionFactory specified");        

        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(cf);
        if (conHolder != null) {
            return conHolder.getConnection();
        }

        logger.debug("Opening Coherence Connection");

        Connection con = cf.createConnection(ConnectionFactoryUtils.class.getClassLoader());
        con.setAutoCommit(false);
        con.setEager(false);
        
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            logger.debug("Registering transaction synchronization for Coherence Connection");
            conHolder = new ConnectionHolder(con);
            conHolder.setSynchronizedWithTransaction(true);
            TransactionSynchronizationManager.registerSynchronization(new ConnectionSynchronization(conHolder, cf));
            TransactionSynchronizationManager.bindResource(cf, conHolder);
        }

        return con;
    }

    /**
     * Determine whether the given Coherence Connection is transactional, that
     * is, bound to the current thread by Spring's transaction facilities.
     * 
     * @param con
     *            the Connection to check
     * @param cf
     *            the ConnectionFactory that the Connection was obtained from
     *            (may be <code>null</code>)
     * @return whether the Connection is transactional
     */
    public static boolean isConnectionTransactional(Connection con, ConnectionFactory cf) {
        if (cf == null) {
            return false;
        }
        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(cf);
        return (conHolder != null && conHolder.getConnection() == con);
    }

    /**
     * Close the given Connection, obtained from the given ConnectionFactory, if
     * it is not managed externally (that is, not bound to the thread).
     * 
     * @param con
     *            the Connection to close if necessary (if this is
     *            <code>null</code>, the call will be ignored)
     * @param cf
     *            the ConnectionFactory that the Connection was obtained from
     *            (can be <code>null</code>)
     * @see #getConnection
     */
    public static void releaseConnection(Connection con, ConnectionFactory cf) {
        try {
            doReleaseConnection(con, cf);
        } catch (Throwable ex) {
            // We don't trust the Coherence driver: It might throw
            // RuntimeException or Error.
            logger.debug("Unexpected exception on closing Coherence Connection", ex);
        }
    }

    /**
     * Actually close the given Connection, obtained from the given
     * ConnectionFactory. Same as {@link #releaseConnection}, but throwing the
     * original ResourceException.
     * 
     * @param con
     *            the Connection to close if necessary (if this is
     *            <code>null</code>, the call will be ignored)
     * @param cf
     *            the ConnectionFactory that the Connection was obtained from
     *            (can be <code>null</code>)
     * @see #doGetConnection
     */
    public static void doReleaseConnection(Connection con, ConnectionFactory cf) {
        if (con == null || isConnectionTransactional(con, cf)) {
            return;
        }
        con.close();
    }

    /**
     * Callback for resource cleanup at the end of a non-native Coherence
     * transaction (e.g. when participating in a JTA transaction).
     */
    private static class ConnectionSynchronization extends
            ResourceHolderSynchronization<ConnectionHolder, ConnectionFactory> {

        public ConnectionSynchronization(ConnectionHolder connectionHolder, ConnectionFactory connectionFactory) {
            super(connectionHolder, connectionFactory);
        }

        @Override
        protected void releaseResource(ConnectionHolder resourceHolder, ConnectionFactory resourceKey) {
            releaseConnection(resourceHolder.getConnection(), resourceKey);
        }
    }  
   
    
    public static ConnectionFactory createConnectionFactory (String cacheConfig) {        
          DefaultConnectionFactory cf = new DefaultConnectionFactory(cacheConfig);
          cf.createConnection(ConnectionFactoryUtils.class.getClassLoader());
          return cf;
     }

}
