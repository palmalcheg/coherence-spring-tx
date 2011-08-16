package org.drools.alternative.persistence.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.WorkingMemory;
import org.drools.alternative.persistence.PersistenceManager;
import org.drools.alternative.persistence.PersistenceDrools;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.domain.ProcessInstanceInfo;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.ProcessInstanceMarshaller;
import org.drools.marshalling.impl.ProcessMarshallerRegistry;
import org.drools.process.core.Process;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.runtime.Environment;

import com.db.itrac.service.api.store.sequence.IdentityGenerator;

public class ProcessInstanceManagerImpl implements ProcessInstanceManager {

    private transient Map<Long, ProcessInstance> processInstances;
    private WorkingMemory workingMemory;
    private PersistenceManager cm;

    public ProcessInstanceManagerImpl(WorkingMemory workingMemory, PersistenceManager cm) {
        this.workingMemory = workingMemory;
        this.cm = cm;
    }

    @Override
    public ProcessInstance getProcessInstance(long id) {
        ProcessInstance processInstance = null;
        Environment environment = this.workingMemory.getEnvironment();
        if (this.processInstances != null) {
            processInstance = this.processInstances.get(id);
            if (processInstance != null) {
                return processInstance;
            }
        }
        ProcessInstanceInfo processInstanceInfo = cm.getById(id);  
        
        // Is Process Instance finished ? 
        if (processInstanceInfo == null)
            return null;

        processInstanceInfo.updateLastReadDate();
        processInstance = (ProcessInstance) getProcessInstance(processInstanceInfo, workingMemory, environment);
        Process process = ((InternalRuleBase) workingMemory.getRuleBase()).getProcess(processInstance.getProcessId());
        if (process == null) {
            throw new IllegalArgumentException("Could not find process " + processInstance.getProcessId());
        }
        processInstance.setProcess(process);
        if (processInstance.getWorkingMemory() == null) {
            processInstance.setWorkingMemory((InternalWorkingMemory) workingMemory);
            ((ProcessInstanceImpl) processInstance).reconnect();
        }
        return processInstance;
    }

    @Override
    public Collection<ProcessInstance> getProcessInstances() {
        return new ArrayList<ProcessInstance>();
    }

    @Override
    public void addProcessInstance(ProcessInstance processInstance) {
        Environment environment = this.workingMemory.getEnvironment();
        IdentityGenerator<Long> seq = (IdentityGenerator<Long>) environment.get(PersistenceDrools.ID_PROCESS_GENERATOR);
        Long pID = seq.generateIdentity();
        processInstance.setId(pID);
        handleCachedProcessInfo(pID, processInstance);
        internalAddProcessInstance(processInstance);
    }

    @Override
    public void internalAddProcessInstance(ProcessInstance processInstance) {
        if (this.processInstances == null) {
            this.processInstances = new ConcurrentHashMap<Long, ProcessInstance>();
        }
        processInstances.put(processInstance.getId(), processInstance);
    }

    @Override
    public void removeProcessInstance(ProcessInstance processInstance) {
        cm.removeById(processInstance.getId());
        internalRemoveProcessInstance(processInstance);
    }

    @Override
    public void internalRemoveProcessInstance(ProcessInstance processInstance) {
        if (this.processInstances != null) {
            processInstances.remove(processInstance.getId());
        }
    }

    public int clearProcessInstances() {
        int size = 0;
        if (processInstances != null) {
            size = processInstances.size();
            for (Map.Entry<Long, ProcessInstance> e : processInstances.entrySet()) {
                handleCachedProcessInfo(e.getKey(), e.getValue());
                ((ProcessInstanceImpl) e.getValue()).disconnect();
            }
        }
        return size;
    }

    private ProcessInstanceInfo handleCachedProcessInfo(long id, ProcessInstance processInstance) {
        ProcessInstanceInfo pi = new ProcessInstanceInfo();
        pi.updateLastReadDate();
        pi.setId(id);
        update(pi, processInstance);
        cm.saveOrUpdate(pi, id);
        return pi;
    }

    public ProcessInstance getProcessInstance(ProcessInstanceInfo info, WorkingMemory workingMemory, Environment env) {
        ProcessInstance processInstance = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(info.getData());
            MarshallerReaderContext context = new MarshallerReaderContext(bais,
                    (InternalRuleBase) workingMemory.getRuleBase(), null, null);
            context.wm = (InternalWorkingMemory) workingMemory;
            ProcessInstanceMarshaller marshaller = getMarshallerFromContext(context);
            processInstance = (ProcessInstance) marshaller.readProcessInstance(context, true);

            context.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("IOException while loading process instance: " + e.getMessage(), e);
        }
        return processInstance;
    }

    private ProcessInstanceMarshaller getMarshallerFromContext(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        String processInstanceType = stream.readUTF();
        return ProcessMarshallerRegistry.INSTANCE.getMarshaller(processInstanceType);
    }

    private void saveProcessInstanceType(MarshallerWriteContext context, ProcessInstance processInstance,
            String processInstanceType) throws IOException {
        ObjectOutputStream stream = context.stream;
        // saves the processInstance type first
        stream.writeUTF(processInstanceType);
    }

    private void update(ProcessInstanceInfo info, ProcessInstance processInstance) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean variablesChanged = false;
        try {
            MarshallerWriteContext context = new MarshallerWriteContext(baos, null, null, null, null);
            String processType = ((ProcessInstanceImpl) processInstance).getProcess().getType();
            saveProcessInstanceType(context, processInstance, processType);
            ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller(processType);
            marshaller.writeProcessInstance(context, processInstance, true);

            context.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("IOException while storing process instance " + processInstance.getId()
                    + ": " + e.getMessage());
        }
        byte[] newByteArray = baos.toByteArray();
        if (variablesChanged || !Arrays.equals(newByteArray, info.getData())) {
            info.setState(processInstance.getState());
            info.setLastModificationDate(new Date());
            info.setData(newByteArray);
            Set<String> eventTypes = info.getEventTypes();
            eventTypes.clear();
            for (String type : processInstance.getEventTypes()) {
                eventTypes.add(type);
            }
        }
    }

}
