package com.app.kfe.controler.communication;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.app.kfe.R;
import com.app.kfe.controler.MessagesManager;
import com.app.kfe.controler.RoomManager;
import com.app.kfe.main.KFE;
import com.app.kfe.model.Player;
import com.app.kfe.model.Server;
import com.app.kfe.model.ServerBasicInfo;
import com.app.kfe.model.messages.Message;
import com.app.kfe.model.messages.MessageType;
import com.app.kfe.model.messages.ServerMessage;
import com.app.kfe.model.messages.ServerMessageType;
import com.app.kfe.utils.Logger;
import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

/**
 * Adam Szeremeta
 * 
 * 12-10-2013
 * 
 * Klasa zarządzająca tworzeniem serwera, rozgłaszaniem i podłączeniem. 
 * Dodatkowo jest tu realizowana wymiana wszystkich komunikatów
 */
public class ServerManager {

	private static final int DISCONNECT_BOOT = 3;
	private static final int DISCONNECT_BROKEN = 2;
	private static final int DISCONNECT_CLOSED = 1;
	private static final int DISCONNECT_EXIT = 0;
	private static final int DISCONNECT_UNKNOWN = -1;

	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int SERVER_PORT = 2236;
	private static final int SERVER_INTERVAL = 50;
	private static ServerManager singleton;

	private Server _serverInstance;
	private boolean _isServerHost;
	private WifiManager _wifiManager;
	private boolean _isAuthenticated;
	private Map<NIOSocket, String> _connectedHosts;

	private ServerTask _serverTask;
	private ClientTask _clientTask;
	private Thread _serverTaskThread;
	private Thread _clientTaskThread;
	private SocketObserver _clientToServerSocketObserver;
	private SocketObserver _serverToClientSocketObserver;
	private ServerConnectionListener _connectionListener;
	private int _disconnectMode;

	public static ServerManager getInstance() {
		if(singleton == null) {
			 synchronized (ServerManager.class) {
	         	if (singleton == null)
	         		singleton = new ServerManager();
			 }
		}
		return singleton;
	}

	private ServerManager() {
		_serverInstance = null;
		_isServerHost = false;
		_isAuthenticated = false;
		_disconnectMode = DISCONNECT_UNKNOWN;
		_connectedHosts = new LinkedHashMap<NIOSocket, String>();
		_wifiManager = (WifiManager) KFE.getContext().getSystemService(Context.WIFI_SERVICE);
		initializeSocketObservers();
	}

	public void connectToServer(ServerBasicInfo serverInfo, ConnectionRequester requester) {
		Logger.trace("ServerManager", "[connectToServer] Connect to server requested ('" + serverInfo.getName() + "', " + serverInfo.getHostIP() + ")");
		ServerConnectionExecutor executor = new ServerConnectionExecutor(requester);
		executor.execute(getServerFromInfo(serverInfo));
	}

	public void disconnectFromServer() {
		Logger.trace("ServerManager","[disconnectFromServer] Disconnect from server requested...");
		if(_serverInstance != null) {
			Logger.trace("ServerManager","[disconnectFromServer] Disconnecting from server '" + _serverInstance.getName() + "' (" + _serverInstance.getHostIP() + ")");
			_disconnectMode = DISCONNECT_EXIT;
			ServerDisconnectionExecutor executor = new ServerDisconnectionExecutor();
			executor.execute();
		}
	}

	public String getIpAddressFromInt(int ip) {
		String s =  Integer.toString((ip & 0xff)) + '.' +
                Integer.toString((ip & 0xff00) >> 8) + '.' +
                Integer.toString((ip & 0xff0000) >> 16) + '.' +
                Integer.toString(((ip & 0xff000000) >> 24) & 0xff);
		return s;
	}

	public Server getServerFromInfo(ServerBasicInfo serverInfo) {
		return new Server(serverInfo.getHostMAC(), serverInfo.getHostIP(), serverInfo.getName());
	}

	public boolean isConnectedToServer() {
		return _serverInstance != null;
	}

	public boolean isConnectedToServer(Server server) {
		return _serverInstance!=null && _serverInstance.equals(server);
	}

	public boolean isServerHost() {
		return _isServerHost;
	}

