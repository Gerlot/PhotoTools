package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsMapFragment;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class LocationsEditActivity extends SherlockFragmentActivity {

	public static final String KEY_EDIT = "edit";

	private boolean editMode_ = false;
	private Location location_;
	private DatabaseLoader databaseLoader_;

	private LinearLayout linearLayoutSave_;
	private TextView textViewTitle_;
	private EditText editTextName_;
	private EditText editTextAddress_;
	private EditText editTextLatitude_;
	private EditText editTextLongitude_;
	private CheckBox checkBoxCarEntry_;
	private CheckBox checkBoxPowerSource_;
	private EditText editTextNotes_;
	
	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private LocationsMapFragment locationsMapFragment_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_edit);
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();

		fragmentContainer_ = (ViewGroup) findViewById(R.id.mapFragmentContainer);
		fragmentManager_ = getSupportFragmentManager();

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

		ArrayList<Location> locations = null;
		if (getIntent().getExtras().getBoolean(KEY_EDIT)) {
			editMode_ = true;
			location_ = getIntent().getExtras().getParcelable(
					LocationsDetailFragment.KEY_LOCATION);
			textViewTitle_.setText("Edit " + location_.getName());
			
			locations = new ArrayList<Location>();
			locations.add(location_);

			editTextName_.setText(location_.getName());
			editTextAddress_.setText(location_.getAddress());
			editTextLatitude_.setText(Double.toString(location_.getCoordinate().getLatitude()));
			editTextLongitude_.setText(Double.toString(location_.getCoordinate().getLongitude()));
			checkBoxCarEntry_.setChecked(location_.hasCarEntry());
			checkBoxPowerSource_.setChecked(location_.hasPowerSource());
			editTextNotes_.setText(location_.getNotes());
		}
		
		locationsMapFragment_ = LocationsMapFragment.newInstance(false, locations);
		FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
		fragmentTransaction.replace(R.id.mapFragmentContainer, locationsMapFragment_);
		fragmentTransaction.commit();

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
		long id = 0;

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
					databaseLoader_.editLocation(location_.getID(), location_);
				}
			}
			else {
				location_ = new Location(id, name, address, coordinate, carEntry, powerSource,
						notes);
				databaseLoader_.addLocation(location_);
			}
			Intent returnIntent = new Intent();
			returnIntent.putExtra("addedname", name);
			returnIntent.putExtra("addedid", id);
			if (editMode_) {
				returnIntent.putExtra(LocationsDetailFragment.KEY_LOCATION, location_);
			}
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}
}
