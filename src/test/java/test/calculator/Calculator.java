package test.calculator;

import org.drools.KnowledgeBase;

public interface Calculator {

	Number executeOperation(String businessKey, KnowledgeBase kbase,
			CalcOperation op, Number value);

}
