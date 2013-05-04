package hu.bute.gb.onlab.PhotoToolsProto;

import hu.bute.gb.onlab.PhotoToolsProto.fragment.DeleteLocationDialog;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class LocationsDetailActivity extends SlidingFragmentActivity {

	private int selectedLocation_ = 0;
	MenuListFragment menuFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_detail);

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			selectedLocation_ = getIntent().getExtras().getInt("index", 0);
			LocationsDetailFragment detailFragment = LocationsDetailFragment
					.newInstance(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.LocationsFragmentContainer, detailFragment).commit();
		}
		else if (savedInstanceState != null) {
			selectedLocation_ = savedInstanceState.getInt("index");
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
		outState.putInt("index", selectedLocation_);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSlidingMenu().showContent(false);
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
			editIntent.putExtra("edit", true);
			editIntent.putExtra("index", selectedLocation_);
			startActivity(editIntent);
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
		DummyModel.getInstance().removeLocationById(selectedLocation_);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("deleted", selectedLocation_);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

}
