package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.EquipmentActivity;
import hu.bute.gb.onlab.PhotoTools.EquipmentDetailActivity;
import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class FriendsDetailFragment extends SherlockFragment {
	
	public static final String KEY_FRIEND = "friend";

	private boolean tabletSize_;
	private DummyModel model_;
	private Activity activity_;
	private Friend friend_;

	private RelativeLayout realtiveLayoutPhone_;
	private ImageButton imageButtonSms_;
	private TextView textViewFriendName_;
	private TextView textViewFriendPhone_;
	private TextView textViewFriendEmail_;
	private TextView textViewFriendAddress_;
	private TextView textViewFriendLentLabel_;
	private LinearLayout linearLayoutLentEquipment_;
	private LayoutInflater inflater_;

	public static FriendsDetailFragment newInstance(Friend friend) {
		FriendsDetailFragment result = new FriendsDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(KEY_FRIEND, friend);
		result.setArguments(arguments);

		return result;
	}

	public static FriendsDetailFragment newInstance(Bundle bundle) {
		FriendsDetailFragment result = new FriendsDetailFragment();
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
		
		if (savedInstanceState == null) {
			if (getArguments() != null) {
				friend_ = getArguments().getParcelable(KEY_FRIEND);
			}
		}
		else if (savedInstanceState != null) {
			friend_ = savedInstanceState.getParcelable(KEY_FRIEND);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		View view = inflater.inflate(R.layout.fragment_friends_detail, container, false);
		inflater_ = inflater;

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

		final FriendsDetailFragment fragment = this;

		if (friend_.getLentItems() == null) {
			// Only add lend new button
			View lendView = inflater.inflate(R.layout.lendnew, null);
			lendView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					EquipmentSelectorDialog dialog = EquipmentSelectorDialog.newInstance(fragment);
					dialog.show(getFragmentManager(), "Select Equipment");
				}
			});
			linearLayoutLentEquipment_.addView(lendView);
		}
		else {
			showLentEquipment();
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		onFriendChanged(friend_);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(KEY_FRIEND, friend_);
		super.onSaveInstanceState(outState);
	}
	
	public void onFriendChanged(Friend friend){
		friend_ = friend;
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
	
	public long getSelectedFriendId(){
		return friend_.getID();
	}

	public void lendEquipment(long equipmentId) {
		boolean lendSuccesfully = model_.lendEquipment(equipmentId, friend_.getID());
		if (lendSuccesfully) {
			showLentEquipment();
		}
		else {
			Toast.makeText(getActivity(),
					getResources().getString(R.string.already_lent),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showLentEquipment() {
		final FriendsDetailFragment fragment = this;
		linearLayoutLentEquipment_.removeAllViews();
		
		// Add lent items
		for (Long item : friend_.getLentItems()) {
			final Equipment equipment = model_.getEquipmentById(item.intValue());
			// Show the lent item
			View lentView = inflater_.inflate(R.layout.lentitem, null);
			TextView textViewLentEquipment = (TextView) lentView
					.findViewById(R.id.textViewLentEquipment);
			textViewLentEquipment.setText(equipment.getName());
			textViewLentEquipment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					long index = equipment.getID();
					Intent myIntent = new Intent();
					if (!tabletSize_) {
						myIntent.setClass(getActivity(), EquipmentDetailActivity.class);
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
		View lendView = inflater_.inflate(R.layout.lendnew, null);
		lendView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EquipmentSelectorDialog dialog = EquipmentSelectorDialog.newInstance(fragment);
				dialog.show(getFragmentManager(), "Select Equipment");
			}
		});
		linearLayoutLentEquipment_.addView(lendView);
	}
}
