package org.drools.pof.config;

import java.sql.Blob;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.tangosol.io.pof.PortableObject;

public class PofTypesAdater extends XmlAdapter<String, Class<?>> {
	
	private static Map<String, Class<?>> types = new HashMap<String, Class<?>>();
	
	static {
        registerType(Collection.class);
        registerType(String.class);
        registerType(Double.class);
        registerType(Integer.class);
        registerType(Long.class);
        registerType(Date.class);
        registerType(Boolean.class);
        registerType(Blob.class);
        registerType(Set.class);
        registerType(List.class);
        registerType(Map.class);
        registerType(int.class, Integer.class);
        registerType(PortableObject.class);
    }
	
	private static void registerType(Class<?> cl) {
        registerType(cl, cl);
    }

    private static void registerType(Class<?> cl, Class<?> mapCl) {
        types.put(cl.getSimpleName().toLowerCase(), mapCl);
    }

	@Override
	public String marshal(Class<?> v) throws Exception {
		return v.getSimpleName().toLowerCase();
	}

	@Override
	public Class<?> unmarshal(String v) throws Exception {
		return types.get(v.toLowerCase());
	}

}
