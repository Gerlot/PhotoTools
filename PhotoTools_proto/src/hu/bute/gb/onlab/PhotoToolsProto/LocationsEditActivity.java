package hu.bute.gb.onlab.PhotoToolsProto;

import hu.bute.gb.onlab.PhotoToolsProto.model.Coordinate;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Location;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class LocationsEditActivity extends SherlockFragmentActivity {

	private DummyModel model_;
	private int selectedLocation_ = 0;
	private boolean editMode_ = false;
	private Location location_;

	private LinearLayout linearLayoutSave_;
	private TextView textViewTitle_;
	private ImageView imageViewMap_;
	private EditText editTextName_;
	private EditText editTextAddress_;
	private EditText editTextLatitude_;
	private EditText editTextLongitude_;
	private CheckBox checkBoxCarEntry_;
	private CheckBox checkBoxPowerSource_;
	private EditText editTextNotes_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_edit);
		model_ = DummyModel.getInstance();

		imageViewMap_ = (ImageView) findViewById(R.id.imageViewMap);
		imageViewMap_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mapIntent = new Intent();
				mapIntent.setClass(LocationsEditActivity.this, OnMapActivity.class);
				mapIntent.putExtra("new", true);
				startActivity(mapIntent);
			}
		});

		linearLayoutSave_ = (LinearLayout) findViewById(R.id.linearLayoutSave);
		linearLayoutSave_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveLocation();
			}
		});

		textViewTitle_ = (TextView) findViewById(R.id.textViewTitle);
		editTextName_ = (EditText) findViewById(R.id.editTextName);
		editTextAddress_ = (EditText) findViewById(R.id.editTextAddress);
		editTextLatitude_ = (EditText) findViewById(R.id.editTextLatitude);
		editTextLongitude_ = (EditText) findViewById(R.id.editTextLongitude);
		checkBoxCarEntry_ = (CheckBox) findViewById(R.id.checkBoxCarEntry);
		checkBoxPowerSource_ = (CheckBox) findViewById(R.id.checkBoxPowerSource);
		editTextNotes_ = (EditText) findViewById(R.id.editTextNotes);

		if (getIntent().getExtras().getBoolean("edit")) {
			editMode_ = true;
			selectedLocation_ = getIntent().getExtras().getInt("index");
			location_ = model_.getLocationById(selectedLocation_);
			textViewTitle_.setText("Edit " + location_.getName());

			imageViewMap_.setImageResource(R.drawable.map);
			editTextName_.setText(location_.getName());
			editTextAddress_.setText(location_.getAddress());
			editTextLatitude_.setText(Double.toString(location_.getCoordinate().getLatitude()));
			editTextLongitude_.setText(Double.toString(location_.getCoordinate().getLongitude()));
			checkBoxCarEntry_.setChecked(location_.hasCarEntry());
			checkBoxPowerSource_.setChecked(location_.hasPowerSource());
			editTextNotes_.setText(location_.getNotes());
		}

	}

	private void saveLocation() {

		// Getting values from controls
		String name = editTextName_.getText().toString();
		String address = editTextAddress_.getText().toString();
		Coordinate coordinate = new Coordinate(Double.parseDouble(editTextLatitude_.getText()
				.toString()), Double.parseDouble(editTextLongitude_.getText().toString()));
		double lat = Double.parseDouble(editTextLatitude_.getText().toString());
		double lon = Double.parseDouble(editTextLongitude_.getText().toString());
		boolean carEntry = checkBoxCarEntry_.isChecked();
		boolean powerSource = checkBoxPowerSource_.isChecked();
		String notes = editTextNotes_.getText().toString();
		int id = 0;

		// Only save if name field isn't empty
		if (name.trim().length() == 0) {
			Toast.makeText(LocationsEditActivity.this,
					getResources().getString(R.string.no_location_name_specified),
					Toast.LENGTH_LONG).show();
		}
		else {
			// Edit existing or add new
			if (editMode_) {
				boolean nameChanged = (!location_.getName().equals(name)) ? true : false;
				boolean addressChanged = (!location_.getAddress().equals(address)) ? true : false;
				boolean latitudeChanged = (location_.getCoordinate().getLatitude() != coordinate
						.getLatitude()) ? true : false;
				boolean longitudeChanged = (location_.getCoordinate().getLongitude() != coordinate
						.getLongitude()) ? true : false;
				boolean carEntryChanged = (location_.hasCarEntry() != carEntry) ? true : false;
				boolean powerSourceChanged = (location_.hasPowerSource() != powerSource) ? true
						: false;
				boolean notesChanged = (!location_.getNotes().equals(notes));

				// Only save if something is changed
				if (nameChanged || addressChanged || latitudeChanged || longitudeChanged
						|| carEntryChanged || powerSourceChanged || notesChanged) {
					// Existing ID
					id = location_.getID();
					location_ = new Location(id, name, address, coordinate, carEntry, powerSource,
							notes);
					model_.editLocation(location_);
				}
			}
			else {
				// Generate new ID for new entry
				id = model_.locationId.getAndIncrement();
				location_ = new Location(id, name, address, coordinate, carEntry, powerSource,
						notes);
				model_.addLocation(location_);
			}
			Intent returnIntent = new Intent();
			returnIntent.putExtra("addedname", name);
			returnIntent.putExtra("addedid", id);
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}
}
