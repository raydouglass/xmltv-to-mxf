package com.dontocsata.xmltv;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.dontocsata.xmltv.model.Channel;
import com.dontocsata.xmltv.model.Program;
import com.dontocsata.xmltv.parser.MainHandler;

public class XmlTvParser {

	public static void main(String[] args) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		Consumer<Channel> channelConsumer = c -> {
			System.out.println(c);
		};
		AtomicInteger count = new AtomicInteger(0);
		Consumer<Program> programConsumer = c -> {
			com.dontocsata.xmltv.mxf.Program program = new com.dontocsata.xmltv.mxf.Program();
			program.setDescription(c.getDescription());

		};
		xmlReader.setContentHandler(new MainHandler(xmlReader, channelConsumer, programConsumer));
		xmlReader.parse(new InputSource(new FileInputStream(new File("test_xmltv.xml"))));
		System.out.println(new Date());
		System.out.println(count.get());
	}
}
