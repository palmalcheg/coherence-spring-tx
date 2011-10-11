package test.calculator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.runtime.EnvironmentName;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

public abstract class JPACalculator implements CalculatorSupport {
	
	@Override
	public Integer getSessionIdByBusisnessKey(String bKey) {
		EntityManagerFactory emf = (EntityManagerFactory) getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY);
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
		LookupSession lookup = em.find(LookupSession.class, bKey);
		if (lookup !=null)
			return lookup.getSessionId();
		return null;
	}

	@Override
	public void mapSessionId(String businessKey, Integer id) {
		EntityManagerFactory emf = (EntityManagerFactory) getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY);
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
		LookupSession lookup = em.find(LookupSession.class, businessKey);
		if (lookup == null ){
			lookup = new LookupSession();
			lookup.setId(businessKey);
			lookup.setSessionId(id);
			em.persist(lookup);
		}
		else{
			lookup.setSessionId(id);
			em.merge(lookup);
		}
	}
}
