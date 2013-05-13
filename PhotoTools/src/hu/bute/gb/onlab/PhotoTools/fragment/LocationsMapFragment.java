package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.datastorage.DbConstants;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationsMapFragment extends SupportMapFragment {

	public static final String TAG = "LocationsMapFragment";
	public static final String KEY_ACTIVEMODE = "activeMode";
	public static final String KEY_SELECTMODE = "selectMode";

	private static ArrayList<Location> locations_ = null;

	private GoogleMap map_;
	private static boolean activeMode_ = true;
	private static boolean selectMode_ = false;

	// DBloader
	private DatabaseLoader databaseLoader_;
	private GetAllTask getAllTask_;

	public static LocationsMapFragment newInstance(boolean activeMode, boolean selectMode,
			ArrayList<Location> locations) {
		activeMode_ = activeMode;
		selectMode_ = selectMode;
		locations_ = locations;
		return new LocationsMapFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();

		if (savedInstanceState != null) {
			activeMode_ = savedInstanceState.getBoolean(KEY_ACTIVEMODE);
			selectMode_ = savedInstanceState.getBoolean(KEY_SELECTMODE);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_ACTIVEMODE, activeMode_);
		outState.putBoolean(KEY_SELECTMODE, selectMode_);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		map_ = getMap();
		map_.clear();

		map_.setMyLocationEnabled(activeMode_);
		map_.getUiSettings().setAllGesturesEnabled(activeMode_);
		map_.getUiSettings().setZoomControlsEnabled(activeMode_);

		// Adding markers
		if (locations_ == null) {
			if (getAllTask_ != null) {
				getAllTask_.cancel(false);
			}
			getAllTask_ = new GetAllTask();
			getAllTask_.execute();
		}
		else {
			refreshMap();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (getAllTask_ != null) {
			getAllTask_.cancel(false);
		}
	}
	
	public void changeLocation(Location location){
		if (locations_ != null && locations_.size() == 1) {
			locations_.set(0, location);
		}
	}

	private void refreshMap() {
		if (locations_ != null) {
			if (locations_.size() == 1) {
				Coordinate coordinate = locations_.get(0).getCoordinate();
				map_.addMarker(
						new MarkerOptions().position(new LatLng(coordinate.getLatitude(),
								coordinate.getLongitude()))).setTitle(locations_.get(0).getName());
				map_.moveCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(coordinate.getLatitude(), coordinate.getLongitude()), 10));
			}
			else {
				for (Location location : locations_) {
					Coordinate coordinate = location.getCoordinate();
					map_.addMarker(
							new MarkerOptions().position(new LatLng(coordinate.getLatitude(),
									coordinate.getLongitude()))).setTitle(location.getName());
				}
			}
		}
	}

	// Background task for getting locations from database
	private class GetAllTask extends AsyncTask<Void, Void, Cursor> {
		private static final String TAG = "GetAllTask";

		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				Cursor result = null;
				result = databaseLoader_.getAllLocations();
				if (!isCancelled()) {
					return result;
				}
				else {
					Log.d(TAG, "Cancelled, closing cursor");
					if (result != null) {
						result.close();
					}
					return null;
				}
			}
			catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);
			Log.d(TAG, "Fetch completed, displaying cursor results!");
			if (result != null && result.getCount() > 0) {
				try {
					if (result != null) {
						while (result.moveToNext()) {
							long id = result.getLong(result
									.getColumnIndex(DbConstants.Location.KEY_ROWID));
							Location location = databaseLoader_.getLocation(id);
							if (location != null) {
								if (locations_ == null) {
									locations_ = new ArrayList<Location>();
								}
								locations_.add(location);
							}
						}
					}
					getAllTask_ = null;
					refreshMap();
				}
				catch (Exception e) {
				}
			}
		}
	}
}
