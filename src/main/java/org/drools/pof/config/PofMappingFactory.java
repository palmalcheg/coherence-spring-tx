package org.drools.pof.config;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PofMappingFactory {

    private static Logger log = LoggerFactory.getLogger(PofMappingFactory.class);

    private static final PofMappingFactory INSTANCE = new PofMappingFactory();

    private static final String MAPPER_CONFIG_XML = "jaxb-pof-mapper.xml";

	private PofMapping mapping;

    private PofMappingFactory() {
        try {
            loadMappingDefinitions(getClass().getClassLoader().getResourceAsStream(MAPPER_CONFIG_XML));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't initialize POF mapping factory", e);
        }
    }
    
    public static PofClass getDescriptorForClass(String className) {
        return INSTANCE.mapping.getPofClass(className);
    }
    
    private void loadMappingDefinitions(InputStream is) throws Exception {
        log.debug("Loading POF mapping definitions...");

        JAXBContext jaxb = JAXBContext.newInstance(PofMapping.class);
    	Unmarshaller um = jaxb.createUnmarshaller();
		mapping = (PofMapping) um.unmarshal(is);
		
        log.debug("Loaded {} POF mapping definitions.", mapping.size());
    }

    
}
