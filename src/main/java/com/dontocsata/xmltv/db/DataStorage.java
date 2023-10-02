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
 package com.dontocsata.xmltv.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.mapdb.DB;

import com.dontocsata.xmltv.XmlTVDataSorage;
import com.dontocsata.xmltv.XmlTvParser.ProgramPair;
import com.dontocsata.xmltv.model.XmlTvChannel;
import com.dontocsata.xmltv.model.XmlTvProgram;
import com.dontocsata.xmltv.mxf.Program;
import com.dontocsata.xmltv.mxf.Season;
import com.dontocsata.xmltv.mxf.SeriesInfo;
import com.dontocsata.xmltv.mxf.Service;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;;

public class DataStorage implements XmlTVDataSorage {

	private Map<String, XmlTvChannel> channels;
	private Collection<XmlTvProgram> xmlTvPrograms;
	private Map<String, Service> services;
	private Map<String, SeriesInfo> series;
	private Map<String, Season> seasons;
	private Map<String, Program> idProgramMap;
	// XmlTvChannel ID=>Program & XmlTvProgram
	private Multimap<String, ProgramPair> channelProgramMap;

	public DataStorage() {
		channels = new HashMap<>();
		xmlTvPrograms = new ArrayList<>();
		services = new HashMap<>();
		series = new HashMap<>();
		seasons = new HashMap<>();
		idProgramMap = new HashMap<>();
		channelProgramMap = Multimaps.newSortedSetMultimap(new HashMap<>(), TreeSet::new);
	}

	public DataStorage(DB db) {
		channels = db.createHashMap("channels").makeOrGet();
		xmlTvPrograms = db.createHashSet("xmlTvPrograms").makeOrGet();
		services = db.createHashMap("services").makeOrGet();
		series = db.createHashMap("series").makeOrGet();
		seasons = db.createHashMap("seasons").makeOrGet();
		idProgramMap = db.createHashMap("idProgramMap").makeOrGet();
		channelProgramMap = Multimaps.newSortedSetMultimap(db.createHashMap("channelProgramMap").makeOrGet(),
				TreeSet::new);
	}

	public void save(Service service) {
		services.put(service.getUid(), service);
	}

	public Service getService(String uid) {
		return services.get(uid);
	}

	public Collection<Service> getServices() {
		return services.values();
	}

	public void save(SeriesInfo series) {
		this.series.put(series.getUid(), series);
	}

	public SeriesInfo getSeries(String uid) {
		return series.get(uid);
	}

	public Collection<SeriesInfo> getSeries() {
		return series.values();
	}

	public void save(Season season) {
		seasons.put(season.getUid(), season);
	}

	public Season getSeason(String uid) {
		return seasons.get(uid);
	}

	public Collection<Season> getSeasons() {
		return seasons.values();
	}

	public void save(Program program) {
		idProgramMap.put(program.getUid(), program);
	}

	public Program getProgram(String uid) {
		return idProgramMap.get(uid);
	}

	public Collection<Program> getPrograms() {
		return idProgramMap.values();
	}

	public void addProgramPair(String xmlTvChannelId, ProgramPair program) {
		channelProgramMap.put(xmlTvChannelId, program);
	}

	public Collection<ProgramPair> getPrograms(String xmlTvChannelId) {
		return channelProgramMap.get(xmlTvChannelId);
	}

	public int getNumberOfPrograms() {
		return channelProgramMap.size();
	}

	public void save(XmlTvChannel channel) {
		channels.put(channel.getId(), channel);
	}

	public XmlTvChannel getChannel(String id) {
		return channels.get(id);
	}

	public Collection<XmlTvChannel> getChannels() {
		return channels.values();
	}

	public void save(XmlTvProgram program) {
		xmlTvPrograms.add(program);
	}

	public Collection<XmlTvProgram> getXmlTvPrograms() {
		return xmlTvPrograms;
	}
}
