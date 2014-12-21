package com.app.kfe.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.app.kfe.R;
import com.app.kfe.adapters.PlayersListAdapter;
import com.app.kfe.controler.GameManager;
import com.app.kfe.controler.RoomManager;
import com.app.kfe.controler.communication.ServerManager;
import com.app.kfe.main.KFE;
import com.app.kfe.model.Player;
import com.app.kfe.rysowanie.Tablica;
import org.json.JSONObject;

/**
 * Created by tobikster on 20.12.14.
 */
public class RoomFragment extends Fragment implements RoomManager.RoomMessagesListener, GameManager.GameMessagesListener {


    PlayersListAdapter mPlayersListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        ListView playersList = (ListView)(view.findViewById(R.id.players_list));
        mPlayersListAdapter = new PlayersListAdapter(KFE.getContext());
        playersList.setAdapter(mPlayersListAdapter);
        Button startGameButton = (Button)(view.findViewById(R.id.start_game_button));
        if(ServerManager.getInstance().isServerHost()) {
            startGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startActivity(new Intent(KFE.getContext(), Tablica.class));
                    GameManager.getInstance().startGame();
                }
            });
        }
        else {
            startGameButton.setEnabled(false);
            startGameButton.setText("Wait for game start");
        }
        RoomManager.getInstance().setRoomMessagesListener(this);
        GameManager.getInstance().setGameMessagesListener(this);
        return view;
    }

    @Override
    public void onPlayerJoinedRoom(Player player) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPlayersListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onGameStartMessageReceived(JSONObject gameObject) {
        Intent startGameIntent = new Intent(KFE.getContext(), Tablica.class);
        startGameIntent.putExtra("game_state", gameObject.toString());
        startActivity(startGameIntent);
    }
}