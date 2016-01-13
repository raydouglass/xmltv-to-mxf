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
 package com.dontocsata.xmltv.model;

import java.io.Serializable;

public class XmlTvProgramId implements Serializable{

	private static final long serialVersionUID = -6383774362916730381L;
	private Integer season;
	private Integer numberOfSeasons;
	private Integer episode;
	private Integer numberOfEpisodes;
	private Integer part;
	private Integer numberOfParts;

	public Integer getSeason() {
		return season;
	}

	public Integer getNumberOfSeasons() {
		return numberOfSeasons;
	}

	public Integer getEpisode() {
		return episode;
	}

	public Integer getNumberOfEpisodes() {
		return numberOfEpisodes;
	}

	public Integer getPart() {
		return part;
	}

	public Integer getNumberOfParts() {
		return numberOfParts;
	}

	public static XmlTvProgramId parse(String str) {
		XmlTvProgramId toRet = new XmlTvProgramId();
		String[] s = str.split("\\.");
		if (s.length >= 1) {
			Integer[] ints = parseInteger(s[0]);
			toRet.season = ints[0];
			toRet.numberOfSeasons = ints[1];
		}
		if (s.length >= 2) {
			Integer[] ints = parseInteger(s[1]);
			toRet.episode = ints[0];
			toRet.numberOfEpisodes = ints[1];
		}
		if (s.length >= 3) {
			Integer[] ints = parseInteger(s[2]);
			toRet.part = ints[0];
			toRet.numberOfParts = ints[1];
		}
		return toRet;
	}

	private static Integer[] parseInteger(String str) {
		if (str.isEmpty()) {
			return new Integer[] { null, null };
		}
		int index = str.indexOf('/');
		if (index != -1) {
			return new Integer[] { Integer.parseInt(str.substring(0, index)),
					Integer.parseInt(str.substring(index + 1)) };
		} else {
			return new Integer[] { Integer.parseInt(str), null };
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		buildToString(sb, season, numberOfSeasons);
		sb.append(".");
		buildToString(sb, episode, numberOfEpisodes);
		sb.append(".");
		buildToString(sb, part, numberOfParts);
		return sb.toString();
	}

	private static void buildToString(StringBuilder sb, Integer first, Integer second) {
		if (first != null) {
			sb.append(first);
			if (second != null) {
				sb.append("/").append(second);
			}
		}
	}

}
