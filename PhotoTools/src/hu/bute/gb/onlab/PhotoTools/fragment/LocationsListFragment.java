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
	private DatabaseLoader databaseLoader;
	private GetAllTask getAllTask;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = (LocationsActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		databaseLoader = PhotoToolsApplication.getDatabaseLoader();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

		/*		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setSelection(selectedPosition_);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (!isEmpty && actionMode_ == null) {
					getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					listAdapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_list_item_multiple_choice);
					setListAdapter(listAdapter);
					populateList();
					actionMode_ = getSherlockActivity().startActionMode(new SelectActionMode());
					return true;
				}
				return false;
			}
		});*/
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
		if (getAllTask != null) {
			getAllTask.cancel(false);
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

	public void addPlaceHolderText() {
		//ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		//listAdapter.add
		//listAdapter.add(getResources().getString(R.string.empty_locationlist));
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
			/*actionMode_ = null;
			getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
			listAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);
			setListAdapter(listAdapter);
			populateList();*/
		}
	}

	/*public void populateList() {
		for (Location location : model_.locations) {
			listAdapter.add(location.getName());
			activity_.locationsOnView.add(Long.valueOf(location.getID()));
			isEmpty = false;
		}
		if (isEmpty) {
			addPlaceHolderText();
		}
	}*/
	
	public void refreshList() {
		if (getAllTask != null) {
			getAllTask.cancel(false);
		}
		getAllTask = new GetAllTask();
		getAllTask.execute();
	}
	
	
	private class GetAllTask extends AsyncTask<Void, Void, Cursor> {
		private static final String TAG = "GetAllTask";

		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				Cursor result = null;
				if (searchFilter != null) {
					result = databaseLoader.getLocationByFilter(searchFilter);
				}
				else {
					result = databaseLoader.getAllLocations();
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
					getAllTask = null;
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