	public void sendMessage(JSONObject message) throws JSONException {
		if(isConnectedToServer()) {
			if(_isServerHost) {
				Logger.debug("ServerManager", "[sendMessage] Sending message [target=" + message.getInt("target") + "] [hosts=" + _connectedHosts.size() + "]");
				switch(message.getInt("target")) {
					case Message.TARGET_ALL:
						Logger.debug("ServerManager", "[sendMessage] Sending message to all hosts...");
						synchronized (_connectedHosts) {
							for(NIOSocket clientSocket : _connectedHosts.keySet()) {
								writeMessageToSocket(clientSocket, message.toString());
							}
						}
						Logger.debug("ServerManager", "[sendMessage] Message has been successfully sent to all hosts");
						break;
					
					case Message.TARGET_HOST:
						dispatchMessage(message, false);
						break;
					
					case Message.TARGET_NOT_HOST:
						Logger.debug("ServerManager", "[sendMessage] Sending message to all hosts except server host...");
						synchronized (_connectedHosts) {
							for(Entry<NIOSocket, String> host : _connectedHosts.entrySet()) {
								if(!host.getValue().equals(_serverInstance.getHostMAC())) {
									writeMessageToSocket(host.getKey(), message.toString());
								}
							}
						}
						Logger.debug("ServerManager", "[sendMessage] Message has been successfully sent to all hosts except server host");
						break;
					
					case Message.TARGET_PLAYER:
						String target = message.getString("target_host");
						Logger.debug("ServerManager", "[sendMessage] Sending message to host " + target);
						synchronized (_connectedHosts) {
							for(Entry<NIOSocket, String> host : _connectedHosts.entrySet()) {
								if(host.getValue().equals(target)) {
									writeMessageToSocket(host.getKey(), message.toString());
									break;
								}
							}
						}
						Logger.debug("ServerManager", "[sendMessage] Message has been successfully sent to host");
						break;
					
					case Message.TARGET_SELF:
						dispatchMessage(message, false);
						break;
				}
			} else {
				switch(message.getInt("target")) {
					case Message.TARGET_SELF:
						dispatchMessage(message, false);
						break;
					default:
						Logger.debug("ServerManager", "[sendMessage] Forwarding message to server: " + message.toString());
						_clientTask.getClientSocket().write(message.toString().getBytes());
				}
			}
		} else {
			Logger.warning("ServerManager", "[sendMessage] Unnable to send message: host is not connected to any server!");
		}
	}

	public void setServerConnectionListener(ServerConnectionListener listener) {
		_connectionListener = listener;
	}

	public void startServer(String serverName, ConnectionRequester requester) {
		Logger.trace("ServerManager","[startServer] Start server requested by client ('" + serverName + "', " + getIpAddressFromInt(_wifiManager.getConnectionInfo().getIpAddress()) + ")");
		if(_serverTask==null && _serverTaskThread==null) {
			_isServerHost = true;
			ServerConnectionExecutor executor = new ServerConnectionExecutor(requester);
			Server server = new Server(_wifiManager.getConnectionInfo().getMacAddress(), getIpAddressFromInt(_wifiManager.getConnectionInfo().getIpAddress()), serverName);
			executor.execute(server);
			// I CO DALEJ ?? ;O
		}
	}

	private void authenticateOnServer(Player player) {
		try {
			ServerMessage message = new ServerMessage(ServerMessageType.PLAYER_AUTHENTICATE, Message.TARGET_HOST, player);
			message.setContent(player.toJSON());
			Logger.debug("ServerManager", "[CLIENT] Sending authentication request...");
			_clientTask.getClientSocket().write(message.toJSON().toString().getBytes());
		} catch (JSONException e) {
			Logger.error("ServerManager", "Unable to send authentication message due to: " + e.getMessage());
		}
	}

	private void clearConnectionResources() {
		if(_clientTask != null) {
			Logger.trace("ServerManager", "[clearConnectionResources] Cancelling client task");
			_clientTask.cancel();
			_clientTask = null;
		}
		if(_serverInstance != null) {
//			if(_isServerHost && _serverInstance.getService().isOpen()) {
//				Logger.trace("ServerManager", "[clearConnectionResources] Closing server service");
//				_serverInstance.getService().close();
//				_serverInstance.setService(null);
//			}
			Logger.trace("ServerManager", "[clearConnectionResources] Clearing server instance");
			_serverInstance = null;
		}
		//RoomManager.getInstance().closeRoom();
	}

