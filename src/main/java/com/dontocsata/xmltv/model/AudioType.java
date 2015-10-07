package com.dontocsata.xmltv.model;

public enum AudioType {
	// 'mono','stereo','dolby','dolby digital','bilingual' and 'surround'
	MONO("mono", 1),
	STEREO("stereo", 2),
	DOLBY("dolby", 3),
	DOLBY_DIGITAL("dolby digital", 4),
	BILINGUAL("bilingual", 0),
	SURROUND("surround", 5);

	private String xmlTvType;
	private int mxfType;

	private AudioType(String xmlTvType, int mxfType) {
		this.xmlTvType = xmlTvType;
		this.mxfType = mxfType;
	}

	public String getXmlTvType() {
		return xmlTvType;
	}

	public int getMxfType() {
		return mxfType;
	}

	public static AudioType parse(String xmlTvString) {
		for (AudioType at : values()) {
			if (at.xmlTvType.equals(xmlTvString)) {
				return at;
			}
		}
		throw new IllegalArgumentException("No AudioType for: " + xmlTvString);
	}

}
