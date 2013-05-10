package hu.bute.gb.onlab.PhotoTools;

import org.joda.time.DateTime;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;
import hu.bute.gb.onlab.PhotoTools.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static int ANIMATION_DURATION = 600;
	private int orientation_;
	private int screenWidth_ = 0;
	private int screenHeight_ = 0;

	private ImageView imageViewCamera_;
	private ImageButton imageButtonMainMenu_;

	private RelativeLayout layoutLocations_;
	private RelativeLayout layoutLocationsOld_;
	private ImageButton imageButtonLocations_;
	private ImageView imageViewLocationsOld_;
	private TextView textViewLocations_;
	private TextView textViewLocationsOld_;

	private RelativeLayout layoutDeadlines_;
	private RelativeLayout layoutDeadlinesOld_;
	private ImageButton imageButtonDeadlines_;
	private ImageView imageViewDeadlinesOld_;
	private TextView textViewDeadlines_;
	private TextView textViewDeadlinesOld_;

	private RelativeLayout layoutEquipment_;
	private RelativeLayout layoutEquipmentOld_;
	private ImageButton imageButtonEquipment_;
	private ImageView imageViewEquipmentOld_;
	private TextView textViewEquipment_;
	private TextView textViewEquipmentOld_;

	private RelativeLayout layoutFriends_;
	private RelativeLayout layoutFriendsOld_;
	private ImageButton imageButtonFriends_;
	private ImageView imageViewFriendsOld_;
	private TextView textViewFriends_;
	private TextView textViewFriendsOld_;

	private RelativeLayout layoutStats_;
	private RelativeLayout layoutStatsOld_;
	private ImageButton imageButtonStats_;
	private ImageView imageViewStatsOld_;
	private TextView textViewStats_;
	private TextView textViewStatsOld_;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// DummyModel.getInstance();
		/*
		 * Equipment equipment = new Equipment(1, "Camera", "Camera",
		 * "My first DSLR", 0);
		 * PhotoToolsApplication.getDatabaseLoader().addEquipment(equipment);
		 * 
		 * equipment = new Equipment(1, "Lenses", "Lenses", "My first DSLR", 0);
		 * PhotoToolsApplication.getDatabaseLoader().addEquipment(equipment);
		 * 
		 * equipment = new Equipment(1, "Filter", "Filter", "My first DSLR", 0);
		 * PhotoToolsApplication.getDatabaseLoader().addEquipment(equipment);
		 * 
		 * equipment = new Equipment(1, "Flashes", "Flashes", "My first DSLR",
		 * 0);
		 * PhotoToolsApplication.getDatabaseLoader().addEquipment(equipment);
		 * 
		 * equipment = new Equipment(1, "Memory Cards & Readers",
		 * "Memory Cards & Readers", "My first DSLR", 0);
		 * PhotoToolsApplication.getDatabaseLoader().addEquipment(equipment);
		 * 
		 * equipment = new Equipment(1, "Accessories", "Accessories",
		 * "My first DSLR", 0);
		 * PhotoToolsApplication.getDatabaseLoader().addEquipment(equipment);
		 */
		
		/*for (int i = 0; i < 1000; i++) {
			Equipment equipment = new Equipment(1, "Lenses", "Lenses", "My first DSLR", 0);
			PhotoToolsApplication.getDatabaseLoader().addEquipment(equipment);
		}*/

		DateTime start = new DateTime(2013, 4, 26, 15, 0);
		DateTime end = new DateTime(2013, 4, 26, 15, 0);
		Deadline deadline = new Deadline(111, "Finish the Model Shoot Retouch", start, end, true,
				"", "");
		// PhotoToolsApplication.getDatabaseLoader().addDeadline(deadline);

		String note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam justo dui, elementum quis sollicitudin sit ametxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		Location location = new Location(111, "something", "1117 Budapest, Irinyi József St 42",
				new Coordinate(0.01, 0.02), false, false, note);
		// PhotoToolsApplication.getDatabaseLoader().addLocation(location);

		Friend friend = new Friend(111, "Bala", "Kavács",
				"+36207664321", "bala@gmail.com", "1117 Budspest, Irinyi Jyzsef St 42 ",
				null);
		//PhotoToolsApplication.getDatabaseLoader().addFriend(friend);
		
		friend = new Friend(111, "Xala", "Kavács",
				"+36207664321", "bala@gmail.com", "1117 Budspest, Irinyi Jyzsef St 42 ",
				null);
		friend.lendItem(1);
		//PhotoToolsApplication.getDatabaseLoader().addFriend(friend);

		// Getting screen dimensions
		Display display = getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			screenWidth_ = size.x;
			screenHeight_ = size.y;
		}
		else {
			screenWidth_ = display.getWidth();
			screenHeight_ = display.getHeight();
		}

		imageViewCamera_ = (ImageView) findViewById(R.id.imageViewCamera);
		imageButtonMainMenu_ = (ImageButton) findViewById(R.id.imageButtonMainMenu);
		imageButtonMainMenu_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openOptionsMenu();
			}
		});

		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/DotsAllForNow.ttf");
		orientation_ = getResources().getConfiguration().orientation;

		// Locations menu
		layoutLocations_ = (RelativeLayout) findViewById(R.id.locationsMenu);
		layoutLocationsOld_ = (RelativeLayout) findViewById(R.id.locationsMenuOld);
		imageButtonLocations_ = (ImageButton) findViewById(R.id.imageButtonLocations);
		imageViewLocationsOld_ = (ImageView) findViewById(R.id.imageViewLocationsOld);
		textViewLocations_ = (TextView) findViewById(R.id.textViewLocations);
		textViewLocations_.setTypeface(font);

		textViewLocationsOld_ = (TextView) findViewById(R.id.textViewLocationsOld);
		textViewLocationsOld_.setTypeface(font);
		layoutLocationsOld_.setVisibility(View.GONE);

		// Deadlines menu
		layoutDeadlines_ = (RelativeLayout) findViewById(R.id.deadlinesMenu);
		layoutDeadlinesOld_ = (RelativeLayout) findViewById(R.id.deadlinesMenuOld);
		imageButtonDeadlines_ = (ImageButton) findViewById(R.id.imageButtonDeadlines);
		imageViewDeadlinesOld_ = (ImageView) findViewById(R.id.imageViewDeadlinesOld);
		textViewDeadlines_ = (TextView) findViewById(R.id.TextViewDeadlines);
		textViewDeadlines_.setTypeface(font);

		textViewDeadlinesOld_ = (TextView) findViewById(R.id.textViewDeadlinesOld);
		textViewDeadlinesOld_.setTypeface(font);
		layoutDeadlinesOld_.setVisibility(View.GONE);

		// Equipment menu
		layoutEquipment_ = (RelativeLayout) findViewById(R.id.equipmentMenu);
		layoutEquipmentOld_ = (RelativeLayout) findViewById(R.id.equipmentMenuOld);
		imageButtonEquipment_ = (ImageButton) findViewById(R.id.imageButtonEquipment);
		imageViewEquipmentOld_ = (ImageView) findViewById(R.id.imageViewEquipmentOld);
		textViewEquipment_ = (TextView) findViewById(R.id.textViewEquipment);
		textViewEquipment_.setTypeface(font);

		textViewEquipmentOld_ = (TextView) findViewById(R.id.textViewEquipmentOld);
		textViewEquipmentOld_.setTypeface(font);
		layoutEquipmentOld_.setVisibility(View.GONE);

		// Friends menu
		layoutFriends_ = (RelativeLayout) findViewById(R.id.friendsMenu);
		layoutFriendsOld_ = (RelativeLayout) findViewById(R.id.friendsMenuOld);
		imageButtonFriends_ = (ImageButton) findViewById(R.id.imageButtonFriends);
		imageViewFriendsOld_ = (ImageView) findViewById(R.id.imageViewFriendsOld);
		textViewFriends_ = (TextView) findViewById(R.id.TextViewFriends);
		textViewFriends_.setTypeface(font);

		textViewFriendsOld_ = (TextView) findViewById(R.id.textViewFriendsOld);
		textViewFriendsOld_.setTypeface(font);
		layoutFriendsOld_.setVisibility(View.GONE);

		// Stats menu
		layoutStats_ = (RelativeLayout) findViewById(R.id.statsMenu);
		layoutStatsOld_ = (RelativeLayout) findViewById(R.id.statsMenuOld);
		imageButtonStats_ = (ImageButton) findViewById(R.id.imageButtonStats);
		imageViewStatsOld_ = (ImageView) findViewById(R.id.imageViewStatsOld);
		textViewStats_ = (TextView) findViewById(R.id.TextViewStats);
		textViewStats_.setTypeface(font);

		textViewStatsOld_ = (TextView) findViewById(R.id.textViewStatsOld);
		textViewStatsOld_.setTypeface(font);
		layoutStatsOld_.setVisibility(View.GONE);

		// Pop-out animation (translation) by orientation
		// Only available on past Honeycomb devices
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			// Hide the action bar
			getActionBar().hide();

			// Show menu button if there is no hardware menu button
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				if (!ViewConfiguration.get(MainActivity.this).hasPermanentMenuKey()) {
					imageButtonMainMenu_.setVisibility(View.VISIBLE);
				}
			}
			else {
				imageButtonMainMenu_.setVisibility(View.VISIBLE);
			}

			// Animations
			if (orientation_ == Configuration.ORIENTATION_PORTRAIT) {
				popOut(layoutLocations_, -(screenWidth_ * 0.25f), -(screenHeight_ * 0.30f));
				popOut(layoutDeadlines_, (screenWidth_ * 0.25f), -(screenHeight_ * 0.30f));
				popOut(layoutEquipment_, -(screenWidth_ * 0.32f), (screenHeight_ * 0.24f));
				popOut(layoutFriends_, 0.0f, (screenWidth_ * 0.50f));
				popOut(layoutStats_, (screenWidth_ * 0.32f), (screenHeight_ * 0.24f));
			}
			else {
				popOut(layoutLocations_, -(screenWidth_ * 0.30f), -(screenHeight_ * 0.04f));
				popOut(layoutDeadlines_, (screenWidth_ * 0.30f), -(screenHeight_ * 0.04f));
				popOut(layoutEquipment_, -(screenWidth_ * 0.35f), (screenHeight_ * 0.45f));
				popOut(layoutFriends_, 0.0f, (screenWidth_ * 0.32f));
				popOut(layoutStats_, (screenWidth_ * 0.35f), (screenHeight_ * 0.45f));
			}

			// OnClickListeners
			imageButtonLocations_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					locationsMenuSelected();
				}
			});

			imageButtonDeadlines_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deadlinesMenuSelected();
				}
			});

			imageButtonEquipment_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					equipmentMenuSelected();
				}
			});

			imageButtonFriends_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					friendsMenuSelected();
				}
			});

			imageButtonStats_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					statsMenuSelected();
				}
			});

		}
		else {
			layoutLocations_.setVisibility(View.GONE);
			layoutLocationsOld_.setVisibility(View.VISIBLE);
			imageViewLocationsOld_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					locationsMenuSelected();
				}
			});

			layoutDeadlines_.setVisibility(View.GONE);
			layoutDeadlinesOld_.setVisibility(View.VISIBLE);
			imageViewDeadlinesOld_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deadlinesMenuSelected();
				}
			});

			layoutEquipment_.setVisibility(View.GONE);
			layoutEquipmentOld_.setVisibility(View.VISIBLE);
			imageViewEquipmentOld_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					equipmentMenuSelected();
				}
			});

			layoutFriends_.setVisibility(View.GONE);
			layoutFriendsOld_.setVisibility(View.VISIBLE);
			imageViewFriendsOld_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					friendsMenuSelected();
				}
			});

			layoutStats_.setVisibility(View.GONE);
			layoutStatsOld_.setVisibility(View.VISIBLE);
			imageViewStatsOld_.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					statsMenuSelected();
				}
			});

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_settings) {
			Intent myIntent = new Intent();
			myIntent.setClass(MainActivity.this, SettingsActivity.class);
			startActivity(myIntent);
		}

		return super.onOptionsItemSelected(item);
	}

	// Pop-out animations
	@SuppressLint("NewApi")
	private void popOut(View view, float x, float y) {
		view.animate().translationXBy(x).translationYBy(y).setDuration(ANIMATION_DURATION);
	}

	// Menu selection methods
	private void locationsMenuSelected() {
		Intent myIntent = new Intent();
		myIntent.setClass(MainActivity.this, LocationsActivity.class);
		startActivity(myIntent);
	}

	private void deadlinesMenuSelected() {
		Intent myIntent = new Intent();
		myIntent.setClass(MainActivity.this, DeadlinesActivity.class);
		startActivity(myIntent);
	}

	private void equipmentMenuSelected() {
		Intent myIntent = new Intent();
		myIntent.setClass(MainActivity.this, EquipmentActivity.class);
		startActivity(myIntent);
	}

	private void friendsMenuSelected() {
		Intent myIntent = new Intent();
		myIntent.setClass(MainActivity.this, FriendsActivity.class);
		startActivity(myIntent);
	}

	private void statsMenuSelected() {
		Intent myIntent = new Intent();
		myIntent.setClass(MainActivity.this, StatsActivity.class);
		startActivity(myIntent);
	}

}