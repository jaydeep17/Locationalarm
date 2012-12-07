package samsung.usid.locationalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "mydb.db";
	public static final int DATABASE_VERSION = 1;

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createAlarms = "CREATE TABLE " + Alarms.TABLE_NAME + "("
				+ Alarms.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Alarms.TITLE + " VARCHAR(225), " + Alarms.DESC
				+ " LONGTEXT, " + Alarms.RADIUS + " VARCHAR(4), "
				+ Alarms.LONGITUDE + " VARCHAR(25), " + Alarms.LATITUDE
				+ " VARCHAR(25), " + Alarms.SETBY + " VARCHAR(225));";
		db.execSQL(createAlarms);

		String createFriends = "CREATE TABLE " + Friends.TABLE_NAME + "("
				+ Friends.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Friends.NAME + " VARCHAR(225), " + Friends.EMAIL
				+ " VARCHAR(225));";
		db.execSQL(createFriends);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+Alarms.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+Friends.TABLE_NAME);
		onCreate(db);
	}
	
	// Wrapper of insert
	public void addAlarm(String title, String desc, String d, String longitude, String latitude, String setby){
		ContentValues cv = new ContentValues();
		cv.put(Alarms.TITLE, title);
		cv.put(Alarms.DESC, desc);
		cv.put(Alarms.RADIUS, d);
		cv.put(Alarms.LONGITUDE, longitude);
		cv.put(Alarms.LONGITUDE, longitude);
		cv.put(Alarms.SETBY, setby);
		this.getWritableDatabase().insert(Alarms.TABLE_NAME, null, cv);
	}
	
	public boolean deleteAlarm(String UID){
		String[] whereArgs = {UID};
		int result = this.getWritableDatabase().delete(Alarms.TABLE_NAME, Alarms.UID + "= ? ", whereArgs);
		return (result != 0);
	}

	public Cursor fetchAllAlarms(){
		Cursor c =this.getWritableDatabase().query(Alarms.TABLE_NAME, new String[]{Alarms.UID, Alarms.TITLE, Alarms.DESC, Alarms.RADIUS}, null, null, null, null, null);
		if( c != null ){
			c.moveToFirst();
		}
		return c;
	}
	
	public void insertSomeAlarms(){
		addAlarm("DA-IICT", "Meet Prof. Banerjee regarding USID", "1", "1234567", "1234567", "me");
		addAlarm("Himalaya Mall", "Buy a Birthday Gift for brother", "1.2", "1234567", "1234567", "me");
		addAlarm("Home", "Call Mr. Sam Sung.", "1.7", "1234567", "1234567", "me");
	}
	
	public boolean deleteAllAlarms(){
		int deleted = this.getWritableDatabase().delete(Alarms.TABLE_NAME, null, null);
		return deleted>0;
	}
}
