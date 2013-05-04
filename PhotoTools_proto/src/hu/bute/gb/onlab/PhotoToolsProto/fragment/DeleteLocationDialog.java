package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.LocationsDetailActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DeleteLocationDialog extends DialogFragment {
	
	private static LocationsDetailActivity activity_;
	
	public static DeleteLocationDialog newInstance(LocationsDetailActivity activity){
		activity_ = activity;
		return new DeleteLocationDialog();
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_location_confirmation)
               .setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       activity_.deleteLocation();
                   }
               })
               .setNegativeButton(R.string.no_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
