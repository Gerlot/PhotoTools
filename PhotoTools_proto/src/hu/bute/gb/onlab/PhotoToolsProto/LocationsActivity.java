package hu.bute.gb.onlab.PhotoToolsProto;

import hu.bute.gb.onlab.PhotoToolsProto.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.LocationsListFragment;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.MenuListFragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public class LocationsActivity extends SherlockFragmentActivity {

	public List<Integer> locationsOnView = new ArrayList<Integer>();

	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private LocationsListFragment locationsListFragment_;
	private SlidingMenu menu;

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

	}	

	public void showLocationDetails(int index) {
		if (fragmentContainer_ != null) {
			LocationsDetailFragment detailFragment = (LocationsDetailFragment) fragmentManager_
					.findFragmentById(R.id.LocationsFragmentContainer);
			if (detailFragment == null
					|| detailFragment.getSelectedLocation() != locationsOnView.get(index)) {
				detailFragment = LocationsDetailFragment.newInstance(locationsOnView.get(index));
				FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
				fragmentTransaction.replace(R.id.LocationsFragmentContainer, detailFragment);
				fragmentTransaction.commit();
			}

		}
		else {
			Intent intent = new Intent(this, LocationsDetailActivity.class);
			intent.putExtra("index", locationsOnView.get(index));
			startActivityForResult(intent, 1);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			// Location deleted
			case 1:
				if (resultCode == RESULT_OK) {
					// The id of the location to remove
					Integer id = Integer.valueOf(data.getIntExtra("deleted", 0));

					// The location of the removed location in the listView
					int index = locationsOnView.indexOf(id);

					locationsOnView.remove(id);
					String toRemove = locationsListFragment_.listAdapter.getItem(index);
					locationsListFragment_.listAdapter.remove(toRemove);
					if (locationsListFragment_.listAdapter.isEmpty()) {
						locationsListFragment_.isEmpty = true;
						locationsListFragment_.addPlaceHolderText();
					}
				}
				break;
			// Location added
			case 2:
				if (resultCode == RESULT_OK) {
					// The id of the location to add
					Integer id = Integer.valueOf(data.getIntExtra("addedid", 0));

					locationsOnView.add(id);
					String toAdd = data.getStringExtra("addedname");

					// If this is the first location, remove place holder text
					if (locationsListFragment_.isEmpty == true) {
						locationsListFragment_.listAdapter.clear();
						locationsListFragment_.isEmpty = false;
					}

					locationsListFragment_.listAdapter.add(toAdd);
				}
				break;
			}
		}
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
		case R.id.action_search:
			return true;
		case R.id.action_nearby_locations:
			Intent mapIntent = new Intent();
			mapIntent.setClass(LocationsActivity.this, OnMapActivity.class);
			mapIntent.putExtra("edit", false);
			startActivity(mapIntent);
			return true;
		case R.id.action_new_location:
			Intent newIntent = new Intent();
			newIntent.setClass(LocationsActivity.this, LocationsEditActivity.class);
			newIntent.putExtra("edit", false);
			startActivityForResult(newIntent, 2);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.locations, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