	private void connectToServer(Server server) {
		Logger.trace("ServerManager", "[connectToServer] Connecting to server requested [serverName='" + server.getName() + "'] [serverAddress='" + server.getHostIP() + ":" + SERVER_PORT + "']");
		disconnectFromServer(false);
		try {
			Logger.debug("ServerManager", "[connectToServer] Connecting to server '" + server.getName() + "' (" + server.getHostIP() + ":" + SERVER_PORT + ")");
			_isServerHost = false;
			_isAuthenticated = false;
			_serverInstance = server;
			_serverInstance.setService(new NIOService());
			_disconnectMode = DISCONNECT_UNKNOWN;
			connectToServer();
		} catch (IOException e) {
			Logger.error("ServerManager", "[CLIENT] Unable to connect to server '" + _serverInstance.getName() + "' due to exception: " + e.getMessage());
			clearConnectionResources();
		}
	}

	private void connectToServer() {
		if(_serverInstance!=null) {
			Logger.trace("ServerManager", "[connectToServer] Initializing client task...");
			_clientTask = new ClientTask(_serverInstance, SERVER_PORT, SERVER_INTERVAL);
			_clientTask.setSocketObserver(_serverToClientSocketObserver);
			_clientTaskThread = new Thread(_clientTask);
			_clientTaskThread.start();
		}
	}

	private void disconnectFromServer(boolean notifyExit) {
		Logger.trace("ServerManager", "[disconnectFromServer] Disconnect from server requested");
		if(isConnectedToServer()) {
			if(notifyExit) {
				Logger.debug("ServerManager", "[disconnectFromServer] Sending PLAYER_EXIT message to all hosts");
				Message message = new ServerMessage(ServerMessageType.PLAYER_EXIT, Message.TARGET_ALL, RoomManager.getInstance().getPlayer());
				MessagesManager.getInstance().sendMessage(message);
			}
			Logger.debug("ServerManager", "[disconnectFromServer] Clearing connection resources...");
			clearConnectionResources();
		}
	}

	private void dispatchMessage(JSONObject message, boolean forward) throws JSONException {
		Logger.debug("ServerManager", "[dispatchMessage] [forward=" + forward + "] [messageType=" + MessageType.valueOf(message.getString("type")) + "] [message=" + message.toString() + "]");
		if(forward) {
			sendMessage(message);
		} else {
			MessageType type = MessageType.valueOf(message.getString("type"));
			if(type.equals(MessageType.SERVER)) {
				handleMessage(new ServerMessage(message));
			} else {
				MessagesManager.getInstance().handleMessage(message);
			}
		}
	}

