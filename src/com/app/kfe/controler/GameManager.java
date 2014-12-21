package com.app.kfe.controler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.app.kfe.controler.communication.ServerManager;
import com.app.kfe.model.Player;
import com.app.kfe.model.messages.GameMessage;
import org.json.JSONException;
import org.json.JSONObject;
import sqlite.model.Game;

import java.io.ByteArrayOutputStream;

/**
 * Created by tobikster on 20.12.14.
 */
public class GameManager {
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
    }
}
