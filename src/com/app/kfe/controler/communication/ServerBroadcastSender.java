package com.app.kfe.controler.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.app.kfe.utils.Logger;
import org.json.JSONException;

public class ServerBroadcastSender implements Runnable {

	private DatagramSocket _sendingSocket;
	private InetAddress _broadcastAddress;
	private int _broadcastPort;
	private int _broadcastInterval;
	private boolean _isCancelled;

	public ServerBroadcastSender(InetAddress broadcastAddress, int broadcastPort, int broadcastInterval) {
		_broadcastAddress = broadcastAddress;
		_broadcastPort = broadcastPort;
		_broadcastInterval = broadcastInterval;
		_isCancelled = false;
	}
	

	public void cancel() {
		_isCancelled = true;
	}

	private void handleTaskCanceled() {
		
	}

	@Override
	public void run() {
		try {
			_sendingSocket = new DatagramSocket();
			_sendingSocket.setReuseAddress(true);
			_sendingSocket.setBroadcast(true);
			_sendingSocket.connect(_broadcastAddress, _broadcastPort);
			while(true) {
				String serverData = BroadcastManager.getInstance().getServerBroadcastInfo().toJSON().toString();
				DatagramPacket packet = new DatagramPacket(serverData.getBytes(), serverData.length(), _broadcastAddress, _broadcastPort);
				_sendingSocket.send(packet);
				Logger.debug("ServerBroadcastSender", "Wyslano broadcast na adres: " + _broadcastAddress + " (PORT: " + _broadcastPort + ")");
				Thread.sleep(_broadcastInterval);
				if (_isCancelled) {
					handleTaskCanceled();
					break;
				}
			}
		} catch (SocketException e) {
			Logger.error("ServerBroadcastSender","Error sending server broadcast (SocketException): " + e.getMessage());
		} catch (IOException e) {
			Logger.error("ServerBroadcastSender","Error sending server broadcast (IOException): " + e.getMessage());
		} catch (InterruptedException e) {
			Logger.error("ServerBroadcastSender","Error sending server broadcast (InterruptedException): " + e.getMessage());
		} catch (JSONException e) {
			Logger.error("ServerBroadcastSender","Error sending server broadcast (JSONException): " + e.getMessage());
		}
	}

}
