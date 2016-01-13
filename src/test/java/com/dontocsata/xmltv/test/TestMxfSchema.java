/*
 * Copyright (c) 2015, Raymond R Douglass III. All rights reserved. DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS
 * FILE HEADER.
 *
 * This file is part of XMLTV-to-MXF.
 *
 * XMLTV-to-MXF is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * XMLTV-to-MXF is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with XMLTV-to-MXF. If not, see
 * <http://www.gnu.org/licenses/>.
 */
 package com.dontocsata.xmltv.test;

import java.io.FileInputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class TestMxfSchema {

	public static void main(String[] args) throws Exception {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new StreamSource(TestMxfSchema.class.getResourceAsStream("/mxf.xsd")));
		Validator validator = schema.newValidator();

		SAXSource source = new SAXSource(new NamespaceFilter(XMLReaderFactory.createXMLReader()),
				new InputSource(new FileInputStream("/Users/ray.douglass/Downloads/basic.xml")));
		validator.validate(source);
	}

	public static class NamespaceFilter extends XMLFilterImpl {

		private String requiredNamespace = "urn:com:dontocsata:xmltv:mxf";

		public NamespaceFilter(XMLReader parent) {
			super(parent);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			// Set the XML's name space for validation purposes
			// This is required because the MXF doesn't have a name space
			if (!uri.equals(requiredNamespace)) {
				uri = requiredNamespace;
			}
			super.startElement(uri, localName, qName, atts);
		}
	}
}
