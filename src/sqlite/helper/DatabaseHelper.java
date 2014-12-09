package sqlite.helper;


import sqlite.model.Gracz;
import sqlite.model.Haslo;
import sqlite.model.Rozgrywka;
import sqlite.model.Stat_gry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;

import com.app.kfe.MainActivity;
import com.app.kfe.R;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class DatabaseHelper extends SQLiteOpenHelper {


	private Context context; 
	
	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "kalamburydb";

	// Table Names
	public static final String GRACZ_TABLE = "gracz";
	public static final String ROZGRYWKA_TABLE = "rozgrywka";
	public static final String STAT_GRY_TABLE = "stat_gry";
	public static final String HASLA_TABLE = "hasla";


	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_DATA = "data";
	private static final String KEY_HASLO = "haslo";

	// NOTES Table - column nmaes
	
	private static final String KEY_NAME = "name";
	private static final String KEY_PUNKTY = "punkty";
	// NOTE_TAGS Table - column names
	private static final String KEY_ROZGRYWKA_ID = "rozgrywka_id";
	private static final String KEY_GRACZ_ID = "gracz_id";

	// Table Create Statements
	// Todo table create statement
	private static final String CREATE_TABLE_ROZGRYWKA = "CREATE TABLE "
			+ ROZGRYWKA_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATA
			+ " DATETIME" + ")";
	
	private static final String CREATE_TABLE_HASLA = "CREATE TABLE "
			+ HASLA_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_HASLO
			+ " TEXT" + ")";
	// Tag table create statement
	private static final String CREATE_TABLE_GRACZ = "CREATE TABLE " + GRACZ_TABLE
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT"
			+ ")";

	// todo_tag table create statement
	private static final String CREATE_TABLE_STAT_GRY = "CREATE TABLE "
			+ STAT_GRY_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_ROZGRYWKA_ID + " INTEGER," + KEY_GRACZ_ID + " INTEGER,"
			+ KEY_PUNKTY + " INTEGER" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_ROZGRYWKA);
		db.execSQL(CREATE_TABLE_GRACZ);
		db.execSQL(CREATE_TABLE_STAT_GRY);
		db.execSQL(CREATE_TABLE_HASLA);
	}
	public boolean isEmpty(String table) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor mcursor = db.rawQuery("SELECT * FROM " + table, null);
		mcursor.moveToFirst();
		if(mcursor.getCount()>0)return false;
		else return true;
	         
	   
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + ROZGRYWKA_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + GRACZ_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + STAT_GRY_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + HASLA_TABLE);

		// create new tables
		onCreate(db);
	}

	// ------------------------ "todos" table methods ----------------//

	/*
	 * Creating a todo
	 */
	
	public void createHasla() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		Charset ch = Charset.forName("UTF-8");
		 InputStream inputStream = context.getResources().openRawResource(R.raw.hasla);
		  InputStreamReader input = new InputStreamReader(inputStream,ch);
	        BufferedReader buffreader = new BufferedReader(input,2*1024);
       String line;
       try {
			while ((line = buffreader.readLine()) != null) {
				values.put(KEY_HASLO,line );
				 db.insert(HASLA_TABLE, null, values);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}
	public ArrayList<Haslo> getAllHasla() {
		ArrayList<Haslo> hasla = new ArrayList<Haslo>();
		String selectQuery = "SELECT  * FROM " + HASLA_TABLE;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
					Haslo td = new Haslo();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				
				td.setHaslo(c.getString(c.getColumnIndex(KEY_HASLO)));

				// adding to todo list
				hasla.add(td);
			} while (c.moveToNext());
		}

		return hasla;
	}
	
	
	public long createRozgrywka(Rozgrywka rozgrywka, long[] gracz_ids,int[] punkty) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		//values.put(KEY_TODO, todo.getNote());
		//values.put(KEY_STATUS, todo.getStatus());
		values.put(KEY_DATA, getDateTime());

		// insert row
		long rozgrywka_id = db.insert(ROZGRYWKA_TABLE, null, values);

		// insert tag_ids
		for (long gra_id: gracz_ids) {
			for(int pkt: punkty){
				createStat_gry(rozgrywka_id, gra_id,pkt);
		
			}
		}

		return rozgrywka_id;
	}

	/*
	 * get single todo
	 */
	public Rozgrywka getRozgrywka(long rozgrywka_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + ROZGRYWKA_TABLE + " WHERE "
				+ KEY_ID + " = " + rozgrywka_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Rozgrywka td = new Rozgrywka();
		td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		
		td.setData(c.getString(c.getColumnIndex(KEY_DATA)));

		return td;
	}

	/**
	 * getting all todos
	 * */
	public ArrayList<Rozgrywka> getAllRozgrywka() {
		ArrayList<Rozgrywka> rozgrywki = new ArrayList<Rozgrywka>();
		String selectQuery = "SELECT  * FROM " + ROZGRYWKA_TABLE;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Rozgrywka td = new Rozgrywka();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				
				td.setData(c.getString(c.getColumnIndex(KEY_DATA)));

				// adding to todo list
				rozgrywki.add(td);
			} while (c.moveToNext());
		}

		return rozgrywki;
	}

	/**
	 * getting all todos under single tag
	 * */
	public List<Rozgrywka> getAllRozgrywkaByTag(String name) {
		List<Rozgrywka> rozgrywki = new ArrayList<Rozgrywka>();

		String selectQuery = "SELECT  * FROM " + ROZGRYWKA_TABLE + " roz, "
				+ GRACZ_TABLE + " gra, " + STAT_GRY_TABLE + " sg WHERE gra."
				+ KEY_NAME + " = '" + name + "'" + " AND roz." + KEY_ID
				+ " = " + "sg." + KEY_ROZGRYWKA_ID + " AND gra." + KEY_ID + " = "
				+ "sg." + KEY_GRACZ_ID;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Rozgrywka rozgrywka = new Rozgrywka();
				rozgrywka.setId(c.getInt((c.getColumnIndex(KEY_ID))));
			
				rozgrywka.setData(c.getString(c.getColumnIndex(KEY_DATA)));

				// adding to todo list
				rozgrywki.add(rozgrywka);
			} while (c.moveToNext());
		}

		return rozgrywki;
	}

	/*
	 * getting todo count
	 */
	public int getRozgrywkaCount() {
		String countQuery = "SELECT  * FROM " + ROZGRYWKA_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a todo
	 */
	public int updateRozgrywka(Rozgrywka rozgrywka) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(KEY_DATA, rozgrywka.getData());

		// updating row
		return db.update(ROZGRYWKA_TABLE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(rozgrywka.getId()) });
	}

	/*
	 * Deleting a todo
	 */
	public void deleteRozgrywka(long rozgrywka_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(ROZGRYWKA_TABLE, KEY_ID + " = ?",
				new String[] { String.valueOf(rozgrywka_id) });
	}

	// ------------------------ "tags" table methods ----------------//

	/*
	 * Creating tag
	 */
	public long createGracz(Gracz gracz) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, gracz.getName());
		

		// insert row
		long gracz_id = db.insert(GRACZ_TABLE, null, values);

		return gracz_id;
	}

	/**
	 * getting all tags
	 * */
	public List<Gracz> getAllGracze() {
		List<Gracz> tags = new ArrayList<Gracz>();
		String selectQuery = "SELECT  * FROM " + GRACZ_TABLE;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Gracz t = new Gracz();
				t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				t.setName(c.getString(c.getColumnIndex(KEY_NAME)));

				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}
	public List<Gracz> getAllGraczeByRozgrywka(long id_rozgrywki) {
		List<Gracz> gracze = new ArrayList<Gracz>();

		String selectQuery = "SELECT  * FROM " + GRACZ_TABLE + " gra, "
				+ STAT_GRY_TABLE + " sta "  + " WHERE sta."
				+ KEY_ROZGRYWKA_ID + " = '" + id_rozgrywki+"'"+ " AND gra."+KEY_ID + "= sta."+KEY_GRACZ_ID ;
		

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Gracz gracz = new Gracz();
				gracz.setId(c.getInt((c.getColumnIndex(KEY_GRACZ_ID))));
				gracz.setName(c.getString(c.getColumnIndex(KEY_NAME)));

				// adding to todo list
				gracze.add(gracz);
			} while (c.moveToNext());
		}

		return gracze;
	}
	/*
	 * Updating a tag
	 */
	public int updateGracz(Gracz gracz) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, gracz.getName());

		// updating row
		return db.update(GRACZ_TABLE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(gracz.getId()) });
	}

	/*
	 * Deleting a tag
	 */
	public void deleteGracz(Gracz gracz, boolean should_delete_all_stat_gry) {
		SQLiteDatabase db = this.getWritableDatabase();

		// before deleting tag
		// check if todos under this tag should also be deleted
		if (should_delete_all_stat_gry) {
			// get all todos under this tag
			List<Rozgrywka> allStatGry = getAllRozgrywkaByTag(gracz.getName());

			// delete all todos
			for (Rozgrywka rozgrywka : allStatGry) {
				// delete todo
				deleteRozgrywka(rozgrywka.getId());
			}
		}

		// now delete the tag
		db.delete(GRACZ_TABLE, KEY_ID + " = ?",
				new String[] { String.valueOf(gracz.getId()) });
	}

	// ------------------------ "todo_tags" table methods ----------------//

	/*
	 * Creating todo_tag
	 */

	public long createStat_gry(long todo_id, long tag_id,int punkty) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ROZGRYWKA_ID, todo_id);
		values.put(KEY_GRACZ_ID, tag_id);
		values.put(KEY_PUNKTY, punkty);
		
		
	
		

		long id = db.insert(STAT_GRY_TABLE, null, values);

		return id;}
	
	
	
	public List<Stat_gry> getStat_gry(long id_gracza, long id_rozgrywki) {
		List<Stat_gry> stat_gier = new ArrayList<Stat_gry>();

		/*String selectQuery = "SELECT  * FROM " + STAT_GRY_TABLE + " st, "
				+ GRACZ_TABLE + " gra, " + ROZGRYWKA_TABLE + " roz WHERE st."
				+ KEY_GRACZ_ID + " = '" + id_gracza + "'" + " AND st." + KEY_ROZGRYWKA_ID
				+ " = " + id_rozgrywki;*/
		String selectQuery = "SELECT  * FROM " + STAT_GRY_TABLE + " st " + " WHERE st."
				+ KEY_GRACZ_ID + " = '" + id_gracza + "'" + " AND st." + KEY_ROZGRYWKA_ID
				+ " = " + id_rozgrywki;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Stat_gry stat_gry = new Stat_gry();
				stat_gry.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				stat_gry.setPunkty(c.getInt(c.getColumnIndex(KEY_PUNKTY)));

				// adding to todo list
				stat_gier.add(stat_gry);
			} while (c.moveToNext());
		}

		return stat_gier;
	}
	

	/*
	 * Updating a todo tag
	 */
	public int updateNoteTag(long id, long tag_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GRACZ_ID, tag_id);

		// updating row
		return db.update(ROZGRYWKA_TABLE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	/*
	 * Deleting a todo tag
	 */
	public void deleteToDoTag(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(ROZGRYWKA_TABLE, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
