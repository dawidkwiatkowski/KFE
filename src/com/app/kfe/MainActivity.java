package com.app.kfe;
import com.app.kfe.baza_danych.Statystyki;
import com.app.kfe.baza_danych.Ustawienia;
import com.app.kfe.rysowanie.Tablica;

import sqlite.helper.DatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
public class MainActivity extends Activity  {
private Button draw2_btn;
//private Button dolacz_btn;
private Button setting_btn;
private Button score_btn;
private Button exit_btn;
MediaPlayer mpButtonClick;
public static final String PREFS_NAME = "MyPrefsFile";
DatabaseHelper db;
boolean silent;
SharedPreferences settings;
private boolean touchStarted = false;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.menuv2);
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
// dolacz_btn = (Button) findViewById(R.id.dolacz_btn);
setting_btn = (Button) findViewById(R.id.setting_btn);
score_btn = (Button) findViewById(R.id.score_btn);
exit_btn = (Button) findViewById(R.id.exit_btn);
final Animation animAlpha = AnimationUtils.loadAnimation(this, R.layout.anim_alpha);
registerForContextMenu(draw2_btn);
draw2_btn.setLongClickable(isRestricted());
 draw2_btn.setOnClickListener(new Button.OnClickListener() {

 @Override
 public void onClick(View v) {
 // TODO Auto-generated method stub

	 openContextMenu(draw2_btn);


 }});


setting_btn.setOnTouchListener(new OnTouchListener() {

   
	private Rect rect;    // Variable rect to hold the bounds of the view

	public boolean onTouch(View v, MotionEvent event) {
	    if(event.getAction() == MotionEvent.ACTION_DOWN){
	        // Construct a rect of the view's bounds
	        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
	        setting_btn.setBackgroundColor(0);
        	setting_btn.setText("Ustawienia");
        	touchStarted = true;

	    }
	    if (event.getAction() == MotionEvent.ACTION_UP ) {
	    	setting_btn.setBackgroundResource(R.drawable.tools);
        	setting_btn.setText("");
        	if (touchStarted) {
        		
            	Intent ustawienia = new Intent(getApplicationContext(), Ustawienia.class);
            	startActivity(ustawienia);
                return true;
            }}
	    if(event.getAction() == MotionEvent.ACTION_MOVE){
	        if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
	        	setting_btn.setBackgroundResource(R.drawable.tools);
            	setting_btn.setText("");
            	touchStarted = false;
            	 return true;
	        }
	    }
	    return false;
	}
});
exit_btn.setOnTouchListener(new OnTouchListener() {

	   
	private Rect rect;    // Variable rect to hold the bounds of the view

	public boolean onTouch(View v, MotionEvent event) {
	    if(event.getAction() == MotionEvent.ACTION_DOWN){
	        // Construct a rect of the view's bounds
	        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
	        exit_btn.setBackgroundColor(0);
	        exit_btn.setText("Wyjœcie");
        	touchStarted = true;

	    }
	    if (event.getAction() == MotionEvent.ACTION_UP ) {
	    	exit_btn.setBackgroundResource(R.drawable.exit);
	    	exit_btn.setText("");
        	if (touchStarted) {
        		
        		finish();
        		System.exit(0);
                return true;
            }}
	    if(event.getAction() == MotionEvent.ACTION_MOVE){
	        if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
	        	exit_btn.setBackgroundResource(R.drawable.exit);
	        	exit_btn.setText("");
            	touchStarted = false;
            	 return true;
	        }
	    }
	    return false;
	}
});
score_btn.setOnTouchListener(new OnTouchListener() {

	   
	private Rect rect;    // Variable rect to hold the bounds of the view

	public boolean onTouch(View v, MotionEvent event) {
	    if(event.getAction() == MotionEvent.ACTION_DOWN){
	        // Construct a rect of the view's bounds
	        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
	        score_btn.setBackgroundColor(0);
	        score_btn.setText("Statystyki");
        	touchStarted = true;

	    }
	    if (event.getAction() == MotionEvent.ACTION_UP ) {
	    	score_btn.setBackgroundResource(R.drawable.stats);
	    	score_btn.setText("");
        	if (touchStarted) {
        		
        		Intent tablica = new Intent(getApplicationContext(), Statystyki.class);
        		startActivity(tablica);
                return true;
            }}
	    if(event.getAction() == MotionEvent.ACTION_MOVE){
	        if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
	        	score_btn.setBackgroundResource(R.drawable.stats);
	        	score_btn.setText("");
            	touchStarted = false;
            	 return true;
	        }
	    }
	    return false;
	}
});
}
@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
{
super.onCreateContextMenu(menu, v, menuInfo);
menu.setHeaderTitle("Select The Action");
menu.add(0, v.getId(), 0, "Single Player");
menu.add(0, v.getId(), 0, "Multi Player");
}
@Override
public boolean onContextItemSelected(MenuItem item)
{
// AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
// String number;
try
{
if(item.getTitle()=="Single Player")
{
Intent tablica = new Intent(getApplicationContext(), Tablica.class);
startActivity(tablica);
}
else if(item.getTitle()=="Multi Player")
{
Intent dolacz = new Intent(getApplicationContext(), com.app.kfe.wifi.WiFiDirectActivity.class);
startActivity(dolacz);
}
else
{return false;}
return true;
}
catch(Exception e)
{
return true;
}
}

} 