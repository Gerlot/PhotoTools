package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.EquipmentActivity;
import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DbConstants;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.helpers.EquipmentAdapter;
import hu.bute.gb.onlab.PhotoTools.helpers.EquipmentCategories;
import hu.bute.gb.onlab.PhotoTools.helpers.SeparatedListAdapter;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class EquipmentListFragment extends SherlockListFragment {

	// Log tag
	public static final String TAG = "EquipmentListFragment";
	public String searchFilter = null;
	public SeparatedListAdapter listAdapter = null;
	public boolean isEmpty = true;

	private EquipmentActivity activity_;
	private int selectedPosition_ = 0;

	private ActionMode actionMode_ = null;

	// State
	private LocalBroadcastManager broadcastManager;

	// DBloader
	private DatabaseLoader databaseLoader;
	private GetAllTask[] getAllTask;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = (EquipmentActivity) activity;
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
		listView.setSelection(selectedPosition_);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (actionMode_ == null) {
					//actionMode_ = getSherlockActivity().startActionMode(new SelectActionMode());
					return true;
				}
				return false;
			}
		});
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
			for (EquipmentCategories category : EquipmentCategories.values()) {
				EquipmentAdapter adapter = (EquipmentAdapter) listAdapter
						.getSectionAdapter(category.toString());
				if (adapter != null && adapter.getCursor() != null) {
					adapter.getCursor().close();
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedEquipment", selectedPosition_);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		if (!isEmpty) {
			Equipment selectedEquipment = (Equipment) getListAdapter().getItem(position);
			activity_.showEquipmentDetails(selectedEquipment);
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

	private final class SelectActionMode implements ActionMode.Callback {
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
			actionMode_ = null;
			//populateList(null);
		}
	}
	
	public void refreshList() {
		if (listAdapter == null) {
			listAdapter = new SeparatedListAdapter(getActivity());
			for (EquipmentCategories category : EquipmentCategories.values()) {
				EquipmentAdapter equipmentAdapter = new EquipmentAdapter(getActivity()
						.getApplicationContext(), null);
				listAdapter.addSection(category.toString(), equipmentAdapter);
			}
		}
		setListAdapter(listAdapter);
		
		EquipmentCategories[] categories = EquipmentCategories.values();
		if (getAllTask == null) {
			getAllTask = new GetAllTask[categories.length];
		}
		
		for (int i = 0; i < getAllTask.length; i++) {
			GetAllTask task = getAllTask[i];
			if (task != null) {
				task.cancel(false);
			}
			task = new GetAllTask(i, categories[i].toString());
			task.execute();
		}
	}
	
	
	private class GetAllTask extends AsyncTask<Void, Void, Cursor> {
		private static final String TAG = "GetAllTask";
		private int index_;
		private String category_;
		
		public GetAllTask(int index, String category){
			index_ = index;
			category_ = category;
		}

		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				Cursor result = null;
				if (searchFilter != null) {
					result = databaseLoader.getEquipmentByCategoryAndFilter(category_, searchFilter);
				}
				else {
					result = databaseLoader.getEquipmentByCategory(category_);
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
			if (result != null) {
				isEmpty = false;
				try {
					EquipmentAdapter equipmentAdapter = (EquipmentAdapter) listAdapter.getSectionAdapter(category_);
					equipmentAdapter.changeCursor(result);
					equipmentAdapter.notifyDataSetChanged();
					setListAdapter(listAdapter);
					getAllTask[index_] = null;
				}
				catch (Exception e) {
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
