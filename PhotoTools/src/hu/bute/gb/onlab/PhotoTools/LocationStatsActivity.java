package hu.bute.gb.onlab.PhotoTools;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LocationStatsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_stats);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_stats, menu);
		return true;
	}

}
