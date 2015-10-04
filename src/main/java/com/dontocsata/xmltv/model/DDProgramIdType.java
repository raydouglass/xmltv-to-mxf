package com.dontocsata.xmltv.model;


public enum DDProgramIdType {

	MOVIE("MV"),
	SHOW("SH"),
	EPISODE("EP"),
	SPORT("SP"),
	UNKNOWN("U");

	private String code;

	DDProgramIdType(String code) {
		this.code = code;
	}

	public static DDProgramIdType parse(String code) {
		if (code == null || code.isEmpty()) {
			return UNKNOWN;
		} else {
			for (DDProgramIdType pi : values()) {
				if (pi.code.equalsIgnoreCase(code)) {
					return pi;
				}
			}
		}
		throw new IllegalArgumentException("Unknown code: " + code);
	}

}
