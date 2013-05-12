package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;

import java.util.ArrayList;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationsMapFragment extends SupportMapFragment {

	public static final String TAG = "LocationsMapFragment";
	private static ArrayList<Location> locations_ = null;

	private GoogleMap map_;
	private static boolean activeMode_ = true;

	public static LocationsMapFragment newInstance(boolean activeMode, ArrayList<Location> locations) {
		locations_ = locations;
		activeMode_ = activeMode;
		return new LocationsMapFragment();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		map_ = getMap();
		map_.clear();

		map_.setMyLocationEnabled(activeMode_);
		map_.getUiSettings().setAllGesturesEnabled(activeMode_);
		map_.getUiSettings().setZoomControlsEnabled(activeMode_);

		if (locations_ != null) {
			Coordinate coordinate = locations_.get(0).getCoordinate();
			map_.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordinate.getLatitude(),
					coordinate.getLongitude()), 10));
			map_.addMarker(new MarkerOptions().position(
					new LatLng(coordinate.getLatitude(), coordinate.getLongitude())));
		}
	}
}
