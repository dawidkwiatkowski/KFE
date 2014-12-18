package com.app.kfe.controler.communication;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import com.app.kfe.main.KFE;
import com.app.kfe.model.Server;
import com.app.kfe.model.ServerBasicInfo;
import com.app.kfe.utils.Logger;

/**
 * Klasa wykorzystywana do rozglaszania w sieci informacji o stworzonym serwerze oraz do odnajdywania rozgloszen w sieci.
 * 
 * Adam Szeremeta
 */
public class BroadcastManager {

	private static final int BROADCAST_PORT = 0xBAB8;
	private static final int BROADCAST_INTERVAL = 2000;

	private static BroadcastManager singleton;

	private ServerBroadcastSender _sendingBroadcastTask;
	private ServerBroadcastListener _listeningBroadcastTask;
	private ServerBasicInfo _serverBroadcast;

	private BroadcastManager() {
		
	}

	public static BroadcastManager getInstance() {
		if(singleton == null) {
			 synchronized (BroadcastManager.class) {
	               if (singleton == null)
	                singleton = new BroadcastManager();
			 }
		}
		return singleton;
	}

	public ServerBasicInfo getServerBroadcastInfo() {
		synchronized(_serverBroadcast) {
			return _serverBroadcast;
		}
	}

	public synchronized void updateServerBroadcastInfo(ServerBasicInfo serverInfo) {
		Logger.trace("BroadcastManager", "[updateServerBroadcastInfo] Update server info requested");
		if(_serverBroadcast != null) {
			synchronized(_serverBroadcast) {
				Logger.debug("BroadcastManager", "Updating server broadcast info\nFROM: " + _serverBroadcast.toString() + "\nTO: " + serverInfo.toString());
				_serverBroadcast = serverInfo;
			}
		}
	}

	public void startSendingServerBroadcast(Server server) {
		Logger.trace("BroadcastManager","[startSendingServerBroadcast] Start sending server broadcast requested for server: "+server.getName()+" ("+server.getHostIP()+")");
		if(_sendingBroadcastTask == null) {
			Logger.debug("BroadcastManager","[startSendingServerBroadcast] Starting to send server broadcast for server: "+server.getName()+" ("+server.getHostIP()+")");
			try {
				_serverBroadcast = new ServerBasicInfo(server);
				_sendingBroadcastTask = new ServerBroadcastSender(getBroadcastAddress(), BROADCAST_PORT, BROADCAST_INTERVAL);
				new Thread(_sendingBroadcastTask).start();
			} catch (IOException ex) {
				Logger.error("BroadcastManager","[startSendingServerBroadcast] Unable to start sending server broadcast due to exception: " + ex.getMessage());
			}
		} else {
			Logger.debug("BroadcastManager","[startSendingServerBroadcast] Unable to start sending server broadcast - another server broadcast is already being send!");
		}
	}

	public void stopSendingServerBroadcast() {
		Logger.trace("BroadcastManager","[stopSendingServerBroadcast] Stop sending server broadcast requested");
		if(_sendingBroadcastTask != null) {
			Logger.debug("BroadcastManager","Stopping to send server broadcast for server: "+_serverBroadcast.getName()+" ("+_serverBroadcast.getHostIP()+")");
			_sendingBroadcastTask.cancel();
			_sendingBroadcastTask = null;
			synchronized (_serverBroadcast) {
				_serverBroadcast = null;
			}
		} else {
			Logger.debug("BroadcastManager","Stop sending server broadcast - no server broadcast is being send!");
		}
	}

	public void startListeningServerBroadcast(ServersDiscoveryListener serversDiscoveryListener) {
		Logger.trace("BroadcastManager","[startListeningServerBroadcast] Start listening for server broadcasts requested");
		if(_listeningBroadcastTask == null || _listeningBroadcastTask.isCancelled()) {
			try {
				Logger.debug("BroadcastManager","[startListeningServerBroadcast] Starting to listen for server broadcasts");
				_listeningBroadcastTask = new ServerBroadcastListener(getListeningAddress(), BROADCAST_PORT, BROADCAST_INTERVAL);
				_listeningBroadcastTask.registerServersListener(serversDiscoveryListener);
				new Thread(_listeningBroadcastTask).start();
			} catch (IOException ex) {
				Logger.debug("BroadcastManager","Unable to start listening server broadcast due to exception: " + ex.getMessage());
			}
		} else {
			Logger.debug("BroadcastManager","Unable to start listening for server broadcast - server broadcast listening is already active!");
		}
	}

	public void stopListeningServerBroadcast() {
		Logger.trace("BroadcastManager","[stopListeningServerBroadcast] Stop listening for server broadcasts requested");
		if(_listeningBroadcastTask != null && _listeningBroadcastTask.isRunning()) {
			Logger.debug("BroadcastManager","[stopListeningServerBroadcast] Stopping to listen for server broadcasts");
			_listeningBroadcastTask.cancel();
			//_listeningBroadcastTask = null;
		} else {
			Logger.debug("BroadcastManager","Stop listening for server broadcast - server broadcast listening is already inactive!");
		}
	}

	public List<ServerBasicInfo> getAllServers() {
		Logger.trace("BroadcastManager","[getAllServers] Found servers list requested");
		if(_listeningBroadcastTask!=null) {
			Logger.trace("BroadcastManager","[getAllServers] Obtaining servers list from broadcast listener...");
			return _listeningBroadcastTask.getAllServers();
		} else {
			return null;
		}
	}

	private InetAddress getBroadcastAddress() throws IOException {
		WifiManager wifi = (WifiManager) KFE.getContext().getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++){
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    }
	    Logger.debug("BroadcastManager","Broadcast address: " + InetAddress.getByAddress(quads));
	    return InetAddress.getByAddress(quads);
	}

	private InetAddress getListeningAddress() throws IOException {
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++){
			quads[k] = (byte) (0);
	    }
	    Logger.debug("BroadcastManager","Listening address: " + Inet4Address.getByAddress(quads));
	    return Inet4Address.getByAddress(quads);
	}

	public interface ServersDiscoveryListener {
		public void onNewServerDiscovered(ServerBasicInfo server);
		public void onServerInfoUpdated();
		public void onServerListeningInterrupted();
		public void onServerListeningStarted();
		public void onServerLost(ServerBasicInfo server);
	}

}
