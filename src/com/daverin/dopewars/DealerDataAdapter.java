package com.daverin.dopewars;

import com.daverin.dopewars.Global.Drug;

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
	public static final String KEY_DEALER_NAME = "name";
	public static final String KEY_DEALER_AVATAR_ID = "avatar_id";
	
	public static final String KEY_GAME_INFO_ID = "_id";
	public static final String KEY_GAME_INFO_TYPE = "type";
	public static final String KEY_GAME_INFO_CASH = "cash";
	public static final String KEY_GAME_INFO_SPACE = "space";
	public static final String KEY_GAME_INFO_DAYS = "days";
	public static final String KEY_GAME_INFO_LOCATION = "location";
	
	public static final String KEY_INVENTORY_ID = "_id";
	public static final String KEY_INVENTORY_DRUG_ID = "drug_id";
	public static final String KEY_INVENTORY_AMOUNT = "drug_amount";
	
	public static final String KEY_AVAILABLE_DRUGS_ID = "_id";
	public static final String KEY_AVAILABLE_DRUGS_NAME = "name";
	public static final String KEY_AVAILABLE_DRUGS_BASE_PRICE = "base_price";
	public static final String KEY_AVAILABLE_DRUGS_RANGE = "price_range";
	public static final String KEY_AVAILABLE_DRUGS_OUTLIER_HIGH = "outlier_high";
	public static final String KEY_AVAILABLE_DRUGS_OUTLIER_LOW = "outlier_low";
	public static final String KEY_AVAILABLE_DRUGS_LOW_PROB = "outlier_low_prob";
	public static final String KEY_AVAILABLE_DRUGS_LOW_MULT = "outlier_low_mult";
	public static final String KEY_AVAILABLE_DRUGS_HIGH_PROB = "outlier_high_prob";
	public static final String KEY_AVAILABLE_DRUGS_HIGH_MULT = "outlier_high_mult";
	
	public static final String KEY_LOCATION_DRUGS_ID = "_id";
	public static final String KEY_LOCATION_DRUGS_NAME = "name";
	public static final String KEY_LOCATION_DRUGS_PRICE = "price";
	
	public static final String KEY_LOCATION_ID = "_id";
	public static final String KEY_LOCATION_NAME = "name";
	public static final String KEY_LOCATION_BASE_NUM_DRUGS = "base_drugs";
	public static final String KEY_LOCATION_DRUG_VARIANCE = "drug_variance";
	public static final String KEY_LOCATION_MAP_BUTTON_X = "map_x";
	public static final String KEY_LOCATION_MAP_BUTTON_Y = "map_y";
	public static final String KEY_LOCATION_HAS_BANK = "has_bank";
	public static final String KEY_LOCATION_HAS_LOANSHARK = "has_loanshark";
	
    private static final String TAG = "DealerDataAdapter";
    
    private static final String DATABASE_NAME = "dopewars";
    private static final String DEALER_TABLE = "dealer_info";
    private static final String GAME_INFO_TABLE = "game_info";
    private static final String INVENTORY_TABLE = "inventory";
    private static final String LOCATION_DRUGS_TABLE = "location_drugs";
    private static final String AVAILABLE_DRUGS_TABLE = "available_drugs";
    private static final String LOCATION_TABLE = "location";
    private static final int DATABASE_VERSION = 1;
    
    private static final String CREATE_DEALER_TABLE =
    	"create table " + DEALER_TABLE + " (" +
    	KEY_DEALER_ID + " integer primary key autoincrement, " +
    	KEY_DEALER_NAME + " text not null, " +
    	KEY_DEALER_AVATAR_ID + " text not null);";
    
    private static final String CREATE_GAME_INFO_TABLE =
    	"create table " + GAME_INFO_TABLE + " (" +
    	KEY_GAME_INFO_ID + " integer primary key autoincrement, " +
    	KEY_GAME_INFO_TYPE + " text not null, " +
    	KEY_GAME_INFO_CASH + " text not null, " +
    	KEY_GAME_INFO_SPACE + " text not null, " +
    	KEY_GAME_INFO_DAYS + " text not null, " +
    	KEY_GAME_INFO_LOCATION + " text not null);";
    
    private static final String INVENTORY_DATABASE_CREATE =
    	"create table " + INVENTORY_TABLE + " (" +
    	KEY_INVENTORY_ID + " integer primary key autoincrement, " +
    	KEY_INVENTORY_DRUG_ID + " text not null, " +
    	KEY_INVENTORY_AMOUNT + " text not null);";

    private static final String LOCATION_DRUGS_CREATE =
    	"create table " + LOCATION_DRUGS_TABLE + " (" +
    	KEY_LOCATION_DRUGS_ID + " integer primary key autoincrement, " +
    	KEY_LOCATION_DRUGS_NAME + " text not null, " +
    	KEY_LOCATION_DRUGS_PRICE + " text not null);";
    
    private static final String AVAILABLE_DRUGS_CREATE =
    	"create table " + AVAILABLE_DRUGS_TABLE + " (" +
    	KEY_AVAILABLE_DRUGS_ID + " integer primary key autoincrement, " +
    	KEY_AVAILABLE_DRUGS_NAME + " text not null, " +
    	KEY_AVAILABLE_DRUGS_BASE_PRICE + " text not null, " +
    	KEY_AVAILABLE_DRUGS_RANGE + " text not null, " +
    	KEY_AVAILABLE_DRUGS_OUTLIER_HIGH + " text not null, " +
    	KEY_AVAILABLE_DRUGS_OUTLIER_LOW + " text not null, " +
    	KEY_AVAILABLE_DRUGS_LOW_PROB + " text not null," +
    	KEY_AVAILABLE_DRUGS_LOW_MULT + " text not null," +
    	KEY_AVAILABLE_DRUGS_HIGH_PROB + " text not null," +
    	KEY_AVAILABLE_DRUGS_HIGH_MULT + " text not null);";
    
    private static final String LOCATION_CREATE =
    	"create table " + LOCATION_TABLE + " (" +
    	KEY_LOCATION_ID + " integer primary key autoincrement, " +
    	KEY_LOCATION_NAME + " text not null, " +
    	KEY_LOCATION_BASE_NUM_DRUGS + " text not null, " +
    	KEY_LOCATION_DRUG_VARIANCE + " text not null, " +
    	KEY_LOCATION_MAP_BUTTON_X + " text not null, " +
    	KEY_LOCATION_MAP_BUTTON_Y + " text not null, " +
    	KEY_LOCATION_HAS_BANK + " text not null, " +
    	KEY_LOCATION_HAS_LOANSHARK + " text not null);";
    
    private final Context context;
    
    private DealerDatabaseHelper dealerDBHelper;
	private SQLiteDatabase db;
	
	/**
	 * ========================================================================
	 * Database-level
	 * ========================================================================
	 */
	
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
			db.execSQL(CREATE_DEALER_TABLE);
			db.execSQL(CREATE_GAME_INFO_TABLE);
			db.execSQL(INVENTORY_DATABASE_CREATE);
			db.execSQL(LOCATION_DRUGS_CREATE);
			db.execSQL(AVAILABLE_DRUGS_CREATE);
			db.execSQL(LOCATION_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DEALER_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + GAME_INFO_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + INVENTORY_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + LOCATION_DRUGS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + AVAILABLE_DRUGS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
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
	
	/**
	 * ========================================================================
	 * Dealer table
	 * ========================================================================
	 */
	
	public String getDealerName() {
		Cursor cursor = db.query(true, DEALER_TABLE, new String[] {KEY_DEALER_NAME},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(0);
			}
		}
		return "Guest";
	}
	
	public String getDealerAvatar() {
		Cursor cursor = db.query(true, DEALER_TABLE, new String[] {KEY_DEALER_AVATAR_ID},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(0);
			}
		}
		return "0";
	}
	
	public long setDealerInfo(String name, String avatar_id) {
		ContentValues args = new ContentValues();
		args.put(KEY_DEALER_NAME, name);
		args.put(KEY_DEALER_AVATAR_ID, avatar_id);
		Cursor cursor = db.query(true, DEALER_TABLE, new String[] {KEY_DEALER_ID}, 
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				return db.update(DEALER_TABLE, args, null, null);
			}
		}
		return db.insert(DEALER_TABLE, null, args);
	}

	/**
	 * ========================================================================
	 * Game info table
	 * ========================================================================
	 */

	public void initGameTable() {
		Cursor cursor = db.query(true, GAME_INFO_TABLE, new String[] {KEY_GAME_INFO_ID}, 
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				return;
			}
		}
		ContentValues initial_game_table = new ContentValues();
		initial_game_table.put(KEY_GAME_INFO_TYPE, "0");
		initial_game_table.put(KEY_GAME_INFO_CASH, "0");
		initial_game_table.put(KEY_GAME_INFO_SPACE, "0");
		initial_game_table.put(KEY_GAME_INFO_DAYS, "0");
		initial_game_table.put(KEY_GAME_INFO_LOCATION, "0");
	}
	
	public String getGameCash() {
		Cursor cursor = db.query(true, GAME_INFO_TABLE, new String[] {KEY_GAME_INFO_CASH},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(0);
			}
		}
		return "0";
	}
	
	public boolean setGameCash(int cash_amount) {
		initGameTable();
		String cash_string = Integer.toString(cash_amount);
		ContentValues args = new ContentValues();
		args.put(KEY_GAME_INFO_CASH, cash_string);
		return db.update(GAME_INFO_TABLE, args, null, null) > 0;
	}
	
	public String getGameSpace() {
		Cursor cursor = db.query(true, GAME_INFO_TABLE, new String[] {KEY_GAME_INFO_SPACE},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(0);
			}
		}
		return "0";
	}
	
	public boolean setGameSpace(int space_amount) {
		initGameTable();
		String space_string = Integer.toString(space_amount);
		ContentValues args = new ContentValues();
		args.put(KEY_GAME_INFO_SPACE, space_string);
		return db.update(GAME_INFO_TABLE, args, null, null) > 0;
	}
	
	public String getDaysLeft() {
		Cursor cursor = db.query(true, GAME_INFO_TABLE, new String[] {KEY_GAME_INFO_DAYS},
				null, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return cursor.getString(0);
			}
		}
		return "0";
	}
	
	public boolean setGameDays(int days_amount) {
		initGameTable();
		String days_string = Integer.toString(days_amount);
		ContentValues args = new ContentValues();
		args.put(KEY_GAME_INFO_DAYS, days_string);
		return db.update(GAME_INFO_TABLE, args, null, null) > 0;
	}
	

	/**
	 * ========================================================================
	 * Game weapons table
	 * ========================================================================
	 */
	

	/**
	 * ========================================================================
	 * Game inventory table
	 * ========================================================================
	 */
	public void clearDealerInventory() {
		db.delete(INVENTORY_TABLE, null, null);
	}
	
	/**
	 * ========================================================================
	 * Location drugs table
	 * ========================================================================
	 */
	public void addDrugAtCurrentLocation(String name, int price) {
		ContentValues args = new ContentValues();
		args.put(KEY_LOCATION_DRUGS_NAME, name);
		args.put(KEY_LOCATION_DRUGS_PRICE, Integer.toString(price));
		db.insert(LOCATION_DRUGS_TABLE, null, args);
	}
	
	public int numLocationDrugs() {
		Cursor cursor = db.query(true, LOCATION_DRUGS_TABLE,
				new String[] {KEY_LOCATION_DRUGS_ID},
				null, null, null, null, null, null);
		if (cursor != null) {
			return cursor.getCount();
		}
		return 0;
	}
	
	public String getLocationDrugName(int index) {
		Cursor cursor = db.query(true, LOCATION_DRUGS_TABLE,
				new String[] {KEY_LOCATION_DRUGS_NAME},
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
	
	public int getLocationDrugPrice(String drug_name) {
		Cursor cursor = db.query(true, LOCATION_DRUGS_TABLE,
				new String[] {KEY_LOCATION_DRUGS_NAME, KEY_LOCATION_DRUGS_PRICE},
				KEY_LOCATION_DRUGS_NAME + "=\"" + drug_name + "\"",
				null, null, null, null, null);
		int price = 0;
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				price = Integer.parseInt(cursor.getString(1));
			}
		}
		return price;
	}
	
	public void clearLocationDrugs() {
		db.delete(LOCATION_DRUGS_TABLE, null, null);
	}
	
	/**
	 * ========================================================================
	 * Game available drugs table
	 * ========================================================================
	 */
	public void addDrug(String name, int base_price, int price_range,
			boolean outlier_high, boolean outlier_low, float outlier_low_prob,
			int outlier_low_mult, float outlier_high_prob,
			int outlier_high_mult) {
		ContentValues args = new ContentValues();
		args.put(KEY_AVAILABLE_DRUGS_NAME, name);
		args.put(KEY_AVAILABLE_DRUGS_BASE_PRICE, Integer.toString(base_price));
		args.put(KEY_AVAILABLE_DRUGS_RANGE, Integer.toString(price_range));
		args.put(KEY_AVAILABLE_DRUGS_OUTLIER_HIGH, Boolean.toString(outlier_high));
		args.put(KEY_AVAILABLE_DRUGS_OUTLIER_LOW, Boolean.toString(outlier_low));
		args.put(KEY_AVAILABLE_DRUGS_LOW_PROB, Float.toString(outlier_low_prob));
		args.put(KEY_AVAILABLE_DRUGS_LOW_MULT, Integer.toString(outlier_low_mult));
		args.put(KEY_AVAILABLE_DRUGS_HIGH_PROB, Float.toString(outlier_high_prob));
		args.put(KEY_AVAILABLE_DRUGS_HIGH_MULT, Integer.toString(outlier_high_mult));
		db.insert(AVAILABLE_DRUGS_TABLE, null, args);
	}
	
	public int numAvailableDrugs() {
		Cursor cursor = db.query(true, AVAILABLE_DRUGS_TABLE, new String[] {KEY_AVAILABLE_DRUGS_ID},
				null, null, null, null, null, null);
		if (cursor != null) {
			return cursor.getCount();
		}
		return 0;
	}
	
	public String getAvailableDrugName(int index) {
		Cursor cursor = db.query(true, AVAILABLE_DRUGS_TABLE, new String[] {KEY_AVAILABLE_DRUGS_NAME},
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
	
	public void clearAvailableDrugs() {
		db.delete(AVAILABLE_DRUGS_TABLE, null, null);
	}
	
	/**
	 * ========================================================================
	 * Game location table
	 * ========================================================================
	 */
	public void addLocation(String name, int base_drugs, int drug_variance,
			int x_position, int y_position, boolean has_bank,
			boolean has_loanshark) {
		ContentValues args = new ContentValues();
		args.put(KEY_LOCATION_NAME, name);
		args.put(KEY_LOCATION_BASE_NUM_DRUGS, Integer.toString(base_drugs));
		args.put(KEY_LOCATION_DRUG_VARIANCE, Integer.toString(drug_variance));
		args.put(KEY_LOCATION_MAP_BUTTON_X, Integer.toString(x_position));
		args.put(KEY_LOCATION_MAP_BUTTON_Y, Integer.toString(y_position));
		args.put(KEY_LOCATION_HAS_BANK, Boolean.toString(has_bank));
		args.put(KEY_LOCATION_HAS_LOANSHARK, Boolean.toString(has_loanshark));
		db.insert(LOCATION_TABLE, null, args);
	}
	
	public int drugCountForLocation(String locationName) {	
		Cursor cursor = db.query(true, LOCATION_TABLE,
				new String[] {KEY_LOCATION_NAME, KEY_LOCATION_BASE_NUM_DRUGS},
				KEY_LOCATION_NAME + "=\"" + locationName + "\"",
				null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return Integer.parseInt(cursor.getString(1));
			}
		}
		return 0;
	}
	
	public int drugVarianceForLocation(String locationName) {	
		Cursor cursor = db.query(true, LOCATION_TABLE,
				new String[] {KEY_LOCATION_NAME, KEY_LOCATION_DRUG_VARIANCE},
				KEY_LOCATION_NAME + "=\"" + locationName + "\"",
				null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				return Integer.parseInt(cursor.getString(1));
			}
		}
		return 0;
	}
}
