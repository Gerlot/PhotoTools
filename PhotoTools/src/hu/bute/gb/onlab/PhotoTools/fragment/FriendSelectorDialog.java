package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import hu.bute.gb.onlab.PhotoTools.helpers.SeparatedListAdapter;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FriendSelectorDialog extends DialogFragment {

	public boolean isEmpty = true;
	public List<Long> friendsOnView = new ArrayList<Long>();

	private static EquipmentDetailFragment fragment_;
	private EditText editTextSearch_;
	private ListView listViewSelector_;
	private SeparatedListAdapter listAdapter_;

	public static FriendSelectorDialog newInstance(EquipmentDetailFragment fragment) {
		fragment_ = fragment;
		return new FriendSelectorDialog();
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
				fragment_.lendEquipment(friendsOnView.get(position));
				dismiss();
			}
		});
		
		populateList(null);
		getDialog().setTitle(getResources().getString(R.string.select_friend));

		return view;
	}

	public class FriendAdapter extends ArrayAdapter<FriendItem> {

		public FriendAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.friends_row, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
				title.setTextColor(getResources().getColor(R.color.abs__primary_text_holo_dark));
			}

			// Only put up sign if has lent item(s)
			ImageView sign = (ImageView) convertView.findViewById(R.id.row_sign);
			if (!getItem(position).hasLentItem) {
				sign.setVisibility(View.INVISIBLE);
			}

			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);

			return convertView;
		}

	}

	private class FriendItem {
		public String tag;
		public boolean hasLentItem = false;
		public int iconRes;

		public FriendItem(String tag, boolean hasLentItem, int iconRes) {
			this.tag = tag;
			this.hasLentItem = hasLentItem;
			this.iconRes = iconRes;
		}
	}

	private void populateList(CharSequence searchFilter) {
		friendsOnView.clear();
		isEmpty = true;

		// Create the friend list with custom adapter
		listAdapter_ = new SeparatedListAdapter(getActivity());
		for (Map.Entry<String, TreeSet<Friend>> alphabet : DummyModel.getInstance().friends
				.entrySet()) {

			FriendAdapter friendAdapter = new FriendAdapter(getActivity());

			TreeSet<Friend> current = alphabet.getValue();
			boolean addedFriend = false;
			friendsOnView.add(Long.valueOf(0));
			for (Friend friend : current) {

				// Only add friends with lent items
				// Filter by search term if searched
				if (searchFilter == null || friend.getFullNameFirstLast().toLowerCase()
						.contains(searchFilter.toString().toLowerCase())) {
					friendAdapter.add(new FriendItem(friend.getFirstName() + " "
							+ friend.getLastName(), false, R.drawable.android_contact));
					friendsOnView.add(Long.valueOf(friend.getID()));
					addedFriend = true;
				}

			}
			if (addedFriend) {
				listAdapter_.addSection(alphabet.getKey(), friendAdapter);
				isEmpty = false;
			}
			else {
				friendsOnView.remove(friendsOnView.size() - 1);
			}
		}
		if (!isEmpty) {
			listViewSelector_.setAdapter(listAdapter_);
		}
		else {
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);
			emptyAdapter.add(getResources().getString(R.string.search_no_result));
			listViewSelector_.setAdapter(emptyAdapter);
		}
	}

}
