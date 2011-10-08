package org.drools.pof;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.drools.pof.config.PofClass;
import org.drools.pof.config.PofMappingFactory;
import org.drools.pof.config.PofProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

public class GenericPofSerializer implements PofSerializer {

    private static Logger log = LoggerFactory.getLogger(GenericPofSerializer.class);

    @Override
    public Object deserialize(PofReader pr) throws IOException {
        PofContext pofContext = pr.getPofContext();
        int userTypeId = pr.getUserTypeId();
        try {
            Class<?> clazz = pofContext.getClass(userTypeId);
            PofClass pofDescriptor = PofMappingFactory.getDescriptorForClass(clazz.getSimpleName());
            Object obj = clazz.newInstance();
            log.trace("Deserialize instance of {}", clazz);
            deserialize(pr, obj, pofDescriptor);
            return obj;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void deserialize(PofReader pr, Object obj, PofClass pofClassDescriptor) throws Exception {
    	PofClass parentPof = pofClassDescriptor.getParent();
        if (parentPof != null) {
            log.trace("Invoke {} parent deserializer methods", parentPof.getClassName());
            deserialize(pr, obj, parentPof);
        }

        SortedSet<PofProperty> props = pofClassDescriptor.getPofProperties();
        for (PofProperty p : props) {
            Object cachedValue = executePofReadMethod(pr, p.getPropertyId(), p);
            if (cachedValue != null) {
                log.trace("desirialize property='{}' of type='{}' with value='{}'",
                        new Object[] { p.getPropertyName(), p.getPropertyType(), cachedValue });
                Class pType = PropertyUtils.getPropertyType(obj, p.getPropertyName());
                if (pType == null) {
                    log.warn("Property {} not found in object {}. Check mapping!", p.getPropertyName(), obj.getClass());
                    continue;
                }
                if (pType.isEnum()) {
                    Enum<?> enumValue = Enum.valueOf(pType, (String) cachedValue);                    
                    PropertyUtils.setProperty(obj, p.getPropertyName(), enumValue);
                } else {
                    PropertyUtils.setProperty(obj, p.getPropertyName(), cachedValue);
                }
            }
        }
    }

    @Override
    public void serialize(PofWriter pw, Object obj) throws IOException {
        PofContext pofContext = pw.getPofContext();
        int userTypeId = pw.getUserTypeId();
        Class<?> clazz = pofContext.getClass(userTypeId);
        PofClass pofDescriptor = PofMappingFactory.getDescriptorForClass(clazz.getSimpleName());
        try {
            log.trace("Serialize instance of {}", obj.getClass());
            serialize(pw, obj, pofDescriptor);
        } catch (Exception e) {
            throw new IOException(e);
        }
        pw.writeRemainder(null);
    }

    private void serialize(PofWriter pw, Object obj, PofClass pofClassDescriptor) throws Exception {
    	PofClass parentPof = pofClassDescriptor.getParent();
        if (parentPof != null) {
            log.trace("Invoke {} parent serializer methods", parentPof.getClassName());
            serialize(pw, obj, parentPof);
        }

        SortedSet<PofProperty> props = pofClassDescriptor.getPofProperties();
        for (PofProperty p : props) {
            Object value = PropertyUtils.getProperty(obj, p.getPropertyName());
            if (value != null) {
                log.trace("Serialize property='{}' of type='{}' with value='{}'",
                        new Object[] { p.getPropertyName(), p.getPropertyType(), value });
                executePofWriteMethod(pw, p.getPropertyId(), p, value);
            }
        }
    }

    private void executePofWriteMethod(PofWriter pw, Integer idx, PofProperty p, Object value) throws Exception {
        if (value == null) {
            return;
        }
        if (Date.class == p.getPropertyType()) {            
            pw.writeLong(idx, ((Date)value).getTime());
        } else if (String.class == p.getPropertyType()) {
            pw.writeString(idx, value.toString());
        } else if (Double.class == p.getPropertyType()) {
            pw.writeDouble(idx, (Double) value);
        } else if (Integer.class == p.getPropertyType()) {
            pw.writeInt(idx, (Integer) value);
        } else if (Boolean.class == p.getPropertyType()) {
            pw.writeBoolean(idx, (Boolean) value);
        } else if (Collection.class.isAssignableFrom(p.getPropertyType())) {
            if (Set.class.isAssignableFrom(p.getPropertyType())) {
                pw.writeCollection(idx, (Set<?>) value);
            } else
                pw.writeCollection(idx, (Collection<?>) value);
        } else if (Map.class.isAssignableFrom(p.getPropertyType())) {
            pw.writeMap(idx, (Map<?, ?>) value);
        } else if (Long.class == p.getPropertyType()) {
            pw.writeLong(idx, (Long) value);
        } else if (Blob.class == p.getPropertyType()) {
            pw.writeByteArray(idx, (byte[]) value);
        } else if (PortableObject.class == p.getPropertyType()) {
            pw.writeObject(idx, value);
        } else
            throw new IllegalArgumentException(p + " invalid");

    }

    private Object executePofReadMethod(PofReader pr, Integer idx, PofProperty p) throws Exception {
        if (Date.class == p.getPropertyType()) {            
            return new Date(pr.readLong(idx));
        } else if (String.class == p.getPropertyType()) {
            return pr.readString(idx);
        } else if (Double.class == p.getPropertyType()) {
            return pr.readDouble(idx);
        } else if (Integer.class == p.getPropertyType()) {
            return pr.readInt(idx);
        } else if (Boolean.class == p.getPropertyType()) {
            return pr.readBoolean(idx);
        } else if (Collection.class.isAssignableFrom(p.getPropertyType())) {
            Collection<?> readCollection = null;
            if (Set.class.isAssignableFrom(p.getPropertyType())) {
                readCollection = pr.readCollection(idx, new HashSet<Object>());
            } else
                readCollection = pr.readCollection(idx, new ArrayList<Object>());
            return readCollection;
        } else if (Map.class.isAssignableFrom(p.getPropertyType())) {
            return pr.readMap(idx, new HashMap<Object, Object>());
        } else if (Long.class == p.getPropertyType()) {
            return pr.readLong(idx);
        } else if (Blob.class == p.getPropertyType()) {
            return pr.readByteArray(idx);
        } else if (PortableObject.class == p.getPropertyType()) {
            return pr.readObject(idx);
        }
        throw new IllegalArgumentException(p + " invalid");
    } 

}
