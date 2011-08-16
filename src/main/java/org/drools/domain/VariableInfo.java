package org.drools.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VariableInfo  {
    
   @Id 
   private Long id;    

   public Long getId() {
        return id;        
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String name;
    private String persister;

    private  Long processInstanceId;
    private  Long workItemId;    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersister() {
        return persister;
    }

    public void setPersister(String persister) {
        this.persister = persister;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getWorkItemId() {
        return workItemId;
    }
    
    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }
    


    public String toString() {
        return getClass().getName() + " id=" + id + " name=" + name + " "
                + "persister=" + persister + ""
                + " processInstanceId=" + processInstanceId
                + " workItemId=" + workItemId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VariableInfo other = (VariableInfo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.persister == null) ? (other.persister != null) : !this.persister.equals(other.persister)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 41 * hash + (this.persister != null ? this.persister.hashCode() : 0);
        return hash;
    }

}
