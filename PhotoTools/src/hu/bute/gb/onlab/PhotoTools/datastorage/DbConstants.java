package hu.bute.gb.onlab.PhotoTools.datastorage;

public class DbConstants {

	// Broadcast Action, amely az adatbazis modosulasat jelzi
	public static final String ACTION_DATABASE_CHANGED = "hu.bute.gb.onlab.PhotoTools.DATABASE_CHANGED";

	// Adatb�zist tartalmaz� f�jl
	public static final String DATABASE_NAME = "database.db";
	// Verzi�sz�m
	public static final int DATABASE_VERSION = 1;

	// �sszes bels� oszt�ly DATABASE_CREATE szkriptje �sszef�zve
	public static String DATABASE_CREATE_ALL = Location.DATABASE_CREATE + Deadline.DATABASE_CREATE
			+ Equipment.DATABASE_CREATE + Friend.DATABASE_CREATE;

	// �sszes bels� oszt�ly DATABASE_DROP szkriptje �sszef�zve
	public static String DATABASE_DROP_ALL = Location.DATABASE_DROP + Deadline.DATABASE_DROP
			+ Equipment.DATABASE_DROP + Friend.DATABASE_DROP;

	// Entityk konstansai k�l�n oszt�lyokban
	// Location
	public static class Location {

		// T�bla neve
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

		// S�ma l�trehoz� szkript
		public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE
				+ " ( " + KEY_ROWID + " integer primary key autoincrement, " + KEY_NAME
				+ " text not null, " + KEY_ADDRESS + " text, " + KEY_LATITUDE + " text, "
				+ KEY_LONGITUDE + " text, " + KEY_CARENTRY + " text, " + KEY_POWERSOURCE
				+ " text, " + KEY_NOTES + " text" + "); ";

		// S�ma t�rl� szktipt
		public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE + "; ";
	}

	// Deadline
	public static class Deadline {

		// T�bla neve
		public static final String DATABASE_TABLE = "deadline";

		// Oszlopnevek
		public static final String KEY_ROWID = "_id";
		public static final String KEY_NAME = "name";
		public static final String KEY_STARTTIME = "startTime";
		public static final String KEY_ENDTIME = "endTime";
		public static final String KEY_ISALLDAY = "isAllDay";
		public static final String KEY_LOCATION = "location";
		public static final String KEY_NOTES = "notes";

		// S�ma l�trehoz� szkript
		public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE
				+ " ( " + KEY_ROWID + " integer primary key autoincrement, " + KEY_NAME
				+ " text not null, " + KEY_STARTTIME + " integer, " + KEY_ENDTIME + " integer, "
				+ KEY_ISALLDAY + " text, " + KEY_LOCATION + " text, " + KEY_NOTES + " text" + "); ";

		// S�ma t�rl� szktipt
		public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE + "; ";
	}

	// Equipment
	public static class Equipment {

		// T�bla neve
		public static final String DATABASE_TABLE = "equipment";

		// Oszlopnevek
		public static final String KEY_ROWID = "_id";
		public static final String KEY_NAME = "name";
		public static final String KEY_CATEGORY = "category";
		public static final String KEY_NOTES = "notes";
		public static final String KEY_LENTTO = "lentTo";

		// S�ma l�trehoz� szkript
		public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE
				+ " ( " + KEY_ROWID + " integer primary key autoincrement, " + KEY_NAME
				+ " text not null, " + KEY_CATEGORY + " text, " + KEY_NOTES + " text, "
				+ KEY_LENTTO + " integer" + "); ";

		// S�ma t�rl� szktipt
		public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE + "; ";

	}

	// Friend
	public static class Friend {

		// T�bla neve
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

		// S�ma l�trehoz� szkript
		public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE
				+ " ( " + KEY_ROWID + " integer primary key autoincrement, "
				+ KEY_FULLNAME + " text not null, " + KEY_FIRSTNAME + " text not null, "
				+ KEY_LASTNAME + " text, " + KEY_PHONENUMBER + " text, " + KEY_EMAILADDRESS
				+ " text, " + KEY_ADDRESS + " text, " + KEY_HASLENT + " text" + "); ";

		// S�ma t�rl� szktipt
		public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE + "; ";

	}

}
