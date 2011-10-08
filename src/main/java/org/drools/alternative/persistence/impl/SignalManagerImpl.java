package org.drools.alternative.persistence.impl;



import java.util.HashSet;
import java.util.List;

import org.drools.alternative.persistence.PersistenceManager;
import org.drools.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.event.DefaultSignalManager;

public class SignalManagerImpl extends DefaultSignalManager {
    
    private PersistenceManager cacheManager;

    public SignalManagerImpl(InternalKnowledgeRuntime workingMemory, PersistenceManager cm) {
        super(workingMemory);
        cacheManager = cm;
        
    }

    public void signalEvent(String type, Object event) {
        for (long id : getProcessInstancesForEvent(type)) {
            getKnowledgeRuntime().getProcessInstance(id);
        }
        super.signalEvent(type, event);
    }
    
    private List<Long> getProcessInstancesForEvent(String type) {      
        HashSet<String> params = new HashSet<String>();
        params.add(type);  
        return  cacheManager.getIdsByEventType(params);
    }

}
