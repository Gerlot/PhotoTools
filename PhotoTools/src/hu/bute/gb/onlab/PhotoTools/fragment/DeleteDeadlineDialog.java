package hu.bute.gb.onlab.PhotoTools.fragment;

import hu.bute.gb.onlab.PhotoTools.DeadlinesDetailActivity;
import hu.bute.gb.onlab.PhotoTools.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DeleteDeadlineDialog extends DialogFragment {
	
	private static DeadlinesDetailActivity activity_;
	
	public static DeleteDeadlineDialog newInstance(DeadlinesDetailActivity activity){
		activity_ = activity;
		return new DeleteDeadlineDialog();
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_deadline_confirmation)
               .setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       activity_.deleteDeadline();
                   }
               })
               .setNegativeButton(R.string.no_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                   }
               });
        
        return builder.create();
    }
}
