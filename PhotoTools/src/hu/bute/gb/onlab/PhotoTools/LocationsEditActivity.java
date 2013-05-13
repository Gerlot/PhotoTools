package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsMapFragment;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
	private View viewMap_;
	private FragmentManager fragmentManager_;
	private LocationsMapFragment locationsMapFragment_;

	private GeocodeTask geocodeTask_ = null;

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
		editTextAddress_.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					Toast.makeText(LocationsEditActivity.this,
							"Getting coordinates from address.", Toast.LENGTH_SHORT)
							.show();
					if (geocodeTask_ != null) {
						geocodeTask_.cancel(false);
					}
					geocodeTask_ = new GeocodeTask();
					geocodeTask_.execute();
				}
			}
		});

		editTextLatitude_ = (EditText) findViewById(R.id.editTextLatitude);
		editTextLongitude_ = (EditText) findViewById(R.id.editTextLongitude);
		checkBoxCarEntry_ = (CheckBox) findViewById(R.id.checkBoxCarEntry);
		checkBoxPowerSource_ = (CheckBox) findViewById(R.id.checkBoxPowerSource);
		editTextNotes_ = (EditText) findViewById(R.id.editTextNotes);

		ArrayList<Location> locations = null;
		if (getIntent().getExtras().getBoolean(KEY_EDIT)) {
			editMode_ = true;
			location_ = getIntent().getExtras().getParcelable(LocationsDetailFragment.KEY_LOCATION);
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

		locationsMapFragment_ = LocationsMapFragment.newInstance(false, false, locations);
		viewMap_ = findViewById(R.id.viewMap);
		viewMap_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mapIntent = new Intent();
				mapIntent.setClass(LocationsEditActivity.this, LocationsMapActivity.class);
				mapIntent.putExtra(LocationsMapActivity.KEY_SINGLELOCATION, true);
				mapIntent.putExtra(LocationsDetailFragment.KEY_LOCATION, location_);
				startActivity(mapIntent);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (geocodeTask_ != null) {
			geocodeTask_.cancel(false);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//locationsMapFragment_ = LocationsMapFragment.newInstance(false, false, locations);
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

	// Background task for geocoding
	private class GeocodeTask extends AsyncTask<Void, Void, List<Address>> {
		private static final String TAG = "GeocodeTask";

		@Override
		protected List<Address> doInBackground(Void... params) {
			String address = editTextAddress_.getText().toString();
			Geocoder geocoder = new Geocoder(LocationsEditActivity.this);
			try {
				List<Address> result = geocoder.getFromLocationName(address, 1);
				if (!isCancelled()) {
					return result;
				}
				else {
					Log.d(TAG, "Cancelled");
					return null;
				}
			}
			catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Address> result) {
			super.onPostExecute(result);
			double latitude = -1;
			double longitude = -1;
			if (result != null && result.size() != 0) {
				latitude = result.get(0).getLatitude();
				longitude = result.get(0).getLongitude();
			}
			else {
				Log.d("location", "error");
			}
			
			if (latitude != -1 && longitude != -1) {
				Toast.makeText(LocationsEditActivity.this,
						"Coordinates set.", Toast.LENGTH_SHORT)
						.show();
				editTextLatitude_.setText(Double.toString(latitude));
				editTextLongitude_.setText(Double.toString(longitude));
				ArrayList<Location> locations = new ArrayList<Location>();
				Location location = null;
				if (location_ != null) {
					location = location_;
				}
				else {
					location = new Location(100, "", "", null, false, false, "");
				}
				location.setCoordinate(new Coordinate(latitude, longitude));
				locations.add(location);
				locationsMapFragment_ = LocationsMapFragment.newInstance(false, false, locations);
			}
			else {
				Toast.makeText(LocationsEditActivity.this,
						"Can't get coordinates.", Toast.LENGTH_SHORT)
						.show();
			}

			geocodeTask_ = null;
		}
	}
}
