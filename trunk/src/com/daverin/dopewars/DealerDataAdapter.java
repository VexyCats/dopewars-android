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
	public static final String KEY_DEALER_NAME = "dealer_name";
	public static final String KEY_DEALER_AVATAR_NAME = "avatar_name";
	public static final String KEY_DEALER_GAME_INFO = "game_info";
	public static final String KEY_DEALER_GAME_ID = "game_id";
	
	public static final String KEY_GAME_STRINGS_ID = "_id";
	public static final String KEY_GAME_STRINGS_VALUES = "string_values";
	
    private static final String TAG = "DealerDataAdapter";
    
    private static final String DATABASE_NAME = "dopewars";
    private static final String DEALER_INFO_TABLE = "dealer_info";
    private static final String GAME_STRINGS_TABLE = "game_strings";
    
    private static final int DATABASE_VERSION = 5;
    
    private static final String CREATE_DEALER_INFO_TABLE =
    	"create table " + DEALER_INFO_TABLE + " (" +
    	KEY_DEALER_ID + " integer primary key autoincrement, " +
    	KEY_DEALER_NAME + " text not null, " +
    	KEY_DEALER_AVATAR_NAME + " text not null, " +
    	KEY_DEALER_GAME_INFO + " text not null, " +
    	KEY_DEALER_GAME_ID + " text not null);";
    
    private static final String CREATE_GAME_STRINGS_TABLE =
    	"create table " + GAME_STRINGS_TABLE + " (" +
    	KEY_GAME_STRINGS_ID + " integer primary key, " +
    	KEY_GAME_STRINGS_VALUES + " text not null);";
    
    private final Context context;
    
    private DealerDatabaseHelper dealerDBHelper;
	private SQLiteDatabase db;
	
	private boolean dealer_table_checked_;
	private boolean game_table_checked_;
	
	/**
	 * ========================================================================
	 * Database-level
	 * ========================================================================
	 */
	
	public DealerDataAdapter(Context ctx) {
		this.context = ctx;
		dealerDBHelper = new DealerDatabaseHelper(context);
		dealer_table_checked_ = false;
		game_table_checked_ = false;
	}
	
	private static class DealerDatabaseHelper extends SQLiteOpenHelper {
		DealerDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_DEALER_INFO_TABLE);
			db.execSQL(CREATE_GAME_STRINGS_TABLE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DEALER_INFO_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + GAME_STRINGS_TABLE);
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
	
	public void initDealerInfo() {
		if (dealer_table_checked_) return;
		
		Cursor cursor = db.query(true, DEALER_INFO_TABLE,
				new String[] {KEY_DEALER_ID},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				dealer_table_checked_ = true;
				return;
			}
		}
		db.delete(DEALER_INFO_TABLE, null, null);
		ContentValues initial_dealer_info = new ContentValues();
		initial_dealer_info.put(KEY_DEALER_NAME, "");
		initial_dealer_info.put(KEY_DEALER_AVATAR_NAME, "");
		initial_dealer_info.put(KEY_DEALER_GAME_INFO, "");
		initial_dealer_info.put(KEY_DEALER_GAME_ID, "0");
		db.insert(DEALER_INFO_TABLE, null, initial_dealer_info);
	}
	
	public String getDealerString(String key) {
		initDealerInfo();
		Cursor cursor = db.query(true, DEALER_INFO_TABLE,
				new String[] {key}, null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(0);
			}
		}
		return "";
	}
	public void setDealerString(String key, String new_value) {
		initDealerInfo();
		ContentValues args = new ContentValues();
		args.put(key, new_value);
		db.update(DEALER_INFO_TABLE, args, null, null);
	}
	
	/**
	 * Game strings table
	 */
	
	public void initGameInfo() {
		if (game_table_checked_) return;
		
		boolean reset_table = false;
		Cursor cursor = db.query(true, GAME_STRINGS_TABLE,
				new String[] {KEY_GAME_STRINGS_ID, KEY_GAME_STRINGS_VALUES},
				KEY_GAME_STRINGS_ID + " == 0", null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() == 0) {
				reset_table = true;
			}
		}
		cursor = db.query(true, GAME_STRINGS_TABLE,
				new String[] {KEY_GAME_STRINGS_ID, KEY_GAME_STRINGS_VALUES},
				KEY_GAME_STRINGS_ID + " == 1", null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() == 0) {
				reset_table = true;
			}
		}
		cursor = db.query(true, GAME_STRINGS_TABLE,
				new String[] {KEY_GAME_STRINGS_ID, KEY_GAME_STRINGS_VALUES},
				KEY_GAME_STRINGS_ID + " == 2", null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() == 0) {
				reset_table = true;
			}
		}
		cursor = db.query(true, GAME_STRINGS_TABLE,
				new String[] {KEY_GAME_STRINGS_ID, KEY_GAME_STRINGS_VALUES},
				KEY_GAME_STRINGS_ID + " == 3", null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() == 0) {
				reset_table = true;
			}
		}
		if (!reset_table) {
			game_table_checked_ = true;
			return;
		}
		db.delete(GAME_STRINGS_TABLE, null, null);
		ContentValues initial_game_info_1 = new ContentValues();
		initial_game_info_1.put(KEY_GAME_STRINGS_ID, 0);
		initial_game_info_1.put(KEY_GAME_STRINGS_VALUES, GameInformation.getDefaultGameString());
		db.insert(GAME_STRINGS_TABLE, null, initial_game_info_1);
		ContentValues initial_game_info_2 = new ContentValues();
		initial_game_info_2.put(KEY_GAME_STRINGS_ID, 1);
		initial_game_info_2.put(KEY_GAME_STRINGS_VALUES, GameInformation.getDefaultGameString());
		db.insert(GAME_STRINGS_TABLE, null, initial_game_info_2);
		ContentValues initial_game_info_3 = new ContentValues();
		initial_game_info_3.put(KEY_GAME_STRINGS_ID, 2);
		initial_game_info_3.put(KEY_GAME_STRINGS_VALUES, GameInformation.getDefaultGameString());
		db.insert(GAME_STRINGS_TABLE, null, initial_game_info_3);
		ContentValues initial_game_info_4 = new ContentValues();
		initial_game_info_4.put(KEY_GAME_STRINGS_ID, 3);
		initial_game_info_4.put(KEY_GAME_STRINGS_VALUES, GameInformation.getDefaultGameString());
		db.insert(GAME_STRINGS_TABLE, null, initial_game_info_4);
	}
	
	public String getGameString(int game) {
		initGameInfo();
		Cursor cursor = db.query(true, GAME_STRINGS_TABLE,
				new String[] {KEY_GAME_STRINGS_ID, KEY_GAME_STRINGS_VALUES},
				KEY_GAME_STRINGS_ID + " == " + Integer.toString(game),
				null, null, null, null, null);
		if (cursor != null) {
			int i = cursor.getCount();
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int j = cursor.getCount();
				int k = cursor.getColumnCount();
				return cursor.getString(1);
			}
		}
		return "";
	}
	public void setGameString(int game, String game_strings) {
		initGameInfo();
		// I don't know databases, get all the values and then put them back with the
		// appropriate one replaced.
		String key = "";
		Cursor cursor = db.query(true, GAME_STRINGS_TABLE,
				new String[] {KEY_GAME_STRINGS_ID, KEY_GAME_STRINGS_VALUES},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > game) {
				cursor.move(game);
				key = cursor.getString(0);
			}
		}
		ContentValues args = new ContentValues();
		args.put(KEY_GAME_STRINGS_ID, key);
		args.put(KEY_GAME_STRINGS_VALUES, game_strings);
		db.update(GAME_STRINGS_TABLE, args, null, null);
	}
}
