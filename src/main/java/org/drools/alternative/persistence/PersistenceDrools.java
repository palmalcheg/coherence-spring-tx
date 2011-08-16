package org.drools.alternative.persistence;


import org.drools.process.instance.ProcessInstanceManagerFactory;
import org.drools.process.instance.WorkItemManagerFactory;
import org.drools.process.instance.event.SignalManagerFactory;

public interface PersistenceDrools extends ProcessInstanceManagerFactory,SignalManagerFactory,WorkItemManagerFactory  {
    
    public static final String CONNECTION = "CMD_CONNECTION";    
    public static final String CONNECTION_FACTORY = "CF_COHERENCE";
    
    public static final String ID_PROCESS_GENERATOR = "Process_SEQ";
    public static final String ID_WI_GENERATOR = "Work_Item_SEQ";
    public static final String ID_SESSION_GENERATOR = "Session_Info_SEQ";    
    public static final String ID_VAR_GENERATOR = "Variables_SEQ";
    
    public static final String CACHE_MANAGER_CLASS = "CM_CLASS";
    
}
