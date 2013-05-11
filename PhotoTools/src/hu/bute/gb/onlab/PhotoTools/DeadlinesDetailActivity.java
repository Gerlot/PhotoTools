package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.fragment.DeadlinesDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.DeleteDeadlineDialog;
import hu.bute.gb.onlab.PhotoTools.fragment.EquipmentDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoTools.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class DeadlinesDetailActivity extends SlidingFragmentActivity {

	public static final int DEADLINE_EDIT = 3;

	private Deadline selectedDeadline_;
	private MenuListFragment menuFragment_;
	private DeadlinesDetailFragment detailFragment_;
	private DatabaseLoader databaseLoader_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deadlines_detail);
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			selectedDeadline_ = getIntent().getExtras().getParcelable(
					DeadlinesDetailFragment.KEY_DEADLINE);
			detailFragment_ = DeadlinesDetailFragment.newInstance(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.DeadlinesFragmentContainer, detailFragment_).commit();
		}
		else if (savedInstanceState != null) {
			selectedDeadline_ = savedInstanceState
					.getParcelable(DeadlinesDetailFragment.KEY_DEADLINE);
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
		outState.putParcelable(DeadlinesDetailFragment.KEY_DEADLINE, selectedDeadline_);
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
			case DEADLINE_EDIT:
				if (resultCode == RESULT_OK) {
					Deadline deadline = data
							.getParcelableExtra(DeadlinesDetailFragment.KEY_DEADLINE);
					selectedDeadline_ = deadline;
					detailFragment_.onDeadlineChanged(deadline);
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
			myIntent.setClass(DeadlinesDetailActivity.this, DeadlinesActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_edit_deadline:
			Intent editIntent = new Intent();
			editIntent.setClass(DeadlinesDetailActivity.this, DeadlinesEditActivity.class);
			editIntent.putExtra(DeadlinesEditActivity.KEY_EDIT, true);
			editIntent.putExtra(DeadlinesDetailFragment.KEY_DEADLINE, selectedDeadline_);
			startActivityForResult(editIntent, DEADLINE_EDIT);
			return true;
		case R.id.action_delete_deadline:
			// Show confirmation dialog
			DeleteDeadlineDialog dialog = DeleteDeadlineDialog
					.newInstance(DeadlinesDetailActivity.this);
			dialog.show(getSupportFragmentManager(), "Confirm delete deadline");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.deadlines_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void deleteDeadline() {
		databaseLoader_.removeDeadline(selectedDeadline_.getID());
		Intent returnIntent = new Intent();
		returnIntent.putExtra("deleted", selectedDeadline_.getID());
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}
