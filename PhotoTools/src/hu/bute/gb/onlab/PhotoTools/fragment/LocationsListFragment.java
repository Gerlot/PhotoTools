package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.LocationsActivity;
import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DbConstants;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.helpers.LocationsAdapter;
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
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LocationsListFragment extends SherlockListFragment {

	// Log tag
	public static final String TAG = "LocationsListFragment";
	public String searchFilter = null;
	public LocationsAdapter listAdapter;
	public boolean isEmpty = true;

	private LocationsActivity activity_;
	private int selectedPosition_ = 0;

	private ActionMode actionMode_ = null;

	// State
	private LocalBroadcastManager broadcastManager;

	// DBloader
	private DatabaseLoader databaseLoader_;
	private GetAllTask getAllTask_;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = (LocationsActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
	}

	@Override
	public void onResume() {
		//super.onResume();
		//listAdapter.notifyDataSetChanged();
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
		if (getAllTask_ != null) {
			getAllTask_.cancel(false);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// Ha van Cursor rendelve az Adapterhez, lezarjuk
		if (listAdapter != null && listAdapter.getCursor() != null) {
			listAdapter.getCursor().close();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedPosition", selectedPosition_);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		if (!isEmpty && actionMode_ == null) {
			Location selectedLocation = (Location) getListAdapter().getItem(position);
			activity_.showLocationDetails(selectedLocation);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity_ = null;
	}

	public void search(String queryString) {
		searchFilter = queryString.toLowerCase();
		refreshList();
	}

	public final class SelectActionMode implements ActionMode.Callback {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			menu.add("Select All").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add("Delete Selected")
					.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete))
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
	}
	
	public void refreshList() {
		if (getAllTask_ != null) {
			getAllTask_.cancel(false);
		}
		getAllTask_ = new GetAllTask();
		getAllTask_.execute();
	}
	
	// Background task for getting locations from database
	private class GetAllTask extends AsyncTask<Void, Void, Cursor> {
		private static final String TAG = "GetAllTask";

		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				Cursor result = null;
				if (searchFilter != null) {
					result = databaseLoader_.getLocationByFilter(searchFilter);
				}
				else {
					result = databaseLoader_.getAllLocations();
				}
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
			if (result != null && result.getCount() > 0) {
				isEmpty = false;
				try {
					if (listAdapter == null) {
						listAdapter = new LocationsAdapter(getActivity().getApplicationContext(), result);
						setListAdapter(listAdapter);
					}
					else {
						listAdapter.changeCursor(result);
					}
					getAllTask_ = null;
				}
				catch (Exception e) {
				}
			}
			else if (result == null) {
				isEmpty = true;
				ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
				emptyAdapter.add(getResources().getString(R.string.search_no_result));
				setListAdapter(emptyAdapter);
				listAdapter = null;
			}
			else {
				isEmpty = true;
				ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
				emptyAdapter.add(getResources().getString(R.string.empty_locationlist));
				setListAdapter(emptyAdapter);
				listAdapter = null;
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
