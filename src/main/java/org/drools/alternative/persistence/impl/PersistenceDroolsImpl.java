package org.drools.alternative.persistence.impl;

import java.lang.reflect.Constructor;

import org.drools.WorkingMemory;
import org.drools.alternative.persistence.PersistenceManager;
import org.drools.alternative.persistence.PersistenceDrools;
import org.drools.domain.ProcessInstanceInfo;
import org.drools.domain.WorkItemInfo;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.event.SignalManager;
import org.drools.runtime.Environment;

public class PersistenceDroolsImpl implements PersistenceDrools {

    @Override
    public ProcessInstanceManager createProcessInstanceManager(WorkingMemory workingMemory) {
        return new ProcessInstanceManagerImpl(workingMemory, createCacheManagerInstance(ProcessInstanceInfo.class,
                workingMemory.getEnvironment()));
    }

    @Override
    public SignalManager createSignalManager(WorkingMemory workingMemory) {
        return new SignalManagerImpl(workingMemory, createCacheManagerInstance(ProcessInstanceInfo.class,
                workingMemory.getEnvironment()));
    }

    @Override
    public WorkItemManager createWorkItemManager(WorkingMemory workingMemory) {
        return new WorkItemManagerImpl(workingMemory, createCacheManagerInstance(WorkItemInfo.class,
                workingMemory.getEnvironment()));
    }

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
