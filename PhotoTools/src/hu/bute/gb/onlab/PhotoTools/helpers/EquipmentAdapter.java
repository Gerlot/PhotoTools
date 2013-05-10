package hu.bute.gb.onlab.PhotoTools.helpers;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class EquipmentAdapter extends CursorAdapter {

	public EquipmentAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View row = inflater.inflate(R.layout.equipment_row, null);
		bindView(row, context, cursor);
		return row;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView text = (TextView) view.findViewById(R.id.row_title);
		ImageView sign = (ImageView) view.findViewById(R.id.row_sign);
		sign.setVisibility(View.GONE);
		CheckBox checkBoxSelect = (CheckBox) view.findViewById(R.id.checkBoxSelect);
		checkBoxSelect.setVisibility(View.GONE);
		
		if (cursor != null) {
			Equipment equipment = DatabaseLoader.getEquipmentByCursor(cursor);
			text.setText(equipment.getName());

			// Put up sign if has lent item(s)
			if (equipment.isLent()) {
				sign.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	@Override
	public Equipment getItem(int position) {
		getCursor().moveToPosition(position);
		return DatabaseLoader.getEquipmentByCursor(getCursor());
	}

}
