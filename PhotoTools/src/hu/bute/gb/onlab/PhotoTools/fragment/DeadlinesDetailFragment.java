package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DeadlinesDetailFragment extends SherlockFragment {

	public static final String KEY_DEADLINE = "deadline";

	private Activity activity_;
	private Deadline deadline_;

	private TextView textViewDeadlineName_;
	private TextView textViewDeadlineTime_;
	private TextView textViewDeadlineDate_;
	private TextView textViewDeadlineLocationLabel_;
	private TextView textViewDeadlineLocation_;
	private TextView textViewDeadlineNotes_;

	public static DeadlinesDetailFragment newInstance(Deadline deadline) {
		DeadlinesDetailFragment result = new DeadlinesDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(KEY_DEADLINE, deadline);
		result.setArguments(arguments);

		return result;
	}

	public static DeadlinesDetailFragment newInstance(Bundle bundle) {
		DeadlinesDetailFragment result = new DeadlinesDetailFragment();
		result.setArguments(bundle);
		return result;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity_ = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			if (getArguments() != null) {
				deadline_ = getArguments().getParcelable(KEY_DEADLINE);
			}
		}
		else if (savedInstanceState != null) {
			deadline_ = savedInstanceState.getParcelable(KEY_DEADLINE);
		}
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
		onDeadlineChanged(deadline_);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(KEY_DEADLINE, deadline_);
		super.onSaveInstanceState(outState);
	}

	public void onDeadlineChanged(Deadline deadline) {
		deadline_ = deadline;
		textViewDeadlineName_.setText(deadline_.getName());

		textViewDeadlineTime_.setVisibility(View.VISIBLE);
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

		textViewDeadlineLocationLabel_.setVisibility(View.VISIBLE);
		textViewDeadlineLocation_.setVisibility(View.VISIBLE);
		if (!deadline_.getLocation().equals("")) {
			textViewDeadlineLocation_.setText(deadline_.getLocation());
		}
		else {
			textViewDeadlineLocationLabel_.setVisibility(View.GONE);
			textViewDeadlineLocation_.setVisibility(View.GONE);
		}

		textViewDeadlineNotes_.setText(getResources().getString(R.string.no_deadline_notes));
		textViewDeadlineNotes_.setTextColor(getResources().getColor(
				R.color.abs__bright_foreground_disabled_holo_light));
		if (!deadline_.getNotes().equals("")) {
			textViewDeadlineNotes_.setText(deadline_.getNotes());
			textViewDeadlineNotes_.setTextColor(getResources().getColor(
					android.R.color.secondary_text_light));
		}
	}

	public long getSelectedDeadlineId() {
		return deadline_.getID();
	}

}
