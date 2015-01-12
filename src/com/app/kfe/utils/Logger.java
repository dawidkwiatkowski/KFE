package com.app.kfe.utils;

import android.util.Log;
import com.app.kfe.R;
import com.app.kfe.main.KFE;


/**
 * Klasa s�u��ca do wypisywania log�w na konsol�.
 * 
 * @author Adam Szeremeta
 */
public class Logger {

	private static final boolean LOG_ENABLED = true;

	private static String getCommonTag() {
		return KFE.getContext().getResources().getString(R.string.app_name);
	}

	public static void debug(String message) {
		if(LOG_ENABLED) Log.d(getCommonTag(), message);
	}

	public static void debug(String tag, String message) {
		if(LOG_ENABLED) Log.d(tag, message);
	}

	public static void error(String message) {
		if(LOG_ENABLED) Log.e(getCommonTag(), message);
	}

	public static void error(String tag, String message) {
		if(LOG_ENABLED) Log.e(tag, message);
	}

	public static void info(String message) {
		if(LOG_ENABLED) Log.i(getCommonTag(), message);
	}

	public static void info(String tag, String message) {
		if(LOG_ENABLED) Log.i(tag, message);
	}

	public static void trace(String message) {
		if(LOG_ENABLED) Log.v(getCommonTag(), message);
	}

	public static void trace(String tag, String message) {
		if(LOG_ENABLED) Log.v(tag, message);
	}

	public static void warning(String message) {
		if(LOG_ENABLED) Log.w(getCommonTag(), message);
	}

	public static void warning(String tag, String message) {
		if(LOG_ENABLED) Log.w(tag, message);
	}

}