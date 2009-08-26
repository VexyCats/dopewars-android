package com.daverin.dopewars;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.LinearLayout;

public class DealerDataAdapter {
	public static final String KEY_DEALER_ID = "_id";
	public static final String KEY_DEALER_NAME = "dealer_name";
	public static final String KEY_DEALER_AVATAR_NAME = "avatar_name";
	public static final String KEY_DEALER_GAME_INFO = "game_info";
	public static final String KEY_DEALER_GAME_INVENTORY = "game_inventory";
	public static final String KEY_DEALER_LOCATION_INVENTORY = "location_inventory";
	
	public static final String KEY_AVAILABLE_DRUGS_ID = "_id";
	public static final String KEY_AVAILABLE_DRUGS_NAME = "drug_name";
	public static final String KEY_AVAILABLE_DRUGS_ATTRIBUTES = "drug_attributes";
	
	public static final String KEY_AVAILABLE_LOCATIONS_ID = "_id";
	public static final String KEY_AVAILABLE_LOCATIONS_NAME = "location_name";
	public static final String KEY_AVAILABLE_LOCATIONS_ATTRIBUTES = "location_attributes";
	
    private static final String TAG = "DealerDataAdapter";
    
    private static final String DATABASE_NAME = "dopewars";
    private static final String DEALER_INFO_TABLE = "dealer_info";
    private static final String AVAILABLE_DRUGS_TABLE = "available_drugs";
    private static final String AVAILABLE_LOCATIONS_TABLE = "available_locations";
    
    private static final int DATABASE_VERSION = 1;
    
    private static final String CREATE_DEALER_INFO_TABLE =
    	"create table " + DEALER_INFO_TABLE + " (" +
    	KEY_DEALER_ID + " integer primary key autoincrement, " +
    	KEY_DEALER_NAME + " text not null, " +
    	KEY_DEALER_AVATAR_NAME + " text not null, " +
    	KEY_DEALER_GAME_INFO + " text not null, " +
    	KEY_DEALER_GAME_INVENTORY + " text not null, " +
    	KEY_DEALER_LOCATION_INVENTORY + " text not null);";
    
    private static final String CREATE_AVAILABLE_DRUGS_TABLE =
    	"create table " + AVAILABLE_DRUGS_TABLE + " (" +
    	KEY_AVAILABLE_DRUGS_ID + " integer primary key autoincrement, " +
    	KEY_AVAILABLE_DRUGS_NAME + " text not null, " +
    	KEY_AVAILABLE_DRUGS_ATTRIBUTES + " text not null);";
    
    private static final String CREATE_AVAILABLE_LOCATIONS_TABLE =
    	"create table " + AVAILABLE_LOCATIONS_TABLE + " (" +
    	KEY_AVAILABLE_LOCATIONS_ID + " integer primary key autoincrement, " +
    	KEY_AVAILABLE_LOCATIONS_NAME + " text not null, " +
    	KEY_AVAILABLE_LOCATIONS_ATTRIBUTES + " text not null);";
    
    private final Context context;
    
    private DealerDatabaseHelper dealerDBHelper;
	private SQLiteDatabase db;
	
	private boolean dealer_table_checked_;
	
	/**
	 * ========================================================================
	 * Database-level
	 * ========================================================================
	 */
	
	public DealerDataAdapter(Context ctx) {
		this.context = ctx;
		dealerDBHelper = new DealerDatabaseHelper(context);
		dealer_table_checked_ = false;
	}
	
