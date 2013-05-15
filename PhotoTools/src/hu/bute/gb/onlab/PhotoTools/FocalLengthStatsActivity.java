package hu.bute.gb.onlab.PhotoTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.api.services.drive.model.File;

public class FocalLengthStatsActivity extends SherlockActivity {

	private ListView listViewFocalLengthes_;
	private Map<String, Integer> focalStats_ = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_focal_length_stats);

		listViewFocalLengthes_ = (ListView) findViewById(R.id.listViewFocalLengthes);
		ArrayAdapter<StatItem> adapter = new ArrayAdapter<StatItem>(FocalLengthStatsActivity.this,
				android.R.layout.simple_expandable_list_item_1);

		List<File> photos = StatsActivity.photos;
		focalStats_ = new HashMap<String, Integer>();
		for (File file : photos) {
			Float focalLengthFloat = file.getImageMediaMetadata().getFocalLength();
			String focalLength = focalLengthFloat.toString();
			if (!focalStats_.containsKey(focalLength)) {
				focalStats_.put(focalLength, 1);
			}
			else {
				int count = focalStats_.get(focalLength).intValue();
				count++;
				focalStats_.remove(focalLength);
				focalStats_.put(focalLength, Integer.valueOf(count));
			}
		}
		
		for (String stat : focalStats_.keySet()) {
			adapter.add(new StatItem(stat, focalStats_.get(stat)));
		}
		listViewFocalLengthes_.setAdapter(adapter);
	}

	private class StatItem {
		private String focalLength_;
		private int count_;

		public StatItem(String focalLength, int count) {
			focalLength_ = focalLength;
			count_ = count;
		}

		@Override
		public String toString() {
			String result = new String(focalLength_ + " - " + count_);
			return result;
		}
	}

}
