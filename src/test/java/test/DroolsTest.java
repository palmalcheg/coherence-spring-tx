package test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import javax.annotation.Resource;

import org.drools.KnowledgeBase;
import org.drools.alternative.persistence.impl.SingleSessionCommandServiceImpl;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.io.ResourceFactory;
import org.drools.pof.config.PofMappingFactory;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-spring-cache-context.xml" })
@TransactionConfiguration(transactionManager="cacheTxManager")
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class DroolsTest extends AbstractJUnit4SpringContextTests {
       
    @Resource(name="cohEnv")
    private Environment environment;

    @Test
    public void testPofConfig() throws Exception {        
    	assertNotNull(PofMappingFactory.getDescriptorForClass("SessionInfo"));
    }
    
    @Test
    @Transactional
    public void testAdHocSubProcess() throws Exception {
        System.out.println("Loading process BPMN2-AdHocSubProcess.bpmn2"); 
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-AdHocSubProcess.bpmn2"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-AdHocSubProcess.drl"), ResourceType.DRL);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);        
        ksession.fireAllRules();
        
        System.out.println("Signaling Hello2");
        ksession.signalEvent("Hello2", null, processInstance.getId());
    }
        
    
    private StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
        return  new CommandBasedStatefulKnowledgeSession(new SingleSessionCommandServiceImpl(kbase, null,environment));
    }
   
   
}
