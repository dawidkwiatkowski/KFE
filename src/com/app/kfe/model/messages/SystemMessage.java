package com.app.kfe.model.messages;

import java.util.Date;

import com.app.kfe.main.KFE;
import com.app.kfe.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;


public class SystemMessage extends Message implements LobbyMessage {

	private final String _message;
	private final Date _sendDate;

	public SystemMessage(String message) {
		super(MessageType.SYSTEM, "", Message.TARGET_SELF);
		_message = message;
		_sendDate = new Date();
	}

	public SystemMessage(int msgResId) {
		super(MessageType.SYSTEM, "", Message.TARGET_SELF);
		_message = KFE.getContext().getString(msgResId);
		_sendDate = new Date();
	}

	public SystemMessage(JSONObject message) throws JSONException {
		super(message);
		_message = message.getString("content");
		_sendDate = Utils.dateFromString(message.getString("date"));
	}

	@Override
	public String getContent() {
		return _message;
	}

	@Override
	public Date getSendDate() {
		return _sendDate;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject message = super.toJSON();
		message.put("content", _message);
		message.put("date", Utils.dateToString(_sendDate));
		return message;
	}

}
