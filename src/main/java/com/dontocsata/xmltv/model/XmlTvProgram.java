package com.dontocsata.xmltv.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XmlTvProgram {

	private String channelId;
	private ZonedDateTime start;
	private ZonedDateTime stop;
	private String title;
	private String subTitle;
	private String description;

	private boolean previouslyShown;
	private ZonedDateTime previouslyShownDate;
	// The date the program or film was finished. This will probably be the same
	// as the copyright date.
	private LocalDate date;
	private DDProgramId ddProgramId;
	private XmlTvProgramId xmlTvProgramId;
	private String onScreenProgramId;

	private List<Credit> credits;
	private List<String> categories;
	private List<String> keywords;

	private String videoAspect;
	private String videoQuality;

	private String uid;

	public String getUid() {
		if (ddProgramId != null) {
			return ddProgramId.toString();
		} else if (uid == null) {
			uid = UUID.randomUUID().toString().replace("-", "");
		}
		return uid;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public ZonedDateTime getStart() {
		return start;
	}

	public void setStart(ZonedDateTime start) {
		this.start = start;
	}

	public ZonedDateTime getStop() {
		return stop;
	}

	public void setStop(ZonedDateTime stop) {
		this.stop = stop;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public DDProgramId getDdProgramId() {
		return ddProgramId;
	}

	public void setDdProgramId(DDProgramId ddProgramId) {
		this.ddProgramId = ddProgramId;
	}

	public XmlTvProgramId getXmlTvProgramId() {
		return xmlTvProgramId;
	}

	public void setXmlTvProgramId(XmlTvProgramId xmlTvProgramId) {
		this.xmlTvProgramId = xmlTvProgramId;
	}

	public List<Credit> getCredits() {
		if (credits == null) {
			credits = new ArrayList<>();
		}
		return credits;
	}

	public List<String> getCategories() {
		if (categories == null) {
			categories = new ArrayList<>();
		}
		return categories;
	}

	public boolean isPreviouslyShown() {
		return previouslyShown;
	}

	public void setPreviouslyShown(boolean previouslyShown) {
		this.previouslyShown = previouslyShown;
	}

	public ZonedDateTime getPreviouslyShownDate() {
		return previouslyShownDate;
	}

	public void setPreviouslyShownDate(ZonedDateTime previouslyShownDate) {
		this.previouslyShownDate = previouslyShownDate;
	}

	public String getOnScreenProgramId() {
		return onScreenProgramId;
	}

	public void setOnScreenProgramId(String onScreenProgramId) {
		this.onScreenProgramId = onScreenProgramId;
	}

	public List<String> getKeywords() {
		if (keywords == null) {
			keywords = new ArrayList<>();
		}
		return keywords;
	}

	public String getVideoAspect() {
		return videoAspect;
	}

	public void setVideoAspect(String videoAspect) {
		this.videoAspect = videoAspect;
	}

	public String getVideoQuality() {
		return videoQuality;
	}

	public void setVideoQuality(String videoQuality) {
		this.videoQuality = videoQuality;
	}

	public boolean isHDTV() {
		return videoQuality != null && "HDTV".equals(videoQuality);
	}

	@Override
	public String toString() {
		return "XmlTvProgram [channelId=" + channelId + ", start=" + start + ", stop=" + stop + ", title=" + title
				+ ", subTitle=" + subTitle + ", description=" + description + ", previouslyShown=" + previouslyShown
				+ ", previouslyShownDate=" + previouslyShownDate + ", date=" + date + ", ddProgramId=" + ddProgramId
				+ ", xmlTvProgramId=" + xmlTvProgramId + ", onScreenProgramId=" + onScreenProgramId + ", credits="
				+ credits + ", categories=" + categories + ", keywords=" + keywords + ", videoAspect=" + videoAspect
				+ ", videoQuality=" + videoQuality + "]";
	}

}
