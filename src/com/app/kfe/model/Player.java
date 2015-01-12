package com.app.kfe.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Klasa reprezentująca użytkownika podczas prowadzenia rozgrywki.
 * 
 * @author mrkaczor
 */
public class Player {

	/**
	 * Adres MAC urządzenia tego gracza, służący do jego identyfikacji.
	 */
	private final String _MACAddress;
	/**
	 * Adres IP tego gracza.
	 */
	private final String _IPAddress;
	/**
	 * Nazwa gracza.
	 */
	private String _login;

	public Player(String mac, String ip, String login) {
		_MACAddress = mac;
		_IPAddress = ip;
		_login = login;
	}

	public Player(JSONObject jsonObject) throws JSONException {
		_MACAddress = jsonObject.getString("mac");
		_IPAddress = jsonObject.getString("ip");
		_login = jsonObject.getString("login");
	}

	public String getMACAddress() {
		return _MACAddress;
	}

	public String getIPAddress() {
		return _IPAddress;
	}

	public String getLogin() {
		return _login;
	}

	public boolean equals(Player p) {
		return p!=null && _MACAddress.equals(p._MACAddress) && _login.equals(p._login);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObject= new JSONObject();
    	jsonObject.put("mac", _MACAddress);
    	jsonObject.put("ip", _IPAddress);
		jsonObject.put("login", _login);
	    return jsonObject;
	}

}