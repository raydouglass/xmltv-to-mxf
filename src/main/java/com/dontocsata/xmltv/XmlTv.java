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
package com.dontocsata.xmltv;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.dontocsata.xmltv.db.DataStorage;
import com.dontocsata.xmltv.db.XmlTvDatabase;
import com.dontocsata.xmltv.model.XmlTvChannel;
import com.dontocsata.xmltv.model.XmlTvProgram;
import com.dontocsata.xmltv.parser.MainHandler;

public class XmlTv {

	private static final Logger log = LoggerFactory.getLogger(XmlTv.class);

	private InputStream xmlTvStream;

	private XmlTVDataSorage storage;
	
	private Collection<XmlTvProgram> tempPrograms = new ArrayList<>();

	public XmlTv(InputStream xmlTvStream, XmlTVDataSorage storage) {
		this.xmlTvStream = xmlTvStream;
		this.storage = storage;
	}

	private Consumer<XmlTvChannel> getChannelConsumer() {
		return c -> {
			storage.save(c);
		};
	}

	private Consumer<XmlTvProgram> getProgramConsumer() {
		return p -> {
			storage.save(p);
		};
	}

	public XmlTv parse() throws XmlTvParseException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setEntityResolver(new EntityResolver() {

				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (systemId != null && systemId.endsWith("xmltv.dtd")) {
						return new InputSource(getClass().getResourceAsStream("/xmltv.dtd"));
					}
					return null;
				}
			});
			xmlReader.setContentHandler(new MainHandler(xmlReader, getChannelConsumer(), getProgramConsumer()));
			xmlReader.parse(new InputSource(xmlTvStream));
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new XmlTvParseException(e);
		}
		return this;
	}

}
