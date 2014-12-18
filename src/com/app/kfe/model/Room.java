package com.app.kfe.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Room {

	/**
	 * Instancja serwera, na którym został utworzony pokój.
	 */
	private Server _server;
	/**
	 * Nazwa pokoju.
	 */
	private String _name;
	/**
	 * Minimalna liczba graczy, wymagana do rozpoczęcia rozgrywki w tym pokoju.
	 */
	private int _minPlayers;
	/**
	 * Maksymalna liczba graczy, dozwolona do rozpoczęcia rozgrywki w tym pokoju.
	 */
	private int _maxPlayers;
	/**
	 * Lista wszystkich graczy, znajdujących się w pokoju.
	 */
	private List<Player> _players;
	/**
	 * Lobby dla tego pokoju.
	 */
	private Lobby _lobby;

	public Room(Server server, String name) {
		_server = server;
		_name = name;
		_minPlayers = 0;
		_maxPlayers = 0;
		_players = new ArrayList<Player>();
		_lobby = new Lobby();
	}

	public Room(Server server, String name, int minPlayers) {
		_server = server;
		_name = name;
		_minPlayers = minPlayers;
		_maxPlayers = 0;
		_players = new ArrayList<Player>();
		_lobby = new Lobby();
	}

	public Room(Server server, String name, int minPlayers, int maxPlayers) {
		_server = server;
		_name = name;
		_minPlayers = minPlayers;
		_maxPlayers = maxPlayers;
		_players = new ArrayList<Player>();
		_lobby = new Lobby();
	}

	public Room(Server server, JSONObject jsonObject) throws JSONException {
		_server = server;
		_name = jsonObject.getString("_name");
		_minPlayers = jsonObject.getInt("_minPlayers");
		_maxPlayers = jsonObject.getInt("_maxPlayers");
		_players = new ArrayList<Player>();
		JSONArray playersArray = jsonObject.getJSONArray("_players");
		for(int i = 0; i < playersArray.length(); i++) {
			_players.add(new Player(playersArray.getJSONObject(i)));
		}
		_lobby = new Lobby();
	}

	/**
	 * Metoda zwraca referencj� do lobby powi�zanego z tym pokojem.
	 * @return Lobby dla tego pokoju
	 */
	public Lobby getLobby() {
		return _lobby;
	}

	/**
	 * Metoda zwraca maksymaln� liczb� graczy, kt�rzy mog� wzi��� udzia� w rozgrywce w tym pokoju.
	 * @return Maksymalna dozwolona liczba graczy
	 */
	public int getMaxPlayers() {
		return _maxPlayers;
	}

	/**
	 * Metoda zwraca minimaln� liczb� graczy wymagan� do zorpocz�cia rozgrywki dla tego pokoju.
	 * @return Minimalna wymagana liczba graczy
	 */
	public int getMinPlayers() {
		return _minPlayers;
	}

	/**
	 * Metoda zwraca nazw� tego pokoju.
	 * @return Nazwa pokoju
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Metoda umo�liwia ustawienie maksymalnej liczby graczy, kt�rzy mog� wzi��� udzia� w rozgrywce w tym pokoju.
	 * @param maxPlayers Maksymalna dozwolona liczba graczy
	 */
	public void setMaxPlayers(int maxPlayers) {
		_maxPlayers = maxPlayers;
	}

	/**
	 * Metoda umo�liwia ustawienie minimalnej liczby graczy wymaganej do zorpocz�cia rozgrywki dla tego pokoju.
	 * @param minPlayers Minimalna wymagana liczba graczy
	 */
	public void setMinPlayers(int minPlayers) {
		_minPlayers = minPlayers;
	}

	/**
	 * Metoda umo�liwia ustawienie nowej nazwy dla tego pokoju.
	 * @param name Nazwa pokoju
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * Metoda powoduje dodanie nowego gracza do pokoju.
	 * @param player Nowy gracz
	 * @return false, je�eli pok�j jest pe�ny lub podany gracz znajduje si� ju� w pokoju,
	 * true w przeciwnym wypadku
	 */
	public boolean addPlayer(Player player) {
		if((_maxPlayers <= 0 || _maxPlayers > 0 && _players.size() < _maxPlayers) && !containsPlayer(player)) {
			_players.add(player);
			return true;
		}
		return false;
	}

	/**
	 * Metoda sprawdza, czy podany gracz znajduje si� w pokoju.
	 * @param player Szukany gracz
	 * @return true, je�eli gracz znajduje si� w pokoju, false w przeciwnym wypadku
	 */
	public boolean containsPlayer(Player player) {
		for(Player p : _players) {
			if(p.equals(player)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Metoda zwraca gracza znajdującego się na podanej pozycji na liście wszystkich graczy w pokoju.
	 * @param playerPosition pozycja gracza na liście
	 * @return gracz znajdujący się na podanej pozycji na liście, lub null jeżeli na wskazanej pozycji nie ma żadnego gracza
	 */
	public Player getPlayer(int playerPosition) {
		if(playerPosition >= 0 && playerPosition < _players.size()) {
			return _players.get(playerPosition);
		}
		return null;
	}

	/**
	 * Metoda zwraca gracza o podanej nazwie w tym pokoju.
	 * @param playerLogin login gracza
	 * @return gracz o podanym loginie lub null, gdy w pokoju nie ma gracza o takiej nazwie
	 */
	public Player getPlayer(String playerLogin) {
		for(Player player : _players) {
			if(player.getLogin().equals(playerLogin)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Metoda zwraca gracza o podanym adresie MAC w tym pokoju.
	 * @param playerMAC adres MAC gracza
	 * @return gracz o podanym adresie MAC lub null, gdy w pokoju nie ma gracza o takim adresie
	 */
	public Player getPlayerByMAC(String playerMAC) {
		for(Player player : _players) {
			if(player.getMACAddress().equals(playerMAC)) {
				return player;
			}
		}
		return null;
	}

	public List<Player> getPlayersList() {
		return _players;
	}

	/**
	 * Metoda zwraca aktualn� liczb� graczy znajduj�cych si� w pokoju.
	 * @return Liczba graczy w pokoju
	 */
	public int numberOfPlayers() {
		return _players.size();
	}

	/**
	 * Metoda powoduje usuniecie wszystkich graczy z pokoju.
	 */
	public void removeAllPlayers() {
		_players.clear();
	}

	/**
	 * Metoda powoduje usuni�cie wybranego gracza z pokoju.
	 * @param player Gracz, kt�ry ma zosta� usuni�ty
	 * @return false, je�eli gracz nie znajduje sie w pokokju, true w przeciwnym wypadku
	 */
	public boolean removePlayer(Player player) {
		for(Player p: _players) {
			if(p.equals(player)) {
				_players.remove(p);
				return true;
			}
		}
		return false;
	}

	/**
	 * Metoda zwraca instancję serwera, na którym znajduje się pokój.
	 * @return instancja serwera, na którym utworzony jest pokój
	 */
	public Server getServer() {
		return _server;
	}

	public Object toJSON() {
		
		JSONObject jsonObject = new JSONObject();
		
		try {
			
			jsonObject.put("_name", getName());
			jsonObject.put("_minPlayers", getMinPlayers());
			jsonObject.put("_maxPlayers", getMaxPlayers());
			
			JSONArray playersArray = new JSONArray();
			for(int i = 0 ; i < _players.size(); i++){
				
				playersArray.put(_players.get(i).toJSON());
			}
			
			jsonObject.put("_players", playersArray);
			
			//TODO lobby ?
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}

	@Override
	public String toString() {
		String room = "{ROOM: ";
		room += "[name=" + _name + "] ";
		room += "[players=" + _players.size() + "]";
		room += "}";
		return room;
	}
}