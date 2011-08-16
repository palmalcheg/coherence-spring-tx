package com.db.itrac.rules;

import java.util.Date;

import com.db.itrac.domain.ItracStatus;
import com.db.itrac.domain.TradeAttributes;

public class TradeUpdated {

    private static final long serialVersionUID = 1L;

    private String itracId = "";

    private String itracStatus = ItracStatus.WAITING_APPROVAL.name();

    private Date eventTime = new Date();

    public TradeUpdated(TradeAttributes t) {
        this.itracId = t.getItracId();
        this.itracStatus = t.getItracStatus().name();
    }

    public String getItracId() {
        return itracId;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getItracStatus() {
        return itracStatus;
    }

}