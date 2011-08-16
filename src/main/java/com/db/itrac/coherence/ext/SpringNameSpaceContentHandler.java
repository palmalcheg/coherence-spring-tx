package com.db.itrac.coherence.ext;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.springframework.context.ApplicationContext;

import com.oracle.coherence.configuration.Configurable;
import com.oracle.coherence.configuration.parameters.ParameterScope;
import com.oracle.coherence.environment.Environment;
import com.oracle.coherence.environment.extensible.ConfigurationContext;
import com.oracle.coherence.environment.extensible.ConfigurationException;
import com.oracle.coherence.environment.extensible.ElementContentHandler;
import com.oracle.coherence.environment.extensible.QualifiedName;
import com.oracle.coherence.environment.extensible.namespaces.AbstractNamespaceContentHandler;
import com.oracle.coherence.schemes.ClassScheme;
import com.tangosol.io.ExternalizableLite;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlValue;
import com.tangosol.util.ExternalizableHelper;

public class SpringNameSpaceContentHandler extends AbstractNamespaceContentHandler
{
 
	public SpringNameSpaceContentHandler()
	{
		super();
 
		registerContentHandler("bean", new ElementContentHandler()
		{
 
			public Object onElement(ConfigurationContext context,
				QualifiedName qualifiedName,
				XmlElement xmlElement) throws ConfigurationException
				{
 
				// find the beanname for the bean to reference
				if (xmlElement.getAttributeMap().containsKey("ref"))
				{
					XmlValue value = xmlElement.getAttribute("ref");
					final String beanName = value.getString();
					Environment environment = context.getEnvironment();
					
                    return new SpringClassScheme(beanName,environment.getResource(ApplicationContext.class));
				}
				else
				{
					throw new ConfigurationException(String.format(
						"The SpringNameSpaceContentHandler expected a 'ref' attribute in the element [%s].",
						xmlElement), "Please add the correct ref attribute.");
				}
				}
		});	}
 
 
	@Configurable
	public static final class SpringClassScheme<T> implements ClassScheme<T>, ExternalizableLite
	{
		private String m_beanName;
 
		private transient ApplicationContext m_springContext;
 
		
		public SpringClassScheme()
		{
			super();
		}
 
		public SpringClassScheme( String beanName , ApplicationContext ctx )
		{
			m_beanName = beanName;
			m_springContext = ctx;
		}
 
		@Override
		public T realize( Environment environment, ClassLoader classLoader, ParameterScope paramScope )
		{
			return (T) m_springContext.getBean(m_beanName);
		}
 
		@Override
		public boolean realizesClassOf( Class<?> clazz )
		{
			return m_springContext.isTypeMatch( m_beanName, clazz );
		}
 
		@Override
		public void readExternal( DataInput in ) throws IOException
		{
			m_beanName = ExternalizableHelper.readSafeUTF( in );
		}
 
		@Override
		public void writeExternal( DataOutput out ) throws IOException
		{
			ExternalizableHelper.writeSafeUTF( out, m_beanName );
		}      
 
	}
}
