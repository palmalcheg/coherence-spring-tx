package test;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import test.SpringParameterizedRunner.Parameters;
import test.calculator.CalcOperation;
import test.calculator.Calculator;

@RunWith(SpringParameterizedRunner.class)
@ContextConfiguration(locations = { "/test-spring-cache-context.xml" })
@TestExecutionListeners({ 	DependencyInjectionTestExecutionListener.class,
							DirtiesContextTestExecutionListener.class })
public class DroolsCalculatorTest implements ApplicationContextAware {
	
	@Parameters
	public static List<Object[]> data() {		
		String calculatorSessionBusinessKey ="calculator1";
		System.out.println("Loading process CalculatorProcess.bpmn2");
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("CalculatorProcess.bpmn2"),ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("CalculatorRules.drl"),ResourceType.DRL);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		return Arrays.asList(new Object[][] {
				
				{calculatorSessionBusinessKey , kbase, CalcOperation.Start, 5f },
				{calculatorSessionBusinessKey , kbase, CalcOperation.Add, 5f },
				{calculatorSessionBusinessKey , kbase, CalcOperation.Sub, 1f },
				{calculatorSessionBusinessKey , kbase, CalcOperation.Div, 3f },
				{calculatorSessionBusinessKey , kbase, CalcOperation.Result, 3f } 
				
		});
	}
	
	@Resource
	private Calculator[] calculators;
	
	private Number value;
	private CalcOperation op;
	private KnowledgeBase kbase;
	private String businessKey;

	public DroolsCalculatorTest(String calculatorSessionBusinessKey, KnowledgeBase kbase, CalcOperation operation,
			Number number) {
		this.kbase = kbase;
		this.op = operation;
		this.value = number;
		this.businessKey = calculatorSessionBusinessKey;
	}

	@Test
	public void makeCalculation() {
		Assert.assertNotNull(kbase);
		Assert.assertNotNull(op);
		Assert.assertNotNull(value);
		for (Calculator calculator : calculators) {
			Number result = calculator.executeOperation(businessKey,kbase,op,value);
			if (op == CalcOperation.Result){
				Assert.assertEquals(value, result);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
	};

}
