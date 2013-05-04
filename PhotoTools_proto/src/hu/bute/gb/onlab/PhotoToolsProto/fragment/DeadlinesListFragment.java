package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.DeadlinesActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.SeparatedListAdapter;
import hu.bute.gb.onlab.PhotoToolsProto.R.color;
import hu.bute.gb.onlab.PhotoToolsProto.R.id;
import hu.bute.gb.onlab.PhotoToolsProto.R.layout;
import hu.bute.gb.onlab.PhotoToolsProto.R.string;
import hu.bute.gb.onlab.PhotoToolsProto.model.Deadline;
import hu.bute.gb.onlab.PhotoToolsProto.model.DeadlineDay;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;

import java.util.Map;

import org.apache.commons.collections15.bag.TreeBag;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class DeadlinesListFragment extends SherlockListFragment {

	// Log tag
	public static final String TAG = "EDeadlinesListFragment";
	public SeparatedListAdapter listAdapter = null;
	public boolean isEmpty = true;

	private DummyModel model_;
	private DeadlinesActivity activity_;
	private int selectedPosition_ = 0;
	private boolean addedSection_ = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = (DeadlinesActivity) activity;
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
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setSelection(selectedPosition_);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//listAdapter.notifyDataSetChanged();
		populateList();
		
	}

	public class DeadlineItem {
		public String tag;
		public String date;
		public boolean isSoon = false;
		public int daysLeft;

		public DeadlineItem(String tag, String date, boolean isSoon, int daysLeft) {
			this.tag = tag;
			this.date = date;
			this.isSoon = isSoon;
			this.daysLeft = daysLeft;
		}
	}

	public class DeadlineAdapter extends ArrayAdapter<DeadlineItem> {

		public DeadlineAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext())
						.inflate(R.layout.deadlinesrow, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			TextView date = (TextView) convertView.findViewById(R.id.row_date);
			date.setText(getItem(position).date);

			TextView left = (TextView) convertView.findViewById(R.id.row_left);
			// Hide how many days left if deadline is not soon
			if (!getItem(position).isSoon) {
				left.setVisibility(View.GONE);
			}
			// Show indicator if deadline is soon
			else {
				switch (getItem(position).daysLeft) {
				case 3:
					left.setText("3 days left");
					left.setTextColor(getResources().getColor(R.color.orange));
					break;
				case 2:
					left.setText("2 days left");
					left.setTextColor(getResources().getColor(R.color.orange));
					break;
				case 1:
					left.setText("Only 1 day left!");
					break;
				default:
					left.setText("It's today!");
					break;
				}
			}

			return convertView;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedDeadline", selectedPosition_);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		if (addedSection_) {
			activity_.showDeadlineDetails(position);
			selectedPosition_ = position;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity_ = null;
	}

	private void populateList() {
		activity_.deadlinesOnView.clear();
		listAdapter = new SeparatedListAdapter(getActivity());
		addedSection_ = false;
		for (Map.Entry<DeadlineDay, TreeBag<Deadline>> day : model_.deadlines.entrySet()) {

			DeadlineAdapter deadlineAdapter = new DeadlineAdapter(getActivity());

			TreeBag<Deadline> current = day.getValue();
			boolean addedDeadline = false;
			activity_.deadlinesOnView.add(Integer.valueOf(0));
			for (Deadline deadline : current) {
				boolean isSoon = false;
				DateMidnight today = (new DateTime()).toDateMidnight();
				DateMidnight startDate = deadline.getStartTime().toDateMidnight();
				int daysBetween = Days.daysBetween(today, deadline.getStartTime()).getDays();
				if (startDate.isAfter(today) && daysBetween <= 3) {
					isSoon = true;
				}

				if (!deadline.isAllDay() && deadline.getEndTime() != null) {
					deadlineAdapter.add(new DeadlineItem(deadline.getName(), deadline
							.getStartTime().toLocalTime().toString("HH:mm")
							+ " - " + deadline.getEndTime().toLocalTime().toString("HH:mm"),
							isSoon, daysBetween));
				}
				else if (!deadline.isAllDay()) {
					deadlineAdapter.add(new DeadlineItem(deadline.getName(), deadline
							.getStartTime().toLocalTime().toString("HH:mm"), isSoon, daysBetween));
				}
				else {
					deadlineAdapter.add(new DeadlineItem(deadline.getName(), "", isSoon,
							daysBetween));
				}

				activity_.deadlinesOnView.add(Integer.valueOf(deadline.getID()));
				addedDeadline = true;

			}
			if (addedDeadline) {
				listAdapter.addSection(day.getKey().toString(), deadlineAdapter);
				addedSection_ = true;
			}
			else {
				activity_.deadlinesOnView.remove(activity_.deadlinesOnView.size() - 1);
			}
		}
		if (addedSection_) {
			setListAdapter(listAdapter);
		}
		else {
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);
			emptyAdapter.add(getResources().getString(R.string.empty_deadlinelist));
			setListAdapter(emptyAdapter);
		}
	}

}
