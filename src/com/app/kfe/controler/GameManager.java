package com.app.kfe.controler;

import com.app.kfe.model.Player;
import com.app.kfe.model.messages.GameMessage;

/**
 * Created by tobikster on 20.12.14.
 */
public class GameManager {
    private static GameManager singleton;

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

    }

    public void startGame() {
        Player player = RoomManager.getInstance().getPlayer();
        MessagesManager.getInstance().sendMessage(new GameMessage(player.getMACAddress(), GameMessage.Type.GAME_START));
    }
}
