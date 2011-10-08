package org.springframework.transaction.coherence;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tangosol.coherence.transaction.Connection;
import com.tangosol.coherence.transaction.ConnectionFactory;
import com.tangosol.coherence.transaction.OptimisticNamedCache;
import com.tangosol.util.filter.EqualsFilter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-spring-cache-context.xml")
public class CoherenceUpdateTransactionTest extends AbstractJUnit4SpringContextTests {
	
	private static final String TX_SAMPLE_CACHE = "tx-sample";

	@Resource
	private ConnectionFactory cf;
	
	private String sample_key = "sample-key";
	
	@Test
	public void txUpdateIsolationTest() {
		
		Connection conn1 = ConnectionFactoryUtils.doGetConnection(cf);
		Connection conn2 = ConnectionFactoryUtils.doGetConnection(cf);
		
		Assert.assertFalse(conn1.equals(conn2));
		
		OptimisticNamedCache c1 = conn1.getNamedCache(TX_SAMPLE_CACHE);
		OptimisticNamedCache c2 = conn2.getNamedCache(TX_SAMPLE_CACHE);
		
		Integer startValue = new Integer(0);
		c1.put(sample_key, startValue);
		
		Assert.assertNull(c2.get(sample_key));
		
		conn1.commit();
		
		Object valueFromCache = c2.get(sample_key);
		Assert.assertNotNull(valueFromCache);
		
		c2.update(sample_key, new Integer(1), new EqualsFilter("intValue", valueFromCache));
		
		Assert.assertTrue(startValue.equals(c1.get(sample_key)));
		
		conn2.commit();
		
		Assert.assertFalse(startValue.equals(c1.get(sample_key)));
		
		conn1.close();
		conn2.close();
		
	}
	
	

}
