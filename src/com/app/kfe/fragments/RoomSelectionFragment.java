package com.app.kfe.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import com.app.kfe.R;
import com.app.kfe.adapters.ServersListAdapter;
import com.app.kfe.controler.communication.BroadcastManager;
import com.app.kfe.model.ServerBasicInfo;
import com.app.kfe.utils.Logger;

/**
 * Created by tobikster on 18.12.14.
 */
public class RoomSelectionFragment extends Fragment implements BroadcastManager.ServersDiscoveryListener {
    private RoomSelectionListener mRoomSelectionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_selection, container, false);
        Button createRoomButton = (Button) (view.findViewById(R.id.button_create_room));
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.trace("RoomSelectionFragment", "[onClick] Host server button clicked");
                if (mRoomSelectionListener != null) {
                    mRoomSelectionListener.onRoomHost();
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        Logger.trace("RoomSelectionFragment", "[onResume] Starting to listen for servers broadcasts");
        BroadcastManager.getInstance().startListeningServerBroadcast(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        Logger.trace("RoomSelectionFragment","[onPause] Stoping to listen for servers broadcasts");
        BroadcastManager.getInstance().stopListeningServerBroadcast();
        super.onPause();
    }

    public void setRoomSelectionListener(RoomSelectionListener listener) {
        mRoomSelectionListener = listener;
    }

    @Override
    public void onNewServerDiscovered(ServerBasicInfo server) {
        Logger.trace("RoomSelectionFragment","[onNewServerDiscovered] New server discovered: "+server.getName()+" ("+server.getHostIP()+")");
        if(mRoomSelectionListener != null) {
            mRoomSelectionListener.onNewRoomDiscovered(null, server);
        } else {
            Logger.error("RoomSelectionFragment","[onNewServerDiscovered] No server discovery listener has been registered!");
        }

    }

    @Override
    public void onServerInfoUpdated() {

    }

    @Override
    public void onServerListeningInterrupted() {

    }

    @Override
    public void onServerListeningStarted() {

    }

    @Override
    public void onServerLost(ServerBasicInfo server) {

    }

    public interface RoomSelectionListener {
        public void onNewRoomDiscovered(ServersListAdapter serverList, ServerBasicInfo server);
        public void onRoomHost();
        public void onRoomInfoUpdated(ServersListAdapter serverList);
        public void onRoomLost(ServersListAdapter serverList, ServerBasicInfo server);
        public void onRoomSearchInterrupted();
        public void onRoomSearchStarted(ServersListAdapter serverList);
        public void onRoomSelected(ServerBasicInfo server);
    }
}