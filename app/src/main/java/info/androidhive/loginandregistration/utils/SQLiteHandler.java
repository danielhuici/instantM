
package info.androidhive.loginandregistration.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;

import info.androidhive.loginandregistration.group.Group;
import info.androidhive.loginandregistration.session.User;

/**
 * Gestiona el acceso y modificación de la base de datos
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// Database versopm
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "android_api";

	// Login table name
	private static final String TABLE_USER = "user_account";
	private static final String TABLE_GROUP = "chat_group";
	private static final String TABLE_CONTACT = "contact";

	// Login Table Columns names

	private static final String KEY_GROUP_ID = "id_chat_group";
    private static final String KEY_GROUP_NAME = "name";
    private static final String KEY_GROUP_DESCRIPTION = "description";

    private static final String KEY_ID_USER = "id_user";
    private static final String KEY_STATE = "state";
    private static final String KEY_BIRTHDAY = "birthday";
	private static final String KEY_USERNAME = "name";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_CONTACT_NAME = "name";

	private static final String KEY_ID_CONTACT = "id_contact";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);

		String CREATE_LOGIN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
				+ KEY_ID_USER + " INTEGER PRIMARY KEY,"
				+ KEY_USERNAME + " TEXT,"
				+ KEY_BIRTHDAY + " DATE,"
				+ KEY_STATE + " TEXT,"
				+ KEY_EMAIL + " TEXT UNIQUE )";

		String CREATE_GROUP_TABLE = "CREATE TABLE " + TABLE_GROUP + "("
				+ KEY_GROUP_ID + " INTEGER PRIMARY KEY,"
                + KEY_GROUP_NAME + " TEXT,"
                + KEY_GROUP_DESCRIPTION + " TEXT )";

		String CREATE_CONTACT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT + "("
				+ KEY_ID_CONTACT + " INTEGER PRIMARY KEY,"
                + KEY_CONTACT_NAME + " TEXT )";

		db.execSQL(CREATE_LOGIN_TABLE);
		db.execSQL(CREATE_GROUP_TABLE);
		db.execSQL(CREATE_CONTACT_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * @param user usuario a insertar
	 * */
	public void addUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USERNAME, user.getUsername()); // Name
		values.put(KEY_EMAIL, user.getEmail()); // Email
		values.put(KEY_ID_USER, user.getId());
		if(user.getState() == null)
			user.setState("Hey there i am using instantM");
		if(user.getBirthdate() == null) {
			try {
				user.setBirthday("01-10-1996");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		values.put(KEY_STATE, user.getState());
		values.put(KEY_ID_USER, user.getId());
		values.put(KEY_BIRTHDAY, new SimpleDateFormat("dd-mm-yyyy").format(user.getBirthdate()));

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection
		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

	/**
	 * Actualiza la información de un usuario en la base de datos (local)
	 * @param user usuario a actualizar.
	 */
	public void updateUser(User user){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USERNAME, user.getUsername()); // Name
		values.put(KEY_EMAIL, user.getEmail()); // Email
		values.put(KEY_ID_USER, user.getId());
		if(user.getState() == null)
			user.setState("Hey there i am using instantM");
		if(user.getBirthdate() == null) {
			try {
				user.setBirthday("01-10-1996");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		values.put(KEY_STATE, user.getState());
		values.put(KEY_ID_USER, user.getId());
		values.put(KEY_BIRTHDAY, new SimpleDateFormat("dd-mm-yyyy").format(user.getBirthdate()));

		db.update(TABLE_USER, values, KEY_ID_USER + " = " + user.getId() , null);
		db.close(); // Closing database connection
	}
	/**
	 * Getting user data from database.
	 * @return usuario.
	 * */
	public User getUserDetails() {
		User user = new User();
		String selectQuery = "SELECT " +  KEY_USERNAME + ", " + KEY_EMAIL + ", " + KEY_STATE + ", " + KEY_BIRTHDAY + ", " + KEY_ID_USER + " FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.setUsername(cursor.getString(0));
			user.setEmail(cursor.getString(1));
			user.setState(cursor.getString(2));
			try {
				user.setBirthday(cursor.getString(3));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			user.setId(cursor.getInt(4));
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}

	/**
	 * Retorna el nombre de usuario del usuario que está utilizando la aplicación.
	 * @return
	 */
	public String getCurrentUsername() {
		String username;
		String selectQuery = "SELECT * FROM " + TABLE_USER;

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
	 * Retorna el id del usuario que está utilizando la aplicación.
	 * @return
	 */
	public int getCurrentID() {
		int id = -1;
		String selectQuery = "SELECT  " + KEY_ID_USER + " FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			id = cursor.getInt(0);
		}
		cursor.close();
		db.close();

		return id;
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

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteGroups() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_GROUP, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

	/**
	 * Añade un grupo a la base de datos.
	 * @param group grupo a insertar.
	 */
	private void addGroup(Group group) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_NAME, group.getName()); // Name
        values.put(KEY_GROUP_ID, group.getId());
        if(group.getDescription() != null)
        	values.put(KEY_GROUP_DESCRIPTION, group.getDescription());
		long id = db.insert(TABLE_GROUP, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New group inserted into sqlite: " + id);
	}

	/**
	 * Add multiple groups to SQLite
	 * @param vGroups lista de grupos a insertar.
	 */
	public void addGroups(List<Group> vGroups) {
		for (Group g: vGroups)
			addGroup(g);
	}

	/**
	 * Retorna los contactos del usuario con sesión iniciada.
	 * @return lista de contactos.
	 */
	public List<String> getContacts() {
		String selectQuery = "SELECT " + KEY_CONTACT_NAME + " FROM " + TABLE_CONTACT;
		List<String> contacts = new ArrayList<>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		while (cursor.moveToNext()) {
			contacts.add(cursor.getString(0));
		}

		cursor.close();
		db.close();

		Log.v(TAG, "Groups: " + contacts);

		return contacts;

	}
}
