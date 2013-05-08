package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.fragment.DeleteEquipmentDialog;
import hu.bute.gb.onlab.PhotoTools.fragment.EquipmentDetailFragment;
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

	private long selectedEquipment_ = 0;
	MenuListFragment menuFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment_detail);

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			selectedEquipment_ = getIntent().getExtras().getLong("index", 0);
			EquipmentDetailFragment detailFragment = EquipmentDetailFragment
					.newInstance(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.EquipmentFragmentContainer, detailFragment).commit();
		}
		else if (savedInstanceState != null) {
			selectedEquipment_ = savedInstanceState.getLong("index");
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
		outState.putLong("index", selectedEquipment_);
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
			myIntent.setClass(EquipmentDetailActivity.this, EquipmentActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_edit_equipment:
			Intent editIntent = new Intent();
			editIntent.setClass(EquipmentDetailActivity.this, EquipmentEditActivity.class);
			editIntent.putExtra("edit", true);
			editIntent.putExtra("index", selectedEquipment_);
			startActivity(editIntent);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.equipment_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItemLend = menu.findItem(R.id.menu_lend_equipment);
		DummyModel model = DummyModel.getInstance();
		Equipment equipment = null;
		for (Map.Entry<String, TreeSet<Equipment>> alphabet : model.equipment.entrySet()) {
			TreeSet<Equipment> current = alphabet.getValue();
			for (Equipment e : current) {
				if (e.getID() == selectedEquipment_) {
					equipment = e;
				}
			}
		}
		if (equipment != null && equipment.isLent()) {
			menuItemLend.setTitle("Equipment is back");
		}
		else {
			menuItemLend.setTitle("Lend this eqipment");
		}

		return super.onPrepareOptionsMenu(menu);
	}

	public void deleteEquipment() {
		DummyModel.getInstance().removeEquipmentById(selectedEquipment_);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("deleted", selectedEquipment_);
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}
