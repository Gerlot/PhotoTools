package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.DeadlinesActivity;
import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DbConstants;
import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import hu.bute.gb.onlab.PhotoTools.helpers.DeadlineDay;
import hu.bute.gb.onlab.PhotoTools.helpers.DeadlinesAdapter;
import hu.bute.gb.onlab.PhotoTools.helpers.SeparatedListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class DeadlinesListFragment extends SherlockFragment {

	// Log tag
	public static final String TAG = "DeadlinesListFragment";
	public String searchFilter = null;
	public SeparatedListAdapter listAdapter = null;
	public boolean isEmpty = true;

	// private ArrayList<String> usedDateStrings_;
	private ArrayList<DeadlineDay> usedDates_;
	private DeadlinesActivity activity_;
	private int selectedPosition_ = 0;

	private DateMidnight today_;
	private DateMidnight displayAfterThis_;
	private DateMidnight displayBeforeThis_;

	private Button buttonMoreBefore_;
	private Button buttonMoreAfter_;
	private ListView listViewDeadlines_;

	// State
	private LocalBroadcastManager broadcastManager;

	// DBloader
	private DatabaseLoader databaseLoader;

	private GetAllTask[] getAllTask;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = (DeadlinesActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		today_ = (new DateTime()).toDateMidnight();
		if (savedInstanceState != null) {
			displayAfterThis_ = new DateMidnight(savedInstanceState.getLong("after"));
			displayBeforeThis_ = new DateMidnight(savedInstanceState.getLong("before"));
		}
		else {
			displayAfterThis_ = today_;
			displayBeforeThis_ = today_.plusDays(7);
		}

		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		databaseLoader = PhotoToolsApplication.getDatabaseLoader();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_deadlines_list, container, false);

		buttonMoreBefore_ = (Button) view.findViewById(R.id.buttonMoreBefore);
		buttonMoreBefore_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Display dates one week earlier
				displayAfterThis_ = displayAfterThis_.minusDays(7);
				onDatesShowingChanged();
			}
		});

		buttonMoreAfter_ = (Button) view.findViewById(R.id.buttonMoreAfter);
		buttonMoreAfter_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Display dates one week later
				displayBeforeThis_ = displayBeforeThis_.plusDays(7);
				onDatesShowingChanged();
			}
		});

		listViewDeadlines_ = (ListView) view.findViewById(R.id.listViewDeadlines);
		listViewDeadlines_.setSelection(selectedPosition_);
		listViewDeadlines_.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!isEmpty) {
					Deadline selectedDeadline = (Deadline) listViewDeadlines_.getAdapter().getItem(
							position);
					activity_.showDeadlineDetails(selectedDeadline);
					selectedPosition_ = position;
				}
			}

		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Unregister the reciever
		IntentFilter filter = new IntentFilter(DbConstants.ACTION_DATABASE_CHANGED);
		broadcastManager.registerReceiver(updateDatabaseReceiver, filter);

		// Refresh buttons and the list
		onDatesShowingChanged();
	}

	public void onDatesShowingChanged() {
		buttonMoreBefore_.setText(getResources().getString(R.string.showing_since) + " "
				+ displayAfterThis_.toString("yyyy. MMM dd.") + " "
				+ getResources().getString(R.string.press_for_more));
		buttonMoreAfter_.setText(getResources().getString(R.string.showing_until) + " "
				+ displayBeforeThis_.toString("yyyy. MMM dd.") + " "
				+ getResources().getString(R.string.press_for_more));
		refreshList();
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister the reciever and cancel running tasks
		broadcastManager.unregisterReceiver(updateDatabaseReceiver);
		if (getAllTask != null) {
			for (GetAllTask task : getAllTask) {
				if (task != null) {
					task.cancel(false);
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Closing existing cursors
		if (listAdapter != null) {
			for (DeadlineDay date : usedDates_) {
				// In case of today, the displayed label is "today"
				String dateString = date.toString();
				if (dateString.equals(today_.toString("yyyy. MMM dd., EEEE"))) {
					dateString = getResources().getString(R.string.today);
				}
				DeadlinesAdapter adapter = (DeadlinesAdapter) listAdapter
						.getSectionAdapter(dateString);
				if (adapter != null && adapter.getCursor() != null) {
					adapter.getCursor().close();
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedDeadline", selectedPosition_);
		outState.putLong("after", displayAfterThis_.getMillis());
		outState.putLong("before", displayBeforeThis_.getMillis());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity_ = null;
	}

	public void refreshList() {
		// usedDateStrings_ =
		// databaseLoader.getUsedDateStrings(displayAfterThis_.getMillis(),
		// displayBeforeThis_.getMillis());
		usedDates_ = databaseLoader.getUsedDates(displayAfterThis_.getMillis(),
				displayBeforeThis_.getMillis());
		if (usedDates_ != null) {
			listAdapter = new SeparatedListAdapter(getActivity());
			for (DeadlineDay date : usedDates_) {
				DeadlinesAdapter deadlinesAdapter = new DeadlinesAdapter(getActivity()
						.getApplicationContext(), null, getResources().getColor(R.color.orange));
				// In case of today, display label as "Today"
				String dateString = date.toString();
				Log.d("deadline", dateString);
				Log.d("deadline", "today is " + today_.toString("yyyy. MMM dd., EEEE"));
				Log.d("deadline", "" + dateString.equals(today_.toString("yyyy. MMM dd., EEEE")));
				if (dateString.equals(today_.toString("yyyy. MMM dd., EEEE"))) {
					dateString = getResources().getString(R.string.today);
				}
				Log.d("deadline", "adding section: " + dateString);
				listAdapter.addSection(dateString, deadlinesAdapter);
			}
			listViewDeadlines_.setAdapter(listAdapter);

			getAllTask = new GetAllTask[usedDates_.size()];

			for (int i = 0; i < getAllTask.length; i++) {
				GetAllTask task = getAllTask[i];
				if (task != null) {
					task.cancel(false);
				}
				task = new GetAllTask(i, usedDates_.get(i));
				task.execute();
			}
		}
		else {
			isEmpty = true;
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);
			listViewDeadlines_.setAdapter(emptyAdapter);
			listAdapter = null;
		}
	}

	private class GetAllTask extends AsyncTask<Void, Void, Cursor> {
		private static final String TAG = "GetAllTask";
		private int index_;
		private DeadlineDay date_;

		public GetAllTask(int index, DeadlineDay date) {
			index_ = index;
			date_ = date;
		}

		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				DateMidnight dateMidnight = date_.getDateInDateTime();
				Cursor result = databaseLoader.getDeadlinesBetweenDays(dateMidnight.getMillis(),
						dateMidnight.plusDays(1).getMillis());
				if (!isCancelled()) {
					return result;
				}
				else {
					Log.d(TAG, "Cancelled, closing cursor");
					if (result != null) {
						result.close();
					}
					return null;
				}
			}
			catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);
			Log.d(TAG, "Fetch completed, displaying cursor results!");
			if (result != null) {
				isEmpty = false;
				try {
					// In case of today, the displayed label is "today"
					String categoryString = date_.toString();
					if (categoryString.equals(today_.toString("yyyy. MMM dd., EEEE"))) {
						categoryString = getResources().getString(R.string.today);
					}
					DeadlinesAdapter deadlinesAdapter = (DeadlinesAdapter) listAdapter
							.getSectionAdapter(categoryString);
					deadlinesAdapter.changeCursor(result);
					deadlinesAdapter.notifyDataSetChanged();
					listViewDeadlines_.setAdapter(listAdapter);
					getAllTask[index_] = null;
				}
				catch (Exception e) {
					Log.d("friend", "hiba!");
				}
			}
		}
	}

	private BroadcastReceiver updateDatabaseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshList();
		}
	};

}
