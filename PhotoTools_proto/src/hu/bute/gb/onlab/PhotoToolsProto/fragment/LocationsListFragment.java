package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.LocationsActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Location;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LocationsListFragment extends SherlockListFragment {

	// Log tag
	public static final String TAG = "LocationsListFragment";
	public ArrayAdapter<String> listAdapter = null;
	public boolean isEmpty = true;

	private DummyModel model_;
	private LocationsActivity activity_;
	private int selectedPosition_ = 0;
	
	private ActionMode actionMode_ = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = (LocationsActivity) activity;
		model_ = DummyModel.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		setListAdapter(listAdapter);

		populateList();

		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setSelection(selectedPosition_);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (!isEmpty && actionMode_ == null) {
					getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice);
					setListAdapter(listAdapter);
					populateList();
					actionMode_ = getSherlockActivity().startActionMode(new SelectActionMode());
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		listAdapter.notifyDataSetChanged();
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
			activity_.showLocationDetails(position);
			selectedPosition_ = position;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity_ = null;
	}
	
	public void addPlaceHolderText(){
		listAdapter.add(getResources().getString(R.string.empty_locationlist));
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
			actionMode_ = null;
			getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
			listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
			setListAdapter(listAdapter);
			populateList();
		}
	}
	
	private void populateList(){
		for (Location location : model_.locations) {
			listAdapter.add(location.getName());
			activity_.locationsOnView.add(Integer.valueOf(location.getID()));
			isEmpty = false;
		}
		if (isEmpty) {
			addPlaceHolderText();
		}
	}

}
