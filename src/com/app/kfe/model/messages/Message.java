package com.app.kfe.model.messages;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Message {

	public static final int TARGET_ALL = 10;
	public static final int TARGET_HOST = 11;
	public static final int TARGET_NOT_HOST = 12;
	public static final int TARGET_PLAYER = 13;
	public static final int TARGET_SELF = 14;

	private final MessageType _type;
	private final String _sourceHost;
	private final String _targetHost;
	private final int _target;

	public Message(MessageType type, String sourceHost, int target) {
		_type = type;
		_sourceHost = sourceHost;
		_target = target==TARGET_PLAYER?TARGET_ALL:target;
		_targetHost = null;
	}

	public Message(MessageType type, String sourceHost, String targetHost) {
		_type = type;
		_sourceHost = sourceHost;
		_target = TARGET_PLAYER;
		_targetHost = targetHost;
	}

	public Message(JSONObject message) throws JSONException {
		_type = MessageType.valueOf(message.getString("type"));
		_target = message.getInt("target");
		_sourceHost = message.getString("source_host");
		if(message.isNull("target_host")) {
			_targetHost = null;
		} else {
			_targetHost = message.getString("target_host");
		}
	}

	public String getSourceHost() {
		return _sourceHost;
	}

	public int getTarget() {
		return _target;
	}

	public String getTargetHost() {
		return _targetHost;
	}

	public MessageType getType() {
		return _type;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject message = new JSONObject();
		message.put("type", getType());
		message.put("target", getTarget());
		message.put("source_host", getSourceHost());
		if(getTargetHost()!=null) {
			message.put("target_host", getTargetHost());
		}
		return message;
	}

}
