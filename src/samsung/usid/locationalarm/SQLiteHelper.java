package samsung.usid.locationalarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "mydb.db";
	public static final int DATABASE_VERSION = 1;
	private SharedPreferences sp;
	private Context context;

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		sp = context.getSharedPreferences(Globals.PREFS_NAME,
				Context.MODE_PRIVATE);
	}

	// 3 tables are created :
	//   i) Alarms Table
	//  ii) Friends Table
	// iii) Permitted Friends Table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createAlarms = "CREATE TABLE " + Alarms.TABLE_NAME + "("
				+ Alarms.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Alarms.TITLE + " VARCHAR(255), " + Alarms.DESC
				+ " LONGTEXT, " + Alarms.RADIUS + " int(11), "
				+ Alarms.LOCATION + " VARCHAR(50), " + Alarms.SETBY
				+ " VARCHAR(225), " + Alarms.CREATED_AT
				+ " timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
				+ Alarms.MODIFIED_AT
				+ " timestamp NOT NULL DEFAULT '0000-00-00 00:00:00');";
		db.execSQL(createAlarms);

		String createFriends = "CREATE TABLE " + Friends.TABLE_NAME + "("
				+ Friends.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Friends.NAME + " VARCHAR(225), " + Friends.EMAIL
				+ " VARCHAR(225));";
		db.execSQL(createFriends);

		String createPermits = "CREATE TABLE " + PermittedFriends.TABLE_NAME
				+ "(" + PermittedFriends.UID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PermittedFriends.NAME + " VARCHAR(225), "
				+ PermittedFriends.EMAIL + " VARCHAR(225));";
		db.execSQL(createPermits);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Alarms.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Friends.TABLE_NAME);
		onCreate(db);
	}

	// Wrapper of insert
	public void addAlarm(String title, String desc, String d, String longitude,
			String latitude, String setby) {
		String tableName = Alarms.TABLE_NAME;
		String query = "INSERT INTO " + tableName + "(" + Alarms.TITLE + ", "
				+ Alarms.DESC + ", " + Alarms.RADIUS + ", " + Alarms.LOCATION
				+ ", " + Alarms.SETBY + ") VALUES('" + title + "', " + "'"
				+ desc + "', " + "'" + d + "', " + "'" + longitude + ","
				+ latitude + "', '" + setby + "');";
		this.getWritableDatabase().execSQL(query);

		tableName = sp.getString(Globals.PREFS_FIXED_EMAIL, "{...}")
				+ "_alarms";
		query = "INSERT INTO " + tableName + "(" + Alarms.TITLE + ", "
				+ Alarms.DESC + ", " + Alarms.RADIUS + ", " + Alarms.LOCATION
				+ ", " + Alarms.SETBY + ") VALUES('" + title + "', " + "'"
				+ desc + "', " + "'" + d + "', " + "'" + longitude + ","
				+ latitude + "', '" + setby + "');";
		logChanges(query);
	}

	public void addFriend(String name, String email) {
		// ContentValues cv = new ContentValues();
		// cv.put(Friends.NAME, name);
		// cv.put(Friends.EMAIL, email);
		// this.getWritableDatabase().insert(Friends.TABLE_NAME, null, cv);
		String tableName = Friends.TABLE_NAME;
		String query = "INSERT INTO " + tableName + "(" + Friends.NAME + ", "
				+ Friends.EMAIL + ") VALUES('" + name + "', '" + email + "');";
		this.getWritableDatabase().execSQL(query);

		tableName = sp.getString(Globals.PREFS_FIXED_EMAIL, "{...}")
				+ "_friends";
		query = "INSERT INTO " + tableName + "(" + Friends.NAME + ", "
				+ Friends.EMAIL + ") VALUES('" + name + "', '" + email + "');";
		logChanges(query);
	}

	

	// fetches permitted friends from my alarms table from the server to the
	// local db, so that the GUI Spinner in "NewAlarm.java" can be populated..
	public void addPermittedFriends(int UID, String name, String email) {
		ContentValues cv = new ContentValues();
		cv.put(PermittedFriends.UID, UID);
		cv.put(PermittedFriends.NAME, name);
		cv.put(PermittedFriends.EMAIL, email);
		this.getWritableDatabase()
				.insert(PermittedFriends.TABLE_NAME, null, cv);
	}

	public void deleteAlarm(String UID) {
		// String[] whereArgs = { UID };
		// int result = this.getWritableDatabase().delete(Alarms.TABLE_NAME,
		// Alarms.UID + "= ? ", whereArgs);
		String tableName = Alarms.TABLE_NAME;
		String query = "DELETE FROM " + tableName + " WHERE " + Alarms.UID
				+ " = " + UID;
		this.getWritableDatabase().execSQL(query);

		tableName = sp.getString(Globals.PREFS_FIXED_EMAIL, "{...}")
				+ "_alarms";
		query = "DELETE FROM " + tableName + " WHERE " + Alarms.UID + " = "
				+ UID;
		logChanges(query);
	}

	public void deleteFriend(String UID) {
		// String[] whereArgs = { UID };
		// int result = this.getWritableDatabase().delete(Friends.TABLE_NAME,
		// Friends.UID + "= ? ", whereArgs);
		String tableName = Friends.TABLE_NAME;
		String query = "DELETE FROM " + tableName + " WHERE " + Friends.UID
				+ " = " + UID;
		this.getWritableDatabase().execSQL(query);

		tableName = sp.getString(Globals.PREFS_FIXED_EMAIL, "{...}")
				+ "_friends";
		query = "DELETE FROM " + tableName + " WHERE " + Friends.UID + " = "
				+ UID;
		logChanges(query);
	}

	public Cursor fetchAllAlarms() {
		Cursor c = this.getWritableDatabase().query(
				Alarms.TABLE_NAME,
				new String[] { Alarms.UID, Alarms.TITLE, Alarms.DESC,
						Alarms.RADIUS }, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public Cursor fetchAllFriends() {
		Cursor c = this.getWritableDatabase().query(Friends.TABLE_NAME,
				new String[] { Friends.UID, Friends.NAME, Friends.EMAIL },
				null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public Cursor fetchAllPermittedFriends() {
		Cursor c = this.getWritableDatabase().query(
				PermittedFriends.TABLE_NAME,
				new String[] { PermittedFriends.UID, PermittedFriends.NAME,
						PermittedFriends.EMAIL }, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		if (c.getCount() == 0) {
			c = null;
		}
		return c;
	}

	public void insertSomeAlarms() {
		addAlarm("DA-IICT", "Meet Prof. Banerjee regarding USID", "1",
				"1234567", "1234567", "me");
		addAlarm("Himalaya Mall", "Buy a Birthday Gift for brother", "1.2",
				"1234567", "1234567", "me");
		addAlarm("Home", "Call Mr. Sam Sung.", "1.7", "1234567", "1234567",
				"me");
	}

	public void insertSomeFriends() {
		addFriend("Jaydeep", "jaydp17@outlook.com");
		addFriend("Parth", "patoliyaparth@gmail.com");
		addFriend("Parag", "sunnydraggon@hotmail.com");
	}

	public void deleteAllAlarms() {
		// int deleted = this.getWritableDatabase().delete(Alarms.TABLE_NAME,
		// null, null);
		String tableName = Alarms.TABLE_NAME;
		String query = "DELETE FROM " + tableName;
		this.getWritableDatabase().execSQL(query);

		tableName = sp.getString(Globals.PREFS_FIXED_EMAIL, "{...}")
				+ "_alarms";
		query = "DELETE FROM " + tableName;
		logChanges(query);
	}

	public void deleteAllFriends() {
		// int deleted = this.getWritableDatabase().delete(Friends.TABLE_NAME,
		// null, null);
		String tableName = Friends.TABLE_NAME;
		String query = "DELETE FROM " + tableName;
		this.getWritableDatabase().execSQL(query);

		tableName = sp.getString(Globals.PREFS_FIXED_EMAIL, "{...}")
				+ "_friends";
		query = "DELETE FROM " + tableName;
		logChanges(query);
	}

	public void deleteAllPermittedFriends() {
		this.getWritableDatabase().delete(PermittedFriends.TABLE_NAME, null,
				null);
	}

	public boolean updateAlarm(String UID, ArrayList<String> names,
			ArrayList<String> values) {
		// TODO updateAlarm sync syntax
		ContentValues args = new ContentValues();
		for (int i = 0; i < names.size(); i++) {
			args.put(names.get(i), values.get(i));
		}
		int updated = this.getWritableDatabase().update(Alarms.TABLE_NAME,
				args, Alarms.UID + " = ?", new String[] { UID });
		return updated == 1;
	}

	public boolean renameFriend(String UID, String name) {
		// TODO updateFriend sync syntax
		ContentValues args = new ContentValues();
		args.put(Friends.NAME, name);
		int updated = this.getWritableDatabase().update(Friends.TABLE_NAME,
				args, Friends.UID + " = ?", new String[] { UID });
		return updated == 1;
	}

	public void logChanges(String query) {

		FileInputStream fin = null;
		StringBuilder sb = null;
		int ch;
		try {
			fin = context.openFileInput(Globals.LOGFILE);
			sb = new StringBuilder();
			while ((ch = fin.read()) != -1) {
				sb.append((char) ch);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONArray jary = null;
		try {
			if (sb == null) {
				jary = new JSONArray();
			} else {
				jary = new JSONArray(sb.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		jary.put(query);

		FileOutputStream fos;
		try {
			fos = context.openFileOutput(Globals.LOGFILE, Context.MODE_PRIVATE);
			fos.write(jary.toString().getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void flushLogFile() {
		File dir = context.getFilesDir();
		File file = new File(dir, Globals.LOGFILE);
		file.delete();
	}

	public JSONArray getLog() {
		FileInputStream fin = null;
		StringBuilder sb = null;
		int ch;
		try {
			fin = context.openFileInput(Globals.LOGFILE);
			sb = new StringBuilder();
			while ((ch = fin.read()) != -1) {
				sb.append((char) ch);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONArray jary = null;
		try {
			if (sb == null) {
				jary = new JSONArray();
			} else {
				jary = new JSONArray(sb.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jary;
	}
}
