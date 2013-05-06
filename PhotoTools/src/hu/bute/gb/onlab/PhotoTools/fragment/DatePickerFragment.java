package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.DeadlinesEditActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private static DeadlinesEditActivity activity_;
	private static int year_;
	private static int month_;
	private static int day_;

	public static DatePickerFragment newInstance(DeadlinesEditActivity activity, int year,
			int month, int day) {
		activity_ = activity;
		year_ = year;
		month_ = month - 1; // Because android counts January as 0
		day_ = day;
		return new DatePickerFragment();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("deadline", "edit: " + year_ + month_ + day_);
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year_, month_, day_);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		activity_.dateSelected(year, month, day);
	}

}
