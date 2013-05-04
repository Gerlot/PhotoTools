package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.EquipmentActivity;
import hu.bute.gb.onlab.PhotoToolsProto.EquipmentDetailActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.R.bool;
import hu.bute.gb.onlab.PhotoToolsProto.R.id;
import hu.bute.gb.onlab.PhotoToolsProto.R.layout;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Equipment;
import hu.bute.gb.onlab.PhotoToolsProto.model.Friend;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class FriendsDetailFragment extends SherlockFragment {

	private boolean tabletSize_;
	private DummyModel model_;
	private Activity activity_;
	private int selectedFriend_ = 0;
	private Friend friend_;

	private RelativeLayout realtiveLayoutPhone_;
	private ImageButton imageButtonSms_;
	private TextView textViewFriendName_;
	private TextView textViewFriendPhone_;
	private TextView textViewFriendEmail_;
	private TextView textViewFriendAddress_;
	private TextView textViewFriendLentLabel_;
	private LinearLayout linearLayoutLentEquipment_;

	public static FriendsDetailFragment newInstance(int index) {
		FriendsDetailFragment fragment = new FriendsDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putInt("index", index);
		fragment.setArguments(arguments);

		return fragment;
	}

	public static FriendsDetailFragment newInstance(Bundle bundle) {
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

		selectedFriend_ = getArguments().getInt("index", 0);
		friend_ = model_.getFriendById(selectedFriend_);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		View view = inflater.inflate(R.layout.fragment_friends_detail, container, false);

		textViewFriendName_ = (TextView) view.findViewById(R.id.textViewFriendName);
		
		realtiveLayoutPhone_ = (RelativeLayout) view.findViewById(R.id.realtiveLayoutPhone);
		realtiveLayoutPhone_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + textViewFriendPhone_.getText()));
				startActivity(callIntent);
			}
		});
		
		imageButtonSms_ = (ImageButton) view.findViewById(R.id.imageButtonSms);
		imageButtonSms_.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("address", textViewFriendPhone_.getText());
				sendIntent.setType("vnd.android-dir/mms-sms");
				startActivity(sendIntent);
			}
		});
		
		textViewFriendPhone_ = (TextView) view.findViewById(R.id.textViewFriendPhone);
		textViewFriendEmail_ = (TextView) view.findViewById(R.id.textViewFriendEmail);
		textViewFriendAddress_ = (TextView) view.findViewById(R.id.textViewFriendAddress);
		textViewFriendLentLabel_ = (TextView) view.findViewById(R.id.textViewFriendLentLabel);
		linearLayoutLentEquipment_ = (LinearLayout) view
				.findViewById(R.id.linearLayoutLentEquipment);

		if (friend_.getLentItems() == null) {
			// Only add lend new button
			View lendView = inflater.inflate(R.layout.lendnew, null);
			lendView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			linearLayoutLentEquipment_.addView(lendView);
		}
		else {
			// Add lent items
			for (Integer item : friend_.getLentItems()) {
				final Equipment equipment = model_.getEquipmentById(item.intValue());
				// Show the lent item
				View lentView = inflater.inflate(R.layout.lentitem, null);
				TextView textViewLentEquipment = (TextView) lentView
						.findViewById(R.id.textViewLentEquipment);
				textViewLentEquipment.setText(equipment.getName());
				textViewLentEquipment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int index = equipment.getID();
						Intent myIntent = new Intent();
						if (!tabletSize_) {
							myIntent.setClass(getActivity(),
									EquipmentDetailActivity.class);
						}
						else {
							myIntent.setClass(getActivity(), EquipmentActivity.class);
						}
						myIntent.putExtra("showanequipment", true);
						myIntent.putExtra("index", index);
						startActivity(myIntent);
					}
				});
				linearLayoutLentEquipment_.addView(lentView);
			}
			// Add lend new button
			View lendView = inflater.inflate(R.layout.lendnew, null);
			lendView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			linearLayoutLentEquipment_.addView(lendView);
		}

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!tabletSize_) {
			activity_.setTitle(friend_.getFirstName() + " " + friend_.getLastName());
			textViewFriendName_.setVisibility(View.GONE);
		}
		else {
			textViewFriendName_.setText(friend_.getFirstName() + " " + friend_.getLastName());
		}
		textViewFriendPhone_.setText(friend_.getPhoneNumber());
		textViewFriendEmail_.setText(friend_.getEmailAddress());
		textViewFriendAddress_.setText(friend_.getAddress());
	}

	public int getSelectedFriend() {
		return selectedFriend_;
	}
}
