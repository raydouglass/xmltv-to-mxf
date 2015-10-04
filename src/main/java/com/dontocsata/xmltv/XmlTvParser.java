package com.dontocsata.xmltv;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.dontocsata.xmltv.parser.MainHandler;

public class XmlTvParser {

	public static void main(String[] args) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(new MainHandler(xmlReader));

		xmlReader.parse(new InputSource(new FileInputStream(new File("test_xmltv.xml"))));
	}
}
