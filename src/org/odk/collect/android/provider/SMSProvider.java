package org.odk.collect.android.provider;

import java.util.HashMap;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.ODKSQLiteOpenHelper;
import org.odk.collect.android.provider.SMSProviderAPI.SMSColumns;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class SMSProvider extends ContentProvider {

	private static final String DATABASE_NAME = "sms.db";
	private static final int DATABASE_VERSION = 1;
	private static final String t = "SMSProvider";
	private static final String SMS_TABLE_NAME = "sms";
	
	private static HashMap<String, String> sSMSProjectionMap;

    private static final int SMS = 1;
    private static final int SMS_ID = 2;
	
	public static class DatabaseHelper extends ODKSQLiteOpenHelper {
		private static final String TEMP_SMS_TABLE_NAME = "sms_v4";
		private static final String MODEL_VERSION = "modelVersion";
		
		public DatabaseHelper(String databaseName) {
			super(Collect.METADATA_PATH, databaseName, null, DATABASE_VERSION);
		}

		 @Override
	        public void onCreate(SQLiteDatabase db) {
	        	onCreateNamed(db, SMS_TABLE_NAME);
	        }

	        private void onCreateNamed(SQLiteDatabase db, String tableName) {
	            db.execSQL("CREATE TABLE " + tableName + " ("
	            		+ SMSColumns._ID + " integer primary key autoincrement, "
	            		+ SMSColumns.INSTANCE_KEY_ID + " TEXT, "
	            		+ SMSColumns.DISPLAY_NAME + " TEXT not null, "
	                    + SMSColumns.FORM_RECEIVED + " TEXT, "
	            		+ SMSColumns.FORM_PARSED + " TEXT, "
	            		+ SMSColumns.TX_ID +" TEXT, " 
	            		+ SMSColumns.TIMESTAMP +" TEXT);");
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        	Log.w(DatabaseHelper.class.getName(),
	              "Upgrading database from version " + oldVersion + " to "
	                  + newVersion + ", which will destroy all old data");
	         	db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE_NAME);
	         	onCreate(db);
	        }
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private DatabaseHelper mDbHelper;

	@Override
	public boolean onCreate() {
		Collect.createODKDirs();

        mDbHelper = new DatabaseHelper(DATABASE_NAME);
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SMS_TABLE_NAME);

        // Get the database and run the query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
