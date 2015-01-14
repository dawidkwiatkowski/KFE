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
	public List<Haslo> listaHasel = new ArrayList<Haslo>();
	public String haslo;
	public DatabaseHelper db;
	
	public Rozgrywka()
	{
		
	}
	
	public void losuj_haslo()
	{
		Random rand = new Random();
		int i = rand.nextInt(listaHasel.size());
		this.haslo =  (listaHasel.get(i).getHaslo());
		listaUzytychHasel.add(i);
		
	}
	
	public void getAllHasla(Context context){
		if( db == null){
			db = new DatabaseHelper(context);
		}
		listaHasel = db.getAllHasla();
		//return db.getAllHasla();
	}
	
	public String getHaslo(){
		return haslo;
	}	
	
}
