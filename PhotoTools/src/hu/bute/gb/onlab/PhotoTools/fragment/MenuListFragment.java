package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.DeadlinesActivity;
import hu.bute.gb.onlab.PhotoTools.EquipmentActivity;
import hu.bute.gb.onlab.PhotoTools.FriendsActivity;
import hu.bute.gb.onlab.PhotoTools.LocationsActivity;
import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.StatsActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.slidingmenu.lib.SlidingMenu;

public class MenuListFragment extends ListFragment {

	public static final String KEY_INDEX = "index";
	private int selectedActivity_;
	private static SlidingMenu menu_;

	public static MenuListFragment newInstance(Integer index, SlidingMenu menu) {
		MenuListFragment result = new MenuListFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(KEY_INDEX, index.intValue());
		result.setArguments(arguments);
		menu_ = menu;
		return result;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			if (getArguments() != null) {
				selectedActivity_ = getArguments().getInt(KEY_INDEX);
			}
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SlidingMenuAdapter adapter = new SlidingMenuAdapter(getActivity());
		adapter.add(new MenuItem("Locations", R.drawable.icon_locations));
		adapter.add(new MenuItem("Deadlines", R.drawable.icon_deadlines));
		adapter.add(new MenuItem("Equipment", R.drawable.icon_equipment));
		adapter.add(new MenuItem("Friends", R.drawable.icon_friends));
		adapter.add(new MenuItem("Stats", R.drawable.icon_stats));

		setListAdapter(adapter);
	}

	private class MenuItem {
		public String tag;
		public int iconRes;

		public MenuItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class SlidingMenuAdapter extends ArrayAdapter<MenuItem> {

		public SlidingMenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.menurow, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		// Hide Sliding menu if clicked on the currently selected item
		if (position == selectedActivity_) {
			menu_.showContent();
		}
		else {
			Intent myIntent = new Intent();
			switch (position) {
			case 0:
				myIntent.setClass(getActivity().getBaseContext(), LocationsActivity.class);
				startActivity(myIntent);
				break;
			case 1:
				myIntent.setClass(getActivity().getBaseContext(), DeadlinesActivity.class);
				startActivity(myIntent);
				break;
			case 2:
				myIntent.setClass(getActivity().getBaseContext(), EquipmentActivity.class);
				startActivity(myIntent);
				break;
			case 3:
				myIntent.setClass(getActivity().getBaseContext(), FriendsActivity.class);
				startActivity(myIntent);
				break;
			case 4:
				myIntent.setClass(getActivity().getBaseContext(), StatsActivity.class);
				startActivity(myIntent);
				break;
			default:
				break;
			}
		}

		super.onListItemClick(listView, view, position, id);
	}
}
