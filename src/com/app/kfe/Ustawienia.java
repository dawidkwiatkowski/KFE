package com.app.kfe;


import java.util.List;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;
import android.util.Log;

public class Ustawienia extends Activity {
	
	private Button btn_save;
	private EditText etName;
	
	
	DatabaseHelper db;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
   

        initUiElements();
        
        db = new DatabaseHelper(getApplicationContext());
        addListenerOnButton();
        
    }
    private void initUiElements() {
    	etName = (EditText) findViewById(R.id.etName);
    	btn_save = (Button) findViewById(R.id.btnSave);
    	
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
				String taskDescription = etName.getText().toString();
				if(taskDescription.equals("")){
					etName.setError("Your task description couldn't be empty string.");
				} else {
					Gracz player = new Gracz(taskDescription);
					long player_id = db.createGracz(player);
					etName.setText("");
					 Toast.makeText(getBaseContext(),"Gracz zosta³ zapisany", Toast.LENGTH_LONG).show();
					List<Gracz> allGracze = db.getAllGracze();
					for (Gracz gracz : allGracze) {
						Log.d("Gracz Name", gracz.getName());
					}
					
				
				}
				db.closeDB();
			}
    	
    	});
    	
    	
    	
	}
    
}