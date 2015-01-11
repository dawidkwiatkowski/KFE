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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import com.app.kfe.R;
import com.app.kfe.bluetooth.Dolacz;
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
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                        );
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
                    	//Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    	//Intent intent = new Intent(getActivity(),Tablica.class);
                        //intent.setType("image/*");
                    	//intent.setClass(getActivity(), Tablica.class);
                    	localIP = Utils.getLocalIPAddress();
                		// Trick to find the ip in the file /proc/net/arp
                		//client_mac_fixed = new String(DeviceDetailFragment.device.deviceAddress).replace("99", "19");
                		//clientIP = Utils.getIPFromMac(client_mac_fixed);
                    	serviceIntent = new Intent(getActivity(), FileTransferService.class);
                        serviceIntent.setAction(FileTransferService.ACTION_OPEN_TABLICA);
                        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "a");
                        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IP_SERVER);
                        //        info.groupOwnerAddress.getHostAddress());
                        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                         
                        getActivity().startService(serviceIntent);
                        
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
//        else{
//        	new ForClientServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
//            .execute();
//        }

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

	        @Override
	        protected String doInBackground(Void... params) {
	            try {
	                ServerSocket serverSocket = new ServerSocket(8989); // zmiana portu
	                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
	                Socket client = serverSocket.accept();

	                InputStream inputstream = client.getInputStream();
	                InputStream inputStream2 = inputstream;
	                String result="";
	                if(WiFiDirectActivity.co_to.equals("tablica"))
	                {
		                	try                
		                {
		                	result = getStringFromInputStream(inputstream);
		                }
		                catch(Exception e){
		                	//result = "canva";
		                }
		                	clientIP = result;  
	                }
	                
	                //String result = "Przyjêto dane";                                
//	                if(result.equalsIgnoreCase("tablica"))
//	        		{
//	        			result="open";
//	        		}
	                else
	                {                 
	                	byte[] array = Tablica.convertInputStreamToByteArray(inputstream);
		                
		                DeviceDetailFragment.bm = BitmapFactory.decodeByteArray(array , 0, array.length);
		                result = "canva";
		                if( DeviceDetailFragment.bm != null)
		                	result = "canva";	                           
		             }
	                //Log.e(WiFiDirectActivity.TAG, "Result = " + result);
	                
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
		          
		                DeviceDetailFragment.pv.odbieraj(bm);          
	        		}
	                
	            }
	        	else
	        	{
	        		open_tablica();
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
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(WiFiDirectActivity.TAG, "Server: connection done");
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                return f.getAbsolutePath();
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
            if (result != null) {
                statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
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

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();

                InputStream inputstream = client.getInputStream();
                InputStream inputStream2 = inputstream;
                String result="";
                if(WiFiDirectActivity.co_to.equals("tablica"))
                {
	                	try                
	                {
	                	result = getStringFromInputStream(inputstream);
	                }
	                catch(Exception e){
	                	//result = "canva";
	                }
	                	clientIP = result;  
                }
                
                //String result = "Przyjêto dane";                                
//                if(result.equalsIgnoreCase("tablica"))
//        		{
//        			result="open";
//        		}
                else
                {                 
                	byte[] array = Tablica.convertInputStreamToByteArray(inputstream);
	                
	                DeviceDetailFragment.bm = BitmapFactory.decodeByteArray(array , 0, array.length);
	                result = "canva";
	                if( DeviceDetailFragment.bm != null)
	                	result = "canva";	                           
	             }
                //Log.e(WiFiDirectActivity.TAG, "Result = " + result);
                
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
	          
	                DeviceDetailFragment.pv.odbieraj(bm);          
        		}
                
            }
        	else
        	{
        		open_tablica();
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
