package com.app.kfe.controler;

import com.app.kfe.controler.communication.ServerManager;
import com.app.kfe.model.messages.Message;
import com.app.kfe.model.messages.MessageType;
import com.app.kfe.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class MessagesManager {

	private static MessagesManager _instance;

	public static MessagesManager getInstance() {
		if(_instance == null) {
			 synchronized (MessagesManager.class) {
	               if (_instance == null) {
	            	   _instance = new MessagesManager();
	               }
			 }
		}
		return _instance;
	}

	private MessagesManager() {
		_instance = null;
	}

	public void handleMessage(JSONObject message) {
		MessageType type;
		try {
			type = MessageType.valueOf(message.getString("type"));
			Logger.debug("MessagesManager", "Handling message (" + type + "): " + message);
			switch(type){
				default:
					break;
			}
		} catch (JSONException e) {
			Logger.error("MessagesManager", "Cannot handle message due to exception: "+e.getMessage());
		}
	}

	public void sendMessage(Message message) {
		try {
			ServerManager.getInstance().sendMessage(message.toJSON());
		} catch(JSONException e) {
			Logger.error("MessagesManager", "Cannot send message due to exception: "+e.getMessage());
		}
	}

}
