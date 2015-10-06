package com.dontocsata.xmltv.mxf;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdom2.Document;
import org.jdom2.transform.JDOMSource;
import org.xml.sax.SAXException;

public class MxfValidator {

	private Schema schema;
	private Validator validator;

	public MxfValidator() throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try (InputStream in = getClass().getResourceAsStream("/mxf.xsd")) {
			schema = schemaFactory.newSchema(new StreamSource(in));
		}
		validator = schema.newValidator();
	}

	public void validate(Source source) throws SAXException, IOException {
		validator.validate(source);
	}

	public void validate(Document doc) throws SAXException, IOException {
		validate(new JDOMSource(doc));
	}
}
