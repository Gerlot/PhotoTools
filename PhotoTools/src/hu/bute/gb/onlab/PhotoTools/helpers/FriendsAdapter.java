package hu.bute.gb.onlab.PhotoTools.helpers;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DbConstants;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends CursorAdapter {

	public FriendsAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View row = inflater.inflate(R.layout.friends_row, null);
		bindView(row, context, cursor);
		return row;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView title = (TextView) view.findViewById(R.id.row_title);
		ImageView sign = (ImageView) view.findViewById(R.id.row_sign);
		sign.setVisibility(View.GONE);
		ImageView icon = (ImageView) view.findViewById(R.id.row_icon);
		icon.setImageResource(R.drawable.android_contact);

		if (cursor != null) {
			Friend friend = DatabaseLoader.getFriendByCursor(cursor);
			if (friend.getLentItems() == null) {
				Log.d("friend", friend.getFullNameFirstLast() + "null");
			}
			title.setText(friend.getFullNameFirstLast());
			if (friend.getLentItems() != null) {
				sign.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public Object getItem(int position) {
		getCursor().moveToPosition(position);
		Friend result = DatabaseLoader.getFriendByCursor(getCursor());
		// Getting lent items
		List<Long> lentItems = null;
		if (result.hasLentItems()) {
			Cursor c = PhotoToolsApplication.getDatabaseLoader().getEquipmentLentTo(
					result.getID());
			while (c.moveToNext()) {
				if (lentItems == null) {
					 lentItems = new ArrayList<Long>();
				}
				lentItems.add(c.getLong(c.getColumnIndex(DbConstants.Equipment.KEY_ROWID)));
			}
			c.close();
		}
		result.setLentItems(lentItems);
		return result;
	}

}
