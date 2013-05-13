package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.SignInRequiredDialog;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.slidingmenu.lib.SlidingMenu;

public class StatsActivity extends SherlockFragmentActivity {
	
	public static final int REQUEST_ACCOUNT_PICKER = 1;

	private SlidingMenu menu;
	private ListView list;
	private static Drive service;
	private GoogleAccountCredential credential;

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
	
	public void signIn(){
		credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}
	
	private Drive getDriveService(GoogleAccountCredential credential) {
	    return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
	        .build();
	  }

}
