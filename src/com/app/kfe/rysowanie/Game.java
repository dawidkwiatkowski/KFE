package com.app.kfe.rysowanie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.widget.Toast;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Haslo;

public class Game {

	public int runda=1;
	public int czas;
	public List<Gamer> lista_graczy = new ArrayList<Gamer>();
	public List<Integer> listaUzytychHasel = new ArrayList<Integer>();
	public List<Haslo> listaHasel = new ArrayList<Haslo>();
	public String haslo;
	public DatabaseHelper db;
	
	public Game()
	{
		
	}
	
	public void losuj_haslo()
	{
		Random rand = new Random();
		int i;
		int licznik=0;
		Toast msg = null;
		do
		{
			i = rand.nextInt(listaHasel.size());
			licznik++;
			if(licznik > 5)
			{
				
				msg = msg.makeText(Tablica.tablica, "Wykorzystano ju¿ wszystkie has³a, resetujê" , msg.LENGTH_SHORT);
				msg.show();
				listaUzytychHasel.clear();
				licznik=0;
			}
		}
		while(listaUzytychHasel.contains(i));
		
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
	
	public void add_used_haslo()
	{
		listaUzytychHasel.add(listaHasel.lastIndexOf(haslo));
	}
	
	public String getHaslo(){
		return haslo;
	}	
	
	public void setHaslo(String haslo)
	{
		this.haslo = haslo;
	}
	
	public void nowa_runda(boolean isGiveUp)
	{
		Gamer gracz1 = lista_graczy.get(0);
		Gamer gracz2 = lista_graczy.get(1);
		Tablica.tablica.newImage();
		if(gracz1.is_drawing){
			gracz1.is_drawing = false;
			if(!isGiveUp){
				gracz1.punkty+=1;
			}
		}
		else{
			gracz1.is_drawing = true;
			if(!isGiveUp){
				gracz1.punkty+=2;
			}
		}
		
		if(gracz2.is_drawing){
			gracz2.is_drawing = false;
			if(!isGiveUp){
				gracz2.punkty+=1;
			}
		}
		else{
			gracz2.is_drawing = true;
			if(!isGiveUp){
				gracz2.punkty+=2;
			}
		}
		
	}
	
}
