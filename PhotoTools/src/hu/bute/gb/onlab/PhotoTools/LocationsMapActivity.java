package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.fragment.LocationsMapFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class LocationsMapActivity extends SherlockFragmentActivity {

	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private LocationsMapFragment locationsMapFragment_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_map);

		fragmentContainer_ = (ViewGroup) findViewById(R.id.mapFragmentContainer);
		fragmentManager_ = getSupportFragmentManager();
		locationsMapFragment_ = LocationsMapFragment.newInstance(true, null);
		FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
		fragmentTransaction.replace(R.id.mapFragmentContainer, locationsMapFragment_);
		fragmentTransaction.commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
