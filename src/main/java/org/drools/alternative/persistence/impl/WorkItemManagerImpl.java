package org.drools.alternative.persistence.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.WorkingMemory;
import org.drools.alternative.persistence.PersistenceManager;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.domain.WorkItemInfo;
import org.drools.marshalling.impl.InputMarshaller;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.OutputMarshaller;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkItemManagerImpl implements WorkItemManager {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();
    private transient Map<Long, WorkItemInfo> workItems;

    private WorkingMemory workingMemory;
    private PersistenceManager cm;    

    public WorkItemManagerImpl(InternalKnowledgeRuntime runtime, PersistenceManager cm) {       
        this.workingMemory = ((InternalWorkingMemoryEntryPoint)runtime).getInternalWorkingMemory();
        this.cm = cm;        
    }

    @Override
    public void completeWorkItem(long id, Map<String, Object> results) {
        Environment env = this.workingMemory.getEnvironment();

        WorkItemInfo workItemInfo = null;
        if (this.workItems != null) {
            workItemInfo = this.workItems.get(id);
            if (workItemInfo != null) {
                cm.saveOrUpdate(workItemInfo , id);
            }
        }

        if (workItemInfo == null) {
            workItemInfo = (WorkItemInfo) cm.getById(id);
        }
        if (log.isDebugEnabled())
            log.debug("Completing WorkItem [id={} , process instance id={} , name={}] ", new Object[] { workItemInfo.getId(),
                workItemInfo.getProcessInstanceId(), workItemInfo.getName() });

        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItem workItem = (WorkItemImpl) getWorkItem(env, workItemInfo);
            workItem.setResults(results);
            long processInstanceId = workItem.getProcessInstanceId();
            ProcessInstance processInstance = workingMemory.getProcessInstance(processInstanceId);
            workItem.setState(WorkItem.COMPLETED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemCompleted", workItem);
            }
            
            cm.removeById(id);
            
            if (workItems != null) {
                this.workItems.remove(workItem.getId());
            }
            if (log.isDebugEnabled())
                log.debug("Removing WorkItem [id={}] ", id);
            workingMemory.fireAllRules();
        }

    }

    @Override
    public void abortWorkItem(long id) {
        Environment env = this.workingMemory.getEnvironment();

        WorkItemInfo workItemInfo = null;
        if (this.workItems != null) {
            workItemInfo = this.workItems.get(id);
            if (workItemInfo != null) {
                cm.saveOrUpdate(workItemInfo, id);
            }
        }

        if (workItemInfo == null) {
            workItemInfo = cm.getById(id);
        }

        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItem workItem = (WorkItemImpl) getWorkItem(env, workItemInfo);
            long processInstanceId = workItem.getProcessInstanceId();
            ProcessInstance processInstance = workingMemory.getProcessInstance(processInstanceId);
            workItem.setState(WorkItem.ABORTED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemAborted", workItem);
            }
            cm.removeById(id);
            if (workItems != null) {
                workItems.remove(workItem.getId());
            }
            workingMemory.fireAllRules();
        }
    }

    @Override
    public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        this.workItemHandlers.put(workItemName, handler);
    }

    @Override
    public Set<WorkItem> getWorkItems() {
        return new HashSet<WorkItem>();
    }

    @Override
    public WorkItem getWorkItem(long id) {
        WorkItemInfo wi = null;
        if (this.workItems != null) {
            wi = this.workItems.get(id);
            if (wi == null) {
                wi = (WorkItemInfo) cm.getById(id);
            }
        }
        if (log.isDebugEnabled())
            log.debug("Quering WorkItem [id={} , process instance id={}, name={}] ",
                new Object[] { wi.getId(), wi.getProcessInstanceId(), wi.getName() });
        return wi != null ? getWorkItem(workingMemory.getEnvironment(), wi) : null;
    }

    @Override
    public void internalExecuteWorkItem(WorkItem workItem) {

        WorkItemInfo workItemInfo = new WorkItemInfo();
        workItemInfo.setProcessInstanceId(workItem.getProcessInstanceId());
        workItemInfo.setName(workItem.getName());
       
        workItemInfo.setId(cm.generateIdentity());
        ((WorkItemImpl) workItem).setId(workItemInfo.getId());
        update(workItemInfo, workItem);

        if (this.workItems == null) {
            this.workItems = new ConcurrentHashMap<Long, WorkItemInfo>();
        }
        workItems.put(workItem.getId(), workItemInfo);

        cm.saveOrUpdate( workItemInfo , workItemInfo.getId());

        WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            handler.executeWorkItem(workItem, this);
        } else {
            log.error("Could not find work item handler for {}", workItem.getName());
        }
    }

    @Override
    public void internalAddWorkItem(WorkItem wi) {
    }

    @Override
    public void internalAbortWorkItem(long id) {
        Environment env = this.workingMemory.getEnvironment();
        
        WorkItemInfo workItemInfo = cm.getById(id);
        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItemImpl workItem = (WorkItemImpl) getWorkItem(env, workItemInfo);
            WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.abortWorkItem(workItem, this);
            } else {
                log.error("Could not find work item handler for " + workItem.getName());
            }
            if (workItems != null) {
                workItems.remove(id);
            }
            cm.removeById(id);
        }
    }

    public void clearWorkItems() {
        if (workItems != null) {
            workItems.clear();
        }
    }

    private WorkItem getWorkItem(Environment env, WorkItemInfo info) {
        WorkItem workItem = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(info.getData());
            MarshallerReaderContext context = new MarshallerReaderContext(bais, null, null, null,env);
            workItem = InputMarshaller.readWorkItem(context);

            context.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("IOException while loading process instance: " + e.getMessage());
        }
        return workItem;
    }

    private void update(WorkItemInfo info, WorkItem workItem) {
        info.setState(workItem.getState());
        info.setProcessInstanceId(workItem.getProcessInstanceId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            MarshallerWriteContext context = new MarshallerWriteContext(baos, null, null, null, null,workingMemory.getEnvironment());
            OutputMarshaller.writeWorkItem(context, workItem);
            context.close();
            info.setData(baos.toByteArray());
        } catch (IOException e) {
            throw new IllegalArgumentException("IOException while storing workItem " + workItem.getId() + ": "
                    + e.getMessage());
        }
    }

    @Override
    public void clear() {
        clearWorkItems();
    }

}
