package com.dontocsata.xmltv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.dontocsata.xmltv.model.DDProgramId;
import com.dontocsata.xmltv.model.DDProgramIdType;
import com.dontocsata.xmltv.model.XmlTvChannel;
import com.dontocsata.xmltv.model.XmlTvProgram;
import com.dontocsata.xmltv.model.XmlTvProgramId;
import com.dontocsata.xmltv.mxf.Channel;
import com.dontocsata.xmltv.mxf.Lineup;
import com.dontocsata.xmltv.mxf.Lineup.Channels;
import com.dontocsata.xmltv.mxf.MXF;
import com.dontocsata.xmltv.mxf.MXF.With.ScheduleEntries;
import com.dontocsata.xmltv.mxf.MxfGenerator;
import com.dontocsata.xmltv.mxf.Program;
import com.dontocsata.xmltv.mxf.ScheduleEntry;
import com.dontocsata.xmltv.mxf.Season;
import com.dontocsata.xmltv.mxf.SeriesInfo;
import com.dontocsata.xmltv.mxf.Service;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class XmlTvParser {

	public static void main(String[] args) throws Exception {

		DatatypeFactory dtf = DatatypeFactory.newInstance();

		// File xmlTvFile = new File("test_xmltv.xml");
		File xmlTvFile = new File("/Users/ray.douglass/Downloads/xmltv_2015_10_04.xml");
		XmlTv xmlTv = new XmlTv(xmlTvFile).parse();

		System.out.println("Creating services");
		// Convert XmlTvChannel to Service
		// XmlTvChannel ID=>Service
		Map<String, Service> services = new TreeMap<>();
		for (XmlTvChannel c : xmlTv.getChannels().values()) {
			Service service = new Service();
			service.setId("s" + (services.size() + 1));
			service.setUid("!Service!" + c.getId());
			// Need better name
			service.setName(c.getDisplayNames().get(0));
			// call sign
			// affiliates
			services.put(c.getId(), service);
		}
		System.out.println("Creatring lineup");
		Lineup lineup = new Lineup();
		lineup.setChannels(new Channels());
		lineup.setId("l1");
		lineup.setUid("!Lineup!" + lineup.getId());
		lineup.setPrimaryProvider("!MCLineup!MainLineup");
		for (XmlTvChannel c : xmlTv.getChannels().values()) {
			Channel channel = new Channel();
			channel.setLineup(lineup.getId());
			channel.setService(services.get(c.getId()).getId());
			channel.setNumber(Integer.toString(c.getChannelNumber()));
			channel.setUid("!Channel!" + lineup.getId() + "!" + channel.getNumber());
			lineup.getChannels().getChannel().add(channel);
		}

		MxfGenerator generator = new MxfGenerator();

		System.out.println("Generating basic MXF");
		MXF mxf = generator.createBasicMXF();
		MXF.With with = mxf.getWith().get(0);
		MXF.With.Lineups lineups = new MXF.With.Lineups();
		lineups.getLineup().add(lineup);
		with.getKeywordsOrKeywordGroupsOrGuideImages().add(lineups);

		MXF.With.Services mxfServices = new MXF.With.Services();
		mxfServices.getService().addAll(services.values());
		with.getKeywordsOrKeywordGroupsOrGuideImages().add(mxfServices);

		System.out.println("Beginning program parsing");
		int count = 0;
		// Create Series & Programs
		int seriesIdSequence = 1;
		int seasonIdSequence = 1;
		int programIdSequence = 1;
		Map<String, SeriesInfo> series = new TreeMap<>();
		Map<String, Season> seasons = new TreeMap<>();
		MXF.With.Programs withPrograms = new MXF.With.Programs();
		Map<String, Program> idProgramMap = new HashMap<>();
		Multimap<String, ProgramPair> channelProgramMap = Multimaps
				.newSortedSetMultimap(new TreeMap<String, Collection<ProgramPair>>(), () -> new TreeSet<ProgramPair>(
						(o1, o2) -> o1.xmlTvProgram.getStart().compareTo(o2.xmlTvProgram.getStart())));
		for (XmlTvProgram p : xmlTv.getPrograms()) {
			if (++count % 25000 == 0) {
				System.out.println("Completed " + count + " programs");
			}
			Program prog = idProgramMap.get(p.getUid());
			if (prog == null) {
				SeriesInfo si = null;
				Season season = null;
				Integer episodeNumber = null;
				if (p.getDdProgramId() != null) {
					DDProgramId progId = p.getDdProgramId();
					if (progId.getType() == DDProgramIdType.EPISODE) {
						String seriesId = progId.getSeriesId();
						if ((si = series.get(seriesId)) == null) {
							si = new SeriesInfo();
							si.setId("si" + seriesIdSequence++);
							si.setUid("!Series!" + si.getId());
							si.setTitle(p.getTitle());
							series.put(seriesId, si);
						}
						if (p.getXmlTvProgramId() != null) {
							XmlTvProgramId xProdId = p.getXmlTvProgramId();
							episodeNumber = xProdId.getEpisode();
							if (xProdId.getSeason() != null) {
								String mapId = progId.getSeriesId() + "_" + xProdId.getSeason();
								if ((season = seasons.get(mapId)) == null) {
									season = new Season();
									season.setId("sn" + seasonIdSequence++);
									season.setSeries(si);
									season.setUid("!Season!" + season.getId());
									season.setTitle(si.getTitle() + " Season " + xProdId.getSeason());
									seasons.put(mapId, season);
								}
							}
						}
					}
				}
				prog = new Program();
				prog.setId(BigInteger.valueOf(programIdSequence++));
				prog.setUid("!Program!" + p.getUid());
				prog.setTitle(p.getTitle());
				if (si != null) {
					prog.setIsSeries(true);
					prog.setSeries(si);
					if (season != null) {
						prog.setSeason(season);
					}
					if (episodeNumber != null) {
						prog.setEpisodeNumber(BigInteger.valueOf(episodeNumber));
					}
				}
				prog.setDescription(p.getDescription());
				prog.setEpisodeTitle(p.getSubTitle());
				if (p.isPreviouslyShown()) {
					XMLGregorianCalendar cal = null;
					if (p.getPreviouslyShownDate() != null) {
						cal = dtf.newXMLGregorianCalendar(GregorianCalendar.from(p.getPreviouslyShownDate()));
					} else {
						cal = dtf.newXMLGregorianCalendar(
								GregorianCalendar.from(ZonedDateTime.parse("1970-01-01T00:00:00Z")));
					}
					prog.setOriginalAirdate(cal);
				}

				withPrograms.getProgram().add(prog);
				idProgramMap.put(p.getUid(), prog);
			}
			ProgramPair pp = new ProgramPair(prog, p);
			channelProgramMap.put(p.getChannelId(), pp);
		}
		MXF.With.SeriesInfos seriesInfos = new MXF.With.SeriesInfos();
		seriesInfos.getSeriesInfo().addAll(series.values());
		with.getKeywordsOrKeywordGroupsOrGuideImages().add(seriesInfos);
		MXF.With.Seasons mxfSeasons = new MXF.With.Seasons();
		mxfSeasons.getSeason().addAll(seasons.values());
		with.getKeywordsOrKeywordGroupsOrGuideImages().add(mxfSeasons);

		with.getKeywordsOrKeywordGroupsOrGuideImages().add(withPrograms);

		System.out.println("Scheduling programs");
		count = 0;
		for (XmlTvChannel c : xmlTv.getChannels().values()) {
			System.out.println("Scheduling " + c.getId() + " (" + ++count + "/" + xmlTv.getChannels().size() + ")");
			String key = c.getId();
			Collection<ProgramPair> programs = channelProgramMap.get(key);
			ScheduleEntries entries = new ScheduleEntries();
			entries.setService(services.get(key));
			boolean first = true;
			for (ProgramPair pp : programs) {
				ScheduleEntry entry = new ScheduleEntry();
				entry.setProgram(pp.program.getId().toString());
				long duration = Duration.between(pp.xmlTvProgram.getStart(), pp.xmlTvProgram.getStop()).getSeconds();
				entry.setDuration(BigInteger.valueOf(duration));
				if (first) {
					entry.setStartTime(dtf.newXMLGregorianCalendar(
							GregorianCalendar.from(pp.xmlTvProgram.getStart().withZoneSameInstant(ZoneId.of("UTC")))));
					first = false;
				}
				entries.getScheduleEntry().add(entry);
			}
			with.getKeywordsOrKeywordGroupsOrGuideImages().add(entries);
		}

		JAXBContext jaxb = generator.getJaxbContext();
		Marshaller marshaller = jaxb.createMarshaller();
		// StringWriter sw = new StringWriter();
		try (BufferedWriter out = new BufferedWriter(new FileWriter("mxf.xml"))) {
			marshaller.marshal(mxf, out);
		}
		// SAXBuilder sb = new SAXBuilder();
		// Document document = sb.build(new StringReader(sw.toString()));
		//
		// XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
		// xmlOut.output(document, System.out);

		// new MxfValidator().validate(document);
	}

	private static class ProgramPair {
		private Program program;
		private XmlTvProgram xmlTvProgram;

		public ProgramPair(Program program, XmlTvProgram xmlTvProgram) {
			this.program = program;
			this.xmlTvProgram = xmlTvProgram;
		}

	}
}
