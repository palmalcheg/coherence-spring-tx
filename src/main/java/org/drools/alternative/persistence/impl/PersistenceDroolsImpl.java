package org.drools.alternative.persistence.impl;

import java.lang.reflect.Constructor;

import org.drools.alternative.persistence.PersistenceDrools;
import org.drools.alternative.persistence.PersistenceManager;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.domain.ProcessInstanceInfo;
import org.drools.domain.WorkItemInfo;
import org.drools.process.instance.WorkItemManager;
import org.drools.runtime.Environment;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.process.instance.event.SignalManager;

public class PersistenceDroolsImpl implements PersistenceDrools {

    @Override
    public ProcessInstanceManager createProcessInstanceManager(InternalKnowledgeRuntime runtime) {
        return new ProcessInstanceManagerImpl(runtime, createCacheManagerInstance(ProcessInstanceInfo.class,
                runtime.getEnvironment()));
    }

    @Override
    public SignalManager createSignalManager(InternalKnowledgeRuntime runtime) {
        return new SignalManagerImpl(runtime, createCacheManagerInstance(ProcessInstanceInfo.class,
                runtime.getEnvironment()));
    }

    @Override
    public WorkItemManager createWorkItemManager(InternalKnowledgeRuntime runtime) {
        return new WorkItemManagerImpl(runtime, createCacheManagerInstance(WorkItemInfo.class,
                runtime.getEnvironment()));
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public static PersistenceManager createCacheManagerInstance(Class clazz, Environment environment) {
        Class cmClazz = (Class) environment.get(PersistenceDrools.CACHE_MANAGER_CLASS);
        Constructor cmConstructor;
        try {
            cmConstructor = cmClazz.getConstructor(Class.class, Environment.class);
            return (PersistenceManager) cmConstructor.newInstance(clazz, environment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
