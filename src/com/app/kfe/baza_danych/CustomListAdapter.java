package com.app.kfe.baza_danych;

import java.util.List;

import com.app.kfe.R;
import com.app.kfe.R.id;
import com.app.kfe.R.layout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
public class CustomListAdapter extends ArrayAdapter<String>{
private final Activity context;
private final List<String> Gracz;
private final List<Integer> punkty;
public CustomListAdapter(Activity context, List<String> Gracz,List<Integer> punkty) {
super(context, R.layout.stat_row_list,Gracz);
this.context = context;
this.Gracz = Gracz;
this.punkty = punkty;
}
@Override
public View getView(int position, View view, ViewGroup parent) {
LayoutInflater inflater = context.getLayoutInflater();
View rowView= inflater.inflate(R.layout.stat_row_list, null, true);
TextView txtGracz = (TextView) rowView.findViewById(R.id.Gracz);
TextView txtPunkty = (TextView) rowView.findViewById(R.id.punkty);
txtGracz.setText(Gracz.get(position));
txtPunkty.setText(Integer.toString(punkty.get(position)));
return rowView;
}
}