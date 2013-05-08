package hu.bute.gb.onlab.PhotoTools;

import hu.bute.gb.onlab.PhotoTools.datastorage.DummyModel;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import hu.bute.gb.onlab.PhotoTools.R;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class FriendsEditActivity extends SherlockActivity {

	private DummyModel model_;
	private long selectedFriend_ = 0;
	private boolean editMode_ = false;
	private Friend friend_;

	private LinearLayout linearLayoutSave_;
	private TextView textViewTitle_;
	private EditText editTextName_;
	private ImageView imageViewContactPicture_;
	private EditText editTextPhoneNumber_;
	private EditText editTextEmail_;
	private EditText editTextAddress_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_edit);
		model_ = DummyModel.getInstance();
		
		linearLayoutSave_ = (LinearLayout) findViewById(R.id.linearLayoutSave);
		linearLayoutSave_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveFriend();
			}
		});

		textViewTitle_ = (TextView) findViewById(R.id.textViewTitle);
		editTextName_ = (EditText) findViewById(R.id.editTextName);
		imageViewContactPicture_ = (ImageView) findViewById(R.id.imageViewContactPicture);
		editTextPhoneNumber_ = (EditText) findViewById(R.id.editTextPhoneNumber);
		editTextEmail_ = (EditText) findViewById(R.id.editTextEmail);
		editTextAddress_ = (EditText) findViewById(R.id.editTextAddress);

		if (getIntent().getExtras().getBoolean("edit")) {
			editMode_ = true;
			selectedFriend_ = getIntent().getExtras().getLong("index");
			Log.d("friend", "" + selectedFriend_);
			friend_ = model_.getFriendById(selectedFriend_);
			textViewTitle_.setText("Edit " + friend_.getFullNameFirstLast());

			editTextName_.setText(friend_.getFullNameFirstLast());
			editTextPhoneNumber_.setText(friend_.getPhoneNumber());
			editTextEmail_.setText(friend_.getEmailAddress());
			editTextAddress_.setText(friend_.getAddress());

		}
	}

	private void saveFriend() {
		// Getting values from controls
		String[] name = editTextName_.getText().toString().split("\\s");
		String firstName = null;
		String lastName = null;
		if (name.length != 0) {
			firstName = name[0];
			if (name.length >= 3) {
				for (int i = 1; i < name.length - 1; i++) {
					firstName += " " + name[i];
				}
			}
			lastName = name[name.length - 1];
		}
		String phoneNumber = editTextPhoneNumber_.getText().toString();
		String emailAddress = editTextEmail_.getText().toString();
		String address = editTextAddress_.getText().toString();
		List<Integer> lentItems = null;
		/*
		 * if (friend_.getLentItems() != null) { lentItems =
		 * friend_.getLentItems(); }
		 */
		
		if (name.length == 0 || firstName == null || firstName.trim().length() == 0) {
			Toast.makeText(FriendsEditActivity.this,
					getResources().getString(R.string.no_friend_name_specified),
					Toast.LENGTH_LONG).show();
		}
		else {
			finish();
		}
	}

}
