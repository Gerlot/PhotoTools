package hu.bute.gb.onlab.PhotoToolsProto.fragment;

import hu.bute.gb.onlab.PhotoToolsProto.EquipmentDetailActivity;
import hu.bute.gb.onlab.PhotoToolsProto.R;
import hu.bute.gb.onlab.PhotoToolsProto.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DeleteEquipmentDialog extends DialogFragment{
	
	private static EquipmentDetailActivity activity_;
	
	public static DeleteEquipmentDialog newInstance(EquipmentDetailActivity activity){
		activity_ = activity;
		return new DeleteEquipmentDialog();
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_equipment_confirmation)
               .setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       activity_.deleteEquipment();
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
