package hu.bute.gb.onlab.PhotoTools;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FocalLengthStatsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_focal_length_stats);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.focal_length_stats, menu);
		return true;
	}

}
