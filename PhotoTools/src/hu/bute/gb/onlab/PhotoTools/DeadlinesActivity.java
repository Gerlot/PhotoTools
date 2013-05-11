package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import hu.bute.gb.onlab.PhotoTools.fragment.DeadlinesDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.DeadlinesListFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public class DeadlinesActivity extends SherlockFragmentActivity {

	//public List<Long> deadlinesOnView = new ArrayList<Long>();
	public static final int DEADLINE_DELETE = 1;
	public static final int DEADLINE_ADD = 2;
	
	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private DeadlinesListFragment deadlinesListFragment_;

	private boolean tabletSize_;
	private SlidingMenu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deadlines);

		DateTime today = new DateTime();
		setTitle("Today is " + today.toString("yyyy.MM.dd., EEEE"));

		fragmentContainer_ = (ViewGroup) findViewById(R.id.DeadlinesFragmentContainer);
		fragmentManager_ = getSupportFragmentManager();
		deadlinesListFragment_ = (DeadlinesListFragment) fragmentManager_
				.findFragmentById(R.id.deadlineslist_fragment);

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
				.replace(R.id.menu_frame, MenuListFragment.newInstance(1, menu)).commit();

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

	public void showDeadlineDetails(Deadline selectedDeadline) {
		if (fragmentContainer_ != null) {
			DeadlinesDetailFragment detailFragment = (DeadlinesDetailFragment) fragmentManager_
					.findFragmentById(R.id.DeadlinesFragmentContainer);
			if (detailFragment == null
					|| detailFragment.getSelectedDeadlineId() != selectedDeadline.getID()) {
				detailFragment = DeadlinesDetailFragment.newInstance(selectedDeadline);
				FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
				fragmentTransaction.replace(R.id.DeadlinesFragmentContainer, detailFragment);
				fragmentTransaction.commit();
			}
		}
		else {
			Intent intent = new Intent(this, DeadlinesDetailActivity.class);
			intent.putExtra(DeadlinesDetailFragment.KEY_DEADLINE, selectedDeadline);
			startActivityForResult(intent, DEADLINE_DELETE); // Listen for delete event
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			// Location deleted
			case DEADLINE_DELETE:
				if (resultCode == RESULT_OK) {
					deadlinesListFragment_.refreshList();
				}
				break;
			// Location added
			case DEADLINE_ADD:
				if (resultCode == RESULT_OK) {
					deadlinesListFragment_.refreshList();
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
			myIntent.setClass(DeadlinesActivity.this, MainActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_new_deadline:
			Intent newIntent = new Intent();
			newIntent.setClass(DeadlinesActivity.this, DeadlinesEditActivity.class);
			newIntent.putExtra(DeadlinesEditActivity.KEY_EDIT, false);
			startActivityForResult(newIntent, DEADLINE_ADD);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.deadlines, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
