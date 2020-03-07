
package info.androidhive.loginandregistration.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// Database versopm
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "android_api";

	// Login table name
	private static final String TABLE_USER = "user_account";
	private static final String TABLE_GROUP = "chat_group";

	// Login Table Columns names
	private static final String KEY_ID_USER = "id_user";
	private static final String KEY_ID_GROUP = "id_chat_group";
	private static final String KEY_USERNAME = "name";
	private static final String KEY_GROUP_NAME = "name";
	private static final String KEY_EMAIL = "email";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ KEY_ID_USER + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT,"
				+ KEY_EMAIL + " TEXT UNIQUE" + ")";

		String CREATE_GROUP_TABLE = "CREATE TABLE " + TABLE_GROUP + "("
				+ KEY_ID_GROUP + " INTEGER PRIMARY KEY," + KEY_GROUP_NAME + " TEXT" + ")";

		db.execSQL(CREATE_LOGIN_TABLE);
		db.execSQL(CREATE_GROUP_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String username, String email) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USERNAME, username); // Name
		values.put(KEY_EMAIL, email); // Email


		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("name", cursor.getString(1));
			user.put("email", cursor.getString(2));
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}

	public String getCurrentUsername() {
		String username;
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			username = cursor.getString(1);
		} else {
			username = null;
		}
		cursor.close();
		db.close();

		Log.v(TAG, "Username: " + username);

		return username;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

	public void addGroup(String name) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_NAME, name); // Name

		// Inserting Row
		long id = db.insert(TABLE_GROUP, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New group inserted into sqlite: " + id);
	}

	/*
	 * Add multiple groups to SQLite
	 */
	public void addGroups(JSONArray groupsListJSON) throws JSONException {
		for (int i = 0; i< groupsListJSON.length(); i++) {
			addGroup(groupsListJSON.getString(i));
			Log.v(TAG, "ADDED GROUP, " + groupsListJSON.getString(i));
		}
	}

	public List<String> getGroups() {
		String selectQuery = "SELECT " + KEY_GROUP_NAME + " FROM " + TABLE_GROUP;
		List<String> groups = new ArrayList<>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// Move to first row
		cursor.moveToFirst();
		Log.v(TAG, cursor.getString(0));
		while (cursor.moveToNext()) {
			Log.v(TAG, cursor.getString(0));
			groups.add(cursor.getString(0));
		}
		cursor.close();
		db.close();

		Log.v(TAG, "Groups: " + groups);

		return groups;
	}

}
