package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.FriendsActivity;
import hu.bute.gb.onlab.PhotoTools.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AddFriendMethodDialog extends DialogFragment {
	
	private static FriendsActivity activity_;
	
	public static AddFriendMethodDialog newInstance(FriendsActivity activity){
		activity_ = activity;
		return new AddFriendMethodDialog();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add friend").setItems(R.array.add_friend, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					activity_.importFriend();
					break;
				case 1:
					activity_.addFriend();
					break;
				default:
					break;
				}
			}
		});

		return builder.create();
	}

}
