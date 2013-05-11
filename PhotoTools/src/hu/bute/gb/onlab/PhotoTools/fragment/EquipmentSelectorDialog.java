package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.helpers.EquipmentAdapter;
import hu.bute.gb.onlab.PhotoTools.helpers.EquipmentCategories;
import hu.bute.gb.onlab.PhotoTools.helpers.SeparatedListAdapter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class EquipmentSelectorDialog extends DialogFragment {

	public String searchFilter = null;
	public SeparatedListAdapter listAdapter = null;
	public boolean isEmpty = true;

	private static FriendsDetailFragment fragment_;
	private EditText editTextSearch_;
	private ListView listViewSelector_;

	// DBloader
	private DatabaseLoader databaseLoader;
	private GetAllTask[] getAllTask;

	public static EquipmentSelectorDialog newInstance(FriendsDetailFragment fragment) {
		fragment_ = fragment;
		return new EquipmentSelectorDialog();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseLoader = PhotoToolsApplication.getDatabaseLoader();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_selector, container);

		editTextSearch_ = (EditText) view.findViewById(R.id.editTextSearch);
		editTextSearch_.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				search(charSequence);
			}

			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable charSequence) {
			}
		});

		listViewSelector_ = (ListView) view.findViewById(R.id.listViewSelector);
		listViewSelector_.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listViewSelector_.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Equipment equipment = (Equipment) listViewSelector_.getAdapter().getItem(position);
				fragment_.lendEquipment(equipment);
				dismiss();
			}
		});

		getDialog().setTitle(getResources().getString(R.string.select_equipment));

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshList();
	}

	@Override
	public void onPause() {
		super.onPause();
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

	public void search(CharSequence charSequence) {
		searchFilter = charSequence.toString().toLowerCase();
		refreshList();
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
		listViewSelector_.setAdapter(listAdapter);

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

		public GetAllTask(int index, String category) {
			index_ = index;
			category_ = category;
		}

		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				Cursor result = null;
				if (searchFilter != null) {
					result = databaseLoader
							.getEquipmentByCategoryAndFilter(category_, searchFilter);
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
					EquipmentAdapter equipmentAdapter = (EquipmentAdapter) listAdapter
							.getSectionAdapter(category_);
					equipmentAdapter.changeCursor(result);
					equipmentAdapter.notifyDataSetChanged();
					listViewSelector_.setAdapter(listAdapter);
					getAllTask[index_] = null;
				}
				catch (Exception e) {
				}
			}
		}
	}
}
