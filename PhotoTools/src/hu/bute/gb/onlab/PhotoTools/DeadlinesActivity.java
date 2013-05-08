package hu.bute.gb.onlab.PhotoTools;

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

	public List<Long> deadlinesOnView = new ArrayList<Long>();

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

	public void showDeadlineDetails(int index) {
		if (fragmentContainer_ != null) {
			DeadlinesDetailFragment detailFragment = (DeadlinesDetailFragment) fragmentManager_
					.findFragmentById(R.id.DeadlinesFragmentContainer);
			if (detailFragment == null
					|| detailFragment.getSelectedDeadline() != deadlinesOnView.get(index)) {
				detailFragment = DeadlinesDetailFragment.newInstance(deadlinesOnView.get(index));
				FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
				fragmentTransaction.replace(R.id.DeadlinesFragmentContainer, detailFragment);
				fragmentTransaction.commit();
			}
		}
		else {
			Intent intent = new Intent(this, DeadlinesDetailActivity.class);
			intent.putExtra("index", deadlinesOnView.get(index));
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
					/*Integer id = Integer.valueOf(data.getIntExtra("deleted", 0));

					// The index of the location to remove in the listView
					int index = deadlinesOnView.indexOf(id);
					// The index of the location to remove in its category
					int lastZero = 1;
					Log.d("deadline", "size: " + deadlinesOnView.size());
					Log.d("deadline", "index: " + index);
					for (int i = 0; i < deadlinesOnView.size(); i++) {
						Log.d("deadline", "" + deadlinesOnView.get(i).intValue());
						if (i >= index) {
							break;
						}
						if (deadlinesOnView.get(i).intValue() == 0) {
							lastZero = i+1;
						}
					}

					// Getting the deadline to remove, and its category
					Deadline deadlineToRemove = DummyModel.getInstance().getDeadlineById(id);
					DeadlineDay category = new DeadlineDay(deadlineToRemove.getStartTime());
					
					// Get, and remove the deadline from the list
					DeadlineAdapter categoryAdapter = (DeadlineAdapter) deadlinesListFragment_.listAdapter
							.getSectionAdapter(category.toString());
					int indexInCategory = index - lastZero;
					Log.d("deadline", "indexincat: " + indexInCategory);
					DeadlineItem toRemove = categoryAdapter.getItem(indexInCategory);
					categoryAdapter.remove(toRemove);
					
					DummyModel.getInstance().removeDeadlineById(id);
					deadlinesOnView.remove(id);*/
				}
				break;
			// Location added
			case 2:
				if (resultCode == RESULT_OK) {
					// The id of the location to add
					Long id = Long.valueOf(data.getIntExtra("addedid", 0));

					deadlinesOnView.add(id);
					String toAdd = data.getStringExtra("addedname");
					// deadlinesListFragment_.listAdapter.add(toAdd);
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
			newIntent.putExtra("edit", false);
			startActivityForResult(newIntent, 2);
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
