package hu.bute.gb.onlab.PhotoToolsProto;

import hu.bute.gb.onlab.PhotoToolsProto.fragment.DeadlinesDetailFragment;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.DeleteDeadlineDialog;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class DeadlinesDetailActivity extends SlidingFragmentActivity {

	private int selectedDeadline_ = 0;
	MenuListFragment menuFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deadlines_detail);

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			selectedDeadline_ = getIntent().getExtras().getInt("index", 0);
			DeadlinesDetailFragment detailFragment = DeadlinesDetailFragment
					.newInstance(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.DeadlinesFragmentContainer, detailFragment).commit();
		}
		else if (savedInstanceState != null) {
			selectedDeadline_ = savedInstanceState.getInt("index");
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
		outState.putInt("index", selectedDeadline_);
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
			myIntent.setClass(DeadlinesDetailActivity.this, DeadlinesActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_edit_deadline:
			Intent editIntent = new Intent();
			editIntent.setClass(DeadlinesDetailActivity.this, DeadlinesEditActivity.class);
			editIntent.putExtra("edit", true);
			editIntent.putExtra("index", selectedDeadline_);
			startActivity(editIntent);
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
		DummyModel.getInstance().removeDeadlineById(selectedDeadline_);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("deleted", selectedDeadline_);
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}
