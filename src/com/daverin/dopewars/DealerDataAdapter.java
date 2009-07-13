package com.daverin.dopewars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DealerDataAdapter {
	public static final String KEY_DEALER_ID = "_id";
	public static final String KEY_DEALER_NAME = "name";
	public static final String KEY_DEALER_AVATAR_ID = "avatar_id";
    private static final String TAG = "DealerDataAdapter";
    
    private static final String DATABASE_NAME = "dealers";
    private static final String DEALER_TABLE = "dealer_info";
    private static final int DATABASE_VERSION = 1;
    
    private static final String DATABASE_CREATE =
    	"create table dealer_info (_id integer primary key autoincrement, " +
    	"name text not null, " +
    	"avatar_id text not null);";
    
    private final Context context;
    
    private DealerDatabaseHelper dealerDBHelper;
	private SQLiteDatabase db;
	
	public DealerDataAdapter(Context ctx) {
		this.context = ctx;
		dealerDBHelper = new DealerDatabaseHelper(context);
	}
	
	private static class DealerDatabaseHelper extends SQLiteOpenHelper {
		DealerDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS dealer_info");
			onCreate(db);
		}
	}
	
	public DealerDataAdapter open() throws SQLException {
		db = dealerDBHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		dealerDBHelper.close();
	}
	
	public Cursor getMostRecentDealer() {
		return db.query(DEALER_TABLE, new String[] {
				KEY_DEALER_ID, KEY_DEALER_NAME, KEY_DEALER_AVATAR_ID},
				null, null, null, null, null);
	}
	
	public String getMostRecentDealerName() {
		Cursor cursor = db.query(true, DEALER_TABLE, new String[] {
				KEY_DEALER_ID, KEY_DEALER_NAME, KEY_DEALER_AVATAR_ID}, null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(1);
			}
		}
		return "Guest";
	}
	
	public String getMostRecentAvatar() {
		Cursor cursor = db.query(true, DEALER_TABLE, new String[] {
				KEY_DEALER_ID, KEY_DEALER_NAME, KEY_DEALER_AVATAR_ID}, null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(2);
			}
		}
		return "0";
	}
	
	public long setMostRecentDealerInfo(String name, String avatar_id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DEALER_NAME, name);
		initialValues.put(KEY_DEALER_AVATAR_ID, avatar_id);
		return db.insert(DEALER_TABLE, null, initialValues);
	}
	
	public long cleanUpDealerInfo() {
		int rows_deleted = 0;
		Cursor cursor = db.query(true, DEALER_TABLE, new String[] {
				KEY_DEALER_ID, KEY_DEALER_NAME, KEY_DEALER_AVATAR_ID}, null, null, null, null, null, null);
		if (cursor != null) {
			int num_to_delete = cursor.getCount() - 10;
			cursor.moveToFirst();
			for (int i = 0; i < num_to_delete; ++i) {
				rows_deleted += db.delete(DEALER_TABLE, KEY_DEALER_ID + "=" + cursor.getString(0), null);
				cursor.moveToNext();
			}
		}
		return rows_deleted;
	}
}
