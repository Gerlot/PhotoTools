package hu.bute.gb.onlab.PhotoToolsProto;

import hu.bute.gb.onlab.PhotoToolsProto.fragment.DeleteFriendDialog;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.FriendsDetailFragment;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.MenuListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class FriendsDetailActivity extends SlidingFragmentActivity {

	private int selectedFriend_ = 0;
	MenuListFragment menuFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_detail);

		if (getIntent().getExtras() != null && savedInstanceState == null) {
			selectedFriend_ = getIntent().getExtras().getInt("index", 0);
			FriendsDetailFragment detailFragment = FriendsDetailFragment.newInstance(getIntent()
					.getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.FriendsFragmentContainer, detailFragment).commit();
		}
		else if (savedInstanceState != null) {
			selectedFriend_ = savedInstanceState.getInt("index");
		}

		// Set the Behind View for the SlidingMenu
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			menuFragment = MenuListFragment.newInstance(9, getSlidingMenu());
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			t.replace(R.id.menu_frame, menuFragment);
			t.commit();
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
		outState.putInt("index", selectedFriend_);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSlidingMenu().showContent(false);
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
			editIntent.putExtra("edit", true);
			editIntent.putExtra("index", selectedFriend_);
			startActivity(editIntent);
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
		finish();
	}
}
