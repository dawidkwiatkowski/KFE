package com.app.kfe;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
public class CustomListAdapter extends ArrayAdapter<String>{
private final Activity context;
private final List<String> data;
private final List<String> Gracz;
private final List<Integer> punkty;
public CustomListAdapter(Activity context,List<String> data, List<String> Gracz,List<Integer> punkty) {
super(context, R.layout.stat_row_list, data);
this.context = context;
this.data = data;
this.Gracz = Gracz;
this.punkty = punkty;
}
@Override
public View getView(int position, View view, ViewGroup parent) {
LayoutInflater inflater = context.getLayoutInflater();
View rowView= inflater.inflate(R.layout.stat_row_list, null, true);
TextView txtData = (TextView) rowView.findViewById(R.id.Data);
TextView txtGracz = (TextView) rowView.findViewById(R.id.Gracz);
TextView txtPunkty = (TextView) rowView.findViewById(R.id.punkty);
txtData.setText(data.get(position));
txtGracz.setText(Gracz.get(position));
txtPunkty.setText(Integer.toString(punkty.get(position)));
return rowView;
}
}