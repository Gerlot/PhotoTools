package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.FriendsActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.SeparatedListAdapter;
import hu.bute.gb.onlab.PhotoToolsProto.R.drawable;
import hu.bute.gb.onlab.PhotoToolsProto.R.id;
import hu.bute.gb.onlab.PhotoToolsProto.R.layout;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Friend;

import java.util.Map;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class FriendsListFragment extends SherlockListFragment {

	// Log tag
	public static final String TAG = "FriendsListFragment";
	public boolean isAllSelected = true;
	public boolean isEmpty = true;

	private DummyModel model_;
	private FriendsActivity activity_;
	private int selectedPosition_ = 0;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = (FriendsActivity) activity;
		model_ = DummyModel.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		allFriendsSelected(null);

		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setSelection(selectedPosition_);
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

	public class FriendAdapter extends ArrayAdapter<FriendItem> {

		public FriendAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.friendsrow, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedPosition", selectedPosition_);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		if (!isEmpty) {
			activity_.showFriendDetails(position);
			selectedPosition_ = position;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity_ = null;
	}
	
	public void search(CharSequence searchFilter){
		if (isAllSelected) {
			allFriendsSelected(searchFilter);
		}
		else {
			lentToSelected(searchFilter);
		}
		
		if (isEmpty) {
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);
			emptyAdapter.add(getResources().getString(R.string.search_no_result));
			setListAdapter(emptyAdapter);
		}
	}

	public void allFriendsSelected(CharSequence searchFilter) {
		activity_.friendsOnView.clear();
		isEmpty = true;

		// Create the friend list with custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(getActivity());
		for (Map.Entry<String, TreeSet<Friend>> alphabet : model_.friends.entrySet()) {

			FriendAdapter friendAdapter = new FriendAdapter(getActivity());

			TreeSet<Friend> current = alphabet.getValue();
			boolean addedFriend = false;
			activity_.friendsOnView.add(Integer.valueOf(0));
			for (Friend friend : current) {

				// Filter by search term if searched
				if (searchFilter == null
						|| friend.getFullNameFirstLast().toLowerCase()
								.contains(searchFilter.toString().toLowerCase())) {
					// Put warning sign if has lent items
					if (friend.getLentItems() != null) {
						friendAdapter.add(new FriendItem(friend.getFirstName() + " "
								+ friend.getLastName(), true, R.drawable.android_contact));
						activity_.friendsOnView.add(Integer.valueOf(friend.getID()));
					}
					else {
						friendAdapter.add(new FriendItem(friend.getFirstName() + " "
								+ friend.getLastName(), false, R.drawable.android_contact));
						activity_.friendsOnView.add(Integer.valueOf(friend.getID()));
					}
					addedFriend = true;
				}
			}
			// Only add section, if has child items
			if (addedFriend) {
				adapter.addSection(alphabet.getKey(), friendAdapter);
				isEmpty = false;
			}
			else {
				activity_.friendsOnView.remove(activity_.friendsOnView.size() - 1);
			}
		}
		setListAdapter(adapter);
	}

	public void lentToSelected(CharSequence searchFilter) {
		activity_.friendsOnView.clear();
		isEmpty = true;

		// Create the friend list with custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(getActivity());
		for (Map.Entry<String, TreeSet<Friend>> alphabet : model_.friends.entrySet()) {

			FriendAdapter friendAdapter = new FriendAdapter(getActivity());

			TreeSet<Friend> current = alphabet.getValue();
			boolean addedFriend = false;
			activity_.friendsOnView.add(Integer.valueOf(0));
			for (Friend friend : current) {

				// Only add friends with lent items
				// Filter by search term if searched
				if ((searchFilter == null || friend.getFullNameFirstLast().toLowerCase()
						.contains(searchFilter.toString().toLowerCase()))
						&& friend.getLentItems() != null) {
					friendAdapter.add(new FriendItem(friend.getFirstName() + " "
							+ friend.getLastName(), false, R.drawable.android_contact));
					activity_.friendsOnView.add(Integer.valueOf(friend.getID()));
					addedFriend = true;
				}

			}
			if (addedFriend) {
				adapter.addSection(alphabet.getKey(), friendAdapter);
				isEmpty = false;
			}
			else {
				activity_.friendsOnView.remove(activity_.friendsOnView.size() - 1);
			}
		}
		if (!isEmpty) {
			setListAdapter(adapter);
		}
		else {
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);
			emptyAdapter.add(getResources().getString(R.string.no_lent_to));
			setListAdapter(emptyAdapter);
		}

	}
}
