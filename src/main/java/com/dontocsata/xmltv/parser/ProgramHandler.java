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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.dontocsata.xmltv.model.AudioType;
import com.dontocsata.xmltv.model.DDProgramId;
import com.dontocsata.xmltv.model.XmlTvProgram;
import com.dontocsata.xmltv.model.XmlTvProgramId;

public class ProgramHandler extends XmlTvHandler<XmlTvProgram> {

	private static final Logger log = LoggerFactory.getLogger(ProgramHandler.class);

	private static final DateTimeFormatter[] FORMATS = new DateTimeFormatter[] {
			DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z"),
			DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("UTC")),
			DateTimeFormatter.ofPattern("yyyyMMddHHmm"), DateTimeFormatter.ofPattern("yyyyMMddHH"),
			DateTimeFormatter.ofPattern("yyyyMMdd") };

	private XmlTvProgram program;
	private StringBuilder cachedString;
	private String tempString;

	public ProgramHandler(XMLReader xmlReader, ContentHandler originalHandler, Consumer<XmlTvProgram> consumer) {
		super(xmlReader, originalHandler, consumer);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempString = null;
		
		// This doesn't make sense (and causes NPE!). If there is a start element then it means the tag
		// is present but empty, ie. an empty string. Thus it should be a new, empty buffer here.
		// cachedString = null; 
		cachedString = new StringBuilder();
		switch (qName) {
		case "episode-num":
			tempString = attributes.getValue("system");
			break;
		case "previously-shown":
			tempString = attributes.getValue("start");
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
		case "programme":
			end(program);
			break;
		case "title":
			program.setTitle(getString());
			break;
		case "desc":
			program.setDescription(getString());
			break;
		case "sub-title":
			program.setSubTitle(getString());
			break;
		case "date":
			program.setDate(parseLocalDate(getString()));
			break;
		case "episode-num":
			if ("xmltv_ns".equals(tempString)) {
				XmlTvProgramId xmlTvProgramId = XmlTvProgramId.parse(getString());
				program.setXmlTvProgramId(xmlTvProgramId);
			} else if ("dd_progid".equals(tempString)) {
				DDProgramId ddProgramId = DDProgramId.parse(getString());
				program.setDdProgramId(ddProgramId);
			} else if ("onscreen".equals(tempString)) {
				program.setOnScreenProgramId(getString());
			}
			break;
		case "previously-shown":
			if (tempString != null) {
				program.setPreviouslyShown(true);
				program.setPreviouslyShownDate(parseZonedDateTime(tempString));
			}
			break;
		case "aspect":
			program.setVideoAspect(getString());
			break;
		case "quality":
			program.setVideoQuality(getString());
			break;
		case "stereo":
			AudioType at = AudioType.parse(getString());
			program.setAudio(at);
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (cachedString == null) {
			cachedString = new StringBuilder();
		}
		cachedString.append(ch, start, length);
	}

	@Override
	public void start(String uri, String localName, String qName, Attributes attributes) {
		program = new XmlTvProgram();
		program.setChannelId(attributes.getValue("channel"));
		program.setStart(parseZonedDateTime(attributes.getValue("start")));
		program.setStop(parseZonedDateTime(attributes.getValue("stop")));
	}

	private String getString() {
	   if(cachedString == null)
	   {
	      log.error("getString: no cachedString for {}", program.toString());
	      return "";
	   }
		return cachedString.toString();
	}

	private static LocalDate parseLocalDate(String text) {
		if (text.length() == 4) {
			// just a year
			return LocalDate.of(Integer.parseInt(text), 1, 1);
		}
		for (DateTimeFormatter dtf : FORMATS) {
			try {
				return LocalDate.parse(text, dtf);
			} catch (DateTimeParseException e) {
				// no-op
			}
		}
		throw new IllegalArgumentException("Unparseable: " + text);
	}

	private static ZonedDateTime parseZonedDateTime(String text) {
		for (DateTimeFormatter dtf : FORMATS) {
			try {
				return ZonedDateTime.parse(text, dtf);
			} catch (DateTimeParseException e) {
				// no-op
			}
		}
		throw new IllegalArgumentException("Unparseable: " + text);
	}
}