	private void handleMessage(ServerMessage message) {
		Player player;
		NIOSocket playerSocket;
		Logger.debug("ServerManager", "[handleMessage] Message type: " + message.getCommand() + "; Message: " + message.toString());
		switch(message.getCommand()) {
			case PLAYER_AUTHENTICATE:
				String playerIP;
				try {
					playerIP = message.getContent().getString("ip");
				} catch (JSONException e) {
					playerIP = null;
				}
				Logger.debug("ServerManager", "Trying to authenticate player from " + playerIP);
				if(playerIP!=null && _connectedHosts.containsValue(playerIP)) {
					synchronized (_connectedHosts) {
						for(Entry<NIOSocket, String> host : _connectedHosts.entrySet()) {
							if(host.getValue().equals(playerIP)) {
								Logger.debug("ServerManager", "[SERVER] Client " + playerIP + " authenticated!");
								_connectedHosts.put(host.getKey(), message.getSourceHost());
								ServerMessage msg = new ServerMessage(ServerMessageType.PLAYER_JOIN, Message.TARGET_ALL, _serverInstance.getHostMAC());
								msg.setContent(message.getContent());
								MessagesManager.getInstance().sendMessage(msg);
								break;
							}
						}
					}
				} else {
					Logger.debug("ServerManager", "[SERVER] Connection for client " + playerIP + " refused!");
					//TODO Handle client connection failure (after player authenticated)
				}
				break;

			case PLAYER_BOOT:
				try {
					player = RoomManager.getInstance().getRoom().getPlayerByMAC(message.getContent().getString("mac"));
					String playerMAC = message.getContent().getString("mac");
					Logger.debug("ServerManager", "[handleMessage] PLAYER_BOOT: " + playerMAC);
					if(_isServerHost) {
						synchronized (_connectedHosts) {
							playerSocket = null;
							for(Entry<NIOSocket, String> host : _connectedHosts.entrySet()) {
								if(host.getValue().equals(playerMAC)) {
									playerSocket = host.getKey();
									break;
								}
							}
							if(playerSocket!=null) {
								Logger.debug("ServerManager", "[handleMessage] PLAYER_BOOT: Closing client socket (" + playerSocket.getIp() + ")");
								playerSocket.close();
								_connectedHosts.remove(playerSocket);
							} else {
								Logger.error("ServerManager", "[handleMessage] PLAYER_BOOT: Cannot remove client socket - no socket found for host " + player.getMACAddress());
							}
							Logger.trace("ServerManager", "[handleMessage] PLAYER_BOOT: Updating server broadcast info");
							BroadcastManager.getInstance().updateServerBroadcastInfo(new ServerBasicInfo(_serverInstance));
						}
					} else {
						Logger.debug("ServerManager", "[handleMessage] PLAYER_BOOT: Target player -> " + player.getLogin());
						if(RoomManager.getInstance().getPlayer().getMACAddress().equals(playerMAC)) {
							_disconnectMode = DISCONNECT_BOOT;
						}
						RoomManager.getInstance().bootPlayer(player);
					}
				} catch (JSONException e) {
					//TODO Handle player boot failure
					Logger.error("ServerManager", "[handleMessage] Cannot boot player due to exception: " + e.getMessage());
				}
				break;

			case PLAYER_EXIT:
				String sourceHost = message.getSourceHost();
				Logger.debug("ServerManager", "[handleMessage] PLAYER_EXIT: " + sourceHost);
				if(!sourceHost.equals(_serverInstance.getHostMAC())) {
					if(_isServerHost) {
						synchronized (_connectedHosts) {
							playerSocket = null;
							for(Entry<NIOSocket, String> host : _connectedHosts.entrySet()) {
								if(host.getValue().equals(sourceHost)) {
									playerSocket = host.getKey();
									break;
								}
							}
							if(playerSocket!=null) {
								Logger.debug("ServerManager", "[handleMessage] PLAYER_EXIT: Closing client socket");
								playerSocket.close();
								_connectedHosts.remove(playerSocket);
							} else {
								Logger.error("ServerManager", "Cannot remove client socket - no socket found for host " + sourceHost);
							}
						}
					}
					RoomManager.getInstance().exitPlayer(_serverInstance, sourceHost);
					if(_isServerHost) {
						Logger.trace("ServerManager", "[handleMessage] PLAYER_EXIT: Updating server broadcast info");
						BroadcastManager.getInstance().updateServerBroadcastInfo(new ServerBasicInfo(_serverInstance));
					}
				} else {
					Logger.debug("ServerManager", "[handleMessage] PLAYER_EXIT: Server HOST is exiting...");
					// TODO _disconnectMode = DISCONNECT_SERVER_CLOSED ?
					if(!_isServerHost) {
						_disconnectMode = DISCONNECT_CLOSED;
					}
				}
				break;

			case PLAYER_JOIN:
				try {
					player = new Player(message.getContent());
					RoomManager.getInstance().joinPlayer(_serverInstance, player);
					if(player.equals(RoomManager.getInstance().getPlayer())) {
						Logger.debug("ServerManager", "AUTHENTICATING PLAYER '" + player.getLogin() + "'");
						_isAuthenticated = true;
					}
					if(_isServerHost) {
						Logger.trace("ServerManager", "[handleMessage] PLAYER_JOIN: Updating server broadcast info");
						BroadcastManager.getInstance().updateServerBroadcastInfo(new ServerBasicInfo(_serverInstance));
					}
				} catch (JSONException e) {
					//TODO Handle client connection failure (after player joined)
					Logger.error("ServerManager", "[handleMessage] PLAYER_JOIN - Unnable to join player due to exception: " + e.getMessage());
				}
				break;

			case SERVER_INFO:
				try {
					 if(!_isServerHost) {
						_serverInstance = new Server(message.getContent());
					 }
					String mac = _wifiManager.getConnectionInfo().getMacAddress();
					int ip = _wifiManager.getConnectionInfo().getIpAddress();
					RoomManager.getInstance().joinRoom(_serverInstance, mac, getIpAddressFromInt(ip));
					authenticateOnServer(RoomManager.getInstance().getPlayer());
				} catch (JSONException e) {
					//TODO Handle client connection failure (after received SERVER_INFO)
				}
				break;
		}
	}

