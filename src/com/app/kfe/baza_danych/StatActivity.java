package com.app.kfe.baza_danych;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.app.kfe.R;
import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;
import sqlite.model.Stat_gry;

import java.util.ArrayList;
import java.util.List;


public class StatActivity extends Activity {

    private ListView mainListView;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_activity_list);
        mainListView = (ListView) findViewById(R.id.statList);
        db = new DatabaseHelper(getApplicationContext());
        List<String> gracze = new ArrayList<String>();
        List<Integer> punkty = new ArrayList<Integer>();
        List<Stat_gry> statystyki;
        String value = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = getIntent().getStringExtra("idRozgrywki");
        }
        int id_rozgrywki = Integer.parseInt(value);


        for (Gracz gra : db.getAllGraczeByRozgrywka(id_rozgrywki)) {
            gracze.add(gra.getName());
            statystyki = db.getStat_gry(gra.getId(), id_rozgrywki);
            punkty.add(statystyki.get(0).getPunkty());

        }
        CustomListAdapter adapter = new CustomListAdapter(StatActivity.this, gracze, punkty);
        db.closeDB();

        mainListView.setAdapter(adapter);
    }
}
