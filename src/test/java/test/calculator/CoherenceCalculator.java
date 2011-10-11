package test.calculator;

import org.drools.alternative.persistence.PersistenceDrools;
import org.springframework.transaction.coherence.ConnectionFactoryUtils;

import com.tangosol.coherence.transaction.Connection;
import com.tangosol.coherence.transaction.ConnectionFactory;
import com.tangosol.coherence.transaction.OptimisticNamedCache;

public abstract class CoherenceCalculator implements CalculatorSupport {

	private static final String TX_LOOKUP = "tx-lookup";

	@Override
	public Integer getSessionIdByBusisnessKey(String bKey) {
		ConnectionFactory cf  = (ConnectionFactory) getEnvironment().get(PersistenceDrools.CONNECTION_FACTORY);
		Connection conn = ConnectionFactoryUtils.doGetConnection(cf);
		OptimisticNamedCache lookup = conn.getNamedCache(TX_LOOKUP);
		return (Integer) lookup.get(bKey);
	}

	@Override
	public void mapSessionId(String businessKey, Integer id) {
		ConnectionFactory cf  = (ConnectionFactory) getEnvironment().get(PersistenceDrools.CONNECTION_FACTORY);
		Connection conn = ConnectionFactoryUtils.doGetConnection(cf);
		OptimisticNamedCache lookup = conn.getNamedCache(TX_LOOKUP);
		lookup.put(businessKey, id);
	}

}
