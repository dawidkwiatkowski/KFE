package com.app.kfe.controler.communication;

import java.net.InetSocketAddress;

import com.app.kfe.controler.RoomManager;
import com.app.kfe.utils.Logger;
import naga.ConnectionAcceptor;

public class ServerConnectionAcceptor implements ConnectionAcceptor {

	private String _serverHost;

	public ServerConnectionAcceptor(String serverHost) {
		_serverHost = serverHost;
	}

	@Override
	public boolean acceptConnection(InetSocketAddress socket) {
		Logger.trace("ServerConnectionAcceptor", "[acceptConnection] Incomming connection from " + socket.getAddress().getHostAddress());
		boolean allow;
		if(socket.getAddress().getHostAddress().equals(_serverHost)) {
			allow = true;
		} else {
			allow = RoomManager.getInstance().allowPlayerJoin();
		}
		Logger.debug("ServerConnectionAcceptor","[acceptConnection] Connection from " + socket.getAddress().getHostAddress() + " " + (allow?"ACCEPTED":"REFUSED"));
		return allow;
	}

}
