package com.dontocsata.xmltv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.dontocsata.xmltv.model.Channel;
import com.dontocsata.xmltv.model.Program;
import com.dontocsata.xmltv.parser.MainHandler;

public class XmlTv {

	private static final Logger log = LoggerFactory.getLogger(XmlTv.class);

	private File xmlTvFile;

	private Map<String, Channel> channels = new HashMap<>();
	private XmlTvDatabase database;

	private Collection<Program> tempPrograms = new ArrayList<>();

	public XmlTv(File xmlTvFile) throws IOException, SQLException {
		this.xmlTvFile = xmlTvFile;
		if (!this.xmlTvFile.exists()) {
			throw new FileNotFoundException(this.xmlTvFile.toString());
		}
		File f = File.createTempFile("xmltv", "db");
		log.debug("Temp database file: {}", f.getAbsolutePath());
		f.deleteOnExit();
		database = new XmlTvDatabase(f);
	}

	public Consumer<Channel> getChannelConsumer() {
		return c -> {
			channels.put(c.getId(), c);
			try {
				database.write(c);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		};
	}

	public Consumer<Program> getProgramConsumer() {
		return p -> {
			tempPrograms.add(p);
			if (tempPrograms.size() >= 10000) {
				try {
					database.write(tempPrograms);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				tempPrograms.clear();
			}
		};
	}

	public Map<String, Channel> getChannels() {
		return channels;
	}

	public XmlTv parse() throws XmlTvParseException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(new MainHandler(xmlReader, getChannelConsumer(), getProgramConsumer()));
			xmlReader.parse(new InputSource(new FileInputStream(xmlTvFile)));
			if (!tempPrograms.isEmpty()) {
				database.write(tempPrograms);
			}
			database.createIndex();
			database.close();
		} catch (IOException | SQLException | SAXException | ParserConfigurationException e) {
			throw new XmlTvParseException(e);
		}
		return this;
	}

}
