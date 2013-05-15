package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.fragment.LocationsMapFragment;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.api.services.drive.model.File;

public class LocationStatsActivity extends SherlockFragmentActivity {

	private ArrayList<Location> locations_;
	private ArrayList<Integer> photoCount_;
	
	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private LocationsMapFragment locationsMapFragment_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_stats);
		
		fragmentContainer_ = (ViewGroup) findViewById(R.id.mapFragmentContainer);
		fragmentManager_ = getSupportFragmentManager();

		List<File> photos = StatsActivity.photos;
		locations_ = new ArrayList<Location>();
		for (File file : photos) {
			boolean add = true;
			Log.d("file", file.getTitle());
			com.google.api.services.drive.model.File.ImageMediaMetadata.Location locationTaken = file
					.getImageMediaMetadata().getLocation();
			if (locationTaken != null) {
				Log.d("file", locationTaken.getLatitude() + ", " + locationTaken.getLongitude());
				double latitude = locationTaken.getLatitude();
				double longitude = locationTaken.getLongitude();
				if (locations_.size() == 0) {
					locations_.add(new Location(100, file.getTitle(), "", new Coordinate(
							latitude, longitude), false, false, ""));
				}
				for (Location location : locations_) {
					// Combine photos nearby
					double locationLatitude = location.getCoordinate().getLatitude();
					double locationLongitude = location.getCoordinate().getLongitude();
					boolean latitudeExists = (latitude < locationLatitude + 0.002)
							&& (latitude > locationLatitude - 0.002);
					boolean longitudeExists = (longitude < locationLongitude + 0.002)
							&& (longitude > locationLongitude - 0.002);
					// If location doesn't exist add to the list
					if (!latitudeExists && !longitudeExists) {
						locations_.add(new Location(100, file.getTitle(), "", new Coordinate(
								latitude, longitude), false, false, ""));
					}
					else {
						// TODO count photoes
					}
				}
			}
		}
		
		locationsMapFragment_ = LocationsMapFragment.newInstance(true, false, locations_);
		FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
		fragmentTransaction.replace(R.id.mapFragmentContainer, locationsMapFragment_);
		fragmentTransaction.commit();
	}

}
