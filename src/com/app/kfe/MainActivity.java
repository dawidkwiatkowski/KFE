package com.app.kfe;

import com.app.kfe.baza_danych.Statystyki;
import com.app.kfe.baza_danych.Ustawienia;
import com.app.kfe.bluetooth.BluetoothMainActivity;
import com.app.kfe.rysowanie.Tablica;

import sqlite.helper.DatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;


public class MainActivity extends Activity {
	
	private Button draw2_btn;
	private Button dolacz_btn;
	private Button setting_btn;
	private Button score_btn;
	private Button exit_btn;
	MediaPlayer mpButtonClick;
	 public static final String PREFS_NAME = "MyPrefsFile";
	DatabaseHelper db;
	boolean silent;
	SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        mpButtonClick = MediaPlayer.create(this, R.raw.button);
        db = new DatabaseHelper(getApplicationContext());
         settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
     
        if(db.isEmpty("hasla")){
        db.createHasla();
        }
       
        addListenerOnButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(getApplicationContext(), BluetoothMainActivity.class));
				result = true;
				break;
		}
		return result | super.onOptionsItemSelected(item);
    }
    
    public void addListenerOnButton() {
    	 
    	draw2_btn = (Button) findViewById(R.id.draw2_btn);
    	dolacz_btn = (Button) findViewById(R.id.dolacz_btn);
    	setting_btn = (Button) findViewById(R.id.setting_btn);
    	score_btn = (Button) findViewById(R.id.score_btn);
    	exit_btn = (Button) findViewById(R.id.exit_btn);
    	 final Animation animAlpha = AnimationUtils.loadAnimation(this, R.layout.anim_alpha);
    	draw2_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(animAlpha);
				   silent = settings.getBoolean("silentMode", true);
				if((mpButtonClick != null) && silent){
					mpButtonClick.start();
					}
				Handler handler = new Handler();
				handler.postDelayed(new Runnable(){
				    public void run() {
				    	Intent tablica = new Intent(getApplicationContext(), Tablica.class);
						startActivity(tablica);
				        }
				}, 600);
				
			}
			
			
    	
    	});
    	
    	dolacz_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				v.startAnimation(animAlpha);
				silent = settings.getBoolean("silentMode", true);
				if((mpButtonClick != null) && silent){
					mpButtonClick.start();
					}
				Handler handler = new Handler();
				handler.postDelayed(new Runnable(){
				    public void run() {
				    	Intent dolacz = new Intent(getApplicationContext(), com.app.kfe.wifi.WiFiDirectActivity.class);
						startActivity(dolacz);
				        }
				}, 600);
			}	
    	
    	});
    	setting_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(animAlpha);
				silent = settings.getBoolean("silentMode", true);
				if((mpButtonClick != null) && silent){
					mpButtonClick.start();
					}
				Handler handler = new Handler();
				handler.postDelayed(new Runnable(){
				    public void run() {
				    	Intent ustawienia = new Intent(getApplicationContext(), Ustawienia.class);
						startActivity(ustawienia);
				        }
				}, 600);
				
			}
    	
    	});
    	
    	score_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(animAlpha);
				silent = settings.getBoolean("silentMode", true);
				if((mpButtonClick != null) && silent){
					mpButtonClick.start();
					}
				Handler handler = new Handler();
				handler.postDelayed(new Runnable(){
				    public void run() {
				    	Intent tablica = new Intent(getApplicationContext(), Statystyki.class);
						startActivity(tablica);
				        }
				}, 600);
				
			}
    	
    	});
    	
    	
    	exit_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(animAlpha);
				silent = settings.getBoolean("silentMode", true);
				if((mpButtonClick != null) && silent){
					mpButtonClick.start();
					}
				Handler handler = new Handler();
				handler.postDelayed(new Runnable(){
				    public void run() {
				    	finish();
			            System.exit(0);
				        }
				}, 600);
				 
			}
    	
    	});
    	
	}
}
