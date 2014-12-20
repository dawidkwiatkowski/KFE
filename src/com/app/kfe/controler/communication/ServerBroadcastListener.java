package com.app.kfe.controler.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.app.kfe.main.KFE;
import com.app.kfe.model.ServerBasicInfo;
import com.app.kfe.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

public class ServerBroadcastListener implements Runnable {

	private static final int READ_TIMEOUT = 500;

	private BroadcastManager.ServersDiscoveryListener _serversListener;
	private Map<Date, ServerBasicInfo> _foundServers;
	private BroadcastsValidator _validator;
	private Thread _validatorThread;
	private DatagramSocket _listeningSocket;
	private InetAddress _listeningAddress;
	private int _listeningPort;
	private int _listeningInterval;
	private boolean _isCancelled;
	private boolean _isRunning;
	private final int BUFFER_SIZE = 1024;
	private DatagramPacket _packet;
	private String _localIP;

	public ServerBroadcastListener(InetAddress listeningAddress, int listeningPort, int listeningInterval) {
		_foundServers = new LinkedHashMap<Date, ServerBasicInfo>();
		_listeningAddress = listeningAddress;
		_listeningPort = listeningPort;
		_listeningInterval = listeningInterval;
		_isCancelled = false;
		_isRunning = false;
	}

	public void cancel() {
		_isCancelled = true;
	}

	public List<ServerBasicInfo> getAllServers() {
		synchronized (_foundServers) {
			Logger.trace("ServerBroadcastListener", "[getAllServers] Returning " + _foundServers.values().size() + " servers");
			return new ArrayList<ServerBasicInfo>(_foundServers.values());
		}
	}

	public boolean isCancelled() {
		return _isCancelled;
	}

	public boolean isRunning() {
		return _isRunning;
	}

	public void registerServersListener(BroadcastManager.ServersDiscoveryListener listener) {
		_serversListener = listener;
	}

	private boolean checkIfServerInfoChanged(ServerBasicInfo oldInfo, ServerBasicInfo newInfo) {
		if(!oldInfo.getName().equals(newInfo.getName())) {
			return true;
		} else if(oldInfo.getPlayersCount() != (newInfo.getPlayersCount())) {
			return true;
		}
		return false;
	}

	private void handleNewServerDiscovered(ServerBasicInfo serverInfo) {
		Logger.trace("ServerBroadcastListener", "[handleNewServerDiscovered] New server: " + serverInfo);
		if(!_localIP.equals(serverInfo.getHostIP())) {
			synchronized(_foundServers) {
				boolean containsServer = false;
				for(Entry<Date, ServerBasicInfo> e : _foundServers.entrySet()) {
					if(e.getValue().equals(serverInfo)) {
						if(checkIfServerInfoChanged(e.getValue(), serverInfo)) {
							updateServerInfo(e.getValue(), serverInfo);
							_serversListener.onServerInfoUpdated();
						}
						e.getKey().setTime(new Date().getTime());
						containsServer = true;
						break;
					}
				}
				if(!containsServer) {
					_foundServers.put(new Date(), serverInfo);
					if(_serversListener != null) {
						_serversListener.onNewServerDiscovered(serverInfo);
					}
				}
			}
		}
	}

	private void handleTaskCanceled() {
		Logger.debug("ServerBroadcastListener","[handleTaskCanceled] Task has been cancelled!");
		_validator.cancel();
		_validator = null;
		if(_listeningSocket!=null && !_listeningSocket.isClosed()) {
			synchronized(_listeningSocket) {
				_listeningSocket.close();
			}
		}
		if(_serversListener != null) {
			_serversListener.onServerListeningInterrupted();
		}
//		synchronized(_foundServers) {
//			_foundServers.clear();
//		}
	}

	private void handleTaskInterrupted() {
		if(_validator!=null) {
			_validator.cancel();
			if(_validatorThread!=null) {
				_validatorThread = null;
			}
		}
	}

	private void updateServerInfo(ServerBasicInfo oldInfo, ServerBasicInfo newInfo) {
		oldInfo.setName(newInfo.getName());
		oldInfo.setPlayersCount(newInfo.getPlayersCount());
	}

