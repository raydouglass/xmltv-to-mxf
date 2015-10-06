package com.dontocsata.xmltv;

import java.io.File;

public class XmlTvParser {

	public static void main(String[] args) throws Exception {
		File xmlTvFile = new File("test_xmltv.xml");
		XmlTv xmlTv = new XmlTv(xmlTvFile).parse();
	}
}
