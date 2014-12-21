package com.app.kfe.model.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tobikster on 20.12.14.
 */
public class GameMessage extends Message {
    public static final String JSON_KEY_MESSAGE_TYPE = "game_message_type";
    public static final String JSON_KEY_CONTENT = "content";

    private Type mGameMessageType;
    private JSONObject mContent;

    public GameMessage(String sourceHost, Type type, JSONObject content) {
        super(MessageType.GAME, sourceHost, TARGET_ALL);
        mGameMessageType = type;
        mContent = content;
    }

    public GameMessage(JSONObject message) throws JSONException{
        super(message);
        mGameMessageType = Type.valueOf(message.getString(JSON_KEY_MESSAGE_TYPE));
        mContent = message.getJSONObject(JSON_KEY_CONTENT);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject message = super.toJSON();
        message.put(JSON_KEY_MESSAGE_TYPE, getGameMessageType().toString());
        message.put(JSON_KEY_CONTENT, mContent);
        return message;
    }

    public Type getGameMessageType() {
        return mGameMessageType;
    }

    public JSONObject getContent() {
        return mContent;
    }

    public enum Type {
        GAME_START
    }
}
