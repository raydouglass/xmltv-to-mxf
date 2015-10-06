package com.dontocsata.xmltv.model;

public class DDProgramId {

	private DDProgramIdType type;
	private String seriesId;
	private String episodeId;

	public DDProgramIdType getType() {
		return type;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public String getEpisodeId() {
		return episodeId;
	}

	@Override
	public String toString() {
		return type.getCode() + seriesId + "." + episodeId;
	}

	public static DDProgramId parse(String text) {
		if ("SH00000001.0000".equals(text)) {
			return null;
		}
		DDProgramId toRet = new DDProgramId();
		String type = text.substring(0, 2);
		toRet.type = DDProgramIdType.parse(type);
		toRet.seriesId = text.substring(2, 10);
		toRet.episodeId = text.substring(11);
		return toRet;
	}

}
