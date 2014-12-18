package com.app.kfe.controler;

import com.app.kfe.R;
import com.app.kfe.controler.communication.ServerManager;
import com.app.kfe.main.KFE;
import com.app.kfe.model.Player;
import com.app.kfe.model.Room;
import com.app.kfe.model.Server;
import com.app.kfe.model.messages.LobbyMessage;
import com.app.kfe.model.messages.Message;
import com.app.kfe.model.messages.ServerMessage;
import com.app.kfe.model.messages.ServerMessageType;
import com.app.kfe.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service providing logic connected to room management.
 * 
 * @author Damian Kaczybura
 */
public class RoomManager {

	private static RoomManager singleton;
	private Room _roomInstance;
	private PlayersFragment _playersListView;
	private LobbyFragment _lobbyView;
	private Player _player;

	public static RoomManager getInstance() {
		if(singleton == null) {
			 synchronized (RoomManager.class) {
	               if (singleton == null)
	                singleton = new RoomManager();
			 }
		}
		return singleton;
	}

	private RoomManager() {
		_roomInstance = null;
	}

	public boolean allowPlayerJoin() {
		Logger.trace("RoomManager", "[allowPlayerJoin] Room: " + (_roomInstance == null ? "null" : _roomInstance.toString()));
		if(_roomInstance != null) {
			return _roomInstance.getMaxPlayers()<=0 || _roomInstance.numberOfPlayers() < _roomInstance.getMaxPlayers();
		}
		return false;
	}

	public boolean bootPlayer(Player player) {
		Logger.debug("RoomManager", "Booting player " + player.getLogin() + " from room");
		if(ServerManager.getInstance().isServerHost()) {
			ServerMessage message = new ServerMessage(ServerMessageType.PLAYER_BOOT, Message.TARGET_ALL, _player);
			JSONObject playerInfo = new JSONObject();;
			try {
				playerInfo.put("mac", player.getMACAddress());
				message.setContent(playerInfo);
				Logger.debug("RoomManager", "Sending player boot message to hosts");
				MessagesManager.getInstance().sendMessage(message);
			} catch (JSONException e) {
				return false;
			}
		}
		if(player.equals(_player)) {
			Logger.debug("RoomManager", "Player booted from current room");
		} else if(_roomInstance.removePlayer(player)) {
			Logger.debug("RoomManager", "Sending player booted info to lobby");
			Message message = new SystemMessage(KFE.getContext().getString(R.string.player_boot_message, player.getLogin()));
			MessagesManager.getInstance().sendMessage(message);
			_playersListView.refreshPlayersList();
		} else {
			return false;
		}
		return true;
	}

	public void closeRoom() {
		Logger.info("RoomManager", "[closeRoom] Clearing room and players data");
		GameplayManager.getInstance().clearGameResources();
		_roomInstance.removeAllPlayers();
		_player = null;
		if(_playersListView!=null) {
			_playersListView.refreshPlayersList();
		}
		_roomInstance = null;
	}

	public Room createRoom(Server server, String roomName) {
		if(ServerManager.getInstance().isConnectedToServer(server) && _roomInstance == null) {
			_roomInstance = new Room(server, roomName);
			initializeRoomSettings(_roomInstance);
			Logger.debug("RoomManager", "Room '" + roomName + "' created");
			_player = createPlayer(server.getHostMAC(), server.getHostIP());
			Logger.debug("RoomManager", "Player '" + _player.getLogin() + "' instantiated");
			return _roomInstance;
		}
		return null;
	}

	public void exitPlayer(Server server, String playerMAC) {
		if(ServerManager.getInstance().isConnectedToServer(server)) {
			Player player = _roomInstance.getPlayerByMAC(playerMAC);
			if(_roomInstance.removePlayer(player)) {
				Logger.debug("RoomManager", "Sending player exited info to lobby");
				Message message = new SystemMessage(KFE.getContext().getString(R.string.room_player_exited_message, player.getLogin()));
				MessagesManager.getInstance().sendMessage(message);
				_playersListView.refreshPlayersList();
			}
		}
	}

	public Player getPlayer() {
		return _player;
	}

	public Room getRoom() {
		return _roomInstance;
	}

	public void joinPlayer(Server server, Player player) {
		if(ServerManager.getInstance().isConnectedToServer(server) && _roomInstance != null) {
			Logger.debug("RoomManager", player.getLogin() + " has joined the room!");
			_roomInstance.addPlayer(player);
			if(_playersListView != null) {
				_playersListView.refreshPlayersList();
			}
			if(!player.equals(_player)) {
				Message message = new SystemMessage(KFE.getContext().getString(R.string.room_player_joined_message, player.getLogin()));
				MessagesManager.getInstance().sendMessage(message);
			}
		}
	}

	/**
	 * Metoda pozwala na synchronizacje menedzera pokoju i serwera.
	 * @param server Instancja serwera, do ktorej powinien byc podlaczony klient w danym momencie
	 * @param mac Adres MAC uzytkownika, ktory zabiega o dolaczenie do pokoju
	 * @param ip Adres IP uzytkownika, ktory zabiega o dolaczenie do pokoju
	 */
	public void joinRoom(Server server, String mac, String ip) {
		if(ServerManager.getInstance().isConnectedToServer(server) && (ServerManager.getInstance().isServerHost() || _roomInstance == null)) {
			if(!ServerManager.getInstance().isServerHost()) {
				Logger.debug("RoomManager","[workflow_test] [player=" + _player + "|null] [room=" + _roomInstance + "|null]");
				_player = createPlayer(mac, ip);
				_roomInstance = server.getRoom();
			}
		}
	}

	public void sendMessageToLobby(LobbyMessage message) {
		if(_roomInstance != null) {
			_roomInstance.getLobby().addMessage(message);
			_lobbyView.refreshMessages();
		}
	}

	public void setCurrentPlayersListView(PlayersFragment fragment) {
		_playersListView = fragment;
	}

	public void setCurrentLobbyView(LobbyFragment fragment) {
		_lobbyView = fragment;
	}

	public LobbyFragment getCurrentLobbyView() {
		return _lobbyView;
	}

	public boolean validateRoomName(String name) {
		return name!=null && name.matches("^[A-Za-z]{1}[\\w<>()\\[\\] ]{2,19}+$");
	}

	private Player createPlayer(String mac, String ip) {
		return new Player(mac, ip, SettingsManager.getInstance().getSettingValue(SettingsManager.SETTING_PLAYERNAME));
	}

	private void initializeRoomSettings(Room room) {
		room.setMaxPlayers(Integer.parseInt(SettingsManager.getInstance().getSettingValue("setting_room_maxPlayers")));
	}

	public void onGameStartMessageReceived(JSONObject gameState) {
		_playersListView.onGameStartMessageReceived(gameState);
	}

}