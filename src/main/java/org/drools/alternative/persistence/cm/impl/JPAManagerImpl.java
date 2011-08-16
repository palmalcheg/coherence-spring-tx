package org.drools.alternative.persistence.cm.impl;



import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.drools.alternative.persistence.PersistenceManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

public class JPAManagerImpl implements PersistenceManager {

    private Class entityClass;
    private Environment environment;

    public JPAManagerImpl(Class entityClass, Environment environment) {
        this.entityClass = entityClass;
        this.environment = environment;
    }

    @Override
    public void initConnection() {
        EntityManagerFactory emf = (EntityManagerFactory) environment.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
        environment.set(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, em);
    }

    @Override
    public <T, ID> T getById(ID id) {
        return (T) getEM().find(entityClass, id);
    }

    @Override
    public <ID> void removeById(ID id) {
        Object obj = getById(id);
        getEM().remove(obj);
    }

    @Override
    public <T, ID> T saveOrUpdate(T object, ID id) {
        Object obj = getById(id);
        if(obj == null) {
            getEM().persist(object);
            return object;
        }        
        return getEM().merge(object);
    }

    @Override
    public List<Long> getIdsByEventType(HashSet<String> params) {
        Query query = getEM().createNamedQuery("ProcessInstancesWaitingForEvent");
        String value = params.iterator().next();
        query.setParameter("type", value);
        return query.getResultList();
    }

    private EntityManager getEM() {
        return (EntityManager) environment.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
    }

}
