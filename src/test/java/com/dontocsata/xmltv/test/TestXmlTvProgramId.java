package com.dontocsata.xmltv.test;

import org.junit.Assert;
import org.junit.Test;

import com.dontocsata.xmltv.model.XmlTvProgramId;


public class TestXmlTvProgramId {

	private static final Integer ONE = Integer.valueOf(1);
	private static final Integer TWO = Integer.valueOf(2);
	private static final Integer THREE = Integer.valueOf(3);
	private static final Integer FOUR = Integer.valueOf(4);

	@Test
	public void test() {
		XmlTvProgramId progId = XmlTvProgramId.parse("1.1.");
		Assert.assertEquals(ONE, progId.getSeason());
		Assert.assertEquals(ONE, progId.getEpisode());
		Assert.assertEquals("1.1.", progId.toString());

		progId = XmlTvProgramId.parse("1.1.1");
		Assert.assertEquals(ONE, progId.getSeason());
		Assert.assertEquals(ONE, progId.getEpisode());
		Assert.assertEquals(ONE, progId.getPart());
		Assert.assertEquals("1.1.1", progId.toString());

		progId = XmlTvProgramId.parse("1.2.3");
		Assert.assertEquals(ONE, progId.getSeason());
		Assert.assertEquals(TWO, progId.getEpisode());
		Assert.assertEquals(THREE, progId.getPart());
		Assert.assertEquals("1.2.3", progId.toString());

		progId = XmlTvProgramId.parse("1/2.2/3.3/4");
		Assert.assertEquals(ONE, progId.getSeason());
		Assert.assertEquals(TWO, progId.getNumberOfSeasons());
		Assert.assertEquals(TWO, progId.getEpisode());
		Assert.assertEquals(THREE, progId.getNumberOfEpisodes());
		Assert.assertEquals(THREE, progId.getPart());
		Assert.assertEquals(FOUR, progId.getNumberOfParts());
		Assert.assertEquals("1/2.2/3.3/4", progId.toString());
	}

}
