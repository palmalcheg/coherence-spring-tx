package com.db.itrac.rules;

import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;

public class TradeVariableInstanceInfo extends VariableInstanceInfo {
    
    private static final long serialVersionUID = 8397302115851637683L;

    private String tradeKey;

    public void setTradeKey(String key) {
        tradeKey = key;
    }

    public String getTradeKey() {
        return tradeKey;
    }
}
