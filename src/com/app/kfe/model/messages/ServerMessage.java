package com.app.kfe.model.messages;

import com.app.kfe.model.Player;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerMessage extends Message {

	private ServerMessageType _command;
	private JSONObject _content;

	public ServerMessage(ServerMessageType command, int target, String sourceHost) {
		super(MessageType.SERVER, sourceHost, (target==TARGET_PLAYER?TARGET_ALL:target));
		_command = command;
	}

	public ServerMessage(ServerMessageType command, int target, Player sourceHost) {
		super(MessageType.SERVER, sourceHost.getMACAddress(), (target==TARGET_PLAYER?TARGET_ALL:target));
		_command = command;
	}

	public ServerMessage(ServerMessageType command, String sourceHost, String targetHost) {
		super(MessageType.SERVER, sourceHost, targetHost);
		_command = command;
	}

	public ServerMessage(ServerMessageType command, Player sourceHost, Player targetHost) {
		super(MessageType.SERVER, sourceHost.getMACAddress(), targetHost.getMACAddress());
		_command = command;
	}

	public ServerMessage(JSONObject message) throws JSONException {
		super(message);
		_command = ServerMessageType.valueOf(message.getString("command"));
		if(!message.isNull("content")) {
			_content = message.getJSONObject("content");
		}
	}

	public ServerMessageType getCommand() {
		return _command;
	}

	public void setContent(JSONObject content) {
		_content = content;
	}

	public JSONObject getContent() {
		return _content;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject message = super.toJSON();
		message.put("command", getCommand().toString());
		if(_content!=null) {
			message.put("content", _content);
		}
		return message;
	}

	@Override
	public String toString() {
		String message = "";
		message += "[command=" + _command + "] ";
		message += "[content=" + (_content==null?"null":_content.toString()) + "] ";
		return message;
	}

}
