package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DeadlinesDetailFragment extends SherlockFragment {

	private DummyModel model_;
	private Activity activity_;
	private long selectedDeadline_ = 0;
	private Deadline deadline_;

	private TextView textViewDeadlineName_;
	private TextView textViewDeadlineTime_;
	private TextView textViewDeadlineDate_;
	private TextView textViewDeadlineLocationLabel_;
	private TextView textViewDeadlineLocation_;
	private TextView textViewDeadlineNotes_;

	public static DeadlinesDetailFragment newInstance(long index) {
		DeadlinesDetailFragment fragment = new DeadlinesDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putLong("index", index);
		fragment.setArguments(arguments);

		return fragment;
	}

	public static DeadlinesDetailFragment newInstance(Bundle bundle) {
		long index = bundle.getLong("index", 0);
		return newInstance(index);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = activity;
		model_ = DummyModel.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectedDeadline_ = getArguments().getLong("index", 0);
		deadline_ = model_.getDeadlineById(selectedDeadline_);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		View view = inflater.inflate(R.layout.fragment_deadlines_detail, container, false);

		textViewDeadlineName_ = (TextView) view.findViewById(R.id.textViewDeadlineName);
		textViewDeadlineTime_ = (TextView) view.findViewById(R.id.textViewDeadlineTime);
		textViewDeadlineDate_ = (TextView) view.findViewById(R.id.textViewDeadlineDate);
		textViewDeadlineLocationLabel_ = (TextView) view
				.findViewById(R.id.textViewDeadlineLocationLabel);
		textViewDeadlineLocation_ = (TextView) view.findViewById(R.id.textViewDeadlineLocation);
		textViewDeadlineNotes_ = (TextView) view.findViewById(R.id.textViewDeadlineNotes);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		textViewDeadlineName_.setText(deadline_.getName());

		if (!deadline_.isAllDay()) {
			if (deadline_.getEndTime() == null) {
				textViewDeadlineTime_.setText(deadline_.getStartTime().toLocalDateTime()
						.toString("HH:mm"));
			}
			else {
				textViewDeadlineTime_.setText(deadline_.getStartTime().toLocalDateTime()
						.toString("HH:mm")
						+ " - " + deadline_.getEndTime().toLocalDateTime().toString("HH:mm"));
			}
		}
		else {
			textViewDeadlineTime_.setVisibility(View.GONE);
		}
		textViewDeadlineDate_.setText(deadline_.getStartTime().toLocalDateTime()
				.toString("yyyy. MMMM dd., EEEE"));

		if (!deadline_.getLocation().equals("")) {
			textViewDeadlineLocation_.setText(deadline_.getLocation());
		}
		else {
			textViewDeadlineLocationLabel_.setVisibility(View.GONE);
			textViewDeadlineLocation_.setVisibility(View.GONE);
		}

		if (!deadline_.getNotes().equals("")) {
			textViewDeadlineNotes_.setText(deadline_.getNotes());
			textViewDeadlineNotes_.setTextColor(getResources().getColor(
					android.R.color.secondary_text_light));
		}
	}

	public long getSelectedDeadline() {
		return selectedDeadline_;
	}

}
