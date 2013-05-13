package hu.bute.gb.onlab.PhotoTools.datastorage;

import hu.bute.gb.onlab.PhotoTools.entities.Deadline;
import hu.bute.gb.onlab.PhotoTools.entities.Equipment;
import hu.bute.gb.onlab.PhotoTools.entities.Friend;
import hu.bute.gb.onlab.PhotoTools.entities.Location;
import hu.bute.gb.onlab.PhotoTools.helpers.Coordinate;
import hu.bute.gb.onlab.PhotoTools.helpers.DeadlineDay;

import java.util.ArrayList;
import java.util.Collections;

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
		String value = "%" + filter + "%";
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

	public Cursor getDeadlinesBetweenDays(long datesAfter, long datesBefore) {
		Cursor cursor = database_.query(DbConstants.Deadline.DATABASE_TABLE, new String[] {
				DbConstants.Deadline.KEY_ROWID, DbConstants.Deadline.KEY_NAME,
				DbConstants.Deadline.KEY_STARTTIME, DbConstants.Deadline.KEY_ENDTIME,
				DbConstants.Deadline.KEY_ISALLDAY, DbConstants.Deadline.KEY_LOCATION,
				DbConstants.Deadline.KEY_NOTES }, DbConstants.Deadline.KEY_STARTTIME + " >= ? AND "
				+ DbConstants.Deadline.KEY_STARTTIME + " <= ?",
				new String[] { Long.toString(datesAfter), Long.toString(datesBefore) }, null, null,
				DbConstants.Deadline.KEY_STARTTIME);
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

	public ArrayList<DeadlineDay> getUsedDates(long datesAfter, long datesBefore) {
		ArrayList<DeadlineDay> result = null;
		Cursor cursor = getDeadlinesBetweenDays(datesAfter, datesBefore);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (result == null) {
					result = new ArrayList<DeadlineDay>();
				}
				DeadlineDay day = new DeadlineDay(new DateTime(cursor.getLong(cursor
						.getColumnIndex(DbConstants.Deadline.KEY_STARTTIME))));
				if (!result.contains(day)) {
					result.add(day);
				}
			}
			Collections.sort(result);
			cursor.close();
		}
		return result;
	}

	public static Deadline getDeadlineByCursor(Cursor c) {
		return new Deadline(c.getLong(c.getColumnIndex(DbConstants.Deadline.KEY_ROWID)), // id
				c.getString(c.getColumnIndex(DbConstants.Deadline.KEY_NAME)), // name
				new DateTime(c.getLong(c.getColumnIndex(DbConstants.Deadline.KEY_STARTTIME))), // startTime
				new DateTime(c.getLong(c.getColumnIndex(DbConstants.Deadline.KEY_ENDTIME))), // endTime
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
	// Összes Equipment lekérése
	public Cursor getAllEquipment() {
		// Cursor minden rekordra (where = null)
		return database_.query(DbConstants.Equipment.DATABASE_TABLE, new String[] {
				DbConstants.Equipment.KEY_ROWID, DbConstants.Equipment.KEY_NAME,
				DbConstants.Equipment.KEY_CATEGORY, DbConstants.Equipment.KEY_NOTES,
				DbConstants.Equipment.KEY_LENTTO }, null, null, null, null,
				DbConstants.Equipment.KEY_NAME);
	}

	public Cursor getEquipmentByCategory(String category) {
		String value = "%" + category + "%";
		return database_.query(DbConstants.Equipment.DATABASE_TABLE, new String[] {
				DbConstants.Equipment.KEY_ROWID, DbConstants.Equipment.KEY_NAME,
				DbConstants.Equipment.KEY_CATEGORY, DbConstants.Equipment.KEY_NOTES,
				DbConstants.Equipment.KEY_LENTTO }, DbConstants.Equipment.KEY_CATEGORY + " LIKE ?",
				new String[] { value }, null, null, DbConstants.Equipment.KEY_NAME);
	}

	public Cursor getEquipmentByCategoryAndFilter(String category, String filter) {
		String categoryValue = "%" + category + "%";
		String filterValue = "%" + filter + "%";
		return database_.query(DbConstants.Equipment.DATABASE_TABLE, new String[] {
				DbConstants.Equipment.KEY_ROWID, DbConstants.Equipment.KEY_NAME,
				DbConstants.Equipment.KEY_CATEGORY, DbConstants.Equipment.KEY_NOTES,
				DbConstants.Equipment.KEY_LENTTO }, DbConstants.Equipment.KEY_CATEGORY
				+ " LIKE ? AND " + DbConstants.Equipment.KEY_NAME + " LIKE ?", new String[] {
				categoryValue, filterValue }, null, null, DbConstants.Equipment.KEY_NAME);
	}

	public Cursor getEquipmentLentTo(long friendId) {
		return database_.query(DbConstants.Equipment.DATABASE_TABLE, new String[] {
				DbConstants.Equipment.KEY_ROWID, DbConstants.Equipment.KEY_NAME,
				DbConstants.Equipment.KEY_CATEGORY, DbConstants.Equipment.KEY_NOTES,
				DbConstants.Equipment.KEY_LENTTO }, DbConstants.Equipment.KEY_LENTTO + " = ?",
				new String[] { Long.toString(friendId) }, null, null,
				DbConstants.Equipment.KEY_NAME);
	}

	// Egy Equipment lekérése
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

	public boolean lendEquipment(Equipment equipment, long friendId) {
		if (equipment.isLent()) {
			return false;
		}
		equipment.setLentTo(friendId);
		editEquipment(equipment.getID(), equipment);
		return true;
	}
	
	public void equipmentBack(Equipment equipment){
		equipment.setLentTo(0);
		editEquipment(equipment.getID(), equipment);
	}

	// DELETE
	public boolean removeEquipment(long id) {
		return database_.delete(DbConstants.Equipment.DATABASE_TABLE,
				DbConstants.Equipment.KEY_ROWID + "=" + id, null) > 0;
	}

	// Friend
	// CREATE
	public long addFriend(Friend friend) {
		ContentValues values = new ContentValues();

		values.put(DbConstants.Friend.KEY_FIRSTCHAR, friend.getFirstName().substring(0, 1));
		values.put(DbConstants.Friend.KEY_FULLNAME,
				friend.getFirstName() + " " + friend.getLastName());
		values.put(DbConstants.Friend.KEY_FIRSTNAME, friend.getFirstName());
		values.put(DbConstants.Friend.KEY_LASTNAME, friend.getLastName());
		values.put(DbConstants.Friend.KEY_PHONENUMBER, friend.getPhoneNumber());
		values.put(DbConstants.Friend.KEY_EMAILADDRESS, friend.getEmailAddress());
		values.put(DbConstants.Friend.KEY_ADDRESS, friend.getAddress());
		values.put(DbConstants.Friend.KEY_HASLENT, Boolean.toString(friend.hasLentItems()));

		return database_.insert(DbConstants.Friend.DATABASE_TABLE, null, values);
	}

	// READ
	// Összes Equipment lekérése
	public Cursor getAllFriends() {
		// Cursor minden rekordra (where = null)
		return database_.query(DbConstants.Friend.DATABASE_TABLE, new String[] {
				DbConstants.Friend.KEY_ROWID, DbConstants.Friend.KEY_FULLNAME,
				DbConstants.Friend.KEY_FIRSTNAME, DbConstants.Friend.KEY_LASTNAME,
				DbConstants.Friend.KEY_PHONENUMBER, DbConstants.Friend.KEY_EMAILADDRESS,
				DbConstants.Friend.KEY_ADDRESS, DbConstants.Friend.KEY_HASLENT }, null, null, null,
				null, DbConstants.Friend.KEY_FIRSTNAME);
	}

	public Cursor getFriendsLentTo() {
		String lentValue = "%true%";
		return database_.query(DbConstants.Friend.DATABASE_TABLE, new String[] {
				DbConstants.Friend.KEY_ROWID, DbConstants.Friend.KEY_FULLNAME,
				DbConstants.Friend.KEY_FIRSTNAME, DbConstants.Friend.KEY_LASTNAME,
				DbConstants.Friend.KEY_PHONENUMBER, DbConstants.Friend.KEY_EMAILADDRESS,
				DbConstants.Friend.KEY_ADDRESS, DbConstants.Friend.KEY_HASLENT },
				DbConstants.Friend.KEY_HASLENT + " LIKE ?", new String[] { lentValue }, null, null,
				DbConstants.Friend.KEY_FULLNAME);
	}

	public Cursor getAllFriendsByCategory(String category) {
		String value = "%" + category + "%";
		return database_.query(DbConstants.Friend.DATABASE_TABLE, new String[] {
				DbConstants.Friend.KEY_ROWID, DbConstants.Friend.KEY_FULLNAME,
				DbConstants.Friend.KEY_FIRSTNAME, DbConstants.Friend.KEY_LASTNAME,
				DbConstants.Friend.KEY_PHONENUMBER, DbConstants.Friend.KEY_EMAILADDRESS,
				DbConstants.Friend.KEY_ADDRESS, DbConstants.Friend.KEY_HASLENT },
				DbConstants.Friend.KEY_FIRSTCHAR + " LIKE ?", new String[] { value }, null, null,
				DbConstants.Friend.KEY_FULLNAME);
	}

	public Cursor getAllFriendsByCategoryAndFilter(String category, String filter) {
		String categoryValue = "%" + category + "%";
		String filterValue = "%" + filter + "%";
		return database_.query(DbConstants.Friend.DATABASE_TABLE, new String[] {
				DbConstants.Friend.KEY_ROWID, DbConstants.Friend.KEY_FULLNAME,
				DbConstants.Friend.KEY_FIRSTNAME, DbConstants.Friend.KEY_LASTNAME,
				DbConstants.Friend.KEY_PHONENUMBER, DbConstants.Friend.KEY_EMAILADDRESS,
				DbConstants.Friend.KEY_ADDRESS, DbConstants.Friend.KEY_HASLENT },
				DbConstants.Friend.KEY_FIRSTCHAR + " LIKE ? AND " + DbConstants.Friend.KEY_FULLNAME
						+ " LIKE ?", new String[] { categoryValue, filterValue }, null, null,
				DbConstants.Friend.KEY_FULLNAME);
	}

	public Cursor getFriendsLentToByCategory(String category) {
		String categoryValue = "%" + category + "%";
		String lentValue = "%true%";
		return database_.query(DbConstants.Friend.DATABASE_TABLE, new String[] {
				DbConstants.Friend.KEY_ROWID, DbConstants.Friend.KEY_FULLNAME,
				DbConstants.Friend.KEY_FIRSTNAME, DbConstants.Friend.KEY_LASTNAME,
				DbConstants.Friend.KEY_PHONENUMBER, DbConstants.Friend.KEY_EMAILADDRESS,
				DbConstants.Friend.KEY_ADDRESS, DbConstants.Friend.KEY_HASLENT },
				DbConstants.Friend.KEY_FIRSTCHAR + " LIKE ? AND " + DbConstants.Friend.KEY_HASLENT
						+ " LIKE ?", new String[] { categoryValue, lentValue }, null, null,
				DbConstants.Friend.KEY_FULLNAME);
	}

	public Cursor getFriendsLentToByCategoryAndFilter(String category, String filter) {
		String categoryValue = "%" + category + "%";
		String lentValue = "%true%";
		String filterValue = "%" + filter + "%";
		return database_.query(DbConstants.Friend.DATABASE_TABLE, new String[] {
				DbConstants.Friend.KEY_ROWID, DbConstants.Friend.KEY_FULLNAME,
				DbConstants.Friend.KEY_FIRSTNAME, DbConstants.Friend.KEY_LASTNAME,
				DbConstants.Friend.KEY_PHONENUMBER, DbConstants.Friend.KEY_EMAILADDRESS,
				DbConstants.Friend.KEY_ADDRESS, DbConstants.Friend.KEY_HASLENT },
				DbConstants.Friend.KEY_FIRSTCHAR + " LIKE ? AND " + DbConstants.Friend.KEY_HASLENT
						+ " LIKE ? AND " + DbConstants.Friend.KEY_FULLNAME + " LIKE ?",
				new String[] { categoryValue, lentValue, filterValue }, null, null,
				DbConstants.Friend.KEY_FULLNAME);
	}

	public ArrayList<String> getUsedCharacters(boolean isAll) {
		ArrayList<String> result = null;
		Cursor cursor = null;
		if (isAll) {
			cursor = getAllFriends();
		}
		else {
			cursor = getFriendsLentTo();
		}
		while (cursor.moveToNext()) {
			if (result == null) {
				result = new ArrayList<String>();
			}
			String first = cursor
					.getString(cursor.getColumnIndex(DbConstants.Friend.KEY_FIRSTNAME)).substring(
							0, 1).toUpperCase();
			if (!result.contains(first)) {
				result.add(first);
			}
		}
		cursor.close();
		return result;
	}

	// Egy Friend lekérése
	public Friend getFriend(long id) {
		// Az Equipment-re mutató cursor
		Cursor cursor = database_.query(DbConstants.Friend.DATABASE_TABLE, new String[] {
				DbConstants.Friend.KEY_ROWID, DbConstants.Friend.KEY_FULLNAME,
				DbConstants.Friend.KEY_FIRSTNAME, DbConstants.Friend.KEY_LASTNAME,
				DbConstants.Friend.KEY_PHONENUMBER, DbConstants.Friend.KEY_EMAILADDRESS,
				DbConstants.Friend.KEY_ADDRESS, DbConstants.Friend.KEY_HASLENT },
				DbConstants.Friend.KEY_ROWID + "=" + id, null, null, null,
				DbConstants.Friend.KEY_FULLNAME);
		// Ha van rekord amire a Cursor mutat
		if (cursor.moveToFirst())
			return getFriendByCursor(cursor);
		// Egyébként null-al térünk vissza
		return null;
	}

	public static Friend getFriendByCursor(Cursor c) {
		Friend result = new Friend(c.getLong(c.getColumnIndex(DbConstants.Friend.KEY_ROWID)), // id
				c.getString(c.getColumnIndex(DbConstants.Friend.KEY_FIRSTNAME)), // firstName
				c.getString(c.getColumnIndex(DbConstants.Friend.KEY_LASTNAME)), // lastName
				c.getString(c.getColumnIndex(DbConstants.Friend.KEY_PHONENUMBER)), // phoneNumber
				c.getString(c.getColumnIndex(DbConstants.Friend.KEY_EMAILADDRESS)), // emailAddress
				c.getString(c.getColumnIndex(DbConstants.Friend.KEY_ADDRESS)), // address
				null, Boolean.parseBoolean(c.getString(c
						.getColumnIndex(DbConstants.Friend.KEY_HASLENT)))); // lent
		return result;
	}

	// UPDATE
	public boolean editFriend(long id, Friend friend) {
		ContentValues values = new ContentValues();

		values.put(DbConstants.Friend.KEY_FIRSTCHAR, friend.getFirstName().substring(0, 1));
		values.put(DbConstants.Friend.KEY_FULLNAME,
				friend.getFirstName() + " " + friend.getLastName());
		values.put(DbConstants.Friend.KEY_FIRSTNAME, friend.getFirstName());
		values.put(DbConstants.Friend.KEY_LASTNAME, friend.getLastName());
		values.put(DbConstants.Friend.KEY_PHONENUMBER, friend.getPhoneNumber());
		values.put(DbConstants.Friend.KEY_EMAILADDRESS, friend.getEmailAddress());
		values.put(DbConstants.Friend.KEY_ADDRESS, friend.getAddress());
		values.put(DbConstants.Friend.KEY_HASLENT, Boolean.toString(friend.hasLentItems()));

		return database_.update(DbConstants.Friend.DATABASE_TABLE, values,
				DbConstants.Friend.KEY_ROWID + "=" + id, null) > 0;
	}

	// DELETE
	public boolean removeFriend(long id) {
		return database_.delete(DbConstants.Friend.DATABASE_TABLE, DbConstants.Friend.KEY_ROWID
				+ "=" + id, null) > 0;
	}
}
