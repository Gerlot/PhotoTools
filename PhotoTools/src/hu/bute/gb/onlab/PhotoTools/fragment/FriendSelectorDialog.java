package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import hu.bute.gb.onlab.PhotoTools.helpers.FriendsAdapter;
import hu.bute.gb.onlab.PhotoTools.helpers.SeparatedListAdapter;

import java.util.ArrayList;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class FriendSelectorDialog extends DialogFragment {

	public String searchFilter = null;
	public SeparatedListAdapter listAdapter = null;
	public boolean isEmpty = true;

	private static EquipmentDetailFragment fragment_;
	private EditText editTextSearch_;
	private ListView listViewSelector_;
	private ArrayList<String> usedCharacters_;

	// DBloader
	private DatabaseLoader databaseLoader_;
	private GetAllTask[] getAllTask;

	public static FriendSelectorDialog newInstance(EquipmentDetailFragment fragment) {
		fragment_ = fragment;
		return new FriendSelectorDialog();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();
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
				Friend friend = (Friend) listViewSelector_.getAdapter().getItem(position);
				fragment_.lendEquipment(friend);
				dismiss();
			}
		});

		getDialog().setTitle(getResources().getString(R.string.select_friend));

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
			for (String character : usedCharacters_) {
				FriendsAdapter adapter = (FriendsAdapter) listAdapter
						.getSectionAdapter(character);
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
		usedCharacters_ = databaseLoader_.getUsedCharacters(true);
		if (usedCharacters_ != null) {
			listAdapter = new SeparatedListAdapter(getActivity());
			for (String character : usedCharacters_) {
				FriendsAdapter friendAdapter = new FriendsAdapter(getActivity()
						.getApplicationContext(), null);
				listAdapter.addSection(character, friendAdapter);
			}
			listViewSelector_.setAdapter(listAdapter);

			getAllTask = new GetAllTask[usedCharacters_.size()];

			for (int i = 0; i < getAllTask.length; i++) {
				GetAllTask task = getAllTask[i];
				if (task != null) {
					task.cancel(false);
				}
				task = new GetAllTask(i, usedCharacters_.get(i));
				task.execute();
			}
		}
		else {
			isEmpty = true;
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);
			emptyAdapter.add(getResources().getString(R.string.empty_friendlist));
			listViewSelector_.setAdapter(emptyAdapter);
			listAdapter = null;
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
					result = databaseLoader_.getAllFriendsByCategoryAndFilter(category_,
							searchFilter);
				}
				else {
					result = databaseLoader_.getAllFriendsByCategory(category_);
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

					FriendsAdapter friendAdapter = (FriendsAdapter) listAdapter
							.getSectionAdapter(category_);
					friendAdapter.changeCursor(result);
					friendAdapter.notifyDataSetChanged();
					listViewSelector_.setAdapter(listAdapter);
					getAllTask[index_] = null;
				}
				catch (Exception e) {
					Log.d("friend", "hiba!");
				}
			}
		}
	}
}
