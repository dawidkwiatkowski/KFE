package com.app.kfe.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Utils {

	private static DateFormat _commonDateFormat;

	static {
		_commonDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	}

	public static String dateToString(Date date) {
		return _commonDateFormat.format(date);
	}

	public static Date dateFromString(String date) {
		try {
			return _commonDateFormat.parse(date);
		} catch (ParseException e) {
			Logger.error("Utils", "Can not parse date from given string: "+date);
			return null;
		}
	}

}
