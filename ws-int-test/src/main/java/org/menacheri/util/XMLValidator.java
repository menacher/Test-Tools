package org.menacheri.util;

import java.io.IOException;
import java.io.StringReader;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.xml.transform.ResourceSource;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to validate an incoming xml String against an xsd. The xsd schema file
 * is provided as a property of this bean in the spring bean definition xml
 * file.
 * 
 * @author Abraham Menacherry
 * 
 */
public class XMLValidator {

	private static final Logger LOG = LoggerFactory.getLogger(XMLValidator.class);
	private ResourceSource schemaSource;
	private SchemaFactory schemaFactory;
	private Schema schema;
	
	/**
	 * This method is invoked by spring container after the schemaSource
	 * property is set on this bean. This method will in turn create the schema
	 * factory object and the schema object which are used by the
	 * {@link #validate(String)} method.
	 */
	@PostConstruct
	public void setupSchema()
	{
		schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			schema = schemaFactory.newSchema(schemaSource);
		} catch (SAXException e) {
			LOG.error("Error occurred while constructing XMLValidator, {}",e);
		}
	}
	
	/**
	 * Validates an incoming xml as String against a schema. It throws
	 * {@link SAXException} if the validation failed.
	 * 
	 * @param xml
	 * @throws SAXException
	 * @throws IOException
	 */
	public void validate(String xml) throws SAXException, IOException
	{
		Validator validator = schema.newValidator();
		StringReader reader = new StringReader(xml);
		StreamSource streamSource=new StreamSource(reader);
		validator.validate(streamSource);
	}

	public ResourceSource getSchemaSource() {
		return schemaSource;
	}

	public void setSchemaSource(ResourceSource schemaSource) {
		this.schemaSource = schemaSource;
	}
	
	public SchemaFactory getSchemaFactory() {
		return schemaFactory;
	}

	public void setSchemaFactory(SchemaFactory schemaFactory) {
		this.schemaFactory = schemaFactory;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}
}
