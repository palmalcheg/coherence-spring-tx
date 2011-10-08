package org.drools.pof.config;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="pof-mapping")
@XmlAccessorType(XmlAccessType.FIELD)
public class PofMapping {
	
	@XmlJavaTypeAdapter(PofClassMap.class)
	@XmlElement(name="pof-classes")
	private HashMap<String, PofClass> pofClasses = new HashMap<String, PofClass>();
    
    public PofClass getPofClass(String key) {
    	return pofClasses.get(key);
    }

	public int size() {
		return pofClasses.size();
	}

	
}
