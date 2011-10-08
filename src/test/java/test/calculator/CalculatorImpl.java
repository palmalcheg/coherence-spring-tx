package test.calculator;

import java.util.Iterator;

import javax.annotation.Resource;

import org.drools.KnowledgeBase;
import org.drools.alternative.persistence.impl.SingleSessionCommandServiceImpl;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tangosol.coherence.transaction.Connection;
import com.tangosol.coherence.transaction.OptimisticNamedCache;

@Component
public class CalculatorImpl implements Calculator {
	
	@Resource
	private Environment environment;
	
	@Resource
	private Connection conn;

	@Transactional
	public Number executeOperation(String businessKey, KnowledgeBase kbase,
			CalcOperation op, Number value) {
		
		CalcEvent event = new CalcEvent();
		event.op = op;
		event.value = value;
		
		OptimisticNamedCache lookup = conn.getNamedCache("tx-lookup");
		Integer id = (Integer) lookup.get(businessKey);
		StatefulKnowledgeSession session = null;
		if (op == CalcOperation.Start || id == null){
			session = createKnowledgeSession(kbase);
			session.insert(event);
			session.startProcess("CalculatorProcess");
			lookup.put(businessKey, session.getId());
		}else{
			session = loadKnowledgeSession(id, kbase);
			session.insert(event);
		}	
		session.fireAllRules();
		return getValue(session);
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
        return  new CommandBasedStatefulKnowledgeSession(new SingleSessionCommandServiceImpl(kbase, null,environment));
    }
    
    private StatefulKnowledgeSession loadKnowledgeSession(int id, KnowledgeBase kbase) {
        return  new CommandBasedStatefulKnowledgeSession(new SingleSessionCommandServiceImpl(id,kbase, null,environment));
    }
    

}
