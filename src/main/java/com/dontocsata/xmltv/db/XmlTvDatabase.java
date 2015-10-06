package com.dontocsata.xmltv.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

import com.dontocsata.xmltv.model.Channel;
import com.dontocsata.xmltv.model.Program;

public class XmlTvDatabase {

	private Connection conn;

	public XmlTvDatabase(File dbFile) throws SQLException {
		conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(
					"create table if not exists channel(id text primary key, displayNames text, icon text, url text)");
			stmt.executeUpdate(
					"create table if not exists program (channelId text, title text, subTitle text, start text, stop text, description text, previouslyShown boolean, previouslyShownDate text, date text, ddProgramId text, xmlTvProgramId text, onScreenProgramId text, credits text, categories text, keywords text)");
		}
	}

	public void write(Channel channel) throws SQLException {
		try (PreparedStatement stmt = conn
				.prepareStatement("insert or replace into channel (id, displayNames, icon, url) VALUES (?,?,?,?)")) {
			stmt.setString(1, channel.getId());
			stmt.setString(2, channel.getDisplayNames().toString());
			stmt.setString(3, channel.getIcon());
			stmt.setString(4, channel.getUrl());
			stmt.executeUpdate();
		}
	}

	public void write(Program... programs) throws SQLException {
		write(Arrays.asList(programs));
	}

	public void write(Collection<Program> programs) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(
				"insert into program (channelId, title, subTitle, start, stop, description, previouslyShown, previouslyShownDate, date, ddProgramId, xmlTvProgramId, onScreenProgramId, credits, categories, keywords) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
			for (Program program : programs) {
				stmt.setString(1, program.getChannelId());
				stmt.setString(2, program.getTitle());
				stmt.setString(3, program.getSubTitle());
				stmt.setString(4, toString(program.getStart()));
				stmt.setString(5, toString(program.getStop()));
				stmt.setString(6, program.getDescription());
				stmt.setBoolean(7, program.isPreviouslyShown());
				stmt.setString(8, toString(program.getPreviouslyShownDate()));
				stmt.setString(9, toString(program.getDate()));
				stmt.setString(10, toString(program.getDdProgramId()));
				stmt.setString(11, toString(program.getXmlTvProgramId()));
				stmt.setString(12, toString(program.getOnScreenProgramId()));
				stmt.setString(13, program.getCredits().toString());
				stmt.setString(14, program.getCategories().toString());
				stmt.setString(15, program.getKeywords().toString());
				stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}

	public void createIndex() throws SQLException {
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("create index if not exists channelId_idx on program (channelId)");
			stmt.executeUpdate("create index if not exists title_idx on program (title)");
			stmt.executeUpdate("create index if not exists subTitle_idx on program (subTitle)");
			stmt.executeUpdate("create index if not exists start_idx on program (start)");
		}
	}

	private String toString(Object o) {
		return o == null ? null : o.toString();
	}

	public void close() throws SQLException {
		conn.close();
	}
}
