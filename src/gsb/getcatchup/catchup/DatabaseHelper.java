package gsb.getcatchup.catchup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	  public static final String TABLE_CONTACTS = "contacts";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_FIRSTNAME = "firstname";
	  public static final String COLUMN_LASTNAME = "lastname";
	  public static final String COLUMN_EMAIL = "email";
	  public static final String COLUMN_PHONE = "phone";
	  public static final String COLUMN_BIRTHDAY = "birthday";
	  public static final String COLUMN_LASTCONTACT = "lastcontact";
	  public static final String COLUMN_SOURCE = "datasource";
	  public static final String COLUMN_SOURCE_ID = "datasourceid";
	  public static final String COLUMN_PORTRAIT = "portrait";
	  public static final String COLUMN_SUBINDEX = "subscription_index";
	  
	  public static final String TABLE_SUBSCRIPTIONS = "subscriptions";
	  public static final String COLUMN_CONTACTID = "contact_id";
	  public static final String COLUMN_CATCHUPDATE = "catchup_date";
	  public static final String COLUMN_CATCHUPINTERVAL = "catchup_interval";

	  
	  private static final String DATABASE_FILE_PATH = "/sdcard/Android/data/";
	  
	  private static final String DATABASE_NAME = "catchup.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATATABLE_CONTACT_CREATE = "create table "
	      + TABLE_CONTACTS + "(" 
		  + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_FIRSTNAME + " text not null, "
	      + COLUMN_LASTNAME + " text not null, "
	      + COLUMN_EMAIL + " text not null, "
	      + COLUMN_PHONE + " text not null, "
	      + COLUMN_BIRTHDAY + " text not null, "
	      + COLUMN_LASTCONTACT + " text not null, "
	      + COLUMN_SOURCE + " text not null, "
	      + COLUMN_SOURCE_ID + " text not null, "
	      + COLUMN_PORTRAIT + " blob, "
	      + COLUMN_SUBINDEX + " integer not null"
	      + ");";

	  // Database creation sql statement
	  private static final String DATATABLE_SUBSCRIPTION_CREATE = "create table "
	      + TABLE_SUBSCRIPTIONS + "(" 
		  + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_CONTACTID + " integer not null, "
	      + COLUMN_CATCHUPDATE + " text not null, "
	      + COLUMN_CATCHUPINTERVAL + " integer not null"
	      + ");";
	  public DatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	  
	  @Override
	  public SQLiteDatabase getWritableDatabase() {
		  SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE + SQLiteDatabase.CREATE_IF_NECESSARY);
		  
		  onCreate(db);
		  return db;
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		try {
	    database.execSQL(DATATABLE_CONTACT_CREATE);

		} catch (SQLiteException e) {
			//Log.i(DatabaseHelper.class.getName(), e.getMessage());
		}
		
		try {
		    database.execSQL(DATATABLE_SUBSCRIPTION_CREATE);
		} catch (SQLiteException e) {
			//Log.i(DatabaseHelper.class.getName(), e.getMessage());
		}
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    //Log.w(DatabaseHelper.class.getName(),
	    //    "Upgrading database from version " + oldVersion + " to "
	    //        + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTIONS);
	    onCreate(db);
	  }
}
