package com.app.kfe;

import java.util.ArrayList;
import java.util.List;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;
import sqlite.model.Rozgrywka;
import sqlite.model.Stat_gry;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
	        Rozgrywka gra1 = new Rozgrywka();
			int punkty=50;
			List<Gracz> allGracze = db.getAllGracze();
			List<Stat_gry> statystyki;
			int ostatniGracz=0;
			if (allGracze != null && !allGracze.isEmpty()) {
				 ostatniGracz=allGracze.size()-1;
				}
			
			
			int id_last_player=allGracze.get(ostatniGracz).getId();
			
			long rozgrywka1_id = db.createRozgrywka(gra1, new long[] { id_last_player },new int[]{punkty});


			Log.e("Todo Count", "Todo count: " + db.getRozgrywkaCount());

			// "Post new Article" - assigning this under "Important" Tag
			// Now this will have - "Androidhive" and "Important" Tags
			//db.createStat_gry(rozgrywka1_id, id_last_player,punkty);
	        ArrayList<String> allRozgrywki = new ArrayList<String>();
	      
	        listAdapter = new ArrayAdapter<String>(this, R.layout.stat_row_list, allRozgrywki);
	        int i=0;
	        
	        for (Gracz gracze : db.getAllGracze()) {
	        	 for (Rozgrywka rozgrywki : db.getAllRozgrywkaByTag(gracze.getName())){
	        		 
	        		 statystyki=db.getStat_gry(gracze.getId(), rozgrywki.getId());
	        		 listAdapter.add(i + "  " + gracze.getName() + "   "+  statystyki.get(1).getPunkty() );
	        		 i++;
	        	 }
	        	 }
				
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
