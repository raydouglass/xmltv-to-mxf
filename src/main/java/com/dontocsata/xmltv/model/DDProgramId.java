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
		return "DDProgramId [type=" + type + ", seriesId=" + seriesId + ", episodeId=" + episodeId + "]";
	}

	public static DDProgramId parse(String text) {
		DDProgramId toRet = new DDProgramId();
		String type = text.substring(0, 2);
		toRet.type = DDProgramIdType.parse(type);
		toRet.seriesId = text.substring(2, 10);
		toRet.episodeId = text.substring(11);
		return toRet;
	}

}