	private void initializeSocketObservers() {
		_clientToServerSocketObserver = new SocketObserver() {

			@Override
			public void connectionOpened(NIOSocket socket) {
				//Logger.debug("ServerManager","[CLIENT-TO-SERVER] [connectionOpened] Connection opened on socket: " + socket + " (" + socket.getIp() + ")");
				synchronized (_connectedHosts) {
					_connectedHosts.put(socket, socket.getIp());
				}
			}

			@Override
			public void connectionBroken(NIOSocket socket, Exception reason) {
				Logger.debug("ServerManager","[CLIENT-TO-SERVER] [connectionBroken] Connection with host " + socket.getIp() + " broken due to: " + (reason==null?"N/A":reason.getMessage()));
				synchronized (_connectedHosts) {
					if(_connectedHosts.containsKey(socket)) {
						Logger.debug("ServerManager", "[CLIENT-TO-SERVER] [connectionBroken] Removing host from connected hosts map...");
						_connectedHosts.remove(socket);
					}
				}
			}

			@Override
			public void packetReceived(NIOSocket socket, byte[] buf) {
				String dataString = new String(buf, 0, buf.length);
				Logger.debug("ServerManager","[CLIENT-TO-SERVER] [packetReceived] New message received on socket " + socket + ":\n" + dataString);
				try {
					JSONObject message = new JSONObject(dataString);
					dispatchMessage(message, true);
				} catch (JSONException e) {
					Logger.error("ServerManager", "[CLIENT-TO-SERVER] Unable to parse received message due to: " + e.getMessage());
				}
			}

			@Override
			public void packetSent(NIOSocket socket, Object packet) {
				Logger.debug("ServerManager","[CLIENT-TO-SERVER] [packetSent] Packet sent on socket " + socket + ":\n"+(packet == null ? "null" : packet.toString()));
			}
		};
		_serverToClientSocketObserver = new SocketObserver() {

			@Override
			public void connectionOpened(NIOSocket socket) {
				//Logger.debug("ServerManager","[SERVER-TO-CLIENT] [connectionOpened] Connection opened on socket: " + socket);
			}

			@Override
			public void connectionBroken(NIOSocket socket, Exception reason) {
				Logger.debug("ServerManager","[SERVER-TO-CLIENT] [connectionBroken] Connection on " + socket + " broken due to: " + (reason==null?"N/A":reason.getMessage()));
				if(!_isServerHost && _isAuthenticated) {
					Logger.debug("ServerManager","[SERVER-TO-CLIENT] [connectionBroken] Disconnecting from server...");
					if(_disconnectMode == DISCONNECT_UNKNOWN) {
						_disconnectMode = DISCONNECT_BROKEN;
					}
					if(_disconnectMode != DISCONNECT_EXIT) {
						new ServerDisconnectionExecutor().execute();
					}
				}
			}

			@Override
			public void packetReceived(NIOSocket socket, byte[] buf) {
				String dataString = new String(buf, 0, buf.length);
				Logger.debug("ServerManager","[SERVER-TO-CLIENT] [packetReceived] New message received on socket " + socket + ":\n" + dataString);
				try {
					JSONObject message = new JSONObject(dataString);
					dispatchMessage(message, false);
				} catch (JSONException e) {
					Logger.error("ServerManager", "[SERVER-TO-CLIENT] Unable to parse received message due to: " + e.getMessage());
				}
			}

			@Override
			public void packetSent(NIOSocket socket, Object packet) {			
				Logger.debug("ServerManager","[SERVER-TO-CLIENT] [packetSent] Packet sent on socket " + socket + ":\n"+(packet == null ? "null" : packet.toString()));
			}
		};
	}

