package hu.bute.gb.onlab.PhotoTools.application;

import hu.bute.gb.onlab.PhotoTools.datastorage.DatabaseLoader;
import android.app.Application;

public class PhotoToolsApplication extends Application {
	private static DatabaseLoader databaseLoader;

	public static DatabaseLoader getDatabaseLoader() {
		return databaseLoader;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Open the database
		databaseLoader = new DatabaseLoader(this);
		databaseLoader.open();
	}

	@Override
	public void onTerminate() {
		// Close the internal database
		databaseLoader.close();
		super.onTerminate();
	}
}
