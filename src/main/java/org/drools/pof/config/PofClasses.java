package org.drools.pof.config;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="pof-classes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PofClasses {

	@XmlElement(name="pof-class")
	ArrayList<PofClass> entries = new ArrayList<PofClass>();

}
