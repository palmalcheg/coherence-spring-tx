package org.drools.alternative.persistence.impl;



import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.alternative.persistence.PersistenceManager;
import org.drools.alternative.persistence.PersistenceDrools;
import org.drools.alternative.persistence.TransactionManager;
import org.drools.alternative.persistence.TransactionSynchronization;
import org.drools.alternative.persistence.utils.SessionMarshallingHelper;
import org.drools.command.Command;
import org.drools.command.CommandService;
import org.drools.command.Context;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.AbstractWorkingMemory.EndOperationListener;
import org.drools.domain.SessionInfo;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.db.itrac.service.api.store.sequence.IdentityGenerator;

public class SingleSessionCommandServiceImpl implements CommandService {

    private Logger logger = LoggerFactory.getLogger(SingleSessionCommandServiceImpl.class);

    private Environment env;
    private SessionMarshallingHelper marshallingHelper;
    private SessionInfo sessionInfo;
    private StatefulKnowledgeSession ksession;
    private KnowledgeCommandContext kContext;
    private volatile boolean doRollback;
    private TransactionManager txm;
    private PersistenceManager cm;

    private static Map<Object, Object> synchronizations = Collections.synchronizedMap(new IdentityHashMap<Object, Object>());

    public SingleSessionCommandServiceImpl(RuleBase ruleBase, SessionConfiguration conf, Environment env) {
        this(new KnowledgeBaseImpl(ruleBase), conf, env);
    }

    public SingleSessionCommandServiceImpl(int sessionId, RuleBase ruleBase, SessionConfiguration conf, Environment env) {
        this(sessionId, new KnowledgeBaseImpl(ruleBase), conf, env);
    }

    public SingleSessionCommandServiceImpl(KnowledgeBase kbase, KnowledgeSessionConfiguration conf, Environment env) {
        if (conf == null) {
            conf = new SessionConfiguration();
        }        
        this.env = env;             

        checkEnvironment(this.env);

        this.sessionInfo = new SessionInfo();

        initTransactionManager(this.env);

        ReteooStatefulSession session = (ReteooStatefulSession) ((KnowledgeBaseImpl) kbase).ruleBase.newStatefulSession(
                (SessionConfiguration) conf, this.env);
        this.ksession = new StatefulKnowledgeSessionImpl(session, kbase);

        this.kContext = new KnowledgeCommandContext(new ContextImpl("ksession", null), null, null, this.ksession, null);

        ((DefaultJDKTimerService) ((StatefulKnowledgeSessionImpl) ksession).session.getTimerService())
                .setCommandExecutor(this);

        this.marshallingHelper = new SessionMarshallingHelper(this.ksession, conf);
        ((StatefulKnowledgeSessionImpl) this.ksession).session.setEndOperationListener(new EndOperationListenerImpl(
                this.sessionInfo));

        try {
            this.txm.begin();

            registerRollbackSync(false);

            IdentityGenerator<Long> seq = (IdentityGenerator<Long>) env.get(PersistenceDrools.ID_SESSION_GENERATOR);

            cm.initConnection();          
            
            int id = seq.generateIdentity().intValue();
            sessionInfo.setId(id);

            sessionInfo.setData(this.marshallingHelper.getSnapshot());

            cm.saveOrUpdate(sessionInfo, id);

            this.txm.commit();

        } catch (Exception t1) {
            try {
                this.txm.rollback();
            } catch (Throwable t2) {
                throw new RuntimeException("Could not commit session or rollback", t2);
            }
            throw new RuntimeException("Could not commit session", t1);
        }

        // update the session id to be the same as the session info id
        ((StatefulKnowledgeSessionImpl) ksession).session.setId(this.sessionInfo.getId());

    }

    public SingleSessionCommandServiceImpl(int sessionId, KnowledgeBase kbase, KnowledgeSessionConfiguration conf,
            Environment env) {
        
        if (conf == null) {
            conf = new SessionConfiguration();
        }

        this.env = env;

        checkEnvironment(this.env);

        initTransactionManager(this.env);

        initKsession(sessionId, kbase, conf);
    }