	private static class DealerDatabaseHelper extends SQLiteOpenHelper {
		DealerDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_DEALER_INFO_TABLE);
			db.execSQL(CREATE_AVAILABLE_DRUGS_TABLE);
			db.execSQL(CREATE_AVAILABLE_LOCATIONS_TABLE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DEALER_INFO_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + AVAILABLE_DRUGS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + AVAILABLE_LOCATIONS_TABLE);
			onCreate(db);
		}
	}
	
	public DealerDataAdapter open() throws SQLException {
		db = dealerDBHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		dealerDBHelper.close();
		db.close();
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
		initial_dealer_info.put(KEY_DEALER_GAME_INFO, "");;
		initial_dealer_info.put(KEY_DEALER_GAME_INVENTORY, "");
		initial_dealer_info.put(KEY_DEALER_LOCATION_INVENTORY, "");
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
	 * ========================================================================
	 * Game available drugs table
	 * ========================================================================
	 */
	public void addDrug(String name, String attributes) {
		ContentValues args = new ContentValues();
		args.put(KEY_AVAILABLE_DRUGS_NAME, name);
		args.put(KEY_AVAILABLE_DRUGS_ATTRIBUTES, attributes);
		db.insert(AVAILABLE_DRUGS_TABLE, null, args);
	}
	
	public int numAvailableDrugs() {
		Cursor cursor = db.query(true, AVAILABLE_DRUGS_TABLE,
				new String[] {KEY_AVAILABLE_DRUGS_ID},
				null, null, null, null, null, null);
		if (cursor != null) {
			return cursor.getCount();
		}
		return 0;
	}
	
	public String getDrugName(int index) {
		Cursor cursor = db.query(true, AVAILABLE_DRUGS_TABLE,
				new String[] {KEY_AVAILABLE_DRUGS_NAME},
				null, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int count = 0;
			while ((count < index) && (!cursor.isAfterLast())) {
				cursor.moveToNext();
				count++;
			}
			if (!cursor.isAfterLast()) {
				return cursor.getString(0);
			}
		}
		return "";
	}
	
	public String getDrugAttributes(String drug_name) {
		Cursor cursor = db.query(true, AVAILABLE_DRUGS_TABLE,
				new String[] {KEY_AVAILABLE_DRUGS_NAME,
				KEY_AVAILABLE_DRUGS_ATTRIBUTES},
				KEY_AVAILABLE_DRUGS_NAME + "=\"" + drug_name + "\"",
				null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
			  cursor.moveToLast();
			  return cursor.getString(1);
			}
		}
		return "";
	}
	
	public void clearAvailableDrugs() {
		db.delete(AVAILABLE_DRUGS_TABLE, null, null);
	}
	
	/*
	public int chooseDrugPrice(String drug_name) {
		Cursor cursor = db.query(true, AVAILABLE_DRUGS_TABLE,
				new String[] {KEY_AVAILABLE_DRUGS_NAME, KEY_AVAILABLE_DRUGS_BASE_PRICE,
				KEY_AVAILABLE_DRUGS_RANGE, KEY_AVAILABLE_DRUGS_OUTLIER_HIGH,
				KEY_AVAILABLE_DRUGS_OUTLIER_LOW, KEY_AVAILABLE_DRUGS_LOW_PROB,
				KEY_AVAILABLE_DRUGS_LOW_MULT, KEY_AVAILABLE_DRUGS_HIGH_PROB,
				KEY_AVAILABLE_DRUGS_HIGH_MULT},
				KEY_AVAILABLE_DRUGS_NAME + "=\"" + drug_name + "\"",
				null, null, null, null, null);
		int price = 0;
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				price = (int)(Integer.parseInt(cursor.getString(1)) -
						(Integer.parseInt(cursor.getString(2)) / 2.0) +
						Global.rand_gen_.nextDouble() *
						Integer.parseInt(cursor.getString(2)));
				if (Boolean.parseBoolean(cursor.getString(3))) {
					if (Global.rand_gen_.nextDouble() < Double.parseDouble(cursor.getString(7))) {
						price = (int)(price * Double.parseDouble(cursor.getString(8)));
					}
				}
				if (Boolean.parseBoolean(cursor.getString(4))) {
					if (Global.rand_gen_.nextDouble() < Double.parseDouble(cursor.getString(5))) {
						price = (int)(price / Double.parseDouble(cursor.getString(6)));
					}
				}
			}
		}
		return price;
	}
	*/
	
	/**
	 * ========================================================================
	 * Game location table
	 * ========================================================================
	 */
	
	public void addLocation(String name, String attributes) {
		ContentValues args = new ContentValues();
		args.put(KEY_AVAILABLE_LOCATIONS_NAME, name);
		args.put(KEY_AVAILABLE_LOCATIONS_ATTRIBUTES, attributes);
		db.insert(AVAILABLE_LOCATIONS_TABLE, null, args);
	}
	
	public int getNumLocations() {
		Cursor cursor = db.query(true, AVAILABLE_LOCATIONS_TABLE,
				new String[] {KEY_AVAILABLE_LOCATIONS_ID},
				null, null, null, null, null, null);
		if (cursor != null) {
			return cursor.getCount();
		}
		return 0;
	}
	
	public String getLocationName(int index) {
		Cursor cursor = db.query(true, AVAILABLE_LOCATIONS_TABLE,
				new String[] {KEY_AVAILABLE_LOCATIONS_NAME},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToPosition(index);
				return cursor.getString(0);
			}
		}
		return "";
	}
	
	public String getLocationAttributes(String location_name) {
		Cursor cursor = db.query(true, AVAILABLE_LOCATIONS_TABLE,
				new String[] {KEY_AVAILABLE_LOCATIONS_NAME,
				KEY_AVAILABLE_LOCATIONS_ATTRIBUTES},
				KEY_AVAILABLE_LOCATIONS_NAME + "=\"" + location_name + "\"",
				null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
			  cursor.moveToLast();
			  return cursor.getString(1);
			}
		}
		return "";
		
	}
	
	public void clearAvailableLocations() {
		db.delete(AVAILABLE_LOCATIONS_TABLE, null, null);
	}
}
