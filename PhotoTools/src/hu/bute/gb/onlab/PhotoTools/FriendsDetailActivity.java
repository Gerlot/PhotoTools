package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.application.PhotoToolsApplication;
import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import hu.bute.gb.onlab.PhotoTools.fragment.DeleteFriendDialog;
import hu.bute.gb.onlab.PhotoTools.fragment.EquipmentDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.FriendsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoTools.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class FriendsDetailActivity extends SlidingFragmentActivity {

	public static final int FRIEND_EDIT = 3;

	private Friend selectedFriend_;
	private MenuListFragment menuFragment;
	private FriendsDetailFragment detailFragment_;
	private DatabaseLoader databaseLoader_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_detail);
		databaseLoader_ = PhotoToolsApplication.getDatabaseLoader();

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			selectedFriend_ = getIntent().getExtras().getParcelable(
					FriendsDetailFragment.KEY_FRIEND);
			detailFragment_ = FriendsDetailFragment.newInstance(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.FriendsFragmentContainer, detailFragment_).commit();
		}
		else if (savedInstanceState != null) {
			selectedFriend_ = savedInstanceState.getParcelable(FriendsDetailFragment.KEY_FRIEND);
		}

		// Set the Behind View for the SlidingMenu
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			menuFragment = MenuListFragment.newInstance(9, getSlidingMenu());
			FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.menu_frame, menuFragment);
			transaction.commit();
		}
		else {
			menuFragment = (MenuListFragment) this.getSupportFragmentManager().findFragmentById(
					R.id.menu_frame);
		}

		// Customize the SlidingMenu
		SlidingMenu menu = getSlidingMenu();
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setSlidingActionBarEnabled(false);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(FriendsDetailFragment.KEY_FRIEND, selectedFriend_);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSlidingMenu().showContent(false);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			case FRIEND_EDIT:
				if (resultCode == RESULT_OK) {
					Friend friend = data.getParcelableExtra(FriendsDetailFragment.KEY_FRIEND);
					selectedFriend_ = friend;
					detailFragment_.onFriendChanged(friend);
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
			myIntent.setClass(FriendsDetailActivity.this, FriendsActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_edit_friend:
			Intent editIntent = new Intent();
			editIntent.setClass(FriendsDetailActivity.this, FriendsEditActivity.class);
			editIntent.putExtra(FriendsEditActivity.KEY_EDIT, true);
			editIntent.putExtra(FriendsDetailFragment.KEY_FRIEND, selectedFriend_);
			startActivityForResult(editIntent, FRIEND_EDIT);
			return true;
		case R.id.action_delete_friend:
			// Show confirmation dialog
			DeleteFriendDialog dialog = DeleteFriendDialog.newInstance(FriendsDetailActivity.this);
			dialog.show(getSupportFragmentManager(), "Confirm delete friend");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.friends_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void deleteFriend() {
		databaseLoader_.removeFriend(selectedFriend_.getID());
		Intent returnIntent = new Intent();
		returnIntent.putExtra("deleted", selectedFriend_.getID());
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}
