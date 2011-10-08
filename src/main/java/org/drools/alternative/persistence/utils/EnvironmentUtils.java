package org.drools.alternative.persistence.utils;

import javax.persistence.EntityManagerFactory;

import org.drools.KnowledgeBaseFactory;
import org.drools.alternative.persistence.PersistenceDrools;
import org.drools.alternative.persistence.cm.impl.CoherenceManagerImpl;
import org.drools.alternative.persistence.cm.impl.JPAManagerImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.tangosol.coherence.transaction.ConnectionFactory;

public abstract class EnvironmentUtils {
    
    public static Environment createEnvironment(AbstractPlatformTransactionManager txManager , ConnectionFactory cf){
        Environment env = new ThreadLocalEnvironmentImpl();
        Environment delegate = KnowledgeBaseFactory.newEnvironment();
        delegate.set(EnvironmentName.TRANSACTION_MANAGER, txManager);
        delegate.set(PersistenceDrools.CACHE_MANAGER_CLASS, CoherenceManagerImpl.class);
        delegate.set(PersistenceDrools.CONNECTION_FACTORY, cf);
        env.setDelegate(delegate);
        return env;
    };
    
    public static Environment createEnvironment(AbstractPlatformTransactionManager txManager , EntityManagerFactory emf){
        Environment env = new ThreadLocalEnvironmentImpl();
        Environment delegate = KnowledgeBaseFactory.newEnvironment();
        delegate.set(EnvironmentName.TRANSACTION_MANAGER, txManager);
        delegate.set(PersistenceDrools.CACHE_MANAGER_CLASS, JPAManagerImpl.class);
        delegate.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.setDelegate(delegate);
        return env;
    };

}
