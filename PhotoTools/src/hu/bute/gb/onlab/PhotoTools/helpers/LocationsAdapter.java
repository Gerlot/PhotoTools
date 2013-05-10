package hu.bute.gb.onlab.PhotoTools.helpers;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationsAdapter extends CursorAdapter {

	public LocationsAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View row = inflater.inflate(R.layout.locations_row, null);
		bindView(row, context, cursor);
		return row;
	}
	
	// UI elemek feltöltése
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView text = (TextView) view.findViewById(R.id.row_title);
		Location location = DatabaseLoader.getLocationByCursor(cursor);
		text.setText(location.getName());
	}


	@Override
	public Location getItem(int position) {
		getCursor().moveToPosition(position);
		return DatabaseLoader.getLocationByCursor(getCursor());
	}
}
