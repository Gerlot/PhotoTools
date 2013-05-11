package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.FriendsActivity;
import hu.bute.gb.onlab.PhotoTools.FriendsDetailActivity;
import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class EquipmentDetailFragment extends SherlockFragment {

	public static final String KEY_EQUIPMENT = "equipment";

	private boolean tabletSize_;
	private DummyModel model_;
	private Activity activity_;
	private Equipment equipment_;

	private TextView textViewEquipmentName_;
	private TextView textViewCategory_;
	private TextView textViewNotes_;
	private TextView textViewLentToLabel_;
	private LinearLayout linearLayoutLentTo_;
	private TextView textViewLentTo_;
	private ImageView imageViewLentTo_;
	private LinearLayout linearLayoutLend_;

	private DatabaseLoader databaseLoader_;

	public static EquipmentDetailFragment newInstance(Equipment equipment) {
		EquipmentDetailFragment result = new EquipmentDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(KEY_EQUIPMENT, equipment);
		result.setArguments(arguments);

		return result;
	}

	public static EquipmentDetailFragment newInstance(Bundle bundle) {
		EquipmentDetailFragment result = new EquipmentDetailFragment();
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
		tabletSize_ = getResources().getBoolean(R.bool.isTablet);
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();

		if (savedInstanceState == null) {
			if (getArguments() != null) {
				equipment_ = getArguments().getParcelable(KEY_EQUIPMENT);
			}
		}
		else if (savedInstanceState != null) {
			equipment_ = savedInstanceState.getParcelable(KEY_EQUIPMENT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		View view = inflater.inflate(R.layout.fragment_equipment_detail, container, false);

		textViewEquipmentName_ = (TextView) view.findViewById(R.id.textViewEquipmentName);
		textViewCategory_ = (TextView) view.findViewById(R.id.textViewCategory);
		textViewNotes_ = (TextView) view.findViewById(R.id.textViewEquipmentNotes);
		textViewLentToLabel_ = (TextView) view.findViewById(R.id.textViewLentToLabel);
		linearLayoutLentTo_ = (LinearLayout) view.findViewById(R.id.linearLayoutLentTo);
		textViewLentTo_ = (TextView) view.findViewById(R.id.textViewLentTo);
		imageViewLentTo_ = (ImageView) view.findViewById(R.id.imageViewLentTo);
		linearLayoutLend_ = (LinearLayout) view.findViewById(R.id.linearLayoutLend);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Lent status may changed, needs update
		equipment_ = databaseLoader_.getEquipment(equipment_.getID());
		onEquipmentChanged(equipment_);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(KEY_EQUIPMENT, equipment_);
		super.onSaveInstanceState(outState);
	}

	public void onEquipmentChanged(Equipment equipment) {
		equipment_ = equipment;
		if (!tabletSize_) {
			activity_.setTitle(equipment_.getName());
			textViewEquipmentName_.setVisibility(View.GONE);
		}
		else {
			textViewEquipmentName_.setText(equipment_.getName());
		}
		textViewCategory_.setText(equipment_.getCategory());
		textViewNotes_.setText(equipment_.getNotes());

		showFriendLentTo();
	}

	public long getSelectedEquipmentId() {
		return equipment_.getID();
	}

	public void lendEquipment(Friend friend) {
		databaseLoader_.lendEquipment(equipment_, friend.getID());
		friend.lendItem(equipment_.getID());
		databaseLoader_.editFriend(friend.getID(), friend);
		Toast.makeText(
				getActivity(),
				equipment_.getName() + " " + getResources().getString(R.string.equipment_lent)
						+ " " + friend.getFullNameFirstLast(), Toast.LENGTH_SHORT).show();
		showFriendLentTo();
	}

	private void showFriendLentTo() {
		if (equipment_.getLentTo() == 0) {
			final EquipmentDetailFragment fragment = this;
			linearLayoutLentTo_.setVisibility(View.GONE);
			linearLayoutLend_.setVisibility(View.VISIBLE);
			linearLayoutLend_.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					FriendSelectorDialog dialog = FriendSelectorDialog.newInstance(fragment);
					dialog.show(getFragmentManager(), "Select Friend");
				}
			});
		}
		else {
			final Friend friend = databaseLoader_.getFriend(equipment_.getLentTo());
			linearLayoutLend_.setVisibility(View.GONE);
			linearLayoutLentTo_.setVisibility(View.VISIBLE);
			textViewLentTo_.setText(friend.getFirstName() + " " + friend.getLastName());
			imageViewLentTo_.setImageResource(R.drawable.android_contact);

			textViewLentTo_.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent showIntent = new Intent();
					if (!tabletSize_) {
						showIntent.setClass(getActivity(), FriendsDetailActivity.class);
					}
					else {
						showIntent.setClass(getActivity(), FriendsActivity.class);
					}
					showIntent.putExtra(FriendsDetailFragment.KEY_FRIEND, friend);
					startActivity(showIntent);
				}
			});
		}
	}
}
