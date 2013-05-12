package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import hu.bute.gb.onlab.PhotoTools.fragment.AddFriendMethodDialog;
import hu.bute.gb.onlab.PhotoTools.fragment.FriendsDetailFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.FriendsListFragment;
import hu.bute.gb.onlab.PhotoTools.fragment.MenuListFragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.slidingmenu.lib.SlidingMenu;

public class FriendsActivity extends SherlockFragmentActivity implements OnNavigationListener {

	public List<Long> friendsOnView = new ArrayList<Long>();

	private static final int FRIEND_DELETE = 1;
	private static final int FRIEND_ADD = 2;
	private static final int IMPORT_CONTACT = 4;

	private MenuItem searchItem_ = null;
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
			long index = arguments.getLong("index");
			friendsOnView.add(Long.valueOf(index));
			// showFriendDetails(0);
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

	public void showFriendDetails(Friend selectedFriend) {
		if (fragmentContainer_ != null) {
			FriendsDetailFragment detailFragment = (FriendsDetailFragment) fragmentManager_
					.findFragmentById(R.id.FriendsFragmentContainer);
			if (detailFragment == null
					|| detailFragment.getSelectedFriendId() != selectedFriend.getID()) {
				detailFragment = FriendsDetailFragment.newInstance(selectedFriend);
				FragmentTransaction fragmentTransaction = fragmentManager_.beginTransaction();
				fragmentTransaction.replace(R.id.FriendsFragmentContainer, detailFragment);
				fragmentTransaction.commit();
			}
		}
		else {
			Intent intent = new Intent(this, FriendsDetailActivity.class);
			intent.putExtra(FriendsDetailFragment.KEY_FRIEND, selectedFriend);
			startActivityForResult(intent, FRIEND_DELETE); // Listen for delete
															// event
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			case FRIEND_DELETE:
				if (resultCode == RESULT_OK) {
					friendsListFragment_.refreshList();
				}
				break;
			case FRIEND_ADD:
				if (resultCode == RESULT_OK) {
					friendsListFragment_.refreshList();
				}
				break;
			case IMPORT_CONTACT:
				if (resultCode == Activity.RESULT_OK) {
					Uri result = data.getData();
					String contactId = result.getLastPathSegment();
					ContentResolver contentResolver = getContentResolver();

					String fullname = "";
					String number = "";
					String email = "";
					String address = "";

					// Query for name
					Cursor nameCursor = contentResolver.query(
							ContactsContract.Contacts.CONTENT_URI, null,
							ContactsContract.Contacts._ID + " = ?", new String[] { contactId },
							null);
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
							new String[] { contactId }, null);
					if (phoneCursor.moveToFirst()) {
						number = phoneCursor.getString(phoneCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
					}
					phoneCursor.close();

					// Query for email
					Cursor emailCursor = getContentResolver().query(Email.CONTENT_URI, null,
							Email.CONTACT_ID + "=?", new String[] { contactId }, null);
					if (emailCursor.moveToFirst()) {
						email = emailCursor.getString(emailCursor.getColumnIndex(Email.DATA));
					}

					// Query for address
					String where = ContactsContract.Data.CONTACT_ID + " = ? AND "
							+ ContactsContract.Data.MIMETYPE + " = ?";
					String[] whereParameters = new String[] { contactId,
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
							lastName, number, email, address, null, false);
					// TODO add friend
					// DummyModel.getInstance().addFriend(firendToAdd);
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
		startActivityForResult(newIntent, FRIEND_ADD);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		switch (itemPosition) {
		case 0:
			if (!friendsListFragment_.isAllSelected) {
				// friendsListFragment_.allFriendsSelected(null);
				friendsListFragment_.isAllSelected = true;
				friendsListFragment_.refreshList();
				return true;
			}
			break;
		case 1:
			if (friendsListFragment_.isAllSelected) {
				// friendsListFragment_.lentToSelected(null);
				friendsListFragment_.isAllSelected = false;
				friendsListFragment_.refreshList();
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
		
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				friendsListFragment_.search(newText);
				return false;
			}
		});
		
		searchItem_ = (MenuItem) menu.getItem(0);
		searchItem_.setOnActionExpandListener(new OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				friendsListFragment_.searchFilter = null;
				friendsListFragment_.refreshList();
				return true;
			}
		});

		/*final EditText editTextSearch = (EditText) menu.findItem(R.id.action_search)
				.getActionView();
		editTextSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				// friendsListFragment_.search(charSequence);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
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
					// friendsListFragment_.allFriendsSelected(null);
				}
				else {
					// friendsListFragment_.lentToSelected(null);
				}
				return true;
			}
		});*/

		return super.onCreateOptionsMenu(menu);
	}

}
