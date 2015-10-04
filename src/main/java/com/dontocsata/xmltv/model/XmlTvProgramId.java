package com.dontocsata.xmltv.model;

public class XmlTvProgramId {

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