	@Override
	public void run() {
		_isRunning = true;
		DhcpInfo dhcp = ((WifiManager) KFE.getContext().getSystemService(Context.WIFI_SERVICE)).getDhcpInfo();
		_localIP = ServerManager.getInstance().getIpAddressFromInt(dhcp.ipAddress);
		byte[] _dataBuffer = new byte[BUFFER_SIZE];
		if(_serversListener != null) {
			_serversListener.onServerListeningStarted();
		}
		try {
			Thread.sleep(_listeningInterval);
			_validator = new BroadcastsValidator(2*_listeningInterval, 2*_listeningInterval);
			_validatorThread = new Thread(_validator);
			_validatorThread.start();
			
			//DatagramSocket ds = new DatagramSocket(BROADCAST_PORT);
			DatagramChannel channel = DatagramChannel.open();
			_listeningSocket = channel.socket();
			//_listeningSocket = new DatagramSocket();
			_listeningSocket.setReuseAddress(true);
			//_listeningSocket.connect(_listeningAddress, BROADCAST_PORT);
			_listeningSocket.bind(new InetSocketAddress(_listeningAddress, _listeningPort));
			_listeningSocket.setSoTimeout(READ_TIMEOUT);
			//Logger.debug("BroadcastManager","Adres nas�uchiwania: "+_listeningSocket.getLocalAddress() + "\t\tSHOULD BE: "+ds.getLocalAddress()+")");
			Logger.debug("ServerBroadcastListener","Oczekiwanie na broadcast z adresu " +_listeningSocket.getLocalAddress()+ " (PORT: "+_listeningSocket.getLocalPort()+")");
			_packet = new DatagramPacket(_dataBuffer, _dataBuffer.length);
			while(true) {
				synchronized (_listeningSocket) {
					if(!_listeningSocket.isClosed()) {
						try {
							Logger.debug("ServerBroadcastListener", "Receiving server broadcasts...");
							_listeningSocket.receive(_packet);
							String dataString = new String(_dataBuffer, 0, _packet.getLength());
							Logger.debug("ServerBroadcastListener","Odebrano broadcast z adresu " +_listeningSocket.getLocalAddress()+ " (PORT: "+_listeningSocket.getLocalPort()+")\n" + dataString);
							handleNewServerDiscovered(new ServerBasicInfo(new JSONObject(dataString)));
						} catch(IOException e) {
							//READ TIMEOUT
							Logger.debug("ServerBroadcastListener", "Listening broadcast timeout!");
						}
					}
				}
				Thread.sleep(_listeningInterval);
				if(_isCancelled) {
					handleTaskCanceled();
					_validatorThread.join();
					_validatorThread = null;
					break;
				}
			}
		} catch (SocketException e) {
			Logger.error("ServerBroadcastListener","Error listening server broadcast (SocketException): "+e.getMessage());
			handleTaskInterrupted();
		} catch (IOException e) {
			Logger.error("ServerBroadcastListener","Error listening server broadcast (IOException): "+e.getMessage());
			handleTaskInterrupted();
		} catch (InterruptedException e) {
			Logger.error("ServerBroadcastListener","Error listening server broadcast (InterruptedException): "+e.getMessage());
			handleTaskInterrupted();
		} catch (JSONException e) {
			Logger.error("ServerBroadcastListener","Error listening server broadcast (JSONException): "+e.getMessage());
			handleTaskInterrupted();
		}
		_isRunning = false;
	}

	private class BroadcastsValidator implements Runnable {

		private final int _expirationTime;
		private boolean _isCancelled;
		private final int _validationInterval;

		public BroadcastsValidator(int interval, int expirationTime) {
			_expirationTime = expirationTime;
			_validationInterval = interval;
		}

		public void cancel() {
			_isCancelled = true;
		}

		@Override
		public void run() {
			Logger.trace("ServerBroadcastListener", "[BroadcastsValidator] STARTED servers broadcasts validation");
			while(true) {
				try {
					synchronized(_foundServers) {
						Map<Date, ServerBasicInfo> notExpiredList = new LinkedHashMap<Date, ServerBasicInfo>();
						Calendar c = Calendar.getInstance();
					    c.setTime(new Date());
					    c.add(Calendar.MILLISECOND, -_expirationTime);
					    Logger.debug("ServerBroadcastListener","[BroadcastsValidator] Current broadcasts count: " + _foundServers.size());
					    for(Entry<Date, ServerBasicInfo> e : _foundServers.entrySet()) {
					    	Logger.debug("ServerBroadcastListener", "[BroadcastsValidator] SB: " + e.getKey() + " (" + e.getValue().toString() + ")");
					    	if(e.getKey().after(c.getTime())) {
					    		notExpiredList.put(e.getKey(), e.getValue());
					    	} else {
					    		Logger.debug("ServerBroadcastListener","[BroadcastsValidator] Detected expired broadcast: '" + e.getValue().getName() + "' (" + e.getValue().getHostIP() + ")");
					    		_serversListener.onServerLost(e.getValue());
					    	}
					    }
					    _foundServers.clear();
					    _foundServers.putAll(notExpiredList);
					    //Logger.debug("ServerBroadcastListener","[BroadcastsValidator] Removed expired broadcasts - current broadcasts count: " + _foundServers.size());
					}
					Thread.sleep(_validationInterval);
					if(_isCancelled) {
						break;
					}
				} catch (InterruptedException e) {
					Logger.error("ServerBroadcastListener","[BroadcastsValidator] Error validating server broadcasts (InterruptedException): "+e.getMessage());
					handleTaskInterrupted();
				}
			}
			Logger.trace("ServerBroadcastListener", "[BroadcastsValidator] STOPPED servers broadcasts validation");
		}

	}

}
