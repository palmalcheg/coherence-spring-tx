package org.drools.alternative.persistence.impl;


import org.drools.KnowledgeBase;
import org.drools.alternative.persistence.KnowledgeStoreService;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;

public class PersistenceDroolsFactory {

    private Environment environment;

    private KnowledgeBase kbase;

    private KnowledgeStoreService provider;

    public StatefulKnowledgeSession newSession() {
        return provider.newStatefulKnowledgeSession(kbase, null, environment);
    }

    public StatefulKnowledgeSession getSession(int sessionId) {
        return provider.loadStatefulKnowledgeSession(sessionId, kbase, null, environment);
    }

    public KnowledgeBase getKnowledgeBase() {
        return kbase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.kbase = knowledgeBase;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    } 

    public void setKnowledgeServiceProvider(KnowledgeStoreService provider) {
        this.provider = provider;
    }

    public Environment getEnvironment() {
        return environment;
    }    

}
