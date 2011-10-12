package org.drools.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkItemInfo implements Versioning{

    private long workItemId;

    private int version;

    private String name;
    private Date creationDate = new Date();
    private long processInstanceId;
    private long state;
    private byte[] data;

    private Map<String, VariableInfo> variables = new HashMap<String, VariableInfo>();

    public WorkItemInfo() {
    }

    public long getId() {
        return workItemId;
    }

    public int getVersion() {
        return this.version;
    }

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public long getState() {
        return state;
    }

    public void setId(long id) {
        this.workItemId = id;
    }

    public Map<String, VariableInfo> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, VariableInfo> variables) {
        this.variables = variables;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public void setState(long state) {
        this.state = state;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
