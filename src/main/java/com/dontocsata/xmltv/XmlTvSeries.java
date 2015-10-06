package com.dontocsata.xmltv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class XmlTvSeries {

	private static AtomicInteger seriesId = new AtomicInteger(1);
	private static AtomicInteger seasonId = new AtomicInteger(1);

	public static class XmlTvSeason {
		public String id = "sn" + seasonId.getAndIncrement();
		public int number;
	}

	public String id = "s" + seriesId.getAndIncrement();
	public String name;
	public List<XmlTvSeason> seasons = new ArrayList<>();
}
