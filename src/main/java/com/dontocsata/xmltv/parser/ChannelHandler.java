package com.dontocsata.xmltv.parser;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.dontocsata.xmltv.model.XmlTvChannel;

public class ChannelHandler extends XmlTvHandler<XmlTvChannel> {

	private static final Logger log = LoggerFactory.getLogger(ChannelHandler.class);

	private XmlTvChannel channel;
	private boolean displayName;

	public ChannelHandler(XMLReader xmlReader, ContentHandler originalHandler, Consumer<XmlTvChannel> consumer) {
		super(xmlReader, originalHandler, consumer);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
			case "display-name":
				displayName = true;
				break;
			case "icon":
				channel.setIcon(attributes.getValue("src"));
				break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
			case "channel":
				end(channel);
				break;
			case "display-name":
				displayName = false;
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (displayName) {
			String str = new String(ch, start, length);
			channel.getDisplayNames().add(str);
		}
	}

	@Override
	public void start(String uri, String localName, String qName, Attributes attributes) {
		channel = new XmlTvChannel();
		channel.setId(attributes.getValue("id"));
	}

}