	private void startServer(Server server) throws IOException {
		Logger.trace("ServerManager","[startServer] Start server requested ('" + server.getName() + "', " + server.getHostIP() + ")");
		_serverInstance = server;
		_serverInstance.setService(new NIOService());
		Logger.debug("ServerManager","[startServer] Creating new room on new server '" + _serverInstance.getName() + "' (" + _serverInstance.getHostIP() + ")");
		_serverInstance.setRoom(RoomManager.getInstance().createRoom(_serverInstance, _serverInstance.getName()));
		_serverTask = new ServerTask(_serverInstance, _connectedHosts, SERVER_PORT, SERVER_INTERVAL);
		_serverTask.setSocketObserver(_clientToServerSocketObserver);
		_serverTaskThread = new Thread(_serverTask);
		_serverTaskThread.start();
		connectToServer();
	}

	private void stopServer() {
		Logger.trace("ServerManager", "[stopServer] Server stop requested");
		if(_isServerHost && _serverTask != null) {
			Logger.debug("ServerManager","[stopServer] Stoping to send server broadcast");
			BroadcastManager.getInstance().stopSendingServerBroadcast();
			Logger.debug("ServerManager", "[stopServer] Sending PLAYER_EXIT message to all hosts except server host");
			Message message = new ServerMessage(ServerMessageType.PLAYER_EXIT, Message.TARGET_NOT_HOST, RoomManager.getInstance().getPlayer());
			MessagesManager.getInstance().sendMessage(message);
			Logger.debug("ServerManager", "[stopServer] Closing all clients sockets...");
			synchronized(_connectedHosts) {
				for(Entry<NIOSocket, String> host : _connectedHosts.entrySet()) {
					Logger.debug("ServerManager", "[stopServer] Closing socket " + host.getKey().getIp());
					host.getKey().closeAfterWrite();
				}
			}
			_serverTask.cancel();
			_serverTask = null;
			disconnectFromServer(false);
			//Logger.debug("ServerManager", "Stopped server '" + _serverInstance.getName() + "' at " + _serverInstance.getHostIP());
		}
	}

	private void writeMessageToSocket(NIOSocket socket, String message) {
		if(socket.isOpen()) {
			socket.write(message.getBytes());
		} else {
			Logger.warning("ServerManager", "Cannot send message to host " + socket.getIp() + ": socket is CLOSED");
		}
	}

	private class ServerConnectionExecutor extends AsyncTask<Server, Void, Void> {

		private static final int ERROR = 0;
		private static final int SUCCESS = 1;
		private static final int TIMEOUT = 2;

		private ConnectionRequester _requester;
		private int _connectionState;

		public ServerConnectionExecutor(ConnectionRequester requester) {
			_requester = requester;
			_connectionState = SUCCESS;
		}

		@Override
		protected void onPreExecute() {
			_requester.showProgress(_isServerHost? R.string.room_selection_creating_room:R.string.room_selection_connecting);
			Logger.debug("ServerManager","[ServerConnectionExecutor] Stoping to listen for servers broadcasts");
			BroadcastManager.getInstance().stopListeningServerBroadcast();
		}

		@Override
		protected Void doInBackground(Server... server) {
			try {
				if(_isServerHost) {
					Logger.debug("ServerManager", "[EXECUTOR] Starting server " + server[0].getName());
					startServer(server[0]);
				} else {
					Logger.debug("ServerManager", "[EXECUTOR] Connecting to server " + server[0].getName());
					connectToServer(server[0]);
				}
				long connectionStart = new Date().getTime();
				while(!_isAuthenticated) {
					Logger.debug("ServerManager", "[EXECUTOR] Waiting for server authentication...");
					Thread.sleep(500);
					if(!_isServerHost && new Date().getTime() - connectionStart > CONNECTION_TIMEOUT) {
						_connectionState = TIMEOUT;
						break;
					}
				}
				if(_isServerHost) {
					BroadcastManager.getInstance().startSendingServerBroadcast(_serverInstance);
				}
			} catch (InterruptedException e) {
				Logger.error("ServerManager", "[EXECUTOR] Can not freeze authentication task!");
				_connectionState = ERROR;
			} catch (IOException e) {
				Logger.error("ServerManager", "[EXECUTOR] Can not start server! Reason: " + e.getMessage());
				_connectionState = ERROR;
			}
			return null;
		}

