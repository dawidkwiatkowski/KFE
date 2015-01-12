package com.app.kfe.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerBasicInfo {

	/**
	 * Nazwa serwera.
	 */
	private String _name;
	/**
	 * Adres IP serwera.
	 */
	private final String _hostIP;
	/**
	 * Adres MAC hosta tego serwera.
	 */
	private final String _hostMAC;
	/**
	 * Ilo�� graczy po��czonych do tego serwera.
	 */
	private int _playersCount;

	public ServerBasicInfo(JSONObject serverInfo) throws JSONException {
		_hostMAC = serverInfo.getString("hostMAC");
		_hostIP = serverInfo.getString("hostIP");
		_name = serverInfo.getString("name");
		_playersCount = serverInfo.getInt("playersCnt");
	}

	public ServerBasicInfo(Server server){
		_hostMAC = server.getHostMAC();
		_hostIP = server.getHostIP();
		_name = server.getName();
		_playersCount = server.getRoom()!=null?server.getRoom().numberOfPlayers():0;
	}

	public ServerBasicInfo(String hostMAC, String hostIP, String name, int players){
		_hostMAC = hostMAC;
		_hostIP = hostIP;
		_name = name;
		_playersCount = players;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getHostIP() {
		return _hostIP;
	}

	public String getHostMAC() {
		return _hostMAC;
	}

	public int getPlayersCount() {
		return _playersCount;
	}

	public void setPlayersCount(int playersCount) {
		if(playersCount>=0) {
			_playersCount = playersCount;
		}
	}

	/**
	 * Zwraca dane serwera w notacji zgodnej z obiektem JSON.
	 * @return Obiekt w notacji JSON z danymi serwera
	 */
	public JSONObject toJSON() throws JSONException {
	    JSONObject jsonObject= new JSONObject();
	    
	    jsonObject.put("name", getName());
	    jsonObject.put("hostMAC", getHostMAC());
	    jsonObject.put("hostIP", getHostIP());
	    jsonObject.put("playersCnt", getPlayersCount());

	    return jsonObject;
	}

	@Override
	public String toString() {
		String info = "{ServerBasicInfo ";
		info += "[name=" + _name + "] ";
		info += "[hostMAC=" + _hostMAC + "] ";
		info += "[hostIP=" + _hostIP + "] ";
		info += "[playersCount=" + _playersCount + "]";
		info += "}";
		return info;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ServerBasicInfo) {
			ServerBasicInfo sbi = (ServerBasicInfo)obj;
			return sbi.getHostMAC().equals(_hostMAC) && sbi.getHostIP().equals(_hostIP);
		}
		return false;
	}
}
