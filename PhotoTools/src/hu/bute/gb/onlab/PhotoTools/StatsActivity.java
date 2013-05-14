package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.SignInRequiredDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.File.ImageMediaMetadata.Location;
import com.google.api.services.drive.model.FileList;
import com.slidingmenu.lib.SlidingMenu;

public class StatsActivity extends SherlockFragmentActivity {

	public static final int REQUEST_ACCOUNT_PICKER = 1;
	public static final int REQUEST_AUTHORIZATION = 2;
	private static final String IMAGE_JPEG = "image/jpeg";

	private SlidingMenu menu;
	private ListView list;
	private static Drive service;
	private GoogleAccountCredential credential;

	private Map<String, File> photos_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		list = (ListView) findViewById(R.id.listView);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.stats));
		list.setAdapter(adapter);

		// configure the SlidingMenu
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, MenuListFragment.newInstance(4, menu)).commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		SignInRequiredDialog dialog = SignInRequiredDialog.newInstance(this);
		dialog.show(getSupportFragmentManager(), "Sign in to Google Drive required");

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Close Sliding menu if it was open
		if (menu != null) {
			menu.showContent(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						credential.setSelectedAccountName(accountName);
						service = getDriveService(credential);
					}
					try {
						retrieveAllFiles();
					}
					catch (IOException e) {
						Log.d("file", "anyad1");
						e.printStackTrace();
					}
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK) {
					try {
						retrieveAllFiles();
					}
					catch (IOException e) {
						Log.d("file", "anyad2");
						e.printStackTrace();
					}
				}
				else {
					startActivityForResult(credential.newChooseAccountIntent(),
							REQUEST_ACCOUNT_PICKER);
				}
				break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent myIntent = new Intent();
			myIntent.setClass(StatsActivity.this, MainActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void signIn() {
		credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}

	private Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
				credential).build();
	}

	private static void retrieveAllFiles() throws IOException {
		final List<File> result = new ArrayList<File>();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Files.List request = null;
				try {
					request = service.files().list().setQ("mimeType = '" + IMAGE_JPEG +"'");
				}
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				do {
					try {
						FileList files = request.execute();

						result.addAll(files.getItems());
						for (File file : result) {
							Log.d("file", file.getTitle());
							Float focalLength = file.getImageMediaMetadata().getFocalLength();
							Location location = file.getImageMediaMetadata().getLocation();
							if (location != null) {
								Log.d("file", "" + location.getLatitude() + ", " + location.getLongitude());
							}
							if (focalLength != null) {
								Log.d("file", "" + file.getImageMediaMetadata().getFocalLength());
							}
						}
						request.setPageToken(files.getNextPageToken());
					}
					catch (IOException e) {
						Log.d("file", "anyad3");
						System.out.println("An error occurred: " + e);
						request.setPageToken(null);
					}
				} while (request.getPageToken() != null && request.getPageToken().length() > 0);

			}
		});
		t.start();
	}

}
