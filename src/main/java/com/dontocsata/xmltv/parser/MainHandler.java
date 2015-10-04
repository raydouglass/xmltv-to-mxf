package com.dontocsata.xmltv.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class MainHandler extends DefaultHandler {

	private XMLReader xmlReader;
	private ChannelHandler channelHandler;
	private ProgramHandler programHandler;

	public MainHandler(XMLReader xmlReader) {
		this.xmlReader = xmlReader;
		channelHandler = new ChannelHandler(xmlReader, this, c -> System.out.println(c));
		programHandler = new ProgramHandler(xmlReader, this, p -> System.out.println(p));
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
