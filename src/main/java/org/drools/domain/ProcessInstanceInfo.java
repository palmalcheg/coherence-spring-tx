package org.drools.domain;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Pojo for process instance
 * 
 * Using JPA 2.0
 * 
 * NOTE: external variables storing is striped off
 * 
 * @author ievdokimov
 * 
 */
@Entity
@NamedQuery(name = "ProcessInstancesWaitingForEvent", 
            query = "select processInstanceInfo.id from ProcessInstanceInfo processInstanceInfo where :type member of processInstanceInfo.eventTypes")
public class ProcessInstanceInfo implements Versioning{

    @Id
    private Long id;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    private String processId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate, lastReadDate, lastModificationDate;

    private int state;

    @Lob
    private byte[] processInstanceByteArray;

    @ElementCollection
    @JoinTable(name = "EVENTTYPES", joinColumns = @JoinColumn(name = "ID"))
    private Set<String> eventTypes = new HashSet<String>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID")
    @MapKey(name = "name")
    private Map<String, VariableInfo> variables = new HashMap<String, VariableInfo>();

    public ProcessInstanceInfo() {
    }

    public long getId() {
        return id;
    }

    public String getProcessId() {
        return processId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public Date getLastReadDate() {
        return lastReadDate;
    }

    public void updateLastReadDate() {
        lastReadDate = new Date();
    }

    public int getState() {
        return state;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public Map<String, VariableInfo> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, VariableInfo> variables) {
        this.variables = variables;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessInstanceInfo other = (ProcessInstanceInfo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        if ((this.processId == null) ? (other.processId != null) : !this.processId.equals(other.processId)) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.lastReadDate != other.lastReadDate
                && (this.lastReadDate == null || !this.lastReadDate.equals(other.lastReadDate))) {
            return false;
        }
        if (this.lastModificationDate != other.lastModificationDate
                && (this.lastModificationDate == null || !this.lastModificationDate.equals(other.lastModificationDate))) {
            return false;
        }
        if (this.state != other.state) {
            return false;
        }
        if (!Arrays.equals(this.processInstanceByteArray, other.processInstanceByteArray)) {
            return false;
        }
        if (this.eventTypes != other.eventTypes && (this.eventTypes == null || !this.eventTypes.equals(other.eventTypes))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 61 * hash + this.version;
        hash = 61 * hash + (this.processId != null ? this.processId.hashCode() : 0);
        hash = 61 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 61 * hash + (this.lastReadDate != null ? this.lastReadDate.hashCode() : 0);
        hash = 61 * hash + (this.lastModificationDate != null ? this.lastModificationDate.hashCode() : 0);
        hash = 61 * hash + this.state;
        hash = 61 * hash + Arrays.hashCode(this.processInstanceByteArray);
        hash = 61 * hash + (this.eventTypes != null ? this.eventTypes.hashCode() : 0);
        return hash;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getData() {
        return processInstanceByteArray;
    }

    public void setData(byte[] processInstanceByteArray) {
        this.processInstanceByteArray = processInstanceByteArray;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setLastReadDate(Date lastReadDate) {
        this.lastReadDate = lastReadDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setEventTypes(Set<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    @Override
    public String toString() {
        return "ProcessInstanceInfo [id=" + id + ", version=" + version + ", processId=" + processId + ", startDate="
                + startDate + ", lastReadDate=" + lastReadDate + ", lastModificationDate=" + lastModificationDate
                + ", state=" + state + "]";
    }

}
