package com.dontocsata.xmltv.parser;

import java.util.function.Consumer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public abstract class XmlTvHandler<T> extends DefaultHandler {

	protected XMLReader xmlReader;
	protected ContentHandler originalHandler;
	protected Consumer<T> consumer;

	public XmlTvHandler(XMLReader xmlReader, ContentHandler originalHandler, Consumer<T> consumer) {
		this.xmlReader = xmlReader;
		this.originalHandler = originalHandler;
		this.consumer = consumer;
	}

	public abstract void start(String uri, String localName, String qName, Attributes attributes);

	public void end(T obj) {
		consumer.accept(obj);
		xmlReader.setContentHandler(originalHandler);
	}

}
