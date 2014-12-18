package com.app.kfe.controler.communication;

import java.io.IOException;
import java.util.Map;

import com.app.kfe.model.Server;
import com.app.kfe.model.messages.Message;
import com.app.kfe.model.messages.ServerMessage;
import com.app.kfe.model.messages.ServerMessageType;
import com.app.kfe.utils.Logger;
import naga.NIOServerSocket;
import naga.NIOService;
import naga.NIOSocket;
import naga.ServerSocketObserverAdapter;
import naga.SocketObserver;

import org.json.JSONException;

public class ServerTask implements Runnable {

	private final Server _serverInstance;
	private final int _serverPort;
	private final int _serverListeningInterval;
	private NIOServerSocket _serverSocket;
	private SocketObserver _socketObserver;
	private Map<NIOSocket, String> _connectedHosts;
	private boolean _isCancelled;

	public ServerTask(Server server, Map<NIOSocket, String> connectedHosts, int port, int listeningInterval) {
		_serverInstance = server;
		_socketObserver = null;
		_connectedHosts = connectedHosts;
		_isCancelled = false;
		_serverPort = port;
		_serverListeningInterval = listeningInterval;
	}

	public void cancel() {
		Logger.info("ServerTask", "[cancel] Cancel server task requested");
		_isCancelled = true;
	}

	public void setSocketObserver(SocketObserver socketObserver) {
		_socketObserver = socketObserver;
	}

	private void handleTaskCanceled() {
		Logger.debug("ServerTask","[handleTaskCanceled] Server task has been canceled! Closing server socket...");
		_serverSocket.close();
		if(_serverInstance.getService().isOpen()) {
			Logger.debug("ServerTask","[handleTaskCanceled] Closing server service...");
			_serverInstance.getService().close();
			_serverInstance.setService(null);
		}
	}

	@Override
	public void run() {
		try {
			if(!_connectedHosts.isEmpty()) {
				_connectedHosts.clear();
			}
			Logger.debug("ServerTask", "Starting server on port " + _serverPort);
			NIOService service = _serverInstance.getService();
			_serverSocket = service.openServerSocket(_serverPort);
			_serverSocket.setConnectionAcceptor(new ServerConnectionAcceptor(_serverInstance.getHostIP()));
			_serverSocket.listen(new ServerSocketObserverAdapter() {
				@Override
				public void newConnection(NIOSocket clientSocket) {
					super.newConnection(clientSocket);
					Logger.debug("ServerTask", "New client connected (" + clientSocket.getIp() + ") to SERVER SOCKET (" + _serverSocket + ") via SOCKET: " + clientSocket);
					if(_socketObserver!=null) {
						Logger.debug("ServerTask", "Starting to listen for client socket (" + clientSocket + ")");
						clientSocket.listen(_socketObserver);
					} else {
						Logger.warning("ServerTask", "Unnable to listen for client socket - socket observer is NULL!");
					}
//					//SEND SERVER INSTANCE DATA TO NEW CLIENT
					try {
						ServerMessage message = new ServerMessage(ServerMessageType.SERVER_INFO, Message.TARGET_ALL, _serverInstance.getHostMAC());
						message.setContent(_serverInstance.toJSON());
						//Write message straight to client socket
						if(clientSocket != null) {
							clientSocket.write(message.toJSON().toString().getBytes());
						}
					} catch (JSONException e) {
						Logger.error("ServerTask", "Unable to transfer server data to new client (" + clientSocket.getIp() + ") - closing connection");
						clientSocket.close();
					}
//					////
				}});
			//BroadcastManager.getInstance().startSendingServerBroadcast(_serverInstance);
			while(true) {
				service.selectBlocking();
				Thread.sleep(_serverListeningInterval);
				if(_isCancelled) {
					if(_connectedHosts.isEmpty()) {
						handleTaskCanceled();
						break;
					} else {
						Logger.debug("ServerTask", "Cancelling server task: waiting for hosts to disconnect...");
					}
				}
			}
		} catch (IOException e) {
			// TODO Handle IOException within ServerTask
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Handle InterruptedException within ServerTask
			e.printStackTrace();
		}
	}

}
