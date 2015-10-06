package com.dontocsata.xmltv.model;

import java.util.ArrayList;
import java.util.List;

public class XmlTvChannel {

	private String id;
	private List<String> displayNames;
	private String icon;
	private String url;

	private int channelNumber = -1;

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
				}
			}
			if(channelNumber<0) {
				throw new IllegalStateException("No acceptable channel number found for: " + this);
			}
		}
		return channelNumber;
	}

	@Override
	public String toString() {
		return "Channel [id=" + id + ", displayNames=" + displayNames + ", icon=" + icon + ", url=" + url + "]";
	}

}
