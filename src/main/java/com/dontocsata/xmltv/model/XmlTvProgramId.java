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

	private String origEpisodeNum;
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
	   // TODO: This incorrectly parses ' . 0 . ' as having season 1
		XmlTvProgramId toRet = new XmlTvProgramId();
		toRet.origEpisodeNum = str; 
		String[] s = str.split("\\.");
		if (s.length >= 1) {
			Integer[] ints = parseInteger(s[0]);
			toRet.season = ints[0]!=null ? (ints[0] + 1) : null;
			toRet.numberOfSeasons = ints[1];
		}
		if (s.length >= 2) {
			Integer[] ints = parseInteger(s[1]);
			toRet.episode = ints[0]!=null ? (ints[0] + 1) : null;
			toRet.numberOfEpisodes = ints[1];
		}
		if (s.length >= 3) {
			Integer[] ints = parseInteger(s[2]);
			toRet.part = ints[0]!=null ? (ints[0] + 1) : null;
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
			return new Integer[] { atoi(str.substring(0, index)),
					atoi(str.substring(index + 1)) };
		} else {
			return new Integer[] { atoi(str), null };
		}
	}

	// Returns null if there is no non-space content
	// Returns 0 if content present but not parseable as number
	// Returns the number otherwise
	private static Integer atoi(String str) {
	   Integer i = null;
	   String s = str.trim();
	   if(s.length() > 0)
	   {
   	   try {
   	      i = Integer.parseInt(str.trim());
   	   }
   	   catch(Exception nex) {
   	     // Deliberately ignoring any parsing errors
   	      i = 0;
   	   }
	   }
	   return i;
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
			sb.append(first - 1);
			if (second != null) {
				sb.append("/").append(second);
			}
		}
	}

}
