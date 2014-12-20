package com.app.kfe.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.app.kfe.R;
import com.app.kfe.adapters.ServersListAdapter;
import com.app.kfe.controler.communication.BroadcastManager;
import com.app.kfe.controler.communication.ServerManager;
import com.app.kfe.main.KFE;
import com.app.kfe.model.ServerBasicInfo;
import com.app.kfe.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobikster on 18.12.14.
 */
public class RoomSelectionFragment extends Fragment implements BroadcastManager.ServersDiscoveryListener {
    private ServersListAdapter mServersListAdapter;
    private RoomSelectionListener mRoomSelectionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_selection, container, false);
        ListView serversList = (ListView) (view.findViewById(R.id.rooms_list));
        mServersListAdapter = new ServersListAdapter(KFE.getContext(), new ArrayList<ServerBasicInfo>());
        serversList.setAdapter(mServersListAdapter);
        serversList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                List<ServerBasicInfo> servers = BroadcastManager.getInstance().getAllServers();
                if (servers != null && servers.size() > 0) {
                    mRoomSelectionListener.onRoomSelected(servers.get(arg2));
                }
                else {
                    Logger.warning("RoomSelectionFragment", "[onItemClick] List data set is out-of-date! Item clicked should not exist!");
                    // TODO Refresh servers list
                }
            }
        });
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
    public void onNewServerDiscovered(final ServerBasicInfo server) {
        Logger.trace("RoomSelectionFragment", "[onNewServerDiscovered] New server discovered: " + server.getName() + " (" + server.getHostIP() + ")");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mServersListAdapter.addServerInfo(server);
            }
        });
        if(mRoomSelectionListener != null) {
            mRoomSelectionListener.onNewRoomDiscovered(server);
        } else {
            Logger.error("RoomSelectionFragment","[onNewServerDiscovered] No server discovery listener has been registered!");
        }

    }

    @Override
    public void onServerInfoUpdated() {
        mServersListAdapter.notifyDataSetChanged();
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
        public void onNewRoomDiscovered(ServerBasicInfo server);
        public void onRoomHost();
        public void onRoomInfoUpdated();
        public void onRoomLost(ServerBasicInfo server);
        public void onRoomSearchInterrupted();
        public void onRoomSearchStarted();
        public void onRoomSelected(ServerBasicInfo server);
    }
}