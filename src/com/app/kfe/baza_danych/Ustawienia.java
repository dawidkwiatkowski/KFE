package com.app.kfe.baza_danych;


import java.util.List;

import com.app.kfe.R;
import com.app.kfe.R.id;
import com.app.kfe.R.layout;
import com.app.kfe.R.menu;
import com.app.kfe.R.raw;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;
import android.util.Log;

public class Ustawienia extends Activity {
	
	private Button btn_save;
	private EditText etName;
	  private CheckBox radioDzwiek;
	 public static final String PREFS_NAME = "MyPrefsFile";
	 MediaPlayer mpButtonClick;
	 boolean silent;
	 
	DatabaseHelper db;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ustawienia);
       
        initUiElements();
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        silent = settings.getBoolean("silentMode", true);
       radioDzwiek.setChecked(silent);


     
        
        db = new DatabaseHelper(getApplicationContext());
        List<Gracz> allGracze = db.getAllGracze();
        int ostatniGracz=0;
        if (allGracze != null && !allGracze.isEmpty()) {
			 ostatniGracz=allGracze.size()-1;
			}
        if(!allGracze.isEmpty()){
       allGracze.get(ostatniGracz).getName();
        etName.setText(allGracze.get(ostatniGracz).getName());
        }
        addListenerOnButton();
        
    }
    private void initUiElements() {
    	etName = (EditText) findViewById(R.id.etName);
    	btn_save = (Button) findViewById(R.id.btnSave);
    	radioDzwiek = (CheckBox) findViewById(R.id.radioDzwiek);
    	 mpButtonClick = MediaPlayer.create(this, R.raw.button);
    	
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    
    
    
    public void addListenerOnButton() {
    	 
    	
    
    	
    	btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mpButtonClick != null && silent){
					mpButtonClick.start();
					}
				String nazwa_gracza = etName.getText().toString();
				
				 SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			      SharedPreferences.Editor editor = settings.edit();
			      
			      editor.putBoolean("silentMode",radioDzwiek.isChecked());
			      // Commit the edits!
			      editor.commit();

				
				if(nazwa_gracza.equals("")){
					etName.setError("Your task description couldn't be empty string.");
				} 
				
				
				else {
					int i=0;
					for (Gracz gracz : db.getAllGracze()) {
						if(nazwa_gracza.equals(gracz.getName())){
							
							 Toast.makeText(getBaseContext(),"Gracz "+ nazwa_gracza +" " +"istnieje ju¿ w bazie ", Toast.LENGTH_LONG).show();
							 i++;
							 break;
						}
					}
					if(i==0){
					Gracz player = new Gracz(nazwa_gracza);
					db.createGracz(player);
					 Toast.makeText(getBaseContext(),"Gracz zosta³ zapisany", Toast.LENGTH_LONG).show();}
											
				}
				db.closeDB();
			}
    	
    	});
    	
    	
    	
	}
    
}