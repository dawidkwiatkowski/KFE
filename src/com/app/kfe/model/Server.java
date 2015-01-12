package com.app.kfe.model;

import naga.NIOService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Klasa reprezentująca obiekt serwera, na którym może być prowadzona rozgrywka.
 * 
 * @author Adam Szeremeta
 */
public class Server {

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
	 * Serwis, na którym postawiony jest serwer.
	 */
	private NIOService _service;
	/**
	 * Pokój rozgrywki utworzony na tym serwerze.
	 */
	private Room _room;
	
	public Server(String hostMAC, String hostIP, String name){
		_hostMAC = hostMAC;
		_hostIP = hostIP;
		_name = name;
		_room = null;
	}

	/**
	 * Tworzy nową instancję serwera na podstawie obiektu JSON.
	 * @param serverData obiekt JSON zawierający dane o serwerze
	 * @throws org.json.JSONException gdy podany obiekt nie zawiera prawidłowych danych o serwerze
	 */
	public Server(JSONObject serverData) throws JSONException {
		_name = serverData.getString("name");
		_hostIP = serverData.getString("host_ip");
		_hostMAC = serverData.getString("host_mac");
		
		if(!serverData.isNull("room")) {
			JSONObject jsonObject = new JSONObject(serverData.getString("room"));
			_room = new Room(this, jsonObject);
		}
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

	public NIOService getService() {
		return _service;
	}

	public void setService(NIOService service) {
		_service = service;
	}

	/**
	 * Metoda zwraca pokój rozgrywki utworzony na tym serwerze.
	 * @return Pokój rozgrywki
	 */
	public Room getRoom() {
		return _room;
	}

	public void setRoom(Room room) {
		_room = room;
	}

	/**
	 * Zwraca dane serwera w notacji zgodnej z obiektem JSON.
	 * @return Obiekt w notacji JSON z danymi serwera
	 */
	public JSONObject toJSON() throws JSONException {
	    JSONObject jsonObject= new JSONObject();
	    
	    jsonObject.put("name", getName());
	    jsonObject.put("host_ip", getHostIP());
	    jsonObject.put("host_mac", getHostMAC());
	    if(_room != null) {
	    	jsonObject.put("room", getRoom().toJSON());
	    }

	    return jsonObject;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Server))
            return false;
		Server p = (Server)o;
        return p._hostIP.equals(_hostIP) && p._name.equals(_name);
	}
}