    public void initKsession(int sessionId, KnowledgeBase kbase, KnowledgeSessionConfiguration conf) {

        cm.initConnection();      

        if (!doRollback && this.ksession != null) {
            return;
            // nothing to initialise
        }

        this.doRollback = false;

        try {
            this.sessionInfo = (SessionInfo) cm.getById(sessionId);
        } catch (Exception e) {
            throw new RuntimeException("Could not find session data for id " + sessionId, e);
        }

        if (sessionInfo == null) {
            throw new RuntimeException("Could not find session data for id " + sessionId);
        }

        if (this.marshallingHelper == null) {
            // this should only happen when this class is first constructed
            this.marshallingHelper = new SessionMarshallingHelper(kbase, conf, env);
        }

        // if this.ksession is null, it'll create a new one, else it'll use the
        // existing one
        this.ksession = this.marshallingHelper.loadSnapshot(this.sessionInfo.getData(), this.ksession);

        // update the session id to be the same as the session info id
        ((StatefulKnowledgeSessionImpl) ksession).session.setId(this.sessionInfo.getId());

        ((StatefulKnowledgeSessionImpl) this.ksession).session.setEndOperationListener(new EndOperationListenerImpl(
                this.sessionInfo));

        ((DefaultJDKTimerService) ((StatefulKnowledgeSessionImpl) ksession).session.getTimerService())
                .setCommandExecutor(this);

        if (this.kContext == null) {
            // this should only happen when this class is first constructed
            this.kContext = new KnowledgeCommandContext(new ContextImpl("ksession", null), null, null, this.ksession, null);
        }

    }

    public Context getContext() {
        return this.kContext;
    }

    public synchronized <T> T execute(Command<T> command) {
        try {
            txm.begin();

            initKsession(this.sessionInfo.getId(), this.marshallingHelper.getKbase(), this.marshallingHelper.getConf());          

            registerRollbackSync(true);

            T result = ((GenericCommand<T>) command).execute(this.kContext);

            txm.commit();

            return result;

        } catch (Exception t1) {
            try {
                txm.rollback();
            } catch (Exception t2) {
                throw new RuntimeException("Could not commit session or rollback", t2);
            }
            throw new RuntimeException("Could not commit session", t1);
        }
    }

    public void dispose() {
        if (ksession != null) {
            ksession.dispose();            
        }
    }

    public int getSessionId() {
        return sessionInfo.getId();
    }

    private void registerRollbackSync(boolean disposable) {
        if (synchronizations.get(this) == null) {
            txm.registerTransactionSynchronization(new SynchronizationImpl(this));
            synchronizations.put(this, this);
        }
    }

    private static class SynchronizationImpl implements TransactionSynchronization {
        

        private SingleSessionCommandServiceImpl service;

        public SynchronizationImpl(SingleSessionCommandServiceImpl service) {
            this.service = service;
        }

        public void afterCompletion(int status) {
            if (status != TransactionManager.STATUS_COMMITTED) {
                this.service.rollback();
            }            
            // always cleanup thread local whatever the result
            SingleSessionCommandServiceImpl.synchronizations.remove(this.service);            
        }

        public void beforeCompletion() {
            StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) this.service.ksession);
            // clean up cached process and work item instances
            if (ksession != null) {
                ((ProcessInstanceManagerImpl) ksession.session.getProcessInstanceManager()).clearProcessInstances();
                ((WorkItemManagerImpl) ksession.session.getWorkItemManager()).clearWorkItems();
            }            
        }

    }

    public class EndOperationListenerImpl implements EndOperationListener {
        private SessionInfo info;

        public EndOperationListenerImpl(SessionInfo info) {
            this.info = info;
        }

        public void endOperation(ReteooWorkingMemory wm) {            
            logger.debug("{} is finishing",sessionInfo.toString());
            this.info.setLastModificationDate(new Date(wm.getLastIdleTimestamp()));

            SessionMarshallingHelper helper = new SessionMarshallingHelper(
                    (StatefulKnowledgeSession) wm.getKnowledgeRuntime(), wm.getSessionConfiguration());
            info.setData(helper.getSnapshot());
            
            cm.saveOrUpdate(info, info.getId());
            
            logger.debug("{} finished", sessionInfo.toString());
        }
    }

    private void rollback() {
        this.doRollback = true;
    }

    private void checkEnvironment(Environment env) {
        if (env.get(PersistenceDrools.ID_SESSION_GENERATOR) == null) {
            throw new IllegalArgumentException("No id generator for session found ");
        }

        if (env.get(EnvironmentName.TRANSACTION_MANAGER) == null) {
            throw new IllegalArgumentException("Transaction Manager must be present in environment");
        }
        cm = PersistenceDroolsImpl.createCacheManagerInstance(SessionInfo.class,env);        
    }

    private void initTransactionManager(Environment env) {
        Object tm = env.get(EnvironmentName.TRANSACTION_MANAGER);
        try {
            this.txm = new DroolsSpringTransactionManager((AbstractPlatformTransactionManager) tm);
        } catch (Exception e) {
            logger.warn("Could not instatiate DroolsSpringTransactionManager");
            throw new RuntimeException("Could not instatiate DroolsSpringTransactionManager", e);
        }

    }  

}
