package org.drools.alternative.persistence;


import java.util.HashSet;
import java.util.List;

import org.drools.domain.Versioning;

public interface PersistenceManager {

    void initConnection();

    <T extends Versioning,ID> T getById(ID id);

    <ID> void removeById(ID id);

    <T extends Versioning,ID>  T saveOrUpdate(T object, ID id);

    List<Long> getIdsByEventType(HashSet<String> params);

    long generateIdentity();


}
