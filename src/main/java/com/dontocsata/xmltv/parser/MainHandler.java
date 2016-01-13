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
 package com.dontocsata.xmltv.parser;

import java.util.function.Consumer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.dontocsata.xmltv.model.XmlTvChannel;
import com.dontocsata.xmltv.model.XmlTvProgram;

public class MainHandler extends DefaultHandler {

	private XMLReader xmlReader;
	private ChannelHandler channelHandler;
	private ProgramHandler programHandler;

	public MainHandler(XMLReader xmlReader, Consumer<XmlTvChannel> channelConsumer, Consumer<XmlTvProgram> programConsumer) {
		this.xmlReader = xmlReader;
		channelHandler = new ChannelHandler(xmlReader, this, channelConsumer);
		programHandler = new ProgramHandler(xmlReader, this, programConsumer);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
			case "tv":
				break;
			case "channel":
				xmlReader.setContentHandler(channelHandler);
				channelHandler.start(uri, localName, qName, attributes);
				break;
			case "programme":
				xmlReader.setContentHandler(programHandler);
				programHandler.start(uri, localName, qName, attributes);
				break;
			default:
				throw new UnsupportedOperationException("No support for: " + qName);
		}
	}

}
