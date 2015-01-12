package com.app.kfe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.app.kfe.R;
import com.app.kfe.controler.RoomManager;
import com.app.kfe.model.Player;

public class PlayersListAdapter extends BaseAdapter {

	private final Context _context;

	public PlayersListAdapter(Context context) {
		_context = context;
	}

	@Override
	public int getCount() {
		return RoomManager.getInstance().getRoom()!=null?RoomManager.getInstance().getRoom().numberOfPlayers():0;
	}

	@Override
	public Object getItem(int position) {
		return RoomManager.getInstance().getRoom().getPlayer(position);
	}

	@Override
	public long getItemId(int position) {
		return RoomManager.getInstance().getRoom().getPlayer(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listitem_player, null, false);
		}
		Player player = RoomManager.getInstance().getRoom().getPlayer(position);
		
		TextView tempTextView=((TextView)convertView.findViewById(R.id.player_login));
		tempTextView.setText(player.getLogin());
		
		return convertView;
	}

}
