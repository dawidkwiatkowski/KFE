// Copyright 2011 Google Inc. All Rights Reserved.

package com.app.kfe.wifi;

import android.R.bool;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import sqlite.helper.DatabaseHelper;

import com.app.kfe.R;
import com.app.kfe.rysowanie.PaintView;
import com.app.kfe.rysowanie.Tablica;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 100000;
    public static final String EXTRAS_CANVAS = "CANVAS";
    public static final String ACTION_SEND_FILE = "com.app.kfe.Wifi.SEND_FILE";
    public static final String ACTION_SEND_TEXT = "com.app.kfe.Wifi.SEND_TEXT";
    public static final String ACTION_SEND_CANVAS = "com.app.kfe.Wifi.SEND_CANVAS";
    public static final String ACTION_SEND_NAME = "com.app.kfe.Wifi.SEND_NAME";
    public static final String ACTION_SEND_WORD = "com.app.kfe.Wifi.SEND_WORD";
    public static final String ACTION_RESEND_WORD = "com.app.kfe.Wifi.RESEND_WORD";
    public static final String ACTION_OPEN_TABLICA= "com.app.kfe.Wifi.OPEN_TABLICA";
    public static final String ACTION_GIVE_UP= "com.app.kfe.Wifi.GIVE_UP";
    public static final String ACTION_END_ROUND= "com.app.kfe.Wifi.END_ROUND";
    public static final String ACTION_LEAVE_GAME= "com.app.kfe.Wifi.LEAVE_GAME";
    public static final String ACTION_CLEAR_SCREEN= "com.app.kfe.Wifi.CLEAR_SCREEN";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static Boolean czy_tak=false;
    String code;
    
    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    protected byte[] appendData(byte[] firstObject,byte[] secondObject){
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
	    try {
	        if (firstObject!=null && firstObject.length!=0)
	            outputStream.write(firstObject);
	        if (secondObject!=null && secondObject.length!=0)   
	            outputStream.write(secondObject);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return outputStream.toByteArray();
	}
    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
//                socket.bind(null);
//                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                try {
                    is = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.d(WiFiDirectActivity.TAG, e.toString());
                }
                DeviceDetailFragment.copyFile(is, stream);
                Log.d(WiFiDirectActivity.TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
        else if (intent.getAction().equals(ACTION_OPEN_TABLICA)) {
        	code = "OT";
        	DatabaseHelper db = new DatabaseHelper(this);
        	
        	sendText(code, DeviceDetailFragment.localIP + ":" + db.getAllGracze().get(0).getName(),intent);
        //	sendText(DeviceDetailFragment.localIP + ":" + "Gracz1", intent);        	
        }
        else if (intent.getAction().equals(ACTION_SEND_CANVAS)) {
        	code = "SC";
        	
        	sendCanvas(code,intent);
        }
        else if (intent.getAction().equals(ACTION_SEND_NAME)) {
        	code = "SN";
        	DatabaseHelper db = new DatabaseHelper(this);
        	sendText(code,db.getAllGracze().get(0).getName(), intent);
        }
        else if (intent.getAction().equals(ACTION_SEND_WORD)) {
        	code = "SW";
        	
        	sendText(code,DeviceDetailFragment.haslo, intent);
        }
        else if (intent.getAction().equals(ACTION_RESEND_WORD)) {
        	code = "RW";
        	
        	sendText(code,"resend", intent);
        }
        else if(intent.getAction().equals(ACTION_GIVE_UP)) {
        	code = "GU";
        	
        	sendText(code,Tablica.gra.getHaslo(), intent);
        }
        else if(intent.getAction().equals(ACTION_END_ROUND)) {
        	code = "ER";
        	
        	sendText(code,Tablica.gra.getHaslo(), intent);
        }
        else if(intent.getAction().equals(ACTION_LEAVE_GAME)) {
        	code = "LG";
        	
        	sendText(code,"end_game", intent);
        }
        else if(intent.getAction().equals(ACTION_CLEAR_SCREEN)) {
        	code = "CS";
        	
        	sendText(code,"clear_screen",intent);
        }
    }
    
    public void sendText(String code, String text, Intent intent){
        String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        Socket socket = new Socket();
        int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

        try {
            Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
            
            byte[] codeArray = code.getBytes();
            byte[] messageArray = text.getBytes();
            byte[] connectArray = appendData(codeArray, messageArray);
            
            Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
            OutputStream stream = socket.getOutputStream();
            InputStream is = null;
            
            //is = new ByteArrayInputStream(text.getBytes());
            is = new ByteArrayInputStream(connectArray);
            DeviceDetailFragment.copyFile(is, stream);
            Log.d(WiFiDirectActivity.TAG, "Client: Data written");
        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void sendCanvas(String code, Intent intent){
        String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        Socket socket = new Socket();
        int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT); 
        
        try {
        	
        
            Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");

            socket.bind(null);
        	socket.connect((new InetSocketAddress(host, port)),SOCKET_TIMEOUT);
        	 
        	
            PaintView pv = ((PaintView) Tablica.tablica.findViewById(R.id.drawing));
            Canvas canvas = null;
            
            canvas = pv.getDrawCanvas();
        
			  pv.setDrawingCacheEnabled(true);
			  Bitmap obrazek = pv.getDrawingCache();
			  
			  ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
			  obrazek.compress(Bitmap.CompressFormat.PNG, 100, streamOut);
			  byte[] yourBytes = streamOut.toByteArray();
			  pv.destroyDrawingCache();
			  
			  byte[] codeArray = code.getBytes();
	             
	          byte[] connectArray = appendData(codeArray, yourBytes);
           
	          Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
            OutputStream stream = socket.getOutputStream();
            InputStream is = null;
            
           // is = new ByteArrayInputStream(yourBytes);
            is = new ByteArrayInputStream(connectArray);
            
            DeviceDetailFragment.copyFile(is, stream);
            Log.d(WiFiDirectActivity.TAG, "Client: Data written");
            
        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        } 
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
