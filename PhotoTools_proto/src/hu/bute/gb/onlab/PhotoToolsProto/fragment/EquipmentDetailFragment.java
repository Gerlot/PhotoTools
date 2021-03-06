package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.FriendsActivity;
import hu.bute.gb.onlab.PhotoToolsProto.FriendsDetailActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.R.bool;
import hu.bute.gb.onlab.PhotoToolsProto.R.drawable;
import hu.bute.gb.onlab.PhotoToolsProto.R.id;
import hu.bute.gb.onlab.PhotoToolsProto.R.layout;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Equipment;
import hu.bute.gb.onlab.PhotoToolsProto.model.Friend;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

public class EquipmentDetailFragment extends SherlockFragment {

	private boolean tabletSize_;
	private DummyModel model_;
	private Activity activity_;
	private int selectedEquipment_ = 0;
	private Equipment equipment_;

	private TextView textViewEquipmentName_;
	private TextView textViewCategory_;
	private TextView textViewNotes_;
	private TextView textViewLentToLabel_;
	private LinearLayout linearLayoutLentTo_;
	private TextView textViewLentTo_;
	private ImageView imageViewLentTo_;
	private LinearLayout linearLayoutLend_;

	public static EquipmentDetailFragment newInstance(int index) {
		EquipmentDetailFragment fragment = new EquipmentDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putInt("index", index);
		fragment.setArguments(arguments);

		return fragment;
	}

	public static EquipmentDetailFragment newInstance(Bundle bundle) {
		int index = bundle.getInt("index", 0);
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
		tabletSize_ = getResources().getBoolean(R.bool.isTablet);

		selectedEquipment_ = getArguments().getInt("index", 0);
		equipment_ = model_.getEquipmentById(selectedEquipment_);
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
		
		final EquipmentDetailFragment fragment = this;

		if (equipment_.getLentTo() == 0) {
			linearLayoutLentTo_.setVisibility(View.GONE);
			linearLayoutLend_.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FriendSelectorDialog dialog = FriendSelectorDialog.newInstance(fragment);
					dialog.show(getFragmentManager(), "Select Friend");
				}
			});
		}
		else {
			showFriendLentTo();
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!tabletSize_) {
			activity_.setTitle(equipment_.getName());
			textViewEquipmentName_.setVisibility(View.GONE);
		}
		else {
			textViewEquipmentName_.setText(equipment_.getName());
		}
		textViewCategory_.setText(equipment_.getCategory());
		textViewNotes_.setText(equipment_.getNotes());
	}

	public int getSelectedEquipment() {
		return selectedEquipment_;
	}
	
	public void lendEquipment(int friendId){
		model_.lendEquipment(equipment_.getID(), friendId);
		showFriendLentTo();
	}
	
	private void showFriendLentTo(){
		Friend friend = model_.getFriendById(equipment_.getLentTo());
		linearLayoutLend_.setVisibility(View.GONE);
		linearLayoutLentTo_.setVisibility(View.VISIBLE);
		textViewLentTo_.setText(friend.getFirstName() + " " + friend.getLastName());
		imageViewLentTo_.setImageResource(R.drawable.android_contact);

		textViewLentTo_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int index = equipment_.getLentTo();
				Intent myIntent = new Intent();
				if (!tabletSize_) {
					myIntent.setClass(getActivity(), FriendsDetailActivity.class);
				}
				else {
					myIntent.setClass(getActivity(), FriendsActivity.class);
				}
				myIntent.putExtra("showafriend", true);
				myIntent.putExtra("index", index);
				startActivity(myIntent);
			}
		});
	}
}
