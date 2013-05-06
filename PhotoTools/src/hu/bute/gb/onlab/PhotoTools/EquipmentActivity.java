package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.fragment.EquipmentDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.EquipmentListFragment;
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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.slidingmenu.lib.SlidingMenu;

public class EquipmentActivity extends SherlockFragmentActivity {

	public List<Integer> equipmentOnView = new ArrayList<Integer>();

	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private EquipmentListFragment equipmentListFragment_;

	private boolean tabletSize_;
	private SlidingMenu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment);

		fragmentContainer_ = (ViewGroup) findViewById(R.id.EquipmentFragmentContainer);
		fragmentManager_ = getSupportFragmentManager();
		equipmentListFragment_ = (EquipmentListFragment) fragmentManager_
				.findFragmentById(R.id.equipmentlist_fragment);

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
				.replace(R.id.menu_frame, MenuListFragment.newInstance(2, menu)).commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (getIntent().getExtras() != null) {
			Bundle arguments = getIntent().getExtras();
			int index = arguments.getInt("index");
			equipmentOnView.add(Integer.valueOf(index));
			showEquipmentDetails(0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Close Sliding menu if it was open
		if (menu != null) {
			menu.showContent(false);
		}
		// Configure the SlidingMenu
		/*
		 * menu = new SlidingMenu(this);
		 * menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		 * menu.setShadowWidthRes(R.dimen.shadow_width);
		 * menu.setShadowDrawable(R.drawable.shadow);
		 * menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		 * menu.setFadeDegree(0.35f); menu.attachToActivity(this,
		 * SlidingMenu.SLIDING_CONTENT); menu.setMenu(R.layout.menu_frame);
		 * getSupportFragmentManager().beginTransaction()
		 * .replace(R.id.menu_frame, MenuListFragment.newInstance(2,
		 * menu)).commit();
		 */
	}

	public void showEquipmentDetails(int index) {
		if (fragmentContainer_ != null) {
			EquipmentDetailFragment detailFragment = (EquipmentDetailFragment) fragmentManager_
					.findFragmentById(R.id.EquipmentFragmentContainer);
			if (detailFragment == null
					|| detailFragment.getSelectedEquipment() != equipmentOnView.get(index)) {
				detailFragment = EquipmentDetailFragment.newInstance(equipmentOnView.get(index));
				FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
				fragmentTransaction.replace(R.id.EquipmentFragmentContainer, detailFragment);
				fragmentTransaction.commit();
			}
		}
		else {
			Intent intent = new Intent(this, EquipmentDetailActivity.class);
			intent.putExtra("index", equipmentOnView.get(index));
			startActivityForResult(intent, 1);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			// Location deleted
			switch (requestCode) {
			case 1:
				if (resultCode == RESULT_OK) {
					// The id of the location to remove
					Integer id = Integer.valueOf(data.getIntExtra("deleted", 0));
					
					// The location of the removed location in the listView
					int index = equipmentOnView.indexOf(id);
					
					equipmentOnView.remove(id);
					
					/*Integer idToRemove = null;
					for (Integer id : equipmentOnView) {
						if (id == Integer.valueOf(data.getIntExtra("deleted", 0))) {
							idToRemove = id;
							break;
						}
					}
					if (idToRemove != null) {
						equipmentOnView.remove(idToRemove);
					}*/
				}
				break;
			case 2:
				/*if (resultCode == RESULT_OK) {
					// The id of the location to add
					Integer id = Integer.valueOf(data.getIntExtra("addedid", 0));
					
					locationsOnView.add(id);
					String toAdd = data.getStringExtra("addedname");
					locationsListFragment_.listAdapter_.add(toAdd);
				}*/
				break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent myIntent = new Intent();
			myIntent.setClass(EquipmentActivity.this, MainActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_search:
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			return true;
		case R.id.action_new_equipment:
			Intent newIntent = new Intent();
			newIntent.setClass(EquipmentActivity.this, EquipmentEditActivity.class);
			newIntent.putExtra("edit", false);
			startActivityForResult(newIntent, 2);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.equipment, menu);
		
		final EditText editTextSearch = (EditText) menu.findItem(R.id.action_search).getActionView();
		editTextSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before,
					int count) {
				equipmentListFragment_.populateList(charSequence);
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
				equipmentListFragment_.populateList(null);
				return true;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
}
