package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsListFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoTools.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.slidingmenu.lib.SlidingMenu;

public class LocationsActivity extends SherlockFragmentActivity {

	public List<Integer> locationsOnView = new ArrayList<Integer>();

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
		case R.id.action_search:
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
		
		final EditText editTextSearch = (EditText) menu.findItem(R.id.action_search).getActionView();
		editTextSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before,
					int count) {
				locationsListFragment_.search(charSequence);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		MenuItem searchItem = (MenuItem) menu.getItem(0);
		searchItem.setOnActionExpandListener(new OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				editTextSearch.requestFocus();
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				editTextSearch.setText("");
				locationsListFragment_.listAdapter.clear();
				locationsListFragment_.populateList();
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

}
