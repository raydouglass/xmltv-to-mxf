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

public class DDProgramId implements Serializable {

	private static final long serialVersionUID = 587967753058414538L;
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
