package hu.bute.gb.onlab.PhotoToolsProto;

import hu.bute.gb.onlab.PhotoToolsProto.model.DummyModel;
import hu.bute.gb.onlab.PhotoToolsProto.model.Equipment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class EquipmentEditActivity extends SherlockActivity {

	private DummyModel model_;
	private int selectedEquipment_ = 0;
	private boolean editMode_ = false;
	private Equipment equipment_;
	private Equipment oldEquipment_;

	private LinearLayout linearLayoutSave_;
	private TextView textViewTitle_;
	private EditText editTextEquipmentName_;
	private Spinner spinnerCategory_;
	private EditText editTextEquipmentNotes_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment_edit);
		model_ = DummyModel.getInstance();

		linearLayoutSave_ = (LinearLayout) findViewById(R.id.linearLayoutSave);
		linearLayoutSave_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveEquipment();
			}
		});

		textViewTitle_ = (TextView) findViewById(R.id.textViewTitle);

		editTextEquipmentName_ = (EditText) findViewById(R.id.editTextEquipmentName);

		spinnerCategory_ = (Spinner) findViewById(R.id.spinnerCategory);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.equipment_categories, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCategory_.setAdapter(adapter);

		editTextEquipmentNotes_ = (EditText) findViewById(R.id.editTextEquipmentNotes);

		if (getIntent().getExtras().getBoolean("edit")) {
			editMode_ = true;
			selectedEquipment_ = getIntent().getExtras().getInt("index");
			equipment_ = model_.getEquipmentById(selectedEquipment_);

			textViewTitle_.setText("Edit " + equipment_.getName());

			editTextEquipmentName_.setText(equipment_.getName());

			if (equipment_.getCategory().equals(getResources().getString(R.string.camera))) {
				spinnerCategory_.setSelection(0);
			}
			else if (equipment_.getCategory().equals(getResources().getString(R.string.lenses))) {
				spinnerCategory_.setSelection(1);
			}
			else if (equipment_.getCategory().equals(getResources().getString(R.string.filters))) {
				spinnerCategory_.setSelection(2);
			}
			else if (equipment_.getCategory().equals(getResources().getString(R.string.flashes))) {
				spinnerCategory_.setSelection(3);
			}
			else if (equipment_.getCategory().equals(
					getResources().getString(R.string.memory_cards_and_readers))) {
				spinnerCategory_.setSelection(4);
			}
			else {
				spinnerCategory_.setSelection(5);
			}

			editTextEquipmentNotes_.setText(equipment_.getNotes());
		}
	}

	// Detect if back was pressed
	boolean backWasPressed = false;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		backWasPressed = event.getKeyCode() == KeyEvent.KEYCODE_BACK;
		return super.dispatchKeyEvent(event);
	}

	private void saveEquipment() {
		// Getting values from controls
		String name = editTextEquipmentName_.getText().toString();
		String category = spinnerCategory_.getSelectedItem().toString();
		String notes = editTextEquipmentNotes_.getText().toString();

		// Only save if name field isn't empty
		if (name.trim().length() == 0) {
			Toast.makeText(EquipmentEditActivity.this,
					getResources().getString(R.string.no_equipment_name_specified),
					Toast.LENGTH_LONG).show();
		}
		else {
			// Edit existing or add new
			if (editMode_) {
				boolean nameChanged = (!equipment_.getName().equals(name)) ? true : false;
				boolean categoryChanged = (!equipment_.getCategory().equals(category)) ? true
						: false;
				boolean notesChanged = (!equipment_.getNotes().equals(notes)) ? true : false;

				// Only save if something is changed
				if (nameChanged || categoryChanged || notesChanged) {
					// Existing ID
					int id = equipment_.getID();
					oldEquipment_ = equipment_;
					equipment_ = new Equipment(id, name, category, notes);
					model_.editEquipment(equipment_, oldEquipment_);
				}
			}
			else {
				// Generate new ID for new entry
				int id = model_.equipmentId.getAndIncrement();
				equipment_ = new Equipment(id, name, category, notes);
				model_.addEquipment(equipment_);
				Log.d("equipment", "" + id);
			}
			finish();
		}
	}
}
