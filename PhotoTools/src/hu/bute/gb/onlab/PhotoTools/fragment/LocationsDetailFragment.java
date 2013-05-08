package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.OnMapActivity;
import hu.bute.gb.onlab.PhotoTools.R;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class LocationsDetailFragment extends SherlockFragment {

	public static final String KEY_LOCATION = "location";

	private DummyModel model_;
	private Activity activity_;
	private Location location_;
	private boolean tabletSize_ = false;

	private ImageView imageViewMap_;
	private TextView textViewAddress_;
	private TextView textViewLatitude_;
	private TextView textViewLongitude_;
	private CheckBox checkBoxCarEntry_;
	private CheckBox checkBoxPowerSource_;
	private TextView textViewNotes_;

	/*
	 * public static LocationsDetailFragment newInstance (long index){
	 * LocationsDetailFragment fragment = new LocationsDetailFragment();
	 * 
	 * Bundle arguments = new Bundle(); arguments.putLong("index", index);
	 * fragment.setArguments(arguments);
	 * 
	 * return fragment; }
	 * 
	 * public static LocationsDetailFragment newInstance (Bundle bundle){ long
	 * index = bundle.getLong("index", 0); return newInstance(index); }
	 */

	public static LocationsDetailFragment newInstance(Location location) {
		LocationsDetailFragment result = new LocationsDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(KEY_LOCATION, location);
		result.setArguments(arguments);

		return result;
	}

	public static LocationsDetailFragment newInstance(Bundle bundle) {
		LocationsDetailFragment result = new LocationsDetailFragment();
		result.setArguments(bundle);
		return result;
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

		if (savedInstanceState == null) {
			if (getArguments() != null) {
				location_ = getArguments().getParcelable(KEY_LOCATION);
			}
		}
		else if (savedInstanceState != null) {
			location_ = savedInstanceState
					.getParcelable(LocationsDetailFragment.KEY_LOCATION);
		}

		// Set title on the Action Bar on phones
		tabletSize_ = getResources().getBoolean(R.bool.isTablet);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		View view = inflater.inflate(R.layout.fragment_locations_detail, container, false);

		imageViewMap_ = (ImageView) view.findViewById(R.id.imageViewMap);
		imageViewMap_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*Intent mapIntent = new Intent();
				mapIntent.setClass(getActivity(), OnMapActivity.class);
				mapIntent.putExtra("edit", true);
				mapIntent.putExtra("index", selectedLocationId_);
				startActivity(mapIntent);*/
			}
		});
		textViewAddress_ = (TextView) view.findViewById(R.id.textViewAddress);
		textViewLatitude_ = (TextView) view.findViewById(R.id.textViewLatitude);
		textViewLongitude_ = (TextView) view.findViewById(R.id.textViewLongitude);
		checkBoxCarEntry_ = (CheckBox) view.findViewById(R.id.checkBoxCarEntry);
		checkBoxPowerSource_ = (CheckBox) view.findViewById(R.id.checkBoxPowerSource);
		textViewNotes_ = (TextView) view.findViewById(R.id.textViewNotes);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		onLocationChanged(location_);
		
		/*if (location_ != null) {
			// Set the activity title on phones
			if (!tabletSize_) {
				activity_.setTitle(location_.getName());
			}
			textViewAddress_.setText(location_.getAddress());
			Coordinate coordinate = location_.getCoordinate();
			textViewLatitude_.setText(Double.toString(coordinate.getLatitude()));
			textViewLongitude_.setText(Double.toString(coordinate.getLongitude()));
			checkBoxCarEntry_.setChecked(location_.hasCarEntry());
			checkBoxPowerSource_.setChecked(location_.hasPowerSource());
			textViewNotes_.setText(location_.getNotes());
		}*/
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(LocationsDetailFragment.KEY_LOCATION, location_);
	}
	
	public void onLocationChanged(Location location){
		location_ = location;
		if (location_ != null) {
			// Set the activity title on phones
			if (!tabletSize_) {
				activity_.setTitle(location_.getName());
			}
			textViewAddress_.setText(location_.getAddress());
			Coordinate coordinate = location_.getCoordinate();
			textViewLatitude_.setText(Double.toString(coordinate.getLatitude()));
			textViewLongitude_.setText(Double.toString(coordinate.getLongitude()));
			checkBoxCarEntry_.setChecked(location_.hasCarEntry());
			checkBoxPowerSource_.setChecked(location_.hasPowerSource());
			textViewNotes_.setText(location_.getNotes());
		}
	}

	public long getSelectedLocationId() {
		return location_.getID();
	}
}
