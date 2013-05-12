package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.DeadlinesEditActivity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	private static DeadlinesEditActivity activity_;
	private static boolean isStartTime_ = false;
	private static int hour_;
	private static int minute_;

	public static TimePickerFragment newInstance(DeadlinesEditActivity activity,
			boolean isStartTime, int hour, int minute) {
		activity_ = activity;
		isStartTime_ = isStartTime;
		hour_ = hour;
		minute_ = minute;
		return new TimePickerFragment();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour_, minute_,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (isStartTime_) {
			activity_.startTimeSelected(hourOfDay, minute);
		}
		else {
			activity_.endTimeSelected(hourOfDay, minute);
		}
	}
}