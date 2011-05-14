// The DealerDataAdapter class encapsulates all database interactions for the
// game.  It also handles versioning of the data.  The version is stored with
// each stored game state, so if a version is found that is not current, it
// will be dropped.  It does not do any serialization/deserialization itself.
// It only stores a single saved game string, the current saved game.
//
// author: joe@daverin.com

package com.daverin.dopewars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DealerDataAdapter {
	public static final String KEY_GAME_ID = "_id";
	public static final String KEY_DEALER_GAME_INFO = "game_info";
	
    private static final String TAG = "DealerDataAdapter";
    
    private static final String DATABASE_NAME = "dopewars";
    private static final String GAME_INFO_TABLE = "dealer_info";
    
    private static final int DATABASE_VERSION = 2;
    
    private static final String CREATE_GAME_INFO_TABLE =
    	"create table " + GAME_INFO_TABLE + " (" +
    	KEY_GAME_ID +              " integer primary key autoincrement, " +
    	KEY_DEALER_GAME_INFO + " text not null);";
    
    private final Context context;
    
    private DealerDatabaseHelper dealerDBHelper;
	private SQLiteDatabase db;
	
	private boolean game_info_table_initialized_;

	// When a database adapter is first created none of the tables have been checked.
	public DealerDataAdapter(Context ctx) {
		this.context = ctx;
		dealerDBHelper = new DealerDatabaseHelper(context);
		game_info_table_initialized_ = false;
	}
	
	// This inner class primarily creates tables if they don't exist and handles version
	// number checked when the database schema is changed.
	private static class DealerDatabaseHelper extends SQLiteOpenHelper {
		DealerDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_GAME_INFO_TABLE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + GAME_INFO_TABLE);
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
	
	public void initializeGameData() {
		if (game_info_table_initialized_) return;
		
		Cursor cursor = db.query(true, GAME_INFO_TABLE,
				new String[] {KEY_GAME_ID},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				game_info_table_initialized_ = true;
				return;
			}
		}
		db.delete(GAME_INFO_TABLE, null, null);
		ContentValues initial_dealer_info = new ContentValues();
		initial_dealer_info.put(KEY_DEALER_GAME_INFO, "");
		db.insert(GAME_INFO_TABLE, null, initial_dealer_info);
	}
	
	public String getGameString() {
		initializeGameData();
		Cursor cursor = db.query(true, GAME_INFO_TABLE,
				new String[] {KEY_DEALER_GAME_INFO}, null, null, null, null,
				null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(0);
			}
		}
		return "";
	}

	public void setGameString(String serialized_game_info) {
		initializeGameData();
		ContentValues args = new ContentValues();
		args.put(KEY_DEALER_GAME_INFO, serialized_game_info);
		db.update(GAME_INFO_TABLE, args, null, null);
	}
}
