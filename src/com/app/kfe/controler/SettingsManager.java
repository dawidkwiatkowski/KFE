package com.app.kfe.controler;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.app.kfe.controler.communication.BroadcastManager;
import com.app.kfe.main.KFE;
import com.app.kfe.utils.Logger;

public class SettingsManager {

	public static final String SETTING_PLAYERNAME = "setting_username";

	private static SettingsManager _instance;
	private SharedPreferences _preferences;

	public static SettingsManager getInstance() {
		if(_instance == null) {
			 synchronized (BroadcastManager.class) {
	               if (_instance == null)
	            	   _instance = new SettingsManager();
			 }
		}
		return _instance;
	}
	
	private SettingsManager()
	{
		_preferences = PreferenceManager.getDefaultSharedPreferences(KFE.getContext());
	}
	
	public void setSetting(String key, String value)
	{
		Editor editor=_preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getSettingValue(String settingId)
	{
		String settingValue = _preferences.getString(settingId, null);
		if(settingValue==null) {
			String settings = "";
			for(String key : _preferences.getAll().keySet()) {
				settings += key + ", ";
			}
			Logger.error("SettingsManager", "Ther's no setting value for key: " + settingId + "\nAVAILABLE SETTINGS: " + settings);
			//TODO Handle not-existing settings (exception)
		}
		return settingValue;
	}

}
