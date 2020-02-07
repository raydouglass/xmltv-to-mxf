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
		Assert.assertEquals(TWO, progId.getSeason());
		Assert.assertEquals(TWO, progId.getEpisode());
		Assert.assertEquals("1.1.", progId.toString());

		progId = XmlTvProgramId.parse("1.1.1");
		Assert.assertEquals(TWO, progId.getSeason());
		Assert.assertEquals(TWO, progId.getEpisode());
		Assert.assertEquals(TWO, progId.getPart());
		Assert.assertEquals("1.1.1", progId.toString());

		progId = XmlTvProgramId.parse("1.2.3");
		Assert.assertEquals(TWO, progId.getSeason());
		Assert.assertEquals(THREE, progId.getEpisode());
		Assert.assertEquals(FOUR, progId.getPart());
		Assert.assertEquals("1.2.3", progId.toString());

		progId = XmlTvProgramId.parse("1/2.2/3.3/4");
		Assert.assertEquals(TWO, progId.getSeason());
		Assert.assertEquals(TWO, progId.getNumberOfSeasons());
		Assert.assertEquals(THREE, progId.getEpisode());
		Assert.assertEquals(THREE, progId.getNumberOfEpisodes());
		Assert.assertEquals(FOUR, progId.getPart());
		Assert.assertEquals(FOUR, progId.getNumberOfParts());
		Assert.assertEquals("1/2.2/3.3/4", progId.toString());
	}

}
