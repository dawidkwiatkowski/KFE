package com.app.kfe.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.app.kfe.R;
import com.app.kfe.controler.communication.BroadcastManager;
import com.app.kfe.model.Room;
import com.app.kfe.model.ServerBasicInfo;
import com.app.kfe.utils.Logger;

public class ServersListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Room> mRoomsList;

	public ServersListAdapter(Context context, List<Room> roomsList) {
		mContext = context;
		mRoomsList = roomsList;
	}

	public List<Room> getRoomsList() {
		return mRoomsList;
	}

	public void setRoomsList(List<Room> roomsList) {
		mRoomsList = roomsList;
		notifyDataSetChanged();
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return mRoomsList.size();
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position Position of the item whose data we want within the adapter's
	 *                 data set.
	 * @return The data at the specified position.
	 */
	@Override
	public Room getItem(int position) {
		return mRoomsList.get(position);
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 *
	 * @param position The position of the item within the adapter's data set whose row id we want.
	 * @return The id of the item at the specified position.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)(mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			convertView = inflater.inflate(R.layout.listitem_room, null, false);
		}
		Room room = getItem(position);
		((TextView)(convertView)).setText(room.getName() + "[" + room.numberOfPlayers() + "]");
		return convertView;
	}
}