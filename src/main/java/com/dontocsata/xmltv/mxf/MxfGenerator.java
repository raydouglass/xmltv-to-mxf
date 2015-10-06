package com.dontocsata.xmltv.mxf;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class MxfGenerator {

	private JAXBContext jaxbContext;

	public MxfGenerator() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(getClass().getPackage().getName());
	}

	public MXF createBasicMXF() throws JAXBException, IOException {
		try (InputStream in = getClass().getResourceAsStream("/basic_mxf.xml")) {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (MXF) unmarshaller.unmarshal(in);
		}
	}

	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

}
