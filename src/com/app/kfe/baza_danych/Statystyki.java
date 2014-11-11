package com.app.kfe.baza_danych;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.app.kfe.R;
import com.app.kfe.R.id;
import com.app.kfe.R.layout;
import com.app.kfe.R.menu;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;
import sqlite.model.Rozgrywka;
import sqlite.model.Stat_gry;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;

public class Statystyki extends Activity  {

	  private ListView mainListView ;  
	 private ArrayAdapter<String> listAdapter ; 
		DatabaseHelper db;
		
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_score);
	        mainListView = (ListView) findViewById( R.id.statListView );  
	        db = new DatabaseHelper(getApplicationContext());
	        
	        final List<Rozgrywka> rozgrywki=db.getAllRozgrywka();
	    
	       
	        Rozgrywka gra1 = new Rozgrywka();
			int pkt=10;
			List<Gracz> allGracze = db.getAllGracze();
				
			int ostatniGracz=0;
						if (allGracze != null && !allGracze.isEmpty()) {
				 ostatniGracz=allGracze.size()-1;
				}
			
			if(!allGracze.isEmpty()){
			int id_last_player=allGracze.get(ostatniGracz).getId();
			
			
			db.createRozgrywka(gra1, new long[] { id_last_player },new int[]{pkt});
			}
	       
	        if (rozgrywki.size() > 0) {// sortowanie listy po datach rozgrywek
	        	
	            Collections.sort(rozgrywki, new Comparator<Rozgrywka>() {
	                @Override
	                public int compare(final Rozgrywka object1, final Rozgrywka object2) {
	                	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
	                	Date data1 = new Date();
	                	Date data2 = new Date();
	                	dateFormat.format(data1);
	                	dateFormat.format(data2);
	                	
	                
						try {
							data1 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.ENGLISH).parse(object1.getData());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}       	
	           
						try {
							data2 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.ENGLISH).parse(object2.getData());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
	                	return (data1.compareTo(data2))*-1;// aby sortowa³o od najstarszej do najmlodszej rozgywki
	                }
	               } );
	           }
	        
	        
	        
	     /*   for (Rozgrywka rozgrywka : rozgrywki){
	        	daty.add(rozgrywka.getData());
	        	
	        	 for ( Gracz gra: db.getAllGraczeByRozgrywka(rozgrywka.getId())){
	 	        	gracze.add(gra.getName());	
	 	        	statystyki=db.getStat_gry(gra.getId(), rozgrywka.getId());
	 	        	punkty.add(statystyki.get(0).getPunkty());
	 	        	
	 	        	
	 	        }
	        }*/
	        
	        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow);  
	        //CustomListAdapter adapter = new CustomListAdapter(Statystyki.this, daty, gracze,punkty);
	        int i=0;
	        for (Rozgrywka rozgrywka : rozgrywki){
	        	 listAdapter.add( i+"   "+rozgrywka.getData());
	        	 i++;
	        }
	             	              
	    
	     
	        mainListView.setOnItemClickListener(new OnItemClickListener()
	        {
	            @Override 
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
	            { 
	            	Intent stat = new Intent(getApplicationContext(), StatActivity.class);
	            	int id_rozgrywki= rozgrywki.size()-position;
	            	stat.putExtra("idRozgrywki",Integer.toString(id_rozgrywki));
					startActivity(stat);
	            }
	        });			
	    	db.closeDB();
	    	 mainListView.setAdapter( listAdapter );  
	    }
	 
	 
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }
}
