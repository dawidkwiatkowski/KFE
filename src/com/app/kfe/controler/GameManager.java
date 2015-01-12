package com.app.kfe.controler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.app.kfe.model.Player;
import com.app.kfe.model.messages.GameMessage;
import com.app.kfe.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import sqlite.model.Game;

import java.io.ByteArrayOutputStream;

/**
 * Created by tobikster on 20.12.14.
 */
public class GameManager {
    public static final int IMAGE_MAX_PART_SIZE = 1000;
    public static final String JSON_KEY_IMAGE = "image";

    private static GameManager singleton;

    private Game mGameInstance;
    private GameMessagesListener mGameMessagesListener;

    public static GameManager getInstance() {
        if(singleton == null) {
            synchronized (GameManager.class) {
                if (singleton == null)
                    singleton = new GameManager();
            }
        }
        return singleton;
    }

    private GameManager() {
        mGameInstance = null;
    }

    public void setGameMessagesListener(GameMessagesListener gameMessagesListener) {
        mGameMessagesListener = gameMessagesListener;
    }

    public void startGame() {
        try {
            mGameInstance = new Game(RoomManager.getInstance().getRoom().getPlayersList());
            mGameInstance.setState(Game.State.ACTIVE);
            Player player = RoomManager.getInstance().getPlayer();
            MessagesManager.getInstance().sendMessage(new GameMessage(player.getMACAddress(), GameMessage.Type.GAME_START, mGameInstance.toJSON()));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendCanvas(Bitmap image) throws JSONException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        String imageString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        int imagePartsCount = imageString.length() / IMAGE_MAX_PART_SIZE;
        Logger.debug("CanvasMessage", "length: " + imageString.length() + ", " + imagePartsCount + " parts");
        for(int i = 0; i < imagePartsCount; ++i) {
            JSONObject imageObject = new JSONObject();
            imageObject.put("partNumber", i);
            imageObject.put("partsCount", imagePartsCount);
            imageObject.put(JSON_KEY_IMAGE, imageString.substring(i * IMAGE_MAX_PART_SIZE, Math.min((i + 1) * IMAGE_MAX_PART_SIZE, imageString.length())));

            GameMessage message = new GameMessage(mGameInstance.getActivePlayer().getMACAddress(), GameMessage.Type.SEND_CANVAS, imageObject);
            Logger.debug("CanvasMessage", message.toJSON().toString());
            MessagesManager.getInstance().sendMessage(message);
        }
    }

    public void onCanvasMessageReceived(JSONObject imageObject){
        if(mGameMessagesListener != null) {
            try {
                byte[] decodedString = Base64.decode(imageObject.getString(JSON_KEY_IMAGE), Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mGameMessagesListener.onCanvasMessageReceived(image);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void restoreGame(JSONObject game) {
        try {
            mGameInstance = new Game(game);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onGameStartMessageReceived(JSONObject gameObject) {
        if(mGameInstance == null) {
            restoreGame(gameObject);
        }
        if(mGameMessagesListener != null) {
            mGameMessagesListener.onGameStartMessageReceived(gameObject);
        }
    }

    public interface GameMessagesListener {
        public void onGameStartMessageReceived(JSONObject gameObject);
        public void onCanvasMessageReceived(Bitmap image);
    }
}
