package com.dontocsata.xmltv;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dontocsata.xmltv.db.DataStorage;
import com.dontocsata.xmltv.model.AudioType;
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
import com.dontocsata.xmltv.mxf.MxfValidator;
import com.dontocsata.xmltv.mxf.Program;
import com.dontocsata.xmltv.mxf.ScheduleEntry;
import com.dontocsata.xmltv.mxf.Season;
import com.dontocsata.xmltv.mxf.SeriesInfo;
import com.dontocsata.xmltv.mxf.Service;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class XmlTvParser {

	private static final Logger log = LoggerFactory.getLogger(XmlTvParser.class);

	public static void main(String[] args) throws Exception {
		log.info("Starting with args={}", Arrays.deepToString(args));
		ArgumentParser argParse = ArgumentParsers.newArgumentParser("XMLTVtoMXF", true)
				.description("This converts an XMLTV file to the Microsoft Windows Media Center MXF XML format.");
		// argParse.addArgument("--db").nargs(1).help("Write the channel and program data to a SQLite database. This
		// will overwrite the file");
		argParse.addArgument("file").nargs(1).action(new ArgumentAction() {

			@Override
			public void run(ArgumentParser parser, Argument arg, Map<String, Object> attrs, String flag, Object value)
					throws ArgumentParserException {
				@SuppressWarnings("unchecked")
				List<String> vals = (List<String>) value;
				File f = new File(vals.get(0));
				if (!f.exists()) {
					throw new ArgumentParserException(new FileNotFoundException(value.toString()), parser);
				}
				attrs.put(arg.getDest(), f);
			}

			@Override
			public void onAttach(Argument arg) {

			}

			@Override
			public boolean consumeArgument() {
				return true;
			}
		}).help("The XMLTV file to parse");
		argParse.addArgument("-o", "--output").setDefault("mxf.xml").help("The MXF file output location");
		argParse.addArgument("--debug").help("Run in debug most which produces detailed logs")
		.action(new ArgumentAction() {

			@Override
			public void run(ArgumentParser parser, Argument arg, Map<String, Object> attrs, String flag,
					Object value) throws ArgumentParserException {
				((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
				.setLevel(ch.qos.logback.classic.Level.TRACE);
			}

			@Override
			public void onAttach(Argument arg) {

			}

			@Override
			public boolean consumeArgument() {
				return false;
			}
		});
		// argParse.addArgument("--low-memory").dest("lowMemory").action(Arguments.storeTrue()).setDefault(Boolean.FALSE).help("Run
		// in low memory mode");
		Namespace ns = null;
		try {
			ns = argParse.parseArgs(args);
		} catch (ArgumentParserException e) {
			argParse.handleError(e);
			System.exit(1);
		}
		DatatypeFactory dtf = DatatypeFactory.newInstance();
		File mxfOutput = new File(ns.getString("output"));
		File xmlTvFile = ns.get("file");
		log.info("XMLTV={}, MXF={}", xmlTvFile, mxfOutput);
		ProgressInputStream progressStream = new ProgressInputStream(xmlTvFile);
		InputStream xmlTvStream = new BufferedInputStream(progressStream);
		DataStorage storage = new DataStorage();
		XmlTv xmlTv = new XmlTv(xmlTvStream, storage);
		// if (ns.get("db") != null) {
		// xmlTv = new XmlTv(xmlTvStream, new File(ns.getString("db")));
		// } else if (ns.getBoolean("lowMemory")) {
		// xmlTv = new XmlTv(true, xmlTvStream);
		// } else {
		// xmlTv = new XmlTv(xmlTvStream);
		// }

		DecimalFormat df = new DecimalFormat("0.00");
		String text = "Reading XMLTV file: " + xmlTvFile + "...";
		doInBackground(progressStream, () -> {
			try {
				xmlTv.parse();
			} catch (Exception e) {
				log.error("Error parsing", e);
				System.err.println("Error parsing XMLTV: " + e.getMessage());
				System.exit(1);
			}
		} , d -> printUpdateProcess(text + df.format(d) + "%"));
		printUpdateProcess(text + "100.00%");
		progressStream.close();

		printProgress("Creating services");
		// Convert XmlTvChannel to Service
		// XmlTvChannel ID=>Service
		log.info("{} channels", storage.getChannels().size());
		for (XmlTvChannel c : storage.getChannels()) {
			Service service = new Service();
			service.setId("s" + c.getId());
			service.setUid(UidGen.service(c));
			// Need better name
			service.setName(c.getDisplayNames().get(0));
			// call sign
			for (String name : c.getDisplayNames()) {
				String split[] = name.split(" ");
				if (split[0].matches("\\d+") && split.length > 1) {
					service.setCallSign(split[1]);
				}
			}
			// TODO affiliates
			log.debug("Created service: {}=>{}", c.getId(), service.getId());
			storage.save(service);
		}

		// Create the Lineup
		printProgress("Creating lineup", true);
		Lineup lineup = new Lineup();
		lineup.setChannels(new Channels());
		lineup.setId("l1");
		lineup.setName("MainLineup");
		lineup.setUid("!Lineup!" + lineup.getId());
		lineup.setPrimaryProvider("!MCLineup!MainLineup");
		// Convert each channel to MXF format
		for (XmlTvChannel c : storage.getChannels()) {
			Channel channel = new Channel();
			channel.setLineup(lineup.getId());
			channel.setService(storage.getService(UidGen.service(c)).getId());
			channel.setNumber(Integer.toString(c.getChannelNumber()));
			if (c.getSubChannelNumber() >= 0) {
				channel.setSubNumber(Integer.toString(c.getSubChannelNumber()));
			}
			int sub = c.getSubChannelNumber() > 0 ? c.getSubChannelNumber() : 0;
			channel.setUid("!Channel!" + lineup.getName() + "!" + channel.getNumber() + "_" + sub);
			lineup.getChannels().getChannel().add(channel);
		}

		MxfGenerator generator = new MxfGenerator();

		printProgress("Generating basic MXF", true);
		MXF mxf = generator.createBasicMXF();
		MXF.With with = mxf.getWith().get(0);
		MXF.With.Lineups lineups = new MXF.With.Lineups();
		lineups.getLineup().add(lineup);

		MXF.With.Services mxfServices = new MXF.With.Services();
		mxfServices.getService().addAll(storage.getServices());

		printProgress("Beginning program parsing", true);
		System.out.println();
		int count = 0;
		// Create Series & Programs
		int seriesIdSequence = 1;
		int seasonIdSequence = 1;
		int programIdSequence = 1;
		MXF.With.Programs withPrograms = new MXF.With.Programs();
		// XmlTvProgram UID=>Program
		int interval = storage.getXmlTvPrograms().size() / 100;
		for (XmlTvProgram p : storage.getXmlTvPrograms()) {
			if (++count % interval == 0) {
				printUpdateProcess("Parsed " + count + "/" + storage.getXmlTvPrograms().size() + " programs");
			}
			Program prog = storage.getProgram(p.getUid());
			if (prog == null) {
				SeriesInfo si = null;
				Season season = null;
				Integer episodeNumber = null;
				if (p.getDdProgramId() != null) {
					DDProgramId progId = p.getDdProgramId();
					if (progId.getType() == DDProgramIdType.EPISODE) {
						String seriesId = progId.getSeriesId();
						String uid = "!Series!" + seriesId;
						if ((si = storage.getSeries(uid)) == null) {
							si = new SeriesInfo();
							si.setId("si" + seriesIdSequence++);
							si.setUid(uid);
							si.setTitle(p.getTitle());
							si.setShortTitle(p.getTitle());
							si.setDescription(p.getTitle());
							si.setShortDescription(p.getTitle());
							log.debug("Created series: {}, id={}", si.getTitle(), si.getId());
							storage.save(si);
						}
						if (p.getXmlTvProgramId() != null) {
							XmlTvProgramId xProdId = p.getXmlTvProgramId();
							episodeNumber = xProdId.getEpisode();
							if (xProdId.getSeason() != null) {
								String seasonUid = "!Season!" + progId.getSeriesId() + "_" + xProdId.getSeason();
								if ((season = storage.getSeason(seasonUid)) == null) {
									season = new Season();
									season.setId("sn" + seasonIdSequence++);
									season.setSeries(si);
									season.setUid(seasonUid);
									season.setTitle(si.getTitle() + " Season " + xProdId.getSeason());
									log.debug("Created season: {}, id={}, seriesId={}", season.getTitle(),
											season.getId(), si.getId());
									storage.save(season);
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
				storage.save(prog);
				log.debug("Unique program: {}=>{}", p.getUid(), prog.getId());
			} else {
				log.debug("Excountered non-unique program: {} ({}), index={}", prog.getTitle(), p.getUid(), count);
			}
			ProgramPair pp = new ProgramPair(prog, p);
			storage.addProgramPair(p.getChannelId(), pp);
		}
		int size = storage.getXmlTvPrograms().size();
		printUpdateProcess("Parsed " + size + "/" + size + " programs");

		MXF.With.SeriesInfos seriesInfos = new MXF.With.SeriesInfos();
		seriesInfos.getSeriesInfo().addAll(storage.getSeries());
		MXF.With.Seasons mxfSeasons = new MXF.With.Seasons();
		mxfSeasons.getSeason().addAll(storage.getSeasons());

		with.setKeywords(new MXF.With.Keywords());
		with.setKeywordGroups(new MXF.With.KeywordGroups());
		with.setGuideImages(new MXF.With.GuideImages());
		with.setPeople(new MXF.With.People());
		with.setSeriesInfos(seriesInfos);
		with.setSeasons(mxfSeasons);
		with.setPrograms(withPrograms);
		with.setAffiliates(new MXF.With.Affiliates());
		with.setServices(mxfServices);

		printProgress("Scheduling programs", true);
		System.out.println();
		count = 0;
		for (XmlTvChannel c : storage.getChannels()) {
			printUpdateProcess("Scheduling " + c.getDisplayNames().get(0) + " (" + ++count + "/"
					+ storage.getChannels().size() + ")");
			Collection<ProgramPair> programs = storage.getPrograms(c.getId());
			ScheduleEntries entries = new ScheduleEntries();
			entries.setService(storage.getService(UidGen.service(c)));
			boolean first = true;
			log.info("Scheduling {} with {} programs", c.getId(), programs.size());
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
				if (pp.xmlTvProgram.isPreviouslyShown()) {
					entry.setIsRepeat(true);
				}
				if (pp.xmlTvProgram.isHDTV()) {
					entry.setIsHdtv(true);
				}
				if (pp.xmlTvProgram.getAudio() != null) {
					entry.setAudioFormat(BigInteger.valueOf(pp.xmlTvProgram.getAudio().getMxfType()));
					if (pp.xmlTvProgram.getAudio() == AudioType.STEREO) {
						entry.setIsStereo(true);
					}
				}
				if (pp.xmlTvProgram.isPremiere()) {
					entry.setIsPremiere(true);
				} else if (pp.xmlTvProgram.isFinale()) {
					entry.setIsFinale(true);
				}

				entries.getScheduleEntry().add(entry);
			}
			with.getScheduleEntries().add(entries);
		}
		with.setLineups(lineups);

		printProgress("Writing MXF file");
		JAXBContext jaxb = generator.getJaxbContext();
		Marshaller marshaller = jaxb.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		// StringWriter sw = new StringWriter();
		try (BufferedWriter out = new BufferedWriter(new FileWriter(mxfOutput))) {
			marshaller.marshal(mxf, out);
		} catch (IOException ex) {
			log.error("Error writing MXF", ex);
			System.err.println("Error writing XMF: " + ex.getMessage());
			System.exit(1);
		}
		System.out.println();
		progressStream = new ProgressInputStream(mxfOutput);
		InputStream mxfStream = new BufferedInputStream(progressStream);

		doInBackground(progressStream, () -> {
			try {
				new MxfValidator().validate(new StreamSource(mxfStream));
			} catch (Exception e) {
				log.error("Error validating MXF", e);
				System.err.println("Error in MXF: " + e.getMessage());
				System.exit(1);
			}
		} , d -> printUpdateProcess("Validating MXF..." + df.format(d) + "%"));
		printProgress("MXF is valid!");
		printProgress(storage.getNumberOfPrograms() + " unique programs");
		System.out.println();
	}

	private static int previousLength = -1;

	private static void printUpdateProcess(String text) {
		log.info(text);
		System.out.print(text);
		if (text.length() < previousLength) {
			for (int i = text.length(); i < previousLength; i++) {
				System.out.print(' ');
			}
		}
		previousLength = text.length();
		System.out.print('\r');
	}

	private static void printProgress(String text) {
		printProgress(text, true);
	}

	private static void printProgress(String text, boolean newLine) {
		if (newLine) {
			System.out.println();
		}
		System.out.print(text);
		log.info(text);
		previousLength = -1;
	}

	public static void doInBackground(ProgressInputStream progressStream, Runnable task,
			Consumer<Double> progressCallback) {
		Thread t = new Thread(task);
		t.start();
		long size = progressStream.getFileSize();
		while (t.isAlive()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
			long current = progressStream.getCurrentPosition();
			double progress = current / (double) size * 100;
			progressCallback.accept(progress);
		}
		progressCallback.accept(100.0);
	}

	public static class ProgramPair implements Comparable<ProgramPair>, Serializable {

		private static final long serialVersionUID = 5070712929071882294L;

		private Program program;
		private XmlTvProgram xmlTvProgram;

		public ProgramPair(Program program, XmlTvProgram xmlTvProgram) {
			this.program = Preconditions.checkNotNull(program);
			this.xmlTvProgram = Preconditions.checkNotNull(xmlTvProgram);
		}

		@Override
		public int compareTo(ProgramPair o) {
			return ComparisonChain.start().compare(xmlTvProgram.getStart(), o.xmlTvProgram.getStart())
					.compare(program.getUid(), o.program.getUid()).result();
		}

	}

	public static class UidGen {

		public static String service(XmlTvChannel channel) {
			return "!Service!" + channel.getId();
		}
	}
}
