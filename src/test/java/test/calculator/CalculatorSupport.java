package test.calculator;

import org.drools.runtime.Environment;

public interface CalculatorSupport {

	Integer getSessionIdByBusisnessKey(String bKey);

	void mapSessionId(String businessKey, Integer id);
	
	Environment getEnvironment();

}
