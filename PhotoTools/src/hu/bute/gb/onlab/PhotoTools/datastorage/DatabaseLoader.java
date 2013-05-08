package hu.bute.gb.onlab.PhotoTools.datastorage;

import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseLoader {

	private Context context_;
	private DatabaseHelper databaseHelper_;
	private SQLiteDatabase database_;

	public DatabaseLoader(Context context) {
		this.context_ = context;
	}

	public void open() throws SQLException {
		// DatabaseHelper objektum
		databaseHelper_ = new DatabaseHelper(context_, DbConstants.DATABASE_NAME);
		// Adatbázis objektum
		database_ = databaseHelper_.getWritableDatabase();
		// Ha nincs még séma, akkor létrehozzuk
		databaseHelper_.onCreate(database_);
	}

	public void close() {
		databaseHelper_.close();
	}

	// CRUD és egyéb metódusok entitásonként
	// Location
	// CREATE
	public long addLocation(Location location) {
		ContentValues values = new ContentValues();

		values.put(DbConstants.Location.KEY_NAME, location.getName());
		values.put(DbConstants.Location.KEY_ADDRESS, location.getAddress());
		values.put(DbConstants.Location.KEY_LATITUDE,
				Double.toString(location.getCoordinate().getLatitude()));
		values.put(DbConstants.Location.KEY_LONGITUDE,
				Double.toString(location.getCoordinate().getLongitude()));
		values.put(DbConstants.Location.KEY_CARENTRY, Boolean.toString(location.hasCarEntry()));
		values.put(DbConstants.Location.KEY_POWERSOURCE,
				Boolean.toString(location.hasPowerSource()));
		values.put(DbConstants.Location.KEY_NOTES, location.getNotes());

		return database_.insert(DbConstants.Location.DATABASE_TABLE, null, values);
	}

	// READ
	// Összes Location lekérése
	public Cursor getAllLocations() {
		// Cursor minden rekordra (where = null)
		return database_.query(DbConstants.Location.DATABASE_TABLE, new String[] {
				DbConstants.Location.KEY_ROWID, DbConstants.Location.KEY_NAME,
				DbConstants.Location.KEY_ADDRESS, DbConstants.Location.KEY_LATITUDE,
				DbConstants.Location.KEY_LONGITUDE, DbConstants.Location.KEY_CARENTRY,
				DbConstants.Location.KEY_POWERSOURCE, DbConstants.Location.KEY_NOTES }, null, null,
				null, null, DbConstants.Location.KEY_NAME);
	}

	public Cursor getLocationByFilter(String filter) {
		// A Location-re mutató cursor
		String value = "%" + filter.toString() + "%";
		Cursor cursor = database_.query(DbConstants.Location.DATABASE_TABLE, new String[] {
				DbConstants.Location.KEY_ROWID, DbConstants.Location.KEY_NAME,
				DbConstants.Location.KEY_ADDRESS, DbConstants.Location.KEY_LATITUDE,
				DbConstants.Location.KEY_LONGITUDE, DbConstants.Location.KEY_CARENTRY,
				DbConstants.Location.KEY_POWERSOURCE, DbConstants.Location.KEY_NOTES },
				DbConstants.Location.KEY_NAME + " LIKE ?", new String[] { value }, null, null,
				DbConstants.Location.KEY_NAME);
		// Ha van rekord amire a Cursor mutat
		if (cursor.moveToFirst())
			return cursor;
		// Egyébként null-al térünk vissza
		return null;
	}

	// Egy Location lekérése
	public Location getLocation(long id) {
		// A Location-re mutató cursor
		Cursor cursor = database_.query(DbConstants.Location.DATABASE_TABLE, new String[] {
				DbConstants.Location.KEY_ROWID, DbConstants.Location.KEY_NAME,
				DbConstants.Location.KEY_ADDRESS, DbConstants.Location.KEY_LATITUDE,
				DbConstants.Location.KEY_LONGITUDE, DbConstants.Location.KEY_CARENTRY,
				DbConstants.Location.KEY_POWERSOURCE, DbConstants.Location.KEY_NOTES },
				DbConstants.Location.KEY_ROWID + "=" + id, null, null, null,
				DbConstants.Location.KEY_NAME);
		// Ha van rekord amire a Cursor mutat
		if (cursor.moveToFirst())
			return getLocationByCursor(cursor);
		// Egyébként null-al térünk vissza
		return null;
	}

	public static Location getLocationByCursor(Cursor c) {
		return new Location(c.getLong(c.getColumnIndex(DbConstants.Location.KEY_ROWID)), // id
				c.getString(c.getColumnIndex(DbConstants.Location.KEY_NAME)), // name
				c.getString(c.getColumnIndex(DbConstants.Location.KEY_ADDRESS)), // address
				new Coordinate(Double.parseDouble(c.getString(c
						.getColumnIndex(DbConstants.Location.KEY_LATITUDE))), Double.parseDouble(c
						.getString(c.getColumnIndex(DbConstants.Location.KEY_LONGITUDE)))), // coordinate
				Boolean.parseBoolean(c.getString(c
						.getColumnIndex(DbConstants.Location.KEY_CARENTRY))), // carEntry
				Boolean.parseBoolean(c.getString(c
						.getColumnIndex(DbConstants.Location.KEY_POWERSOURCE))), // powerSource
				c.getString(c.getColumnIndex(DbConstants.Location.KEY_NOTES)) // notes
		);
	}

	// UPDATE
	public boolean editLocation(long id, Location location) {
		ContentValues values = new ContentValues();

		values.put(DbConstants.Location.KEY_NAME, location.getName());
		values.put(DbConstants.Location.KEY_ADDRESS, location.getAddress());
		values.put(DbConstants.Location.KEY_LATITUDE,
				Double.toString(location.getCoordinate().getLatitude()));
		values.put(DbConstants.Location.KEY_LONGITUDE,
				Double.toString(location.getCoordinate().getLongitude()));
		values.put(DbConstants.Location.KEY_CARENTRY, Boolean.toString(location.hasCarEntry()));
		values.put(DbConstants.Location.KEY_POWERSOURCE,
				Boolean.toString(location.hasPowerSource()));
		values.put(DbConstants.Location.KEY_NOTES, location.getNotes());

		return database_.update(DbConstants.Location.DATABASE_TABLE, values,
				DbConstants.Location.KEY_ROWID + "=" + id, null) > 0;
	}

	// DELETE
	public boolean removeLocation(long id) {
		return database_.delete(DbConstants.Location.DATABASE_TABLE, DbConstants.Location.KEY_ROWID
				+ "=" + id, null) > 0;
	}

	// Deadline
	// CREATE
	public long addDeadline(Deadline deadline) {
		ContentValues values = new ContentValues();

		values.put(DbConstants.Deadline.KEY_NAME, deadline.getName());
		values.put(DbConstants.Deadline.KEY_STARTTIME, deadline.getStartTime().getMillis());
		values.put(DbConstants.Deadline.KEY_ENDTIME, deadline.getEndTime().getMillis());
		values.put(DbConstants.Deadline.KEY_ISALLDAY, Boolean.toString(deadline.isAllDay()));
		values.put(DbConstants.Deadline.KEY_LOCATION, deadline.getLocation());
		values.put(DbConstants.Deadline.KEY_NOTES, deadline.getNotes());

		return database_.insert(DbConstants.Deadline.DATABASE_TABLE, null, values);
	}

	// READ
	// Összes Location lekérése
	public Cursor getAllDeadlines() {
		// Cursor minden rekordra (where = null)
		return database_.query(DbConstants.Deadline.DATABASE_TABLE, new String[] {
				DbConstants.Deadline.KEY_ROWID, DbConstants.Deadline.KEY_NAME,
				DbConstants.Deadline.KEY_STARTTIME, DbConstants.Deadline.KEY_ENDTIME,
				DbConstants.Deadline.KEY_ISALLDAY, DbConstants.Deadline.KEY_LOCATION,
				DbConstants.Deadline.KEY_NOTES }, null, null, null, null,
				DbConstants.Deadline.KEY_NAME);
	}

	public Cursor getDeadlineByFilter(String filter) {
		String value = "%" + filter.toString() + "%";
		Cursor cursor = database_.query(DbConstants.Deadline.DATABASE_TABLE, new String[] {
				DbConstants.Deadline.KEY_ROWID, DbConstants.Deadline.KEY_NAME,
				DbConstants.Deadline.KEY_STARTTIME, DbConstants.Deadline.KEY_ENDTIME,
				DbConstants.Deadline.KEY_ISALLDAY, DbConstants.Deadline.KEY_LOCATION,
				DbConstants.Deadline.KEY_NOTES }, DbConstants.Deadline.KEY_NAME + " LIKE ?",
				new String[] { value }, null, null, DbConstants.Deadline.KEY_NAME);
		// Ha van rekord amire a Cursor mutat
		if (cursor.moveToFirst())
			return cursor;
		// Egyébként null-al térünk vissza
		return null;
	}

	// Egy Location lekérése
	public Deadline getDeadline(long id) {
		// A Deadline-ra mutató cursor
		Cursor cursor = database_.query(DbConstants.Deadline.DATABASE_TABLE, new String[] {
				DbConstants.Deadline.KEY_ROWID, DbConstants.Deadline.KEY_NAME,
				DbConstants.Deadline.KEY_STARTTIME, DbConstants.Deadline.KEY_ENDTIME,
				DbConstants.Deadline.KEY_ISALLDAY, DbConstants.Deadline.KEY_LOCATION,
				DbConstants.Deadline.KEY_NOTES }, DbConstants.Deadline.KEY_ROWID + "=" + id, null,
				null, null, DbConstants.Deadline.KEY_NAME);
		// Ha van rekord amire a Cursor mutat
		if (cursor.moveToFirst())
			return getDeadlineByCursor(cursor);
		// Egyébként null-al térünk vissza
		return null;
	}

	public static Deadline getDeadlineByCursor(Cursor c) {
		return new Deadline(
				c.getLong(c.getColumnIndex(DbConstants.Deadline.KEY_ROWID)), // id
				c.getString(c.getColumnIndex(DbConstants.Deadline.KEY_NAME)), // name
				new DateTime((long) c.getInt(c.getColumnIndex(DbConstants.Deadline.KEY_STARTTIME))), // startTime
				new DateTime((long) c.getInt(c.getColumnIndex(DbConstants.Deadline.KEY_ENDTIME))), // endTime
				Boolean.parseBoolean(c.getString(c
						.getColumnIndex(DbConstants.Deadline.KEY_ISALLDAY))), // isAllDay
				c.getString(c.getColumnIndex(DbConstants.Deadline.KEY_LOCATION)), // location
				c.getString(c.getColumnIndex(DbConstants.Deadline.KEY_NOTES)) // notes
		);
	}

	// UPDATE
	public boolean editDeadline(long id, Deadline deadline) {
		ContentValues values = new ContentValues();

		values.put(DbConstants.Deadline.KEY_NAME, deadline.getName());
		values.put(DbConstants.Deadline.KEY_STARTTIME, deadline.getStartTime().getMillis());
		values.put(DbConstants.Deadline.KEY_ENDTIME, deadline.getEndTime().getMillis());
		values.put(DbConstants.Deadline.KEY_ISALLDAY, Boolean.toString(deadline.isAllDay()));
		values.put(DbConstants.Deadline.KEY_LOCATION, deadline.getLocation());
		values.put(DbConstants.Deadline.KEY_NOTES, deadline.getNotes());

		return database_.update(DbConstants.Deadline.DATABASE_TABLE, values,
				DbConstants.Deadline.KEY_ROWID + "=" + id, null) > 0;
	}

	// DELETE
	public boolean removeDeadline(long id) {
		return database_.delete(DbConstants.Deadline.DATABASE_TABLE, DbConstants.Deadline.KEY_ROWID
				+ "=" + id, null) > 0;
	}

	// Equipment
	// CREATE
	public long addEquipment(Equipment equipment) {
		ContentValues values = new ContentValues();

		values.put(DbConstants.Equipment.KEY_NAME, equipment.getName());
		values.put(DbConstants.Equipment.KEY_CATEGORY, equipment.getCategory());
		values.put(DbConstants.Equipment.KEY_NOTES, equipment.getNotes());
		values.put(DbConstants.Equipment.KEY_LENTTO, equipment.getLentTo());

		return database_.insert(DbConstants.Equipment.DATABASE_TABLE, null, values);
	}

	// READ
	// Összes Location lekérése
	public Cursor getAllEquipment() {
		// Cursor minden rekordra (where = null)
		return database_.query(DbConstants.Equipment.DATABASE_TABLE, new String[] {
				DbConstants.Equipment.KEY_ROWID, DbConstants.Equipment.KEY_NAME,
				DbConstants.Equipment.KEY_CATEGORY, DbConstants.Equipment.KEY_NOTES,
				DbConstants.Equipment.KEY_LENTTO }, null, null, null, null,
				DbConstants.Equipment.KEY_NAME);
	}

	public Cursor getEquipmentByFilter(String filter) {
		String value = "%" + filter.toString() + "%";
		Cursor cursor = database_.query(DbConstants.Equipment.DATABASE_TABLE, new String[] {
				DbConstants.Equipment.KEY_ROWID, DbConstants.Equipment.KEY_NAME,
				DbConstants.Equipment.KEY_CATEGORY, DbConstants.Equipment.KEY_NOTES,
				DbConstants.Equipment.KEY_LENTTO }, DbConstants.Equipment.KEY_NAME + " LIKE ?",
				new String[] { value }, null, null, DbConstants.Equipment.KEY_NAME);
		// Ha van rekord amire a Cursor mutat
		if (cursor.moveToFirst())
			return cursor;
		// Egyébként null-al térünk vissza
		return null;
	}

	// Egy Location lekérése
	public Equipment getEquipment(long id) {
		// Az Equipment-re mutató cursor
		Cursor cursor = database_.query(DbConstants.Equipment.DATABASE_TABLE, new String[] {
				DbConstants.Equipment.KEY_ROWID, DbConstants.Equipment.KEY_NAME,
				DbConstants.Equipment.KEY_CATEGORY, DbConstants.Equipment.KEY_NOTES,
				DbConstants.Equipment.KEY_LENTTO }, DbConstants.Equipment.KEY_ROWID + "=" + id,
				null, null, null, DbConstants.Equipment.KEY_NAME);
		// Ha van rekord amire a Cursor mutat
		if (cursor.moveToFirst())
			return getEquipmentByCursor(cursor);
		// Egyébként null-al térünk vissza
		return null;
	}

	public static Equipment getEquipmentByCursor(Cursor c) {
		return new Equipment(c.getLong(c.getColumnIndex(DbConstants.Equipment.KEY_ROWID)), // id
				c.getString(c.getColumnIndex(DbConstants.Equipment.KEY_NAME)), // name
				c.getString(c.getColumnIndex(DbConstants.Equipment.KEY_CATEGORY)), // category
				c.getString(c.getColumnIndex(DbConstants.Equipment.KEY_NOTES)), // notes
				(long) c.getInt(c.getColumnIndex(DbConstants.Equipment.KEY_LENTTO)) // lentTo
		);
	}

	// UPDATE
	public boolean editEquipment(long id, Equipment equipment) {
		ContentValues values = new ContentValues();

		values.put(DbConstants.Equipment.KEY_NAME, equipment.getName());
		values.put(DbConstants.Equipment.KEY_CATEGORY, equipment.getCategory());
		values.put(DbConstants.Equipment.KEY_NOTES, equipment.getNotes());
		values.put(DbConstants.Equipment.KEY_LENTTO, equipment.getLentTo());

		return database_.update(DbConstants.Equipment.DATABASE_TABLE, values,
				DbConstants.Equipment.KEY_ROWID + "=" + id, null) > 0;
	}

	// DELETE
	public boolean removeEquipment(long id) {
		return database_.delete(DbConstants.Equipment.DATABASE_TABLE, DbConstants.Equipment.KEY_ROWID
				+ "=" + id, null) > 0;
	}

	// Friend
}
