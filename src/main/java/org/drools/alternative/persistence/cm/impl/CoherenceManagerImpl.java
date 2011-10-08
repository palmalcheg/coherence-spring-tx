package org.drools.alternative.persistence.cm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.alternative.persistence.PersistenceManager;
import org.drools.alternative.persistence.PersistenceDrools;
import org.drools.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.coherence.ConnectionFactoryUtils;

import com.oracle.coherence.common.sequencegenerators.ClusteredSequenceGenerator;
import com.tangosol.coherence.transaction.Connection;
import com.tangosol.coherence.transaction.ConnectionFactory;
import com.tangosol.coherence.transaction.OptimisticNamedCache;
import com.tangosol.coherence.transaction.TransactionState;
import com.tangosol.util.filter.ContainsAllFilter;

public class CoherenceManagerImpl implements PersistenceManager {

    private Logger log = LoggerFactory.getLogger(CoherenceManagerImpl.class);

    private String nameOfCache;
    private Environment environment;
    private ClusteredSequenceGenerator seq = new ClusteredSequenceGenerator("GetObjectID", 1);

    public CoherenceManagerImpl(Class clazz, Environment env) {
        this.nameOfCache = clazz.getName();
        this.environment = env;
    }

    private OptimisticNamedCache getCache() {
        return getCache(nameOfCache);
    }

    protected OptimisticNamedCache getCache(String cachName) {
        Connection conn = (Connection) environment.get(PersistenceDrools.CONNECTION);

        TransactionState transactionState = conn.getTransactionState();
        if (log.isDebugEnabled())
            log.debug("{} Aquired connection : xid={}, isolation={} , status={}", new Object[] { nameOfCache,
                    transactionState.getXid(), transactionState.getIsolation().name(), transactionState.getStatus().name() });
        return conn.getNamedCache("tx-" + nameOfCache);
    }

    @Override
    public void initConnection() {
        ConnectionFactory cf = (ConnectionFactory) environment.get(PersistenceDrools.CONNECTION_FACTORY);
        environment.set(PersistenceDrools.CONNECTION, ConnectionFactoryUtils.doGetConnection(cf));
    }

    @Override
    public <T, ID> T getById(ID id) {
        if (log.isDebugEnabled())
            log.debug("Quering {} [id={}] from cache", nameOfCache, id);
        return (T) getCache().get(id);
    }

    @Override
    public <ID> void removeById(ID id) {
        getCache().remove(id);
        if (log.isDebugEnabled())
            log.debug("{} [id={}] removed from cache", nameOfCache, id);
    }

    @Override
    public <T, ID> T saveOrUpdate(T object, ID id) {
        OptimisticNamedCache cache = getCache();
        if (cache.containsKey(id)) {
            cache.update(id, object, null);
            if (log.isDebugEnabled())
                log.debug("{} [id={}] updated in cache", nameOfCache, id);
        } else {
            cache.insert(id, object);
            if (log.isDebugEnabled())
                log.debug("{} [id={}] inserted into cache", nameOfCache, id);
        }
        return (T) getById(id);
    }

    @Override
    public List<Long> getIdsByEventType(HashSet<String> params) {
        if (log.isDebugEnabled())
            log.debug("{} quering for events {}", nameOfCache, Arrays.toString(params.toArray(new String[0])));
        Set keySet = getCache().keySet(new ContainsAllFilter("getEventTypes", params));
        if (log.isDebugEnabled())
            log.debug("{} quered , size={}", nameOfCache, keySet.size());
        return new ArrayList<Long>(keySet);
    }

    @Override
    public long generateIdentity() {
        return seq.next();
    }

}
