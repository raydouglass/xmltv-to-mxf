package com.dontocsata.xmltv;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.function.Consumer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.dontocsata.xmltv.db.XmlTvDatabase;
import com.dontocsata.xmltv.model.Channel;
import com.dontocsata.xmltv.model.Program;
import com.dontocsata.xmltv.parser.MainHandler;

public class XmlTvParser {

	public static void main(String[] args) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		XmlTvDatabase db = new XmlTvDatabase(new File("data.db"));
		Consumer<Channel> channelConsumer = c -> {
			try {
				db.write(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Collection<Program> programs = new ArrayList<>();
		Consumer<Program> programConsumer = c -> {
			programs.add(c);
			if (programs.size() == 10000) {
				try {
					db.write(programs);
				} catch (Exception e) {
					e.printStackTrace();
				}
				programs.clear();
			}
		};
		xmlReader.setContentHandler(new MainHandler(xmlReader, channelConsumer, programConsumer));
		// xmlReader.parse(new InputSource(new FileInputStream(new File("test_xmltv.xml"))));
		xmlReader.parse(new InputSource(new FileInputStream(new File("/Users/ray/Downloads/xmltv_2015_10_04.xml"))));
		System.out.println(new Date());
		db.createIndex();
		db.close();
	}
}
