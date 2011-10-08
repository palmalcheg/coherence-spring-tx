package org.drools.pof.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PofClassMap extends XmlAdapter<PofClasses,Map<String,PofClass>> {

	@Override
	public PofClasses marshal(Map<String, PofClass> map) throws Exception {
		PofClasses r = new PofClasses();
		r.entries.addAll(map.values());
		return r ;
	}

	@Override
	public Map<String, PofClass> unmarshal(PofClasses classes) throws Exception {
		Map<String, PofClass> map = new HashMap<String, PofClass>();
		for (PofClass pc : classes.entries) {
			map.put(pc.getClassName(), pc);
		}
		return map ;
	}

}
