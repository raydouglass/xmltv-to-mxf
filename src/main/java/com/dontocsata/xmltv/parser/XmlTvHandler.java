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
