package test.calculator;

import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.drools.KnowledgeBase;
import org.drools.alternative.persistence.impl.SingleSessionCommandServiceImpl;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

public abstract class AbstractCalculator extends TransactionProxyFactoryBean implements Calculator {
	
	public Number executeOperation(String businessKey, KnowledgeBase kbase,
			CalcOperation op, Number value) {
		
		CalcEvent event = new CalcEvent();
		event.op = op;
		event.value = value;

		Integer id = getSupport().getSessionIdByBusisnessKey (businessKey); 
		StatefulKnowledgeSession session = null;
		if (op == CalcOperation.Start || id == null){
			session = createKnowledgeSession(kbase);
			session.insert(event);
			session.startProcess("CalculatorProcess");
			getSupport().mapSessionId(businessKey, session.getId());
		}else{
			session = loadKnowledgeSession(id, kbase);
			session.insert(event);
		}	
		session.fireAllRules();
		return getValue(session);
	}
	
	@PostConstruct
	public void construct() {
		setTarget(this);
	}
	
	private Number getValue(StatefulKnowledgeSession session) {
		QueryResults queryResults = session.getQueryResults("GetValue");
		Iterator<QueryResultsRow> iter = queryResults.iterator();
		Number ret = null;
        while(iter.hasNext())
        {
        	QueryResultsRow result = iter.next();
            ret = (Number) result.get("currentValue");
        }
		return ret;
	}

	private StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
        return  new CommandBasedStatefulKnowledgeSession(new SingleSessionCommandServiceImpl(kbase, null,getSupport().getEnvironment()));
    }
    
    private StatefulKnowledgeSession loadKnowledgeSession(int id, KnowledgeBase kbase) {
        return  new CommandBasedStatefulKnowledgeSession(new SingleSessionCommandServiceImpl(id,kbase, null,getSupport().getEnvironment()));
    }
    
    public abstract CalculatorSupport getSupport();

}
