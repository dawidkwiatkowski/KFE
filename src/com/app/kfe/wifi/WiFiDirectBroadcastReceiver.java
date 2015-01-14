/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.kfe.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import com.app.kfe.R;
import com.app.kfe.rysowanie.Tablica;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private static WiFiDirectBroadcastReceiver singleton;
    private WifiP2pManager manager;
    private Channel channel;
    private Activity activity;
    DeviceListFragment fragment;

    public static WiFiDirectBroadcastReceiver getInstance(WifiP2pManager manager, Channel channel, Activity activity) {
        if(singleton == null) {
            synchronized (WiFiDirectBroadcastReceiver.class) {
                if (singleton == null)
                    singleton = new WiFiDirectBroadcastReceiver();
            }
        }
        singleton.setManager(manager);
        singleton.setChannel(channel);
        singleton.setActivity(activity);
        return singleton;
    }

    public  WiFiDirectBroadcastReceiver() {
        super();
        this.fragment = (DeviceListFragment) WiFiDirectActivity.activity.getFragmentManager().findFragmentById(R.id.frag_list);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setManager(WifiP2pManager manager) {
        this.manager = manager;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    /*
                 * (non-Javadoc)
                 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
                 * android.content.Intent)
                 */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                if (activity.equals(WiFiDirectActivity.activity))
                    ((WiFiDirectActivity) activity).setIsWifiP2pEnabled(true);
                else if (activity.equals(Tablica.activity)) {
                    ((Tablica) activity).setIsWifiP2pEnabled(true);
                }
            } else {
                if (activity.equals(WiFiDirectActivity.activity)) {
                    ((WiFiDirectActivity) activity).setIsWifiP2pEnabled(false);
                    ((WiFiDirectActivity) activity).resetData();
                } else if (activity.equals(Tablica.activity)) {
                    ((Tablica) activity).setIsWifiP2pEnabled(false);
                    ((Tablica) activity).resetData();
                }

            }
            Log.d(WiFiDirectActivity.TAG, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, (PeerListListener) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_list));
            }
            Log.d(WiFiDirectActivity.TAG, "P2P peers changed");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP

                DeviceDetailFragment fragment = (DeviceDetailFragment) activity.getFragmentManager().findFragmentById(R.id.frag_detail);
                manager.requestConnectionInfo(channel, fragment);
            } else {
                // It's a disconnect
                if (activity.equals(WiFiDirectActivity.activity)) {
                    ((WiFiDirectActivity) activity).resetData();
                } else if (activity.equals(Tablica.activity)) {
                    ((Tablica) activity).resetData();
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            if (activity.equals(WiFiDirectActivity.activity)) {

                fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
            } else if (activity.equals(Tablica.activity)) {

                fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
            }

        }
    }
}
