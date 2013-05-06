package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.EquipmentActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.SeparatedListAdapter;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Equipment;

import java.util.Map;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class EquipmentListFragment extends SherlockListFragment {

	// Log tag
	public static final String TAG = "EquipmentListFragment";
	public SeparatedListAdapter listAdapter = null;
	public boolean isEmpty = true;

	private DummyModel model_;
	private EquipmentActivity activity_;
	private int selectedPosition_ = 0;

	private ActionMode actionMode_ = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = (EquipmentActivity) activity;
		model_ = DummyModel.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
					actionMode_ = getSherlockActivity().startActionMode(new SelectActionMode());
					populateList(null);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		populateList(null);
	}

	private class EquipmentItem {
		public String tag;
		public boolean isLent = false;

		public EquipmentItem(String tag, boolean isLent) {
			this.tag = tag;
			this.isLent = isLent;
		}
	}

	public class EquipmentAdapter extends ArrayAdapter<EquipmentItem> {

		public EquipmentAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext())
						.inflate(R.layout.equipmentrow, null);
			}

			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			// Only put up sign if has lent item(s)
			ImageView sign = (ImageView) convertView.findViewById(R.id.row_sign);
			sign.setVisibility(View.INVISIBLE);
			if (getItem(position).isLent) {
				sign.setVisibility(View.VISIBLE);
			}

			CheckBox checkBoxSelect = (CheckBox) convertView.findViewById(R.id.checkBoxSelect);
			checkBoxSelect.setVisibility(View.GONE);
			if (actionMode_ != null) {
				checkBoxSelect.setVisibility(View.VISIBLE);
			}

			return convertView;
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
			activity_.showEquipmentDetails(position);
			selectedPosition_ = position;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity_ = null;
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
			populateList(null);
		}
	}

	public void populateList(CharSequence searchFilter) {
		// Clearing the list adapter and the id container in base activity
		listAdapter = new SeparatedListAdapter(getActivity());
		activity_.equipmentOnView.clear();
		isEmpty = true;
		for (Map.Entry<String, TreeSet<Equipment>> categories : model_.equipment.entrySet()) {

			EquipmentAdapter equipmentAdapter = new EquipmentAdapter(getActivity());

			TreeSet<Equipment> current = categories.getValue();
			// Add a 0 value because for each category headers
			activity_.equipmentOnView.add(Integer.valueOf(0));
			for (Equipment equipment : current) {
				if (searchFilter == null || equipment.getName().toLowerCase().contains(searchFilter.toString().toLowerCase())) {
					equipmentAdapter.add(new EquipmentItem(equipment.getName(), equipment.isLent()));
					activity_.equipmentOnView.add(Integer.valueOf(equipment.getID()));
				}
			}
			listAdapter.addSection(categories.getKey(), equipmentAdapter);
			setListAdapter(listAdapter);
			isEmpty = false;
		}
	}

}
