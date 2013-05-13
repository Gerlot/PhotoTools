package hu.bute.gb.onlab.PhotoTools;

import java.util.ArrayList;

import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsMapFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class LocationsMapActivity extends SherlockFragmentActivity {

	public static final String KEY_SINGLELOCATION = "singlelocation";

	private boolean singleLocation_ = false;
	private Location location_ = null;

	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private LocationsMapFragment locationsMapFragment_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_map);

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			singleLocation_ = getIntent().getExtras().getBoolean(KEY_SINGLELOCATION);
			if (singleLocation_) {
				location_ = getIntent().getExtras().getParcelable(
						LocationsDetailFragment.KEY_LOCATION);
				if (location_ != null) {
					setTitle(location_.getName());
				}
				else {
					setTitle("Select on map");
				}
			}
		}
		else if (savedInstanceState != null) {
			singleLocation_ = savedInstanceState.getBoolean(KEY_SINGLELOCATION);
		}

		fragmentContainer_ = (ViewGroup) findViewById(R.id.mapFragmentContainer);
		fragmentManager_ = getSupportFragmentManager();

		if (singleLocation_ && location_ != null) {
			ArrayList<Location> locations = new ArrayList<Location>();
			locations.add(location_);
			locationsMapFragment_ = LocationsMapFragment.newInstance(true, true, locations);
		}
		else {
			locationsMapFragment_ = LocationsMapFragment.newInstance(true, false, null);
		}

		FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
		fragmentTransaction.replace(R.id.mapFragmentContainer, locationsMapFragment_);
		fragmentTransaction.commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_SINGLELOCATION, singleLocation_);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
