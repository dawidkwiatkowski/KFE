package com.app.kfe.controler.communication;

import java.io.IOException;

import com.app.kfe.model.Server;
import com.app.kfe.utils.Logger;
import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;

public class ClientTask implements Runnable {

	private final Server _serverInstance;
	private final int _serverPort;
	private final int _serverListeningInterval;
	private NIOSocket _clientSocket;
	private SocketObserver _socketObserver;
	private boolean _isCancelled;

	public ClientTask(Server server, int port, int listeningInterval) {
		_serverInstance = server;
		_serverPort = port;
		_serverListeningInterval = listeningInterval;
		_socketObserver = null;
	}

	public void cancel() {
		Logger.trace("ClientTask", "[cancel] Cancel client task requested");
		_isCancelled = true;
	}

	public NIOSocket getClientSocket() {
		return _clientSocket;
	}

	public void setSocketObserver(SocketObserver socketObserver) {
		_socketObserver = socketObserver;
	}

	private void handleTaskCanceled() {
		Logger.debug("ClientTask","Client task has been canceled! Closing client socket (WQS=" + _clientSocket.getWriteQueueSize() + ")...");
		_clientSocket.close();
		_clientSocket = null;
	}

	@Override
	public void run() {
		try {
			NIOService service = new NIOService();
			_clientSocket = service.openSocket(_serverInstance.getHostIP(), _serverPort);
			Logger.debug("ClientTask", "Socket to server opened: " + _clientSocket);
			_clientSocket.listen(_socketObserver);
			Logger.debug("ClientTask","Started listening for socket: " + _clientSocket);
			while(true) {
				service.selectNonBlocking();
				Thread.sleep(_serverListeningInterval);
				if(_isCancelled) {
					Logger.trace("ClientTask", "CLIENT TASK CANCELLED - WRITE QUEUE SIZE = " + _clientSocket.getWriteQueueSize());
					if(_clientSocket.getWriteQueueSize() == 0) {
						handleTaskCanceled();
						break;
					} else {
						Logger.trace("ClientTask", "Cancelling client task: waiting for client socket write queue to be cleared...");
					}
				}
			}
		} catch (IOException e) {
			// TODO Handle IOException within ClientTask
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Handle InterruptedException within ClientTask
			e.printStackTrace();
		}
	}

}
