package com.db.itrac.rules;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import org.drools.persistence.processinstance.persisters.VariablePersister;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.itrac.domain.TradeAttributes;
import com.oracle.coherence.common.sequencegenerators.SequenceGenerator;
import com.tangosol.coherence.transaction.Connection;
import com.tangosol.coherence.transaction.ConnectionFactory;
import com.tangosol.coherence.transaction.OptimisticNamedCache;
import com.tangosol.util.Filter;
import com.tangosol.util.QueryHelper;

public class TradeCachePersister implements VariablePersister {

    private static Logger log = LoggerFactory.getLogger(TradeCachePersister.class);

    public static final String TRANSACTIONAL_CACHE = "TransactionalCache";
    public static final String CACHE_TRANSACTION_FACTORY = "coherence";
    public static final String ID_TRADE_GENERATOR = "coherence-id-gen";
    public static final String STM = "coherence-stm";
    public static final String TRADE_KEY_FORMAT = "%s";

    @Override
    public Object getExternalPersistedVariable(VariableInstanceInfo vi, Environment env) {
        log.info(">>>> get External {} , {} ", vi.getName(), vi.getId());
        if (vi != null && vi instanceof TradeVariableInstanceInfo) {
            OptimisticNamedCache stm = getConnection(env).getNamedCache(STM);
            return stm.get(((TradeVariableInstanceInfo) vi).getTradeKey());
        }
        return null;

    }

    @Override
    public VariableInstanceInfo persistExternalVariable(String name, Object o, VariableInstanceInfo oldValue, Environment env) {
        if (o == null || (oldValue != null && oldValue.getPersister().equals(""))) {
            return null;
        }
        try {
            boolean newVariable = false;
            SequenceGenerator seq = (SequenceGenerator) env.get(ID_TRADE_GENERATOR);
            OptimisticNamedCache stm = getConnection(env).getNamedCache(STM);
            TradeAttributes tradeToSave = (TradeAttributes) o;
            String itracId = tradeToSave.getItracId();
            
            if (itracId == null) {
                tradeToSave.setItracId(Long.toString(seq.next()));
                newVariable = true;
            }
            
            TradeVariableInstanceInfo result = null;
            if (oldValue instanceof TradeVariableInstanceInfo) {
                result = (TradeVariableInstanceInfo) oldValue;
            }
            if (result == null) {
                result = new TradeVariableInstanceInfo();                
            }
            result.setPersister(this.getClass().getName());
            result.setName(name);
            
            String key = String.format(TRADE_KEY_FORMAT, itracId, tradeToSave.getVersion());
            result.setTradeKey(key);
            log.info("<<<<<< persist External Trade {} , {} ", key, tradeToSave);
            if (newVariable) {
                stm.insert(key, tradeToSave);
            } else {
                Filter versionFilter = QueryHelper.createFilter("version = ?1 ", new Object[] { tradeToSave.getVersion() } );
                stm.update(key, tradeToSave, versionFilter);
            }
            return result;
        } catch (Throwable t) {
            throw new RuntimeException("Could not persist external variable", t);
        }
    }

    private static Connection getConnection(Environment env) {
        TransactionSynchronizationRegistry tsr = (TransactionSynchronizationRegistry) env
                .get(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY);
        ConnectionFactory cf = (ConnectionFactory) env.get(CACHE_TRANSACTION_FACTORY);
        Connection connection = (Connection) tsr.getResource(cf);
        if ((connection == null || connection.isClosed()) && tsr.getTransactionStatus() == Status.STATUS_ACTIVE) {
            connection = cf.createConnection(TRANSACTIONAL_CACHE);
            connection.setAutoCommit(false);
            tsr.putResource(cf, connection);
            tsr.registerInterposedSynchronization(new CoherenceConnectionSynchronization(connection));
        }
        return connection;
    }

    private static class CoherenceConnectionSynchronization implements Synchronization {

        private Connection connection;

        public CoherenceConnectionSynchronization(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void afterCompletion(int status) {
            log.info(" >>>>>> Complete Coherence with Transaction Status : {} ", status == Status.STATUS_COMMITTED ? "COMMITTED" : "ROLLBACK");
            if (Status.STATUS_COMMITTED == status) {
                connection.commit();
                
            } else {
                connection.rollback();
            }
        }

        @Override
        public void beforeCompletion() {
            if (connection == null || connection.isClosed()){
                throw new IllegalStateException("Coehrence Connection cannot be commited !!!");
            }
        }
    }

}