		@Override
        protected void onPostExecute(Void result) {
			if(_connectionState==SUCCESS) {
				Logger.debug("ServerManager", "[EXECUTOR] Connected successfully!");
				_requester.onConnectionEstabilished();
			} else {
				int msgResId = -1;
				if(_connectionState==ERROR) {
					Logger.debug("ServerManager", "[EXECUTOR] Connection failed - ERROR!");
					msgResId = R.string.room_selection_connecting_error;
				} else if(_connectionState==TIMEOUT) {
					Logger.debug("ServerManager", "[EXECUTOR] Connection failed - TIMEOUT!");
					msgResId = R.string.room_selection_connecting_timeout;
				}
				clearConnectionResources();
				_requester.onConnectionFailed(msgResId);
			}
		}

	}

	private class ServerDisconnectionExecutor extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			Logger.debug("ServerManager", "[EXECUTOR] Disconnect from server requested! Mode: " + _disconnectMode);
			int task = _disconnectMode==DISCONNECT_EXIT?R.string.room_selection_disconnecting:R.string.room_selection_connection_broken;
			if(_connectionListener!=null) {
				_connectionListener.showProgress(task);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if(_disconnectMode==DISCONNECT_BROKEN || _disconnectMode==DISCONNECT_UNKNOWN) {
					//Wait some time for message with reason...
					Logger.debug("ServerManager", "[EXECUTOR] Disconnect mode: " + (_disconnectMode==DISCONNECT_BROKEN?"BROKEN":"UNKNOWN") + " - waiting for reason...");
					Thread.sleep(2000);
				}
				
				if(_disconnectMode == DISCONNECT_BOOT) {
					Logger.debug("ServerManager","[EXECUTOR] Possible disconnection reason: PLAYER_BOOT");
				} else if(_disconnectMode == DISCONNECT_BROKEN) {
					Logger.debug("ServerManager","[EXECUTOR] Possible disconnection reason: CONNECTION_BROKEN");
				} else if(_disconnectMode == DISCONNECT_CLOSED) {
					Logger.debug("ServerManager","[EXECUTOR] Possible disconnection reason: SERVER_CLOSED");
				} else if(_disconnectMode == DISCONNECT_EXIT) {
					Logger.debug("ServerManager","[EXECUTOR] Possible disconnection reason: PLAYER_EXIT");
				} else {
					Logger.debug("ServerManager","[EXECUTOR] Possible disconnection reason: unknown");
				}
				
				if(_isServerHost) {
					Logger.debug("ServerManager", "[EXECUTOR] Stopping server");
					stopServer();
				} else {
					Logger.debug("ServerManager", "[EXECUTOR] Disconnecting from server");
					disconnectFromServer(_disconnectMode==DISCONNECT_EXIT);
				}
				if(_isServerHost) {
					Logger.debug("ServerManager", "[EXECUTOR] Waiting for server task to finish...");
					_serverTaskThread.join();
				}
				_clientTaskThread.join();
				// DONE !
				_serverTaskThread = null;
				_clientTaskThread = null;
			} catch (InterruptedException e) {
				// TODO Handle disconnection request failure 
				//_connectionRequester.onConnectionFailed(null);
			}
			return null;
		}

		@Override
        protected void onPostExecute(Void result) {
			Logger.debug("ServerManager", "[EXECUTOR] Disconnect request finished!");
			if(_disconnectMode == DISCONNECT_EXIT) {
				_connectionListener.onConnectionClosed();
			} else {
				int msgResId;
				switch(_disconnectMode) {
					case DISCONNECT_BOOT:
						msgResId = R.string.room_player_booted;
						break;
					case DISCONNECT_CLOSED:
						msgResId = R.string.room_closed;
						break;
					default:
						msgResId = R.string.room_selection_connection_broken;
				}
				_connectionListener.onConnectionBroken(msgResId);
			}
			if(!_isServerHost) {
				//_connectionListener.onServerDisconnected();
			}
			_connectionListener = null;
			_disconnectMode = DISCONNECT_UNKNOWN;
		}

	}

	public interface ConnectionRequester {
		public void showProgress(int taskResId);
		public void onConnectionEstabilished();
		public void onConnectionFailed(int msgResId);
	}

	public interface ServerConnectionListener {
		public void showProgress(int taskResId);
		public void onConnectionClosed();
		public void onConnectionBroken(int msgResId);
	}

}
