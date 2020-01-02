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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlTvChannel implements Serializable{
	private static final long serialVersionUID = 207861002617575768L;

	private static Pattern channelSubPattern = Pattern.compile("(\\d+)[\\-\\.](\\d+)");

	private String id;
	private List<String> displayNames;
	private String icon;
	private String url;

	private int channelNumber = -1;
	private int subChannelNumber = -1;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getDisplayNames() {
		if (displayNames == null) {
			displayNames = new ArrayList<>();
		}
		return displayNames;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Attempts to derive the channel number given the channel's display names
	 */
	public int getChannelNumber() {
		if (channelNumber < 0) {
			for (String name : displayNames) {
				if (name.matches("\\d+")) {
					channelNumber = Integer.parseInt(name);
				} else {
					Matcher matcher = channelSubPattern.matcher(name);
					if (matcher.find()) {
						channelNumber = Integer.parseInt(matcher.group(1));
						subChannelNumber = Integer.parseInt(matcher.group(2));
					}
				}
			}
			if(channelNumber<0) {
				for (String name : displayNames) {
					for (String split : name.split("\\s")) {
						if (split.matches("\\d+")) {
							channelNumber = Integer.parseInt(split);
						} else {
							Matcher matcher = channelSubPattern.matcher(split);
							if (matcher.find()) {
								channelNumber = Integer.parseInt(matcher.group(1));
								subChannelNumber = Integer.parseInt(matcher.group(2));
							}
						}
					}
				}
			}
			if (channelNumber < 0) {
				throw new IllegalStateException("No acceptable channel number found for: " + this);
			}
		}
		return channelNumber;
	}

	public int getSubChannelNumber() {
		getChannelNumber();
		return subChannelNumber;
	}

	@Override
	public String toString() {
		return "Channel [id=" + id + ", displayNames=" + displayNames + ", icon=" + icon + ", url=" + url + "]";
	}

}
