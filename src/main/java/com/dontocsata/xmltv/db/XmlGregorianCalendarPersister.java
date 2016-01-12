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
