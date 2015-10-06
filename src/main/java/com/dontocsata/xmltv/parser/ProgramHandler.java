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

import com.dontocsata.xmltv.model.DDProgramId;
import com.dontocsata.xmltv.model.Program;
import com.dontocsata.xmltv.model.XmlTvProgramId;

public class ProgramHandler extends XmlTvHandler<Program> {

	private static final Logger log = LoggerFactory.getLogger(ProgramHandler.class);

	private static final DateTimeFormatter[] FORMATS = new DateTimeFormatter[] {
			DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z"),
			DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("UTC")),
			DateTimeFormatter.ofPattern("yyyyMMddHHmm"), DateTimeFormatter.ofPattern("yyyyMMddHH"),
			DateTimeFormatter.ofPattern("yyyyMMdd") };

	private Program program;
	private String cachedString;
	private String tempString;

	public ProgramHandler(XMLReader xmlReader, ContentHandler originalHandler, Consumer<Program> consumer) {
		super(xmlReader, originalHandler, consumer);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempString = null;
		cachedString=null;
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
				program.setTitle(cachedString);
				break;
			case "desc":
				program.setDescription(cachedString);
				break;
			case "sub-title":
				program.setSubTitle(cachedString);
				break;
			case "date":
				program.setDate(parseLocalDate(cachedString));
				break;
			case "episode-num":
				if ("xmltv_ns".equals(tempString)) {
					XmlTvProgramId xmlTvProgramId = XmlTvProgramId.parse(cachedString);
					program.setXmlTvProgramId(xmlTvProgramId);
				} else if ("dd_progid".equals(tempString)) {
					DDProgramId ddProgramId = DDProgramId.parse(cachedString);
					program.setDdProgramId(ddProgramId);
				} else if ("onscreen".equals(tempString)) {
					program.setOnScreenProgramId(cachedString);
				}
				break;
			case "previously-shown":
				program.setPreviouslyShown(true);
				if (tempString != null) {
					program.setPreviouslyShownDate(parseLocalDate(tempString));
				}
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		cachedString = new String(ch, start, length);
	}

	@Override
	public void start(String uri, String localName, String qName, Attributes attributes) {
		program = new Program();
		program.setChannelId(attributes.getValue("channel"));
		program.setStart(parseZonedDateTime(attributes.getValue("start")));
		program.setStop(parseZonedDateTime(attributes.getValue("stop")));
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
		for(DateTimeFormatter dtf:FORMATS) {
			try {
				return ZonedDateTime.parse(text,dtf);
			} catch (DateTimeParseException e) {
				// no-op
			}
		}
		throw new IllegalArgumentException("Unparseable: " + text);
	}

}
