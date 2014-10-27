package com.app.kfe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {
	
	private Button draw2_btn;
	private Button dolacz_btn;
	private Button setting_btn;
	private Button score_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void addListenerOnButton() {
    	 
    	draw2_btn = (Button) findViewById(R.id.draw2_btn);
    	dolacz_btn = (Button) findViewById(R.id.dolacz_btn);
    	setting_btn = (Button) findViewById(R.id.setting_btn);
    	score_btn = (Button) findViewById(R.id.score_btn);
    	
    	draw2_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tablica = new Intent(getApplicationContext(), Tablica.class);
				startActivity(tablica);
			}
			
			
    	
    	});
    	
    	dolacz_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent dolacz = new Intent(getApplicationContext(), Dolacz.class);
				startActivity(dolacz);
			}
			
			
    	
    	});
    	setting_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ustawienia = new Intent(getApplicationContext(), Ustawienia.class);
				startActivity(ustawienia);
			}
    	
    	});
    	
    	score_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tablica = new Intent(getApplicationContext(), Statystyki.class);
				startActivity(tablica);
			}
    	
    	});
    	
    	setting_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ustawienia = new Intent(getApplicationContext(), Ustawienia.class);
				startActivity(ustawienia);
			}
    	
    	});
    	
    	score_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tablica = new Intent(getApplicationContext(), Statystyki.class);
				startActivity(tablica);
			}
    	
    	});
    	
	}
}
