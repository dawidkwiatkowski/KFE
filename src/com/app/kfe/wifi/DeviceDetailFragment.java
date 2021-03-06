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

import android.R.string;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.kfe.R;import com.app.kfe.dialogs.EndGameDialog;
import com.app.kfe.rysowanie.PaintView;
import com.app.kfe.rysowanie.Tablica;
import com.app.kfe.wifi.FileTransferService;
import com.app.kfe.wifi.DeviceListFragment.DeviceActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

	public static final String IP_SERVER = "192.168.49.1";
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    public static View mContentView = null;
    public static  WifiP2pDevice device;
    public static  WifiP2pDevice device2;
    public static WifiP2pInfo info;
    public static Intent serviceIntent;
    public ProgressDialog progressDialog = null;
    public static Bitmap bm = null;
    public static PaintView pv;
    public static String localIP;
    public static String client_mac_fixed;
    public static String clientIP;
    public static String gamer;
    public static String opponent=null;
    public static String code;
    public static String haslo;
    private Button but;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	
        mContentView = inflater.inflate(R.layout.device_detail, null);
       
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
        
            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true);
                ((DeviceActionListener) getActivity()).connect(config);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
                    	new ForClientServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
                        .execute();
                    	localIP = Utils.getLocalIPAddress();

                    	serviceIntent = new Intent(getActivity(), FileTransferService.class);
                        serviceIntent.setAction(FileTransferService.ACTION_OPEN_TABLICA);
                        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");
                        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
                        //        info.groupOwnerAddress.getHostAddress());
                        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                         
                        getActivity().startService(serviceIntent);
                        
                        gamer = "Gracz1";
                        
                        Intent intent = new Intent(getActivity(),Tablica.class);
                        intent.putExtra("isGame", true);
                        startActivity(intent);
                    	
                    	
                    }
                });

        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
       
        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
        	new TextServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
            	.execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }
        else{
        	new ForClientServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
            .execute();
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment.device = device;
        DeviceDetailFragment.device2 = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
      

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }
    
	public static void sendCanvasService(boolean is_owner ){
		
		//DeviceDetailFragment.device = DeviceListFragment.getDevice();
		
	localIP = Utils.getLocalIPAddress();
			
		Intent serviceIntent = new Intent(Tablica.activity, FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_CANVAS);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");
        //serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
        		//DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());
        //serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        
        if(localIP.equals(IP_SERVER)){
        	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);
        	}else{
        	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
        	}
        if(is_owner)
        {
        	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8989);
        }
        else
        {
        	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        }
        Tablica.activity.startService(serviceIntent); 
	}
	
public static void sendClearScreenService(boolean is_owner ){
		
		//DeviceDetailFragment.device = DeviceListFragment.getDevice();
		
	localIP = Utils.getLocalIPAddress();
			
		Intent serviceIntent = new Intent(Tablica.activity, FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_CLEAR_SCREEN);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");
        //serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
        		//DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());
        //serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        
        if(localIP.equals(IP_SERVER)){
        	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);
        	}else{
        	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
        	}
        if(is_owner)
        {
        	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8989);
        }
        else
        {
        	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        }
        Tablica.activity.startService(serviceIntent); 
	}
	
public static void sendGamerNameService(boolean isOwner){
		
		//DeviceDetailFragment.device = DeviceListFragment.getDevice();
		
	localIP = Utils.getLocalIPAddress();
			
		Intent serviceIntent = new Intent(Tablica.activity, FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_NAME);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");

    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);


    	 if(isOwner)
         {
         	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8989);
         }
         else
         {
         	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
         }

        Tablica.activity.startService(serviceIntent); 
	}
public static void requestGamerNameService(boolean isOwner){
	
	//DeviceDetailFragment.device = DeviceListFragment.getDevice();
	
localIP = Utils.getLocalIPAddress();
		
	Intent serviceIntent = new Intent(Tablica.activity, FileTransferService.class);
    serviceIntent.setAction(FileTransferService.ACTION_REQUEST_NAME);
    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");

	//serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);
	 
	if(localIP.equals(IP_SERVER)){
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);
	    	}else{
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
	    	}

	 if(isOwner)
     {
     	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8989);
     }
     else
     {
     	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
     }

    Tablica.activity.startService(serviceIntent); 
}




public static void sendEndRoundService(boolean is_owner, boolean giveUp ){
	
	localIP = Utils.getLocalIPAddress();
	
	Intent serviceIntent = new Intent(Tablica.activity, FileTransferService.class);
	if(!giveUp)
	{
		serviceIntent.setAction(FileTransferService.ACTION_END_ROUND);
	}
	else
	{
		serviceIntent.setAction(FileTransferService.ACTION_GIVE_UP);
	}
    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");

    
    if(localIP.equals(IP_SERVER)){
    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);
    	}else{
    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
    	}
    if(is_owner)
    {
    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8989);
    }
    else
    {
    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
    }
    Tablica.activity.startService(serviceIntent); 
}


	public static void sendWordService(boolean is_owner, String haslo ){
				
		localIP = Utils.getLocalIPAddress();
		DeviceDetailFragment.haslo = haslo;	
		Intent serviceIntent = new Intent(Tablica.activity, FileTransferService.class);
	    serviceIntent.setAction(FileTransferService.ACTION_SEND_WORD);
	    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");

	    
	    if(localIP.equals(IP_SERVER)){
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);
	    	}else{
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
	    	}
	    if(is_owner)
	    {
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8989);
	    }
	    else
	    {
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
	    }
	    Tablica.activity.startService(serviceIntent); 
	}
	
	public static void resendWordService(boolean is_owner, String haslo ){
		
		localIP = Utils.getLocalIPAddress();
		DeviceDetailFragment.haslo = haslo;	
		Intent serviceIntent = new Intent(Tablica.activity, FileTransferService.class);
	    serviceIntent.setAction(FileTransferService.ACTION_RESEND_WORD);
	    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");

	    
	    if(localIP.equals(IP_SERVER)){
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);
	    	}else{
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
	    	}
	    if(is_owner)
	    {
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8989);
	    }
	    else
	    {
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
	    }
	    Tablica.activity.startService(serviceIntent); 
	}
	
	public static void sendEndGameService(boolean is_owner ){
		
		localIP = Utils.getLocalIPAddress();
		
		Intent serviceIntent = new Intent(Tablica.activity, FileTransferService.class);
	    serviceIntent.setAction(FileTransferService.ACTION_LEAVE_GAME);
	    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");

	    
	    if(localIP.equals(IP_SERVER)){
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, clientIP);
	    	}else{
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
	    	}
	    if(is_owner)
	    {
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8989);
	    }
	    else
	    {
	    	serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
	    }
	    Tablica.activity.startService(serviceIntent); 
	}
	
	 public static class ForClientServerAsyncTask extends AsyncTask<Void, Void, String> {

	        private Context context;
	        private TextView statusText;

	        /**
	         * @param context
	         * @param statusText
	         */
	        public ForClientServerAsyncTask(Context context, View statusText) {
	            this.context = context;
	            this.statusText = (TextView) statusText;
	        }
	        protected byte[] getCodeByteArray(byte[] table){
	    		if(table.length >= 2){
	    			byte[] result = new byte[2];
	    			for(int i =0; i < 2; i++){
	    				result[i] = table[i];
	    			}
	    			return result;
	    		}
	    		else{
	    			return table;
	    		}
	    	}
	    	
	    	protected byte[] getMessageByteArray(byte[] table){
	    		if(table.length > 2){
	    			byte[] result = new byte[table.length-2];
	    			for(int i =0; i < table.length-2; i++){
	    				result[i] = table[i+2];
	    			}
	    			return result;
	    		}
	    		else{
	    			return null;
	    		}
	    	}
	        @Override
	        protected String doInBackground(Void... params) {
	            try {
	                ServerSocket serverSocket = new ServerSocket(8989); // zmiana portu
	                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
	                Socket client = serverSocket.accept();

	                InputStream inputstream = client.getInputStream();
	              
	                String result="bb";
	                byte[] receivedByteArray = Tablica.convertInputStreamToByteArray(inputstream);
	                code = new String(getCodeByteArray(receivedByteArray));
	                
	                if(code.equals("SN")){
	                	opponent = new String (getMessageByteArray(receivedByteArray));
	                	
	                }
	                else if(code.equals("RN")){
	                	
	                	result = "request_name";		
	                	
	                	
	                }
	                else if(code.equals("SC")){
	                	
	                	byte[] array = getMessageByteArray(receivedByteArray);
		                DeviceDetailFragment.bm = BitmapFactory.decodeByteArray(array , 0, array.length);
		                result = "canva";
		                if( DeviceDetailFragment.bm != null)
		                	result = "canva";
		                
		               
	                }
	                else if(code.equals("SW"))
	                {
	                	haslo = new String (getMessageByteArray(receivedByteArray));
	                }	               
	                else if(code.equals("RW"))
	                {
	                	result = "resend";
	                }
	                else if(code.equals("GU"))
	                {
	                	result = new String (getMessageByteArray(receivedByteArray));
	                }
	                else if(code.equals("ER"))
	                {
	                	result = new String (getMessageByteArray(receivedByteArray));
	                }
	                else if(code.equals("LG"))
	                {
	                	result = "endgame";
	                }
	                else if(code.equals("CS"))
	                {
	                	result = "clear_screen";
	                }
	                		
	                
	                serverSocket.close();
	                return result;
	                
	            } catch (IOException e) {
	                Log.e(WiFiDirectActivity.TAG, e.getMessage());
	                return null;
	            }
	        }

	        /*
	         * (non-Javadoc)
	         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	         */
	        @Override
	        protected void onPostExecute(String result) {
	            
	        	if(result.equals("canva"))
	        	{
	        		if (!result.isEmpty()) {
	        	
		                statusText.setText("Otrzymany tekst - " + result);
		
		                DeviceDetailFragment.pv = ((PaintView) Tablica.tablica.findViewById(R.id.drawing));
		                if(DeviceDetailFragment.bm == null)
		                	statusText.setText("null");
		                new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
	                     .execute();
		                DeviceDetailFragment.pv.odbieraj(bm);          
	        		}
	                
	            }
	        	else if(code.equals("SW"))
	        	{
	        		Tablica.set_haslo(haslo);
	        		new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                    .execute();
	        		
	        	}
	        	else if(code.equals("RW"))
	        	{
	        	  DeviceDetailFragment.sendWordService(false, Tablica.gra.getHaslo());
	        	  new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                  .execute();
	        	}
	        	else if (code.equals("GU"))
	        	{
//	        		if(Tablica.gra.lista_graczy.get(0).is_drawing)
//	        		{
//	        			Tablica.tablica.cdown.cancel();
//	        		}
	        		if(Tablica.tablica.cdown!=null)
        			{
        				Tablica.tablica.cdown.cancel();
        			}
	        		Tablica.gra.nowa_runda(true);
	        		Tablica.gra.setHaslo(result);
	        		Tablica.tablica.zmianaGraczy();
	        		Tablica.tablica.newImage();
	        		Tablica.tablica.show_endgame();
	        		new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                    .execute();
	        	}
	        	 else if(code.equals("ER"))
                {
//	        		 if(Tablica.gra.lista_graczy.get(0).is_drawing)
//		        		{
//	        			 Tablica.tablica.cdown.cancel();
//		        		}
	        		 
	        		 if(Tablica.tablica.cdown!=null)
	        			{
	        				Tablica.tablica.cdown.cancel();
	        			}
	        		 Tablica.gra.nowa_runda(false);
	        		 Tablica.gra.setHaslo(result);
	        		 Tablica.tablica.zmianaGraczy();
	        		 Tablica.tablica.newImage();
	        		 Tablica.tablica.show_endgame();
	        		 new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                     .execute();
                }
	        	 else if(code.equals("SN"))
	        	 {
	        		 new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                     .execute();
	        		 if(Tablica.isGame)
	        		 {
	        			 Tablica.tablica.gra.lista_graczy.get(1).nazwa_gracza = opponent;
	        		 }
	        	 }
	        	 else if(code.equals("RN")){
	                	
	        		 new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                     .execute();	
	                	sendGamerNameService(false);
	                	
	                }
	        	 else if(code.equals("LG"))
	                {
//	        		 if(Tablica.gra.lista_graczy.get(0).is_drawing)
//		        		{
//	        			 Tablica.tablica.cdown.cancel();
//		        		}
	        		 if(Tablica.tablica.cdown!=null)
	        			{
	        				Tablica.tablica.cdown.cancel();
	        			}
	        		 	Tablica.tablica.zapisz_do_bazy();
	                	Tablica.tablica.finish();
	                }
	        	 else if(code.equals("CS"))
	        	 {
	        		 Tablica.tablica.newImage();
	        		 new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, mContentView.findViewById(R.id.status_text))
	             	.execute();
	        	 }
	        }

	        /*
	         * (non-Javadoc)
	         * @see android.os.AsyncTask#onPreExecute()
	         */
	        @Override
	        protected void onPreExecute() {
	            statusText.setText("Opening a server socket");
	        }
	        
	        private static String getStringFromInputStream(InputStream is) {
	       	 
	    		BufferedReader br = null;
	    		StringBuilder sb = new StringBuilder();
	     
	    		String line;
	    		try {
	     
	    			br = new BufferedReader(new InputStreamReader(is));
	    			while ((line = br.readLine()) != null) {
	    				sb.append(line);
	    			}
	     
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		} finally {
	    			if (br != null) {
	    				try {
	    					br.close();
	    				} catch (IOException e) {
	    					e.printStackTrace();
	    				}
	    			}
	    		}
	     
	    		return sb.toString();
	     
	    	}
	        
	        public void open_tablica()
	        {
	        	
	        	
	        	Intent dolacz = new Intent(context, com.app.kfe.rysowanie.Tablica.class);
	        	dolacz.putExtra("isGame", true);
	        	context.startActivity(dolacz);
	        }
	    }

    

 
    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class TextServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public TextServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }
        
        protected byte[] getCodeByteArray(byte[] table){
    		if(table.length >= 2){
    			byte[] result = new byte[2];
    			for(int i =0; i < 2; i++){
    				result[i] = table[i];
    			}
    			return result;
    		}
    		else{
    			return table;
    		}
    	}
    	
    	protected byte[] getMessageByteArray(byte[] table){
    		if(table.length > 2){
    			byte[] result = new byte[table.length-2];
    			for(int i =0; i < table.length-2; i++){
    				result[i] = table[i+2];
    			}
    			return result;
    		}
    		else{
    			return null;
    		}
    	}
        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();

                InputStream inputstream = client.getInputStream();
                String result="aa";
                byte[] receivedByteArray = Tablica.convertInputStreamToByteArray(inputstream);
                code = new String(getCodeByteArray(receivedByteArray));

                if(code.equals("OT")){
                	String temp = new String (getMessageByteArray(receivedByteArray));
                	clientIP = temp.split(":")[0];
                	opponent = temp.split(":")[1];
                	
                	open_tablica();
                }
                else if(code.equals("SC")){
                	byte[] array = getMessageByteArray(receivedByteArray);
	                
	                DeviceDetailFragment.bm = BitmapFactory.decodeByteArray(array , 0, array.length);
	                result = "canva";
	                if( DeviceDetailFragment.bm != null)
	                	result = "canva";
	                
	                
                }
                else if(code.equals("SW"))
                {
                	haslo = new String (getMessageByteArray(receivedByteArray));
                	
                }
                else if (code.equals("GU"))
	        	{
	        		result = new String (getMessageByteArray(receivedByteArray));
	        	}
	        	 else if(code.equals("ER"))
                {
	        		result = new String (getMessageByteArray(receivedByteArray));
                }
	        	 else if(code.equals("LG"))
                {
                	result = "endgame";
                }
	        	 else if(code.equals("CS"))
                {
                	result = "clear_screen";
                }
	        	 else if(code.equals("RW"))
	                {
	                	result = "resend";
	                }
	        	 else if(code.equals("RN")){
	                	
	                	result = "request_name";		
	                	
	                	
	                }
	        	 else if(code.equals("SN")){
                	opponent = new String (getMessageByteArray(receivedByteArray));
                	
                }
                serverSocket.close();
                return result;
                
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            
        	if(result.equals("canva"))
        	{
        		if (!result.isEmpty()) {
        	
	                statusText.setText("Otrzymany tekst - " + result);
	
	                DeviceDetailFragment.pv = ((PaintView) Tablica.tablica.findViewById(R.id.drawing));
	                if(DeviceDetailFragment.bm == null)
	                	statusText.setText("null");
	                new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                    .execute();
	                DeviceDetailFragment.pv.odbieraj(bm);          
        		}
                
            }
        	else if(code.equals("SW"))
        	{
        		Tablica.set_haslo(haslo);
        		new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                .execute();
        	}
        	else if (code.equals("GU"))
        	{
//        		if(Tablica.gra.lista_graczy.get(0).is_drawing)
//        		{
//        			Tablica.tablica.cdown.cancel();
//        		}
        		if(Tablica.tablica.cdown!=null)
        			{
        				Tablica.tablica.cdown.cancel();
        			}
        		Tablica.gra.nowa_runda(true);
        		Tablica.gra.setHaslo(result);
        		Tablica.tablica.zmianaGraczy();
        		Tablica.tablica.newImage();
        		 Tablica.tablica.show_endgame();
        		 new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                 .execute();
        	}
        	 else if(code.equals("ER"))
             {
//        		 if(Tablica.gra.lista_graczy.get(0).is_drawing)
//	        		{
//        			 Tablica.tablica.cdown.cancel();
//	        		}
        		 if(Tablica.tablica.cdown!=null)
     			{
     				Tablica.tablica.cdown.cancel();
     			}
	        		 Tablica.gra.nowa_runda(false);
	        		 Tablica.gra.setHaslo(result);
	        		 Tablica.tablica.zmianaGraczy();
	        		 Tablica.tablica.newImage();
	        		 Tablica.tablica.show_endgame();
	        		 new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                     .execute();
             }
        	 else if(code.equals("LG"))
             {
//        		 if(Tablica.gra.lista_graczy.get(0).is_drawing)
//	        		{
//        			 Tablica.tablica.cdown.cancel();
//	        		}
        		 if(Tablica.tablica.cdown!=null)
     			{
     				Tablica.tablica.cdown.cancel();
     			}
        		 Tablica.tablica.zapisz_do_bazy();
             	Tablica.tablica.finish();
             }
        	 else if(code.equals("CS"))
             {
             	Tablica.tablica.newImage();
             	new TextServerAsyncTask(Tablica.tablica, mContentView.findViewById(R.id.status_text))
            	.execute();
             }
        	 else if(code.equals("RW"))
	        	{
	        	  DeviceDetailFragment.sendWordService(true, Tablica.gra.getHaslo());
	        	  new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
               .execute();
	        	}
        	 else if(code.equals("RN")){
             	
             			
        		 new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                 .execute();
        		 sendGamerNameService(true);
             	
             }
        	 else if(code.equals("SN")){
        		  new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                  .execute();
              	
              }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }
        
        private static String getStringFromInputStream(InputStream is) {
       	 
    		BufferedReader br = null;
    		StringBuilder sb = new StringBuilder();
     
    		String line;
    		try {
     
    			br = new BufferedReader(new InputStreamReader(is));
    			while ((line = br.readLine()) != null) {
    				sb.append(line);
    			}
     
    		} catch (IOException e) {
    			e.printStackTrace();
    		} finally {
    			if (br != null) {
    				try {
    					br.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}
     
    		return sb.toString();
     
    	}
        
        public void open_tablica()
        {
        	
        	Intent dolacz = new Intent(context, com.app.kfe.rysowanie.Tablica.class);
        	dolacz.putExtra("isGame", true);
        	context.startActivity(dolacz);
        	
        }
    }
    
    
    
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

}
