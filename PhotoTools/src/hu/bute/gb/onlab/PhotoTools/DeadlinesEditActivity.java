package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import hu.bute.gb.onlab.PhotoTools.fragment.DatePickerFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.DeadlinesDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.EquipmentDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.TimePickerFragment;
import hu.bute.gb.onlab.PhotoTools.R;

import org.joda.time.DateTime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class DeadlinesEditActivity extends SherlockFragmentActivity {

	public static final String KEY_EDIT = "edit";

	private boolean editMode_ = false;
	private Deadline deadline_;
	private DatabaseLoader databaseLoader_;

	private LinearLayout linearLayoutSave_;
	private TextView textViewTitle_;
	private DateTime selectedDate_ = new DateTime();
	private DateTime selectedStartTime_ = selectedDate_;
	private DateTime selectedEndTime_ = selectedDate_;
	private EditText editTextDeadlineName_;
	private Button buttonDate_;
	private Button buttonStartTime_;
	private TextView textViewSeparator_;
	private Button buttonEndTime_;
	private CheckBox checkBoxAllDay_;
	private EditText editTextLocation_;
	private EditText editTextDeadlineNotes_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deadlines_edit);
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();

		linearLayoutSave_ = (LinearLayout) findViewById(R.id.linearLayoutSave);
		linearLayoutSave_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveDeadline();
			}
		});

		textViewTitle_ = (TextView) findViewById(R.id.textViewTitle);
		editTextDeadlineName_ = (EditText) findViewById(R.id.editTextDeadlineName);
		buttonDate_ = (Button) findViewById(R.id.buttonDate);
		buttonStartTime_ = (Button) findViewById(R.id.buttonStartTime);
		textViewSeparator_ = (TextView) findViewById(R.id.textViewSeparator);
		buttonEndTime_ = (Button) findViewById(R.id.buttonEndTime);
		checkBoxAllDay_ = (CheckBox) findViewById(R.id.checkBoxAllDay);
		checkBoxAllDay_.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// Set start time to midnight if all day selected
					DateTime day = deadline_.getStartTime();
					deadline_.setStartTime(new DateTime(day.getYear(), day.getMonthOfYear(), day
							.getDayOfMonth(), 0, 0));
					selectedStartTime_ = deadline_.getStartTime();

					buttonStartTime_.setVisibility(View.GONE);
					textViewSeparator_.setVisibility(View.GONE);
					buttonEndTime_.setVisibility(View.GONE);
				}
				else {
					buttonStartTime_.setVisibility(View.VISIBLE);
					textViewSeparator_.setVisibility(View.VISIBLE);
					buttonEndTime_.setVisibility(View.VISIBLE);
					buttonStartTime_
							.setText(selectedStartTime_.toLocalDateTime().toString("HH:mm"));
				}
			}
		});

		editTextLocation_ = (EditText) findViewById(R.id.editTextDeadlineLocation);
		editTextDeadlineNotes_ = (EditText) findViewById(R.id.editTextDeadlineNotes);

		if (getIntent().getExtras().getBoolean(KEY_EDIT)) {
			editMode_ = true;
			deadline_ = getIntent().getExtras().getParcelable(DeadlinesDetailFragment.KEY_DEADLINE);

			textViewTitle_.setText("Edit " + deadline_.getName());

			selectedDate_ = deadline_.getStartTime();
			selectedStartTime_ = selectedDate_;
			selectedEndTime_ = deadline_.getEndTime();

			editTextDeadlineName_.setText(deadline_.getName());
			buttonDate_.setText(selectedDate_.toLocalDateTime().toString("yyyy. MM. dd., EEEE"));

			if (deadline_.isAllDay()) {
				buttonStartTime_.setVisibility(View.GONE);
				textViewSeparator_.setVisibility(View.GONE);
				buttonEndTime_.setVisibility(View.GONE);
				checkBoxAllDay_.setChecked(true);
			}
			else {
				buttonStartTime_.setText(selectedStartTime_.toLocalDateTime().toString("HH:mm"));
				if (selectedEndTime_ != null) {
					buttonEndTime_.setText(selectedEndTime_.toLocalDateTime().toString("HH:mm"));
				}
				else {
					buttonEndTime_.setText(selectedStartTime_.toLocalDateTime().toString("HH:mm"));
				}
			}

			editTextDeadlineNotes_.setText(deadline_.getNotes());
			editTextLocation_.setText(deadline_.getLocation().toString());

		}
		else {
			DateTime now = new DateTime();
			selectedDate_ = now;
			selectedStartTime_ = now.plusHours(1);
			selectedEndTime_ = now.plusHours(2);
			buttonDate_.setText(selectedDate_.toString("yyyy. MM. dd., EEEE"));
			buttonStartTime_.setText(selectedStartTime_.toString("HH:mm"));
			buttonEndTime_.setText(selectedEndTime_.toString("HH:mm"));
		}

		// Showing Date and Time picker dialogs on click
		buttonDate_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment datePicker = DatePickerFragment.newInstance(
						DeadlinesEditActivity.this, selectedDate_.getYear(),
						selectedDate_.getMonthOfYear(), selectedDate_.getDayOfMonth());
				datePicker.show(getSupportFragmentManager(), "datePicker");
			}
		});

		buttonStartTime_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment timePicker = TimePickerFragment.newInstance(
						DeadlinesEditActivity.this, true, selectedStartTime_.getHourOfDay(),
						selectedStartTime_.getMinuteOfHour());
				timePicker.show(getSupportFragmentManager(), "timePicker");
			}
		});

		buttonEndTime_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment timePicker = TimePickerFragment.newInstance(
						DeadlinesEditActivity.this, false, selectedEndTime_.getHourOfDay(),
						selectedEndTime_.getMinuteOfHour());
				timePicker.show(getSupportFragmentManager(), "timePicker");
			}
		});

	}

	public void dateSelected(int year, int month, int day) {
		// Android date picker counts January as 0
		selectedDate_ = new DateTime(year, month + 1, day, 0, 0);

		// Change the start time's date also
		int hour = selectedStartTime_.getHourOfDay();
		int minute = selectedStartTime_.getMinuteOfHour();
		selectedStartTime_ = new DateTime(selectedDate_.getYear(), selectedDate_.getMonthOfYear(),
				selectedDate_.getDayOfMonth(), hour, minute);
		buttonDate_.setText(selectedDate_.toString("yyyy. MM. dd., EEEE"));
	}

	public void startTimeSelected(int hour, int minute) {
		selectedStartTime_ = new DateTime(selectedDate_.getYear(), selectedDate_.getMonthOfYear(),
				selectedDate_.getDayOfMonth(), hour, minute);
		buttonStartTime_.setText(selectedStartTime_.toLocalDateTime().toString("HH:mm"));
	}

	public void endTimeSelected(int hour, int minute) {
		selectedEndTime_ = new DateTime(selectedDate_.getYear(), selectedDate_.getMonthOfYear(),
				selectedDate_.getDayOfMonth(), hour, minute);
		buttonEndTime_.setText(selectedEndTime_.toLocalDateTime().toString("HH:mm"));
	}

	private void saveDeadline() {

		// Getting values from controls
		String name = editTextDeadlineName_.getText().toString();
		boolean isAllDay = checkBoxAllDay_.isChecked();
		String location = editTextLocation_.getText().toString();
		String notes = editTextDeadlineNotes_.getText().toString();
		long id = 0;

		// Only save if name field isn't empty
		if (name.trim().length() == 0) {
			Toast.makeText(DeadlinesEditActivity.this,
					getResources().getString(R.string.no_deadline_name_specified),
					Toast.LENGTH_LONG).show();
		}
		else {
			// Edit existing or add new
			if (editMode_) {
				boolean nameChanged = (!deadline_.getName().equals(name)) ? true : false;
				boolean startTimeChanged = (!deadline_.getStartTime().equals(selectedStartTime_)) ? true
						: false;
				boolean endTimeChanged = false;
				if (deadline_.getEndTime() != null) {
					endTimeChanged = (!deadline_.getEndTime().equals(selectedEndTime_)) ? true
							: false;
				}
				boolean allDayChanged = (deadline_.isAllDay() != isAllDay);
				boolean locationChanged = (!deadline_.getLocation().equals(location)) ? true
						: false;
				boolean notesChanged = (!deadline_.getNotes().equals(notes));
				// Only save if something is changed
				if (nameChanged || startTimeChanged || endTimeChanged || allDayChanged
						|| locationChanged || notesChanged) {
					// Existing ID
					id = deadline_.getID();
					deadline_ = new Deadline(id, name, selectedStartTime_, selectedEndTime_,
							isAllDay, location, notes);
					databaseLoader_.editDeadline(deadline_.getID(), deadline_);
				}
			}
			else {
				deadline_ = new Deadline(id, name, selectedStartTime_, selectedEndTime_, isAllDay,
						location, notes);
				databaseLoader_.addDeadline(deadline_);

			}
			Intent returnIntent = new Intent();
			returnIntent.putExtra("addedname", name);
			returnIntent.putExtra("addedid", id);
			if (editMode_) {
				returnIntent.putExtra(DeadlinesDetailFragment.KEY_DEADLINE, deadline_);
			}
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}

}