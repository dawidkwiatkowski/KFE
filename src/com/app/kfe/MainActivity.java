package com.app.kfe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.app.kfe.baza_danych.Statystyki;
import com.app.kfe.baza_danych.Ustawienia;
import com.app.kfe.rysowanie.Tablica;
import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;

public class MainActivity extends Activity {
    private ImageButton draw2_btn;
    //private Button dolacz_btn;
    private ImageButton setting_btn;
    private ImageButton score_btn;
    private ImageButton exit_btn;
    MediaPlayer mpButtonClick;
    public static final String PREFS_NAME = "MyPrefsFile";
    DatabaseHelper db;
    SharedPreferences settings;
    private boolean touchStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuv2);
        mpButtonClick = MediaPlayer.create(this, R.raw.button);
        db = new DatabaseHelper(getApplicationContext());
        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (db.isEmpty("hasla")) {
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
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void addListenerOnButton() {
        draw2_btn = (ImageButton) findViewById(R.id.draw2_btn);
// dolacz_btn = (Button) findViewById(R.id.dolacz_btn);
        setting_btn = (ImageButton) findViewById(R.id.setting_btn);
        score_btn = (ImageButton) findViewById(R.id.score_btn);
        exit_btn = (ImageButton) findViewById(R.id.exit_btn);
        registerForContextMenu(draw2_btn);
        draw2_btn.setLongClickable(isRestricted());
        draw2_btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                openContextMenu(draw2_btn);


            }
        });
        
        setting_btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            	Intent ustawienia = new Intent(getApplicationContext(), Ustawienia.class);
                startActivity(ustawienia);

            }
        });
        
        exit_btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            	finish();
                System.exit(0);

            }
        });
        
        score_btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            	Intent tablica = new Intent(getApplicationContext(), Statystyki.class);
                startActivity(tablica);

            }
        });
               
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Single Player");
        menu.add(0, v.getId(), 0, "Multi Player");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
// AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
// String number;
        try {
            if (item.getTitle() == "Single Player") {
                Intent tablica = new Intent(getApplicationContext(), Tablica.class);
                startActivity(tablica);
            } else if (item.getTitle() == "Multi Player") {
                
            	if(db.getAllGracze().isEmpty())
            	{
            		Gracz player = new Gracz("Player");
            		db.createGracz(player);
            	}
            	Intent dolacz = new Intent(getApplicationContext(), com.app.kfe.wifi.WiFiDirectActivity.class);
                startActivity(dolacz);
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

} 