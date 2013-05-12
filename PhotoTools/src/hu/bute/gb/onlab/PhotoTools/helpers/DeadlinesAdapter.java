package hu.bute.gb.onlab.PhotoTools.helpers;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.entities.Deadline;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DeadlinesAdapter extends CursorAdapter {
	
	private int orange_;

	public DeadlinesAdapter(Context context, Cursor c, int orange) {
		super(context, c, false);
		orange_ = orange;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View row = inflater.inflate(R.layout.deadlines_row, null);
		bindView(row, context, cursor);
		return row;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView title = (TextView) view.findViewById(R.id.row_title);
		TextView date = (TextView) view.findViewById(R.id.row_date);
		TextView left = (TextView) view.findViewById(R.id.row_left);

		if (cursor != null) {
			Deadline deadline = DatabaseLoader.getDeadlineByCursor(cursor);
			title.setText(deadline.getName());

			boolean isSoon = false;
			DateMidnight today = (new DateTime()).toDateMidnight();
			DateMidnight startDate = deadline.getStartTime().toDateMidnight();
			int daysBetween = Days.daysBetween(today, deadline.getStartTime()).getDays();
			if (startDate.isAfter(today) && daysBetween <= 3) {
				isSoon = true;
			}

			// Display date
			if (!deadline.isAllDay() && deadline.getEndTime() != null) {
				date.setText(deadline.getStartTime().toLocalTime().toString("HH:mm") + " - "
						+ deadline.getEndTime().toLocalTime().toString("HH:mm"));
			}
			else if (!deadline.isAllDay()) {
				date.setText(deadline.getStartTime().toLocalTime().toString("HH:mm"));
			}
			else {
				date.setText("");
			}

			// Display how many days left if necessary
			// Hide how many days left if deadline is not soon
			left.setVisibility(View.GONE);
			// Show indicator if deadline is soon
			switch (daysBetween) {
			case 3:
				left.setVisibility(View.VISIBLE);
				left.setText("3 days left");
				left.setTextColor(orange_);
				break;
			case 2:
				left.setVisibility(View.VISIBLE);
				left.setText("2 days left");
				left.setTextColor(orange_);
				break;
			case 1:
				left.setVisibility(View.VISIBLE);
				left.setText("Only 1 day left!");
				break;
			default:
				left.setText("It's today!");
				break;
			}
		}

	}

	@Override
	public Deadline getItem(int position) {
		getCursor().moveToPosition(position);
		return DatabaseLoader.getDeadlineByCursor(getCursor());
	}
}
