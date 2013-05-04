package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.OnMapActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.R.bool;
import hu.bute.gb.onlab.PhotoToolsProto.R.id;
import hu.bute.gb.onlab.PhotoToolsProto.R.layout;
import hu.bute.gb.onlab.PhotoToolsProto.model.Coordinate;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Location;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class LocationsDetailFragment extends SherlockFragment {
	
	private DummyModel model_;
	private Activity activity_;
	private int selectedLocation_ = 0;
	private Location location_;
	private boolean tabletSize_ = false;
	
	private ImageView imageViewMap_;
	private TextView textViewAddress_;
	private TextView textViewLatitude_;
	private TextView textViewLongitude_;
	private CheckBox checkBoxCarEntry_;
	private CheckBox checkBoxPowerSource_;
	private TextView textViewNotes_;
	
	public static LocationsDetailFragment newInstance (int index){
		LocationsDetailFragment fragment = new LocationsDetailFragment();
		
		Bundle arguments = new Bundle();
		arguments.putInt("index", index);
		fragment.setArguments(arguments);
		
		return fragment;
	}
	
	public static LocationsDetailFragment newInstance (Bundle bundle){
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
		selectedLocation_ = getArguments().getInt("index", 0);
		location_ = model_.getLocationById(selectedLocation_);
		
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
				Intent mapIntent = new Intent();
				mapIntent.setClass(getActivity(), OnMapActivity.class);
				mapIntent.putExtra("edit", true);
				mapIntent.putExtra("index", selectedLocation_);
				startActivity(mapIntent);
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

	public int getSelectedLocation() {
		return selectedLocation_;
	}
}
