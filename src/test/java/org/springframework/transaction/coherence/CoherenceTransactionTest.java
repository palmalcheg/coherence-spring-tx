package org.springframework.transaction.coherence;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.tangosol.coherence.transaction.Connection;
import com.tangosol.coherence.transaction.ConnectionFactory;
import com.tangosol.coherence.transaction.OptimisticNamedCache;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-cache-context.xml")
@TransactionConfiguration(transactionManager="cacheTxManager")
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public class CoherenceTransactionTest extends AbstractJUnit4SpringContextTests {
	
	private static final String TX_SAMPLE_CACHE = "tx-sample";

	@Resource
	private ConnectionFactory cf;
	
	private Double sample_value = new Double(0.9d);
	private String sample_key = "sample-key";
	
	@Test
	public void txIsPresentTest() {
		Assert.assertNotNull("Transaction must be already bound",TransactionSynchronizationManager.getResource(cf));
		Connection conn = ConnectionFactoryUtils.doGetConnection(cf);
		Assert.assertNotNull(conn);
	}
	
	@Test
	public void txInsertTest() {
		Connection conn = ConnectionFactoryUtils.doGetConnection(cf);
		OptimisticNamedCache cache = conn.getNamedCache(TX_SAMPLE_CACHE);
		Assert.assertNotNull(cache);
		Assert.assertTrue(0 == cache.size());
		Assert.assertNull(cache.get(sample_key));
		cache.put(sample_key, sample_value);
		Assert.assertTrue(1 == cache.size());
	}
	
	@Test
	public void txRemoveTest() {
		Connection conn = ConnectionFactoryUtils.doGetConnection(cf);
		OptimisticNamedCache cache = conn.getNamedCache(TX_SAMPLE_CACHE);
		Assert.assertNotNull(cache);
		Assert.assertNull(cache.remove(sample_key));
	}
	
	@Test	
	public void txIsolationTest() {
		Connection conn = ConnectionFactoryUtils.doGetConnection(cf);
		OptimisticNamedCache cache = conn.getNamedCache(TX_SAMPLE_CACHE);
		Assert.assertNotNull(cache);
		Assert.assertNull(cache.remove(sample_key));
	}

}
