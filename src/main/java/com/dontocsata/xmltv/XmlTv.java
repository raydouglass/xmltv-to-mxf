package com.dontocsata.xmltv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.dontocsata.xmltv.db.XmlTvDatabase;
import com.dontocsata.xmltv.model.XmlTvChannel;
import com.dontocsata.xmltv.model.XmlTvProgram;
import com.dontocsata.xmltv.parser.MainHandler;

public class XmlTv {

	private static final Logger log = LoggerFactory.getLogger(XmlTv.class);

	private InputStream xmlTvStream;

	private Map<String, XmlTvChannel> channels = new HashMap<>();
	private Collection<XmlTvProgram> programs = new ArrayList<>();
	private XmlTvDatabase database;

	private Collection<XmlTvProgram> tempPrograms = new ArrayList<>();

	public XmlTv(InputStream xmlTvStream) throws IOException {
		this.xmlTvStream = xmlTvStream;
	}

	public XmlTv(InputStream xmlTvStream, File dbFile) throws IOException, SQLException {
		this(xmlTvStream);
		database = new XmlTvDatabase(dbFile);
	}

	public Consumer<XmlTvChannel> getChannelConsumer() {
		return c -> {
			channels.put(c.getId(), c);
			if (database != null) {
				try {
					database.write(c);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	public Consumer<XmlTvProgram> getProgramConsumer() {
		return p -> {
			programs.add(p);
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

	public Map<String, XmlTvChannel> getChannels() {
		return channels;
	}

	public Collection<XmlTvProgram> getPrograms() {
		return programs;
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
