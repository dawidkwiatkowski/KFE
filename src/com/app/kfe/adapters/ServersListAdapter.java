package com.app.kfe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.app.kfe.R;
import com.app.kfe.controler.communication.BroadcastManager;
import com.app.kfe.model.ServerBasicInfo;

import java.util.List;

public class ServersListAdapter extends BaseAdapter {

	private Context mContext;
	private List<ServerBasicInfo> mServersList;

	public ServersListAdapter(Context context, List<ServerBasicInfo> serversList) {
		mContext = context;
		mServersList = serversList;
	}

	public List<ServerBasicInfo> getServersList() {
		return mServersList;
	}

	public void setServersList(List<ServerBasicInfo> serversList) {
		mServersList = serversList;
		notifyDataSetChanged();
	}

	public void addServerInfo(ServerBasicInfo server) {
		mServersList.add(server);
		notifyDataSetChanged();
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return mServersList.size();
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position Position of the item whose data we want within the adapter's
	 *                 data set.
	 * @return The data at the specified position.
	 */
	@Override
	public ServerBasicInfo getItem(int position) {
		return BroadcastManager.getInstance().getAllServers().get(position);
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
		ServerBasicInfo room = getItem(position);
		((TextView) (convertView)).setText(room.getName() + " [" + room.getPlayersCount() + "]");
		return convertView;
	}
}