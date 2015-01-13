package com.app.kfe.rysowanie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Haslo;

public class Rozgrywka {

	public int runda=1;
	public int czas;
	public List<Gracz> lista_graczy = new ArrayList<Gracz>();
	public List<Integer> listaUzytychHasel = new ArrayList<Integer>();
	public String haslo;
	public DatabaseHelper db;
	
	
	public String losuj_haslo()
	{
		Random rand = new Random();
		int i = rand.nextInt();
		
		return haslo;
	}
	
	public ArrayList<Haslo> getAllHasla(Context context){
		if( db == null){
			db = new DatabaseHelper(context);
		}
		
		return db.getAllHasla();
	}
	
	
}
