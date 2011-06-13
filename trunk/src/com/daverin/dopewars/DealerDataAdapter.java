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
  // The database version number tells the application when to invalidate
  // old saved data.  When this number increases everyone loses their
  // saved games.
  private static final int DATABASE_VERSION = 2;

  // This is the name of the key field for the database.  Since there is
  // only ever one entry, it's pretty useless.
  public static final String KEY_GAME_ID = "_id";

  // This is the name of the value field for the database.  It should
  // contain a serialized version of a GameInfo object.
  public static final String KEY_DEALER_GAME_INFO = "game_info";

  // This database name and table name are what Android needs to identify
  // this data store.
  private static final String ANDROID_DATABASE_NAME = "dopewars";
  private static final String ANDROID_DATA_STORE = "dealer_info";

  // This tag serves as an identifier for logging.
  private static final String TAG = "DealerDataAdapter";

  // These are for convenience.  The helper makes connecting to a database
  // a little easier, and it can keep around the opened database in "db".
  private DealerDatabaseHelper dealerDBHelper;
	private SQLiteDatabase db;

  // The database needs to check that its data has been initialized, but
  // we don't want to open databases just to check, so this will check on
  // the first access to an opened database, then remember so it doesn't
  // check again.
  private boolean game_info_table_initialized_;


  // This inner class primarily creates tables if they don't exist and
  // handles version number checking when the database schema is changed.
  private static class DealerDatabaseHelper extends SQLiteOpenHelper {
    DealerDatabaseHelper(Context context) {
      super(context, ANDROID_DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This will create a new data store using the preset names and fields.
    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.w(TAG, "Creating database from scratch.");
      db.execSQL("create table " + ANDROID_DATA_STORE + " (" +
          KEY_GAME_ID + " integer primary key autoincrement, " +
          KEY_DEALER_GAME_INFO + " text not null);");
      }

    // This will upgrade the database, which in this case means destroying
    // the old database and creating a new one from scratch.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion +
          " to " + newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS " + ANDROID_DATA_STORE);
      onCreate(db);
    }
  }

  // When a database adapter is first created the data store hasn't been
  // checked for initialization yet.
  public DealerDataAdapter(Context ctx) {
    dealerDBHelper = new DealerDatabaseHelper(ctx);
    game_info_table_initialized_ = false;
  }

  // Opening the database populates the "db" object with a database pointer
  // we can interact with.
  public DealerDataAdapter open() throws SQLException {
    db = dealerDBHelper.getWritableDatabase();
    return this;
  }

  // Closing the database closes all open database connections that the
  // database helper has opened.
  public void close() {
    dealerDBHelper.close();
  }

  // Whenever data is accessed, there is a check that the storage has been
  // initialized.  This takes the form of a query to the database, and if
  // the expected data isn't found, a blank value is placed into the right
  // spot in the database.  This should only happen once, there is a check
  // that will shortcut evaluation if this has been run before.
  public void initializeGameData() {
    if (game_info_table_initialized_) return;
    game_info_table_initialized_ = true;

    // Try to get the game info value from the data store.  If one is
    // found, we can just return, there's nothing more to do.
    Cursor cursor = db.query(true, ANDROID_DATA_STORE,
        new String[] {KEY_GAME_ID},
        null, null, null, null, null, null);
    if (cursor != null) {
      if (cursor.getCount() > 0) {
        return;
      }
    }

    // Clear out the data store, just in case something weird crept in.
    db.delete(ANDROID_DATA_STORE, null, null);

    // If the game info wasn't found in the data store, then create a new
    // one and add it to the data store.
    ContentValues initial_dealer_info = new ContentValues();
    initial_dealer_info.put(KEY_DEALER_GAME_INFO, "");
    db.insert(ANDROID_DATA_STORE, null, initial_dealer_info);
  }

  // Retrieve the saved game string from the data store.
  public String getGameString() {
    initializeGameData();
    Cursor cursor = db.query(true, ANDROID_DATA_STORE,
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

  // Set the value of the saved game string in the data store.
  public void setGameString(String serialized_game_info) {
    initializeGameData();
    ContentValues args = new ContentValues();
    args.put(KEY_DEALER_GAME_INFO, serialized_game_info);
    db.update(ANDROID_DATA_STORE, args, null, null);
  }
}
