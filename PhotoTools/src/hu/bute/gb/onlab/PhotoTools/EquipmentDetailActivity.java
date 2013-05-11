package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.fragment.DeleteEquipmentDialog;
import hu.bute.gb.onlab.PhotoTools.fragment.EquipmentDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoTools.R;

import java.util.Map;
import java.util.TreeSet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class EquipmentDetailActivity extends SlidingFragmentActivity {
	
	public static final int EQUIPMENT_EDIT = 3;

	private Equipment selectedEquipment_;
	private MenuListFragment menuFragment_;
	private EquipmentDetailFragment detailFragment_;
	private DatabaseLoader databaseLoader_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment_detail);
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			selectedEquipment_ = getIntent().getExtras().getParcelable(
					EquipmentDetailFragment.KEY_EQUIPMENT);
			detailFragment_ = EquipmentDetailFragment.newInstance(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.EquipmentFragmentContainer, detailFragment_).commit();
		}
		else if (savedInstanceState != null) {
			selectedEquipment_ = savedInstanceState
					.getParcelable(EquipmentDetailFragment.KEY_EQUIPMENT);
		}

		// Set the Behind View for the SlidingMenu
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			menuFragment_ = MenuListFragment.newInstance(9, getSlidingMenu());
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			t.replace(R.id.menu_frame, menuFragment_);
			t.commit();
		}
		else {
			menuFragment_ = (MenuListFragment) this.getSupportFragmentManager().findFragmentById(
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
		outState.putParcelable(EquipmentDetailFragment.KEY_EQUIPMENT, selectedEquipment_);
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
			case EQUIPMENT_EDIT:
				if (resultCode == RESULT_OK) {
					Equipment equipment = data.getParcelableExtra(EquipmentDetailFragment.KEY_EQUIPMENT);
					selectedEquipment_ = equipment;
					detailFragment_.onEquipmentChanged(equipment);
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
			myIntent.setClass(EquipmentDetailActivity.this, EquipmentActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_edit_equipment:
			Intent editIntent = new Intent();
			editIntent.setClass(EquipmentDetailActivity.this, EquipmentEditActivity.class);
			editIntent.putExtra(EquipmentEditActivity.KEY_EDIT, true);
			editIntent.putExtra(EquipmentDetailFragment.KEY_EQUIPMENT, selectedEquipment_);
			startActivityForResult(editIntent, EQUIPMENT_EDIT);
			return true;
		case R.id.action_delete_equipment:
			// Show confirmation dialog
			DeleteEquipmentDialog dialog = DeleteEquipmentDialog
					.newInstance(EquipmentDetailActivity.this);
			dialog.show(getSupportFragmentManager(), "Confirm delete equipment");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void deleteEquipment() {
		databaseLoader_.removeEquipment(selectedEquipment_.getID());
		Intent returnIntent = new Intent();
		returnIntent.putExtra("deleted", selectedEquipment_.getID());
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}
