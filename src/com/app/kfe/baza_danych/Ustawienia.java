package com.app.kfe.baza_danych;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import com.app.kfe.R;

import android.app.Activity;
import android.content.SharedPreferences;
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
import sqlite.model.Haslo;
import sqlite.model.Rozgrywka;

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
        
      
        if(!allGracze.isEmpty()){
      
        etName.setText(allGracze.get(0).getName());
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
        getMenuInflater().inflate(R.menu.ustawienia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch(item.getItemId()) {
            case R.id.update_database:
                try {
                    URL url = new URL("https://raw.githubusercontent.com/dawidkwiatkowski/KFE/master/123.txt");
                    new UpdateKeyStringDatabaseTask().execute(url);
                }
                catch (MalformedURLException mue) {}
                result = true;
            break;
            case R.id.tmp_print_key_strings_database:
                for(Haslo haslo: db.getAllHasla()) {
                    Log.d("Hasla", haslo.getHaslo());
                }
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }
    
    
    
//    public void proste_statystyki{
//    	String nazwa_gracza;
//    	 db = new DatabaseHelper(getApplicationContext());
//    	 Gracz player1 = new Gracz(" nazwa_gracza");
//    	 Gracz player2 = new Gracz(" nazwa_gracza");
//    	 int pkt;
//			db.createGracz(player1);
//			db.createGracz(player2);
//			List<Gracz> allGracze=new ArrayList<Gracz>();
//			allGracze.add(player1);
//			allGracze.add(player2);
//			Rozgrywka gra1 = new Rozgrywka();
//			db.createRozgrywka(gra1, new long[] { player1.getId(), player2.getId()},new int[]{pkt,pkt});
//			
//			
//	}
//   
//    }
    
    
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
							
							 Toast.makeText(getBaseContext(),"Gracz "+ nazwa_gracza +" " +"istnieje ju�� w bazie ", Toast.LENGTH_LONG).show();
							 i++;
							 break;
						}
					}
					if(i==0){
					Gracz player = new Gracz(nazwa_gracza);
					if(db.getAllGracze().isEmpty()){
					db.createGracz(player);}
					else{
						player.setId(1);
						db.updateGracz(player);
					}
					 Toast.makeText(getBaseContext(),"Gracz zosta�� zapisany", Toast.LENGTH_LONG).show();}
											
				}
				db.closeDB();
			}
    	
    	});
    	
    	
    	
	}
    private class UpdateKeyStringDatabaseTask extends AsyncTask<URL, Integer, InputStream> {
        @Override
        protected InputStream doInBackground(URL... params) {
            InputStream result = null;
            try {
                for(URL url : params) {
                    HttpURLConnection urlConnection = (HttpURLConnection)(url.openConnection());
                    result = urlConnection.getInputStream();
                }
            }
            catch (IOException ioe) {}
            return result;
        }
        @Override
        protected void onPostExecute(InputStream inputStream) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null) {
                    db.addHaslo(line);
                }
            } catch (IOException e) {}
        }
    }
    
}