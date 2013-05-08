package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.R;
import android.os.Bundle;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class OnMapActivity extends SherlockActivity {
	
	private DummyModel model_;
	private int selectedLocation_ = 0;
	private Location location_;
	
	private ImageView imageViewMap_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_map);
		model_ = DummyModel.getInstance();
		
		imageViewMap_ = (ImageView) findViewById(R.id.imageViewMap);
		
		if (getIntent().getExtras().getBoolean("edit")) {
			selectedLocation_ = getIntent().getExtras().getInt("index");
			location_ = model_.getLocationById(selectedLocation_);
			setTitle(location_.getName());
		}
		else if (getIntent().getExtras().getBoolean("new")) {
			setTitle("Select location on map");
		}
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
