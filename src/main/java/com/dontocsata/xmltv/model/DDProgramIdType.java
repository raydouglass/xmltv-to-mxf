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

	public String getCode() {
		return code;
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
