package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.SeparatedListAdapter;
import hu.bute.gb.onlab.PhotoTools.model.DummyModel;
import hu.bute.gb.onlab.PhotoTools.model.Equipment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class EquipmentSelectorDialog extends DialogFragment{
	
	public boolean isEmpty = true;
	public List<Integer> equipmentOnView = new ArrayList<Integer>();

	private static FriendsDetailFragment fragment_;
	private EditText editTextSearch_;
	private ListView listViewSelector_;
	private SeparatedListAdapter listAdapter_;

	public static EquipmentSelectorDialog newInstance(FriendsDetailFragment fragment) {
		fragment_ = fragment;
		return new EquipmentSelectorDialog();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_selector, container);
		
		editTextSearch_ = (EditText) view.findViewById(R.id.editTextSearch);
		editTextSearch_.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				populateList(charSequence);
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
				fragment_.lendEquipment(equipmentOnView.get(position));
				dismiss();
			}
		});
		
		populateList(null);
		getDialog().setTitle(getResources().getString(R.string.select_equipment));

		return view;
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
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
				title.setTextColor(getResources().getColor(R.color.abs__primary_text_holo_dark));
			}

			// Only put up sign if has lent item(s)
			ImageView sign = (ImageView) convertView.findViewById(R.id.row_sign);
			sign.setVisibility(View.INVISIBLE);
			if (getItem(position).isLent) {
				sign.setVisibility(View.VISIBLE);
			}

			CheckBox checkBoxSelect = (CheckBox) convertView.findViewById(R.id.checkBoxSelect);
			checkBoxSelect.setVisibility(View.GONE);

			return convertView;
		}

	}

	private class EquipmentItem {
		public String tag;
		public boolean isLent = false;

		public EquipmentItem(String tag, boolean isLent) {
			this.tag = tag;
			this.isLent = isLent;
		}
	}

	private void populateList(CharSequence searchFilter) {
		listAdapter_ = new SeparatedListAdapter(getActivity());
		equipmentOnView.clear();
		isEmpty = true;
		for (Map.Entry<String, TreeSet<Equipment>> categories : DummyModel.getInstance().equipment.entrySet()) {

			EquipmentAdapter equipmentAdapter = new EquipmentAdapter(getActivity());

			TreeSet<Equipment> current = categories.getValue();
			// Add a 0 value because for each category headers
			equipmentOnView.add(Integer.valueOf(0));
			for (Equipment equipment : current) {
				if (searchFilter == null || equipment.getName().toLowerCase().contains(searchFilter.toString().toLowerCase())) {
					equipmentAdapter.add(new EquipmentItem(equipment.getName(), equipment.isLent()));
					equipmentOnView.add(Integer.valueOf(equipment.getID()));
				}
			}
			listAdapter_.addSection(categories.getKey(), equipmentAdapter);
			listViewSelector_.setAdapter(listAdapter_);
			isEmpty = false;
		}
	}

}
