package sqlite.model;

import com.app.kfe.model.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobikster on 21.12.14.
 */
public class Game {
    public static final String JSON_KEY_PLAYERS = "players";
    public static final String JSON_KEY_ACTIVE_PLAYER_INDEX = "active_player";
    public static final String JSON_KEY_STATE = "game_state";

    private List<Player> mPlayers;
    private State mState;
    private int mActivePlayerIndex;

    public Game(){
        this(new ArrayList<Player>());
    }

    public Game(List<Player> players) {
        mPlayers = players;
        mState = State.PREPARING;
        mActivePlayerIndex = 0;
    }

    public Game(JSONObject gameObject) throws JSONException {
        JSONArray players = gameObject.getJSONArray(JSON_KEY_PLAYERS);
        mPlayers = new ArrayList<Player>(players.length());
        for(int i = 0; i < players.length(); ++i) {
            mPlayers.add(new Player(players.getJSONObject(i)));
        }

        mActivePlayerIndex = gameObject.getInt(JSON_KEY_ACTIVE_PLAYER_INDEX);
        mState = State.valueOf(gameObject.getString(JSON_KEY_STATE));
    }

    public void setPlayers(List<Player> players) {
        mPlayers = players;
    }

    public void setState(State state) {
        mState = state;
    }

    public int getActivePlayerIndex() {
        return mActivePlayerIndex;
    }

    public void setActivePlayerIndex(int activePlayerIndex) {
        mActivePlayerIndex = activePlayerIndex;
    }

    public Player getActivePlayer() {
        return mPlayers.get(mActivePlayerIndex);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject game = new JSONObject();

        JSONArray players = new JSONArray();
        for(Player player : mPlayers) {
            players.put(player.toJSON());
        }

        game.put(JSON_KEY_PLAYERS, players);
        game.put(JSON_KEY_ACTIVE_PLAYER_INDEX, mActivePlayerIndex);
        game.put(JSON_KEY_STATE, mState);

        return game;
    }

    public enum State {
        PREPARING,
        ACTIVE,
        PAUSED,
        FINISHED
    }
}
