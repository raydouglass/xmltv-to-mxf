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

import java.sql.SQLException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

public class XmlGregorianCalendarPersister extends BaseDataType {

	private static DatatypeFactory df;

	static {
		try {
			df = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	protected XmlGregorianCalendarPersister() {
		super(SqlType.STRING, new Class<?>[] { XMLGregorianCalendar.class });
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		XMLGregorianCalendar cal = (XMLGregorianCalendar) javaObject;
		return javaObject == null ? null : cal.toXMLFormat();
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
		if (sqlArg != null) {
			return df.newXMLGregorianCalendar(sqlArg.toString());
		}
		return null;
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
		if (defaultStr != null) {
			return df.newXMLGregorianCalendar(defaultStr);
		}
		return null;
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		String cal = results.getString(columnPos);
		if (cal != null) {
			return df.newXMLGregorianCalendar(cal);
		}
		return null;
	}

}
