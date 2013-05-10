package hu.bute.gb.onlab.PhotoTools.datastorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

	public DatabaseHelper(Context context, String name) {
		super(context, name, null, DbConstants.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create all tables
		db.execSQL(DbConstants.Location.DATABASE_CREATE);
		db.execSQL(DbConstants.Deadline.DATABASE_CREATE);
		db.execSQL(DbConstants.Equipment.DATABASE_CREATE);
		db.execSQL(DbConstants.Friend.DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL(DbConstants.DATABASE_DROP_ALL);
		//db.execSQL(DbConstants.DATABASE_CREATE_ALL);
		// Drop all tables
		db.execSQL(DbConstants.Location.DATABASE_DROP);
		db.execSQL(DbConstants.Deadline.DATABASE_DROP);
		db.execSQL(DbConstants.Equipment.DATABASE_DROP);
		db.execSQL(DbConstants.Friend.DATABASE_DROP);
		
		// Create all tables
		db.execSQL(DbConstants.Location.DATABASE_CREATE);
		db.execSQL(DbConstants.Deadline.DATABASE_CREATE);
		db.execSQL(DbConstants.Equipment.DATABASE_CREATE);
		db.execSQL(DbConstants.Friend.DATABASE_CREATE);
	}

}
