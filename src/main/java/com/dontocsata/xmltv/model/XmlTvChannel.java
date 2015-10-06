package com.dontocsata.xmltv.model;

import java.util.ArrayList;
import java.util.List;

public class XmlTvChannel {

	private String id;
	private List<String> displayNames;
	private String icon;
	private String url;

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

	@Override
	public String toString() {
		return "Channel [id=" + id + ", displayNames=" + displayNames + ", icon=" + icon + ", url=" + url + "]";
	}

}
