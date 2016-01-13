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

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.dontocsata.xmltv.mxf.SeriesInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

public class XmlTvDatabase {

	private JdbcConnectionSource conn;
	private Dao<SeriesInfo, String> seriesDao;

	public XmlTvDatabase(File dbFile) throws SQLException {
		String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
		conn = new JdbcConnectionSource(jdbcUrl);
		DataPersisterManager.registerDataPersisters(new XmlGregorianCalendarPersister());
		seriesDao = createDao(seriesDbTable());
	}

	private enum DatabaseFieldFlag {
		ID,
		UNIQUE,
		NON_NULL,
		INDEXED;
	}

	private <T, ID> Dao<T, ID> createDao(DatabaseTableConfig<T> config) throws SQLException {
		Dao<T, ID> dao = DaoManager.createDao(conn, config);
		dao.setObjectCache(true);
		TableUtils.createTableIfNotExists(conn, config);
		return dao;
	}

	private DatabaseTableConfig<SeriesInfo> seriesDbTable() {
		List<DatabaseFieldConfig> fields = new ArrayList<>();
		// fields.add(create("identifier", DataType.STRING, EnumSet.of(DatabaseFieldFlag.ID)));
		fields.add(create("uid", DataType.STRING, EnumSet.of(DatabaseFieldFlag.ID)));
		fields.add(create("id", DataType.STRING, EnumSet.of(DatabaseFieldFlag.UNIQUE, DatabaseFieldFlag.NON_NULL)));
		fields.add(create("title", DataType.STRING));
		fields.add(create("shortTitle", DataType.STRING));
		fields.add(create("description", DataType.STRING));
		fields.add(create("shortDescription", DataType.STRING));
		fields.add(create("startAirdate", DataType.STRING));
		fields.add(create("endAirdate", DataType.STRING));
		// TODO guide image
		// fields.add(create("guideImage", DataType.STRING));
		return new DatabaseTableConfig<>(SeriesInfo.class, fields);
	}

	private DatabaseFieldConfig create(String name, DataType dataType) {
		return create(name, dataType, EnumSet.noneOf(DatabaseFieldFlag.class));
	}

	private DatabaseFieldConfig create(String name, DataType dataType, EnumSet<DatabaseFieldFlag> flags) {
		DatabaseFieldConfig field = new DatabaseFieldConfig(name);
		if (flags.contains(DatabaseFieldFlag.ID)) {
			field.setId(true);
			field.setCanBeNull(false);
		}
		if (flags.contains(DatabaseFieldFlag.UNIQUE)) {
			field.setUnique(true);
		}
		if (flags.contains(DatabaseFieldFlag.NON_NULL)) {
			field.setCanBeNull(false);
		} else {
			field.setCanBeNull(true);
		}
		if (flags.contains(DatabaseFieldFlag.INDEXED)) {
			field.setIndex(true);
		}
		return field;
	}

	public void save(SeriesInfo si) throws SQLException {
		seriesDao.createOrUpdate(si);
	}

	public SeriesInfo getSeries(String uid) throws SQLException {
		return seriesDao.queryForId(uid);
	}

	public Collection<SeriesInfo> getAllSeries() throws SQLException {
		return seriesDao.queryForAll();
	}
}
