package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsListFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsDetailFragment.ILocationsDetailFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.slidingmenu.lib.SlidingMenu;

public class LocationsActivity extends SherlockFragmentActivity implements ILocationsDetailFragment  {

	//public List<Long> locationsOnView = new ArrayList<Long>();
	public static final int LOCATION_DELETE = 1;
	public static final int LOCATION_ADD = 2;

	private MenuItem searchItem_ = null;
	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private LocationsListFragment locationsListFragment_;
	private SlidingMenu menu;

	private EditText editTextSearch_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations);

		fragmentContainer_ = (ViewGroup) findViewById(R.id.LocationsFragmentContainer);
		fragmentManager_ = getSupportFragmentManager();
		locationsListFragment_ = (LocationsListFragment) fragmentManager_
				.findFragmentById(R.id.locationslist_fragment);

		// Configure the SlidingMenu
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, MenuListFragment.newInstance(0, menu)).commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Close Sliding menu if it was open
		if (menu != null) {
			menu.showContent(false);
		}
	}
	
	public void showLocationDetails(Location selectedLocation){
		if (fragmentContainer_ != null) {
			LocationsDetailFragment detailFragment = (LocationsDetailFragment) fragmentManager_
					.findFragmentById(R.id.LocationsFragmentContainer);
			if (detailFragment == null
					|| detailFragment.getSelectedLocationId() != selectedLocation.getID()) {
				detailFragment = LocationsDetailFragment.newInstance(selectedLocation);
				FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
				fragmentTransaction.replace(R.id.LocationsFragmentContainer, detailFragment);
				fragmentTransaction.commit();
			}
		}
		else {
			Intent intent = new Intent(this, LocationsDetailActivity.class);
			intent.putExtra(LocationsDetailFragment.KEY_LOCATION, selectedLocation);
			startActivityForResult(intent, LOCATION_DELETE); // Listen for delete event
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			case LOCATION_DELETE:
				if (resultCode == RESULT_OK) {
					locationsListFragment_.refreshList();
				}
				break;
			case LOCATION_ADD:
				if (resultCode == RESULT_OK) {
					locationsListFragment_.refreshList();
				}
				break;
			}
		}
	}

	@Override
	public void onOptionsMenuClosed(android.view.Menu menu) {
		super.onOptionsMenuClosed(menu);
		Toast.makeText(LocationsActivity.this, "closed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent myIntent = new Intent();
			myIntent.setClass(LocationsActivity.this, MainActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_nearby_locations:
			Intent mapIntent = new Intent();
			mapIntent.setClass(LocationsActivity.this, LocationsMapActivity.class);
			mapIntent.putExtra(LocationsMapActivity.KEY_SINGLELOCATION, false);
			startActivity(mapIntent);
			return true;
		case R.id.action_new_location:
			Intent newIntent = new Intent();
			newIntent.setClass(LocationsActivity.this, LocationsEditActivity.class);
			newIntent.putExtra("edit", false);
			startActivityForResult(newIntent, LOCATION_ADD);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.locations, menu);
		
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				locationsListFragment_.search(newText);
				return false;
			}
		});
		
		searchItem_ = (MenuItem) menu.getItem(0);
		searchItem_.setOnActionExpandListener(new OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				locationsListFragment_.searchFilter = null;
				locationsListFragment_.refreshList();
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void showOnMap(Location location) {
		Intent mapIntent = new Intent();
		mapIntent.setClass(LocationsActivity.this, LocationsMapActivity.class);
		mapIntent.putExtra(LocationsMapActivity.KEY_SINGLELOCATION, true);
		mapIntent.putExtra(LocationsDetailFragment.KEY_LOCATION, location);
		startActivity(mapIntent);
	}

}
