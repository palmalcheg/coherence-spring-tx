package org.drools.pof.config;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="pof-class")
@XmlAccessorType(XmlAccessType.FIELD)
public class PofClass {
	
	@XmlAttribute(required=false)
	@XmlIDREF
	private PofClass parent;
	
	@XmlAttribute
	@XmlID
	private String className;
	
	@XmlElement(name="pof")
	private SortedSet <PofProperty> pofProperties =   new TreeSet<PofProperty>();
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public PofClass getParent() {
		return parent;
	}

	public void setParent(PofClass parent) {
		this.parent = parent;
	}

	public void setPofProperties(SortedSet<PofProperty> pofProperties) {
		this.pofProperties = pofProperties;
	}

	public SortedSet<PofProperty> getPofProperties() {
		return pofProperties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PofClass other = (PofClass) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		return true;
	}

	
}
