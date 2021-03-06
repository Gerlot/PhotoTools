package hu.bute.gb.onlab.PhotoToolsProto;

import hu.bute.gb.onlab.PhotoToolsProto.fragment.AddFriendMethodDialog;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.FriendsDetailFragment;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.FriendsListFragment;
import hu.bute.gb.onlab.PhotoToolsProto.fragment.MenuListFragment;
import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Friend;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.slidingmenu.lib.SlidingMenu;

public class FriendsActivity extends SherlockFragmentActivity implements OnNavigationListener {

	public List<Integer> friendsOnView = new ArrayList<Integer>();

	private static final int SHOW_DETAILS = 1;
	private static final int ADD_FRIEND = 2;
	private static final int IMPORT_CONTACT = 3;

	private ViewGroup fragmentContainer_;
	private FragmentManager fragmentManager_;
	private FriendsListFragment friendsListFragment_;

	private boolean tabletSize_;
	private SlidingMenu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		fragmentContainer_ = (ViewGroup) findViewById(R.id.FriendsFragmentContainer);
		fragmentManager_ = getSupportFragmentManager();
		friendsListFragment_ = (FriendsListFragment) fragmentManager_
				.findFragmentById(R.id.friendslist_fragment);

		tabletSize_ = getResources().getBoolean(R.bool.isTablet);
		String menuItems[] = { "All Friends", "Lent To" };
		ArrayAdapter<String> listMenu = new ArrayAdapter<String>(FriendsActivity.this,
				R.layout.listmenurow, menuItems);
		listMenu.setDropDownViewResource(R.layout.listmenu_dropdownrow);

		// Configure the SlidingMenu
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, MenuListFragment.newInstance(3, menu)).commit();

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(listMenu, this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (getIntent().getExtras() != null) {
			Bundle arguments = getIntent().getExtras();
			int index = arguments.getInt("index");
			friendsOnView.add(Integer.valueOf(index));
			showFriendDetails(0);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Close Sliding menu if it was open
		if (menu != null) {
			menu.showContent(false);
		}
	}

	public void showFriendDetails(int index) {
		if (fragmentContainer_ != null) {
			FriendsDetailFragment detailFragment = (FriendsDetailFragment) fragmentManager_
					.findFragmentById(R.id.FriendsFragmentContainer);
			if (detailFragment == null
					|| detailFragment.getSelectedFriend() != friendsOnView.get(index)) {
				detailFragment = FriendsDetailFragment.newInstance(friendsOnView.get(index));
				FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
				fragmentTransaction.replace(R.id.FriendsFragmentContainer, detailFragment);
				fragmentTransaction.commit();
			}
		}
		else {
			Intent intent = new Intent(this, FriendsDetailActivity.class);
			intent.putExtra("index", friendsOnView.get(index));
			startActivityForResult(intent, SHOW_DETAILS);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			case SHOW_DETAILS:
				break;
			case ADD_FRIEND:
				break;
			case IMPORT_CONTACT:
				if (resultCode == Activity.RESULT_OK) {
					Uri result = data.getData();
					String id = result.getLastPathSegment();
					ContentResolver contentResolver = getContentResolver();

					String fullname = "";
					String number = "";
					String email = "";
					String address = "";

					// Query for name
					Cursor nameCursor = contentResolver.query(
							ContactsContract.Contacts.CONTENT_URI, null,
							ContactsContract.Contacts._ID + " = ?", new String[] { id }, null);
					if (nameCursor.moveToFirst()) {
						fullname = nameCursor.getString(nameCursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					}

					// Separate first name and last name
					String[] name = fullname.split("\\s");
					String firstName = "";
					String lastName = "";
					if (name.length != 0) {
						firstName = name[0];
						if (name.length >= 3) {
							for (int i = 1; i < name.length - 1; i++) {
								firstName += " " + name[i];
							}
						}
						lastName = name[name.length - 1];
					}

					// Query for phone
					Cursor phoneCursor = contentResolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[] { id }, null);
					if (phoneCursor.moveToFirst()) {
						number = phoneCursor.getString(phoneCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
					}
					phoneCursor.close();

					// Query for email
					Cursor emailCursor = getContentResolver().query(Email.CONTENT_URI, null,
							Email.CONTACT_ID + "=?", new String[] { id }, null);
					if (emailCursor.moveToFirst()) {
						email = emailCursor.getString(emailCursor.getColumnIndex(Email.DATA));
					}

					String where = ContactsContract.Data.CONTACT_ID + " = ? AND "
							+ ContactsContract.Data.MIMETYPE + " = ?";
					String[] whereParameters = new String[] { id,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
					Cursor addressCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
							null, where, whereParameters, null);
					while (addressCursor.moveToNext()) {
						String street = addressCursor
								.getString(addressCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
						String city = addressCursor
								.getString(addressCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
						String postalCode = addressCursor
								.getString(addressCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
						String country = addressCursor
								.getString(addressCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
						if (postalCode == null) {
							postalCode = "";
						}
						if (city == null) {
							city = "";
						}
						if (country == null) {
							country = "";
						}
						if (street == null) {
							street = "";
						}
						address = postalCode + " " + city + " " + country + " " + street;
						address = address.trim();
					}
					addressCursor.close();

					// Show toast
					Toast.makeText(
							FriendsActivity.this,
							"Added: " + firstName + " " + lastName + " - " + number + " - " + email
									+ " - " + address, Toast.LENGTH_LONG).show();
					// Add friend
					Friend firendToAdd = new Friend(
							DummyModel.getInstance().friendId.getAndIncrement(), firstName,
							lastName, number, email, address, null);
					DummyModel.getInstance().addFriend(firendToAdd);
				}
				break;

			}
		}
	}

	public void importFriend() {
		Intent importIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(importIntent, IMPORT_CONTACT);
	}

	public void addFriend() {
		Intent newIntent = new Intent();
		newIntent.setClass(FriendsActivity.this, FriendsEditActivity.class);
		newIntent.putExtra("edit", false);
		startActivityForResult(newIntent, ADD_FRIEND);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		switch (itemPosition) {
		case 0:
			if (!friendsListFragment_.isAllSelected) {
				friendsListFragment_.allFriendsSelected(null);
				friendsListFragment_.isAllSelected = true;
				return true;
			}
			break;
		case 1:
			if (friendsListFragment_.isAllSelected) {
				friendsListFragment_.lentToSelected(null);
				friendsListFragment_.isAllSelected = false;
				return true;
			}
			break;
		default:
			break;
		}

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent myIntent = new Intent();
			myIntent.setClass(FriendsActivity.this, MainActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(myIntent);
			finish();
			return true;
		case R.id.action_search:
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			return true;
		case R.id.action_add_friend:
			AddFriendMethodDialog dialog = AddFriendMethodDialog.newInstance(FriendsActivity.this);
			dialog.show(fragmentManager_, "Add Friend Method");
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.friends, menu);
		
		final EditText editTextSearch = (EditText) menu.findItem(R.id.action_search).getActionView();
		editTextSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before,
					int count) {
				friendsListFragment_.search(charSequence);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		MenuItem searchItem = (MenuItem) menu.getItem(0);
		searchItem.setOnActionExpandListener(new OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				editTextSearch.requestFocus();
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				editTextSearch.setText("");
				if (friendsListFragment_.isAllSelected) {
					friendsListFragment_.allFriendsSelected(null);
				}
				else {
					friendsListFragment_.lentToSelected(null);
				}
				return true;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}

}
