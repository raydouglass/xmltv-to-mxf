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
