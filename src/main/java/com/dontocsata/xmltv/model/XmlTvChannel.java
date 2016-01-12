package com.dontocsata.xmltv.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlTvChannel implements Serializable{
	private static final long serialVersionUID = 207861002617575768L;

	private static Pattern channelSubPattern = Pattern.compile("(\\d+)\\-(\\d+)");

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
