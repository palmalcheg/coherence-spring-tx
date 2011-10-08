package org.drools.pof.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement(name="pof")
@XmlAccessorType(XmlAccessType.FIELD)
public class PofProperty implements Comparable<PofProperty> {

	@XmlAttribute(name="id")
    private Integer propertyId;
	
	@XmlAttribute(name="property")
    private  String propertyName;
	
	@XmlJavaTypeAdapter(value=PofTypesAdater.class)
	@XmlAttribute(name="type")
    private Class<?> propertyType;

    public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setPropertyType(Class<?> propertyType) {
		this.propertyType = propertyType;
	}

	@Override
    public int compareTo(PofProperty property) {
        return this.getPropertyId().compareTo(property.getPropertyId());
    }

    public Integer getPropertyId() {
        return propertyId;
    }

	public String getPropertyName() {
		return propertyName;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}

    @Override
    public String toString() {
        return "PofProperty [propertyId=" + propertyId + ", propertyName=" + propertyName + ", propertyType=" + propertyType
                + "]";
    }

}