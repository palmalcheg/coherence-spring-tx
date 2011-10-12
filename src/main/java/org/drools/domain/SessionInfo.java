package org.drools.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class SessionInfo implements Versioning{
    
    @Id
    private 
    int                        id;

    @Version
    @Column(name="OPTLOCK")
    private int                version;

    @Temporal(TemporalType.TIMESTAMP)
    private Date               startDate, lastModificationDate;    
  
    @Lob
    private byte[]             rulesByteArray;

    
    public SessionInfo() {
        this.startDate = new Date();
    }

    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getVersion() {
        return this.version;
    }   
    
    public byte[] getData() {
        return this.rulesByteArray;
    }
    
    public Date getStartDate() {
        return this.startDate;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    public void setLastModificationDate(Date date) {
        this.lastModificationDate = date;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public void setData(byte[] data) {
        this.rulesByteArray = data;
    }

    @Override
    public String toString() {
        return "SessionInfo [id=" + id + ", version=" + version + ", startDate=" + startDate + ", lastModificationDate="
                + lastModificationDate + "]";
    }
    
}
