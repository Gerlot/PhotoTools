package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.fragment.DeleteLocationDialog;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class LocationsDetailActivity extends SlidingFragmentActivity {

	private Location selectedLocation_;
	private MenuListFragment menuFragment;
	private LocationsDetailFragment detailFragment_;
	private DatabaseLoader databaseLoader_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_detail);
		databaseLoader_ = PhotoToolsApplication.getTodoDbLoader();

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			selectedLocation_ = getIntent().getExtras().getParcelable(
					LocationsDetailFragment.KEY_LOCATION);
			detailFragment_ = LocationsDetailFragment.newInstance(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.LocationsFragmentContainer, detailFragment_).commit();
		}
		else if (savedInstanceState != null) {
			selectedLocation_ = savedInstanceState
					.getParcelable(LocationsDetailFragment.KEY_LOCATION);
		}

		// Set the Behind View for the SlidingMenu
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			menuFragment = MenuListFragment.newInstance(9, getSlidingMenu());
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			t.replace(R.id.menu_frame, menuFragment);
			t.commit();
		}
		else {
			menuFragment = (MenuListFragment) this.getSupportFragmentManager().findFragmentById(
					R.id.menu_frame);
		}

		// Customize the SlidingMenu
		SlidingMenu menu = getSlidingMenu();
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setSlidingActionBarEnabled(false);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(LocationsDetailFragment.KEY_LOCATION, selectedLocation_);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSlidingMenu().showContent(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			// Location deleted
			case 3:
				if (resultCode == RESULT_OK) {
					Location location = data.getParcelableExtra(LocationsDetailFragment.KEY_LOCATION);
					detailFragment_.onLocationChanged(location);
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
			myIntent.setClass(LocationsDetailActivity.this, LocationsActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_edit_location:
			Intent editIntent = new Intent();
			editIntent.setClass(LocationsDetailActivity.this, LocationsEditActivity.class);
			editIntent.putExtra(LocationsEditActivity.KEY_EDIT, true);
			editIntent.putExtra(LocationsDetailFragment.KEY_LOCATION, selectedLocation_);
			startActivityForResult(editIntent, 3);
			return true;
		case R.id.action_delete_location:
			// Show confirmation dialog
			DeleteLocationDialog dialog = DeleteLocationDialog
					.newInstance(LocationsDetailActivity.this);
			dialog.show(getSupportFragmentManager(), "Confirm delete location");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.locations_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void deleteLocation() {
		// DummyModel.getInstance().removeLocationById(selectedLocation_);
		databaseLoader_.removeLocation(selectedLocation_.getID());
		Intent returnIntent = new Intent();
		returnIntent.putExtra("deleted", selectedLocation_.getID());
		setResult(RESULT_OK, returnIntent);
		finish();
	}

}
