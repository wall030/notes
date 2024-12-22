package com.example.notes.persistence;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.notes.Note;
import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "notes.db";

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTES_ID = "id";
    public static final String COLUMN_NOTES_TITLE = "title";
    public static final String COLUMN_NOTES_CONTENT = "content";
    public static final String COLUMN_NOTES_CATEGORY_ID = "category_id";
    public static final String COLUMN_NOTES_IS_FAVORITE = "is_favorite";
    public static final String COLUMN_NOTES_TIMESTAMP = "timestamp";


    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORIES_ID = "id";
    public static final String COLUMN_CATEGORIES_NAME = "name";


    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NOTES + " (" +
                        COLUMN_NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NOTES_TITLE + " TEXT," +
                        COLUMN_NOTES_CONTENT + " TEXT," +
                        COLUMN_NOTES_CATEGORY_ID + " INTEGER," +
                        COLUMN_NOTES_IS_FAVORITE + " INTEGER," +
                        COLUMN_NOTES_TIMESTAMP + " TEXT" +
                        ")"
        );
        db.execSQL(
                "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                        COLUMN_CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_CATEGORIES_NAME + " TEXT" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    public void insertNote(String time, String title, String content, int categoryId) {
        ContentValues note = new ContentValues();
        note.put(COLUMN_NOTES_TIMESTAMP, time);
        note.put(COLUMN_NOTES_TITLE, title);
        note.put(COLUMN_NOTES_CONTENT, content);
        note.put(COLUMN_NOTES_CATEGORY_ID, categoryId);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NOTES, null, note);
    }

    public void insertCategory(String name) {
        ContentValues category = new ContentValues();
        category.put(COLUMN_CATEGORIES_NAME, name);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CATEGORIES, null, category);
    }

    public ArrayList<Note> getAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Note> noteList = new ArrayList<>();

        // Query the notes table
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);

        // Check if the cursor has data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract data from each row
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CONTENT));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TIMESTAMP));

                // Create a Note object and add it to the list
                Note note = new Note(id, title, content, timestamp);
                noteList.add(note);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return noteList;
    }

    public void updateNote(int id, String title, String content, String timestamp) {
        ContentValues note = new ContentValues();
        note.put(COLUMN_NOTES_TITLE, title);
        note.put(COLUMN_NOTES_CONTENT, content);
        note.put(COLUMN_NOTES_TIMESTAMP, timestamp);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NOTES, note, COLUMN_NOTES_ID + " = ?", new String[]{Integer.toString(id)});
    }

    public Note getNoteById(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES +
                " WHERE " + COLUMN_NOTES_ID + "=" + noteId, null);

        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TITLE));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CONTENT));
        String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TIMESTAMP));
        cursor.close();
        return new Note(noteId, title, content, timestamp);

    }
}
