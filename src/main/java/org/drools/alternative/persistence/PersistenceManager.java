package org.drools.alternative.persistence;


import java.util.HashSet;
import java.util.List;

public interface PersistenceManager {

    void initConnection();

    <T,ID> T getById(ID id);

    <ID> void removeById(ID id);

    <T,ID>  T saveOrUpdate(T object, ID id);

    List<Long> getIdsByEventType(HashSet<String> params);


}
