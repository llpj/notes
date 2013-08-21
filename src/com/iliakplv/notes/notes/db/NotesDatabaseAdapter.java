package com.iliakplv.notes.notes.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.iliakplv.notes.notes.AbstractNote;

/**
 * Author: Ilya Kopylov
 * Date:  21.08.2013
 */
public class NotesDatabaseAdapter {

	// Database
	private static final String DATABASE_NAME	= "notes.db";
	public static final int DATABASE_VERSION	= 1;

	// Common keys
	public static final String KEY_ID = "_id";

	// Tables
	// Table: Notes
	public static final String TABLE_NOTES					= "notes";
	public static final int NOTES_KEY_NAME_COLUMN			= 1;
	public static final String NOTES_KEY_NAME				= "name";
	public static final int NOTES_KEY_BODY_COLUMN			= 2;
	public static final String NOTES_KEY_BODY				= "body";
	public static final int NOTES_KEY_CREATE_DATE_COLUMN	= 3;
	public static final String NOTES_KEY_CREATE_DATE		= "create_date";
	public static final int NOTES_KEY_CHANGE_DATE_COLUMN	= 4;
	public static final String NOTES_KEY_CHANGE_DATE		= "change_date";

	// Scheme creation
	static final String CREATE_SCHEME_COMMAND =
			"CREATE TABLE " + TABLE_NOTES +
					" (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					NOTES_KEY_NAME + " TEXT NOT NULL, " +
					NOTES_KEY_BODY + " TEXT NOT NULL, " +
					NOTES_KEY_CREATE_DATE + " LONG, " +
					NOTES_KEY_CHANGE_DATE + " LONG);";

	private SQLiteDatabase db;
	private NotesDatabaseOpenHelper dbHelper;


	// Constructors

	public NotesDatabaseAdapter(Context context) {
		dbHelper = new NotesDatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	// Queries

	public Cursor getAllNotesCursor() {
		return db.query(TABLE_NOTES,
				new String[] {KEY_ID, NOTES_KEY_NAME, NOTES_KEY_BODY, NOTES_KEY_CREATE_DATE, NOTES_KEY_CHANGE_DATE},
				null, null, null, null, null);
	}


	// Data modification

	public long insertNote(AbstractNote note) {
		return db.insert(TABLE_NOTES, null, contentValuesForNote(note));
	}

	public boolean updateNote(long id, AbstractNote note) {
		return db.update(TABLE_NOTES, contentValuesForNote(note), whereClauseForId(id), null) > 0;
	}

	public boolean deleteNote(long id) {
		return db.delete(TABLE_NOTES, whereClauseForId(id), null) > 0;
	}


	// Util methods

	private static ContentValues contentValuesForNote(AbstractNote note) {
		ContentValues cv = new ContentValues();
		cv.put(NOTES_KEY_NAME, note.getTitle());
		cv.put(NOTES_KEY_BODY, note.getBody());
		cv.put(NOTES_KEY_CREATE_DATE, note.getCreateTime().getMillis());
		cv.put(NOTES_KEY_CHANGE_DATE, note.getChangeTime().getMillis());
		return cv;
	}

	private static String whereClauseForId(long id) {
		return KEY_ID + "=" + id;
	}


	// Database open and close

	public void open() {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			db = dbHelper.getReadableDatabase();
		}
	}

	public void close() {
		if (db != null) {
			db.close();
		}
	}

}
