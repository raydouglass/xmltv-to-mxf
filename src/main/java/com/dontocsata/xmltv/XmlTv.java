package com.dontocsata.xmltv;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private DataStorage storage;
	private XmlTvDatabase database;

	private Collection<XmlTvProgram> tempPrograms = new ArrayList<>();

	public XmlTv(InputStream xmlTvStream, DataStorage storage) {
		this.xmlTvStream = xmlTvStream;
		this.storage = storage;
	}

	private Consumer<XmlTvChannel> getChannelConsumer() {
		return c -> {
			storage.save(c);
			if (database != null) {
				try {
					database.write(c);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private Consumer<XmlTvProgram> getProgramConsumer() {
		return p -> {
			storage.save(p);
			if (database != null) {
				tempPrograms.add(p);
				if (tempPrograms.size() >= 10000) {
					try {
						database.write(tempPrograms);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
					tempPrograms.clear();
				}
			}
		};
	}

	public XmlTv parse() throws XmlTvParseException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(new MainHandler(xmlReader, getChannelConsumer(), getProgramConsumer()));
			xmlReader.parse(new InputSource(xmlTvStream));
			if (database != null) {
				if (!tempPrograms.isEmpty()) {
					database.write(tempPrograms);
				}
				database.createIndex();
				database.close();
			}
		} catch (IOException | SQLException | SAXException | ParserConfigurationException e) {
			throw new XmlTvParseException(e);
		}
		return this;
	}

}
