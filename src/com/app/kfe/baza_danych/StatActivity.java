package com.app.kfe.baza_danych;



import java.util.ArrayList;
import java.util.List;

import com.app.kfe.R;
import com.app.kfe.R.id;
import com.app.kfe.R.layout;
import com.app.kfe.R.menu;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;
import sqlite.model.Stat_gry;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class StatActivity extends Activity {
	
	 private ListView mainListView ;  
		DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_activity_list);
        mainListView = (ListView) findViewById( R.id.statList);  
        db = new DatabaseHelper(getApplicationContext());
        List<String> gracze = new ArrayList<String>();
        List<Integer> punkty= new ArrayList<Integer>();
        List<Stat_gry> statystyki;
        String value="";
        
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
             value = getIntent().getStringExtra("idRozgrywki");
        }
        int id_rozgrywki= Integer.parseInt(value);
        

	      
	        	
	        	 for ( Gracz gra: db.getAllGraczeByRozgrywka(id_rozgrywki)){
	 	        	gracze.add(gra.getName());	
	 	        	statystyki=db.getStat_gry(gra.getId(), id_rozgrywki);
	 	        	punkty.add(statystyki.get(0).getPunkty());
	 	        	
	 	        	 	        }
	        	 CustomListAdapter adapter = new CustomListAdapter(StatActivity.this,gracze,punkty);
	        	 db.closeDB();
	        	 
		    	 mainListView.setAdapter( adapter); 
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
    

}
