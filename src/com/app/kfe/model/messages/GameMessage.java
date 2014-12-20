package com.app.kfe.model.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tobikster on 20.12.14.
 */
public class GameMessage extends Message {
    private Type mGameMessageType;

    public GameMessage(String sourceHost, Type type) {
        super(MessageType.GAME, sourceHost, TARGET_ALL);
        mGameMessageType = type;
    }

    public GameMessage(JSONObject message) throws JSONException{
        super(message);
        mGameMessageType = Type.valueOf(message.getString("gameMessageType"));
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject message = super.toJSON();
        message.put("gameMessageType", getGameMessageType().toString());
        return message;
    }

    public Type getGameMessageType() {
        return mGameMessageType;
    }

    public enum Type {
        GAME_START
    }
}
