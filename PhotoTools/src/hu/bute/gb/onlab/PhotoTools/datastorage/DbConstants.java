package hu.bute.gb.onlab.PhotoTools.datastorage;

public class DbConstants {

	// Broadcast Action, amely az adatbazis modosulasat jelzi
	public static final String ACTION_DATABASE_CHANGED = "hu.bute.gb.onlab.PhotoTools.DATABASE_CHANGED";

	// Adatbázist tartalmazó fájl
	public static final String DATABASE_NAME = "database.db";
	// Verziószám
	public static final int DATABASE_VERSION = 1;

	// Összes belsõ osztály DATABASE_CREATE szkriptje összefûzve
	public static String DATABASE_CREATE_ALL = Location.DATABASE_CREATE + Deadline.DATABASE_CREATE
			+ Equipment.DATABASE_CREATE + Friend.DATABASE_CREATE;

	// Összes belsõ osztály DATABASE_DROP szkriptje összefûzve
	public static String DATABASE_DROP_ALL = Location.DATABASE_DROP + Deadline.DATABASE_DROP
			+ Equipment.DATABASE_DROP + Friend.DATABASE_DROP;

	// Entityk konstansai külön osztályokban
	// Location
	public static class Location {

		// Tábla neve
		public static final String DATABASE_TABLE = "location";

		// Oszlopnevek
		public static final String KEY_ROWID = "_id";
		public static final String KEY_NAME = "name";
		public static final String KEY_ADDRESS = "address";
		public static final String KEY_LATITUDE = "latitude";
		public static final String KEY_LONGITUDE = "longitude";
		public static final String KEY_CARENTRY = "carentry";
		public static final String KEY_POWERSOURCE = "powersource";
		public static final String KEY_NOTES = "notes";

		// Séma létrehozó szkript
		public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE
				+ " ( " + KEY_ROWID + " integer primary key autoincrement, " + KEY_NAME
				+ " text not null, " + KEY_ADDRESS + " text, " + KEY_LATITUDE + " text, "
				+ KEY_LONGITUDE + " text, " + KEY_CARENTRY + " text, " + KEY_POWERSOURCE
				+ " text, " + KEY_NOTES + " text" + "); ";

		// Séma törlõ szktipt
		public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE + "; ";
	}

	// Deadline
	public static class Deadline {

		// Tábla neve
		public static final String DATABASE_TABLE = "deadline";

		// Oszlopnevek
		public static final String KEY_ROWID = "_id";
		public static final String KEY_NAME = "name";
		public static final String KEY_STARTTIME = "startTime";
		public static final String KEY_ENDTIME = "endTime";
		public static final String KEY_ISALLDAY = "isAllDay";
		public static final String KEY_LOCATION = "location";
		public static final String KEY_NOTES = "notes";

		// Séma létrehozó szkript
		public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE
				+ " ( " + KEY_ROWID + " integer primary key autoincrement, " + KEY_NAME
				+ " text not null, " + KEY_STARTTIME + " integer, " + KEY_ENDTIME + " integer, "
				+ KEY_ISALLDAY + " text, " + KEY_LOCATION + " text, " + KEY_NOTES + " text" + "); ";

		// Séma törlõ szktipt
		public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE + "; ";
	}

	// Equipment
	public static class Equipment {

		// Tábla neve
		public static final String DATABASE_TABLE = "equipment";

		// Oszlopnevek
		public static final String KEY_ROWID = "_id";
		public static final String KEY_NAME = "name";
		public static final String KEY_CATEGORY = "category";
		public static final String KEY_NOTES = "notes";
		public static final String KEY_LENTTO = "lentTo";

		// Séma létrehozó szkript
		public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE
				+ " ( " + KEY_ROWID + " integer primary key autoincrement, " + KEY_NAME
				+ " text not null, " + KEY_CATEGORY + " text, " + KEY_NOTES + " text, "
				+ KEY_LENTTO + " integer" + "); ";

		// Séma törlõ szktipt
		public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE + "; ";

	}

	// Friend
	public static class Friend {

		// Tábla neve
		public static final String DATABASE_TABLE = "friend";

		// Oszlopnevek
		public static final String KEY_ROWID = "_id";
		public static final String KEY_FULLNAME = "fullName";
		public static final String KEY_FIRSTNAME = "firstName";
		public static final String KEY_LASTNAME = "lastName";
		public static final String KEY_PHONENUMBER = "phoneNumber";
		public static final String KEY_EMAILADDRESS = "emailAddress";
		public static final String KEY_ADDRESS = "address";
		public static final String KEY_HASLENT = "hasLent";

		// Séma létrehozó szkript
		public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE
				+ " ( " + KEY_ROWID + " integer primary key autoincrement, "
				+ KEY_FULLNAME + " text not null, " + KEY_FIRSTNAME + " text not null, "
				+ KEY_LASTNAME + " text, " + KEY_PHONENUMBER + " text, " + KEY_EMAILADDRESS
				+ " text, " + KEY_ADDRESS + " text, " + KEY_HASLENT + " text" + "); ";

		// Séma törlõ szktipt
		public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE + "; ";

	}

}
