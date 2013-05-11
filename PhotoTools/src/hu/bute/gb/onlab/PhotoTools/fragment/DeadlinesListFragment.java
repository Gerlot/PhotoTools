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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;

public class DeadlinesListFragment extends SherlockListFragment {

	// Log tag
	public static final String TAG = "DeadlinesListFragment";
	public String searchFilter = null;
	public SeparatedListAdapter listAdapter = null;
	public boolean isEmpty = true;

	private ArrayList<String> usedDateStrings_;
	private ArrayList<DeadlineDay> usedDates_;
	private DeadlinesActivity activity_;
	private int selectedPosition_ = 0;

	private DateMidnight today_;
	private DateMidnight displayAfterThis_;
	private DateMidnight displayBeforeThis_;

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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setSelection(selectedPosition_);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Kódból regisztraljuk az adatbazis modosulasara figyelmezteto
		// Receiver-t
		IntentFilter filter = new IntentFilter(DbConstants.ACTION_DATABASE_CHANGED);
		broadcastManager.registerReceiver(updateDatabaseReceiver, filter);
		// Frissitjuk a lista tartalmat, ha visszater a user
		refreshList();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// Kiregisztraljuk az adatbazis modosulasara figyelmezteto Receiver-t
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
		// Ha van Cursor rendelve az Adapterhez, lezarjuk
		if (listAdapter != null) {
			for (String date : usedDateStrings_) {
				DeadlinesAdapter adapter = (DeadlinesAdapter) listAdapter
						.getSectionAdapter(date);
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
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		if (!isEmpty) {
			Deadline selectedDeadline = (Deadline) getListAdapter().getItem(position);
			activity_.showDeadlineDetails(selectedDeadline);
			//selectedPosition_ = position;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity_ = null;
	}

	public void refreshList() {
		usedDateStrings_ = databaseLoader.getUsedDateStrings(displayAfterThis_.getMillis(),
				displayBeforeThis_.getMillis());
		usedDates_ = databaseLoader.getUsedDates(displayAfterThis_.getMillis(),
				displayBeforeThis_.getMillis());
		if (usedDateStrings_ != null) {
			listAdapter = new SeparatedListAdapter(getActivity());
			for (String date : usedDateStrings_) {
				DeadlinesAdapter deadlinesAdapter = new DeadlinesAdapter(getActivity()
						.getApplicationContext(), null, getResources().getColor(R.color.orange));
				// In case of today, display label as "Today"
				String dateString = date;
				if (date.equals(today_.toString("yyyy. MMM dd., EEEE"))) {
					dateString = getResources().getString(R.string.today);
				}
				listAdapter.addSection(dateString, deadlinesAdapter);
			}
			setListAdapter(listAdapter);

			getAllTask = new GetAllTask[usedDateStrings_.size()];

			for (int i = 0; i < getAllTask.length; i++) {
				GetAllTask task = getAllTask[i];
				if (task != null) {
					task.cancel(false);
				}
				task = new GetAllTask(i, usedDateStrings_.get(i), usedDates_.get(i));
				task.execute();
			}
		}
		else {
			isEmpty = true;
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);
			setListAdapter(emptyAdapter);
			listAdapter = null;
		}
	}

	private class GetAllTask extends AsyncTask<Void, Void, Cursor> {
		private static final String TAG = "GetAllTask";
		private int index_;
		private String category_;
		private DeadlineDay date_;

		public GetAllTask(int index, String category, DeadlineDay date) {
			index_ = index;
			category_ = category;
			date_ = date;
		}

		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				DateMidnight dateMidnight = date_.getDateInDateTime();
				Cursor result = databaseLoader.getDeadlinesBetweenDays(
						dateMidnight.getMillis(), dateMidnight.plusDays(1).getMillis());
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
					String categoryString = category_;
					if (category_.equals(today_.toString("yyyy. MMM dd., EEEE"))) {
						categoryString = getResources().getString(R.string.today);
					}
					DeadlinesAdapter deadlinesAdapter = (DeadlinesAdapter) listAdapter
							.getSectionAdapter(categoryString);
					deadlinesAdapter.changeCursor(result);
					deadlinesAdapter.notifyDataSetChanged();
					setListAdapter(listAdapter);
					getAllTask[index_] = null;
				}
				catch (Exception e) {
					Log.d("friend", "hiba!");
				}
			}
			/*
			 * if (listAdapter.isEmpty()) { isEmpty = true; ArrayAdapter<String>
			 * emptyAdapter = new ArrayAdapter<String>(getActivity(),
			 * android.R.layout.simple_list_item_1);
			 * emptyAdapter.add(getResources().getString(R.string.no_lent_to));
			 * setListAdapter(emptyAdapter); // listAdapter = null; }
			 */
		}
	}

	private BroadcastReceiver updateDatabaseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshList();
		}
	};

}
