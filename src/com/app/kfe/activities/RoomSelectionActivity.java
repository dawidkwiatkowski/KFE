package com.app.kfe.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;
import com.app.kfe.R;
import com.app.kfe.adapters.ServersListAdapter;
import com.app.kfe.controler.communication.BroadcastManager;
import com.app.kfe.controler.communication.ServerManager;
import com.app.kfe.fragments.RoomSelectionFragment;
import com.app.kfe.main.KFE;
import com.app.kfe.model.ServerBasicInfo;
import com.app.kfe.utils.Logger;

/**
 * Created by tobikster on 18.12.14.
 */
public class RoomSelectionActivity extends Activity implements RoomSelectionFragment.RoomSelectionListener, ServerManager.ConnectionRequester {
    private RoomSelectionFragment mRoomSelectionFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_selection);
        mRoomSelectionFragment = (RoomSelectionFragment)(getFragmentManager().findFragmentById(R.id.fragment_room_selection));
        mRoomSelectionFragment.setRoomSelectionListener(this);
    }

    @Override
    public void onNewRoomDiscovered(ServersListAdapter serverList, ServerBasicInfo server) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(KFE.getContext(), "Room discovered!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRoomHost() {
        Toast.makeText(KFE.getContext(), "Tworzenie pokoju...", Toast.LENGTH_LONG).show();
        Logger.debug("StartupActivity", "[hostRoom] Stoping to listen for server broadcast");
        BroadcastManager.getInstance().stopListeningServerBroadcast();
        ServerManager.getInstance().startServer("test", this);
    }

    @Override
    public void onRoomInfoUpdated(ServersListAdapter serverList) {

    }

    @Override
    public void onRoomLost(ServersListAdapter serverList, ServerBasicInfo server) {

    }

    @Override
    public void onRoomSearchInterrupted() {

    }

    @Override
    public void onRoomSearchStarted(ServersListAdapter serverList) {

    }

    @Override
    public void onRoomSelected(ServerBasicInfo server) {

    }

    @Override
    public void showProgress(int taskResId) {

    }

    @Override
    public void onConnectionEstablished() {

    }

    @Override
    public void onConnectionFailed(int msgResId) {

    }
}