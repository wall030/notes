package com.example.notes.persistence;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notes.Category;
import com.example.notes.Note;
import java.util.ArrayList;
import java.util.List;

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

    public ArrayList<Note> getAllNotes(Context context ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Note> noteList = new ArrayList<>();

        SharedPreferences preferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String sortBy = preferences.getString("SortBy", COLUMN_NOTES_TIMESTAMP);

        // Query the notes table
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_NOTES_TIMESTAMP + " DESC", null);

        // Check if the cursor has data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract data from each row
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CONTENT));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TIMESTAMP));
                int category = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CATEGORY_ID));

                // Create a Note object and add it to the list
                Note note = new Note(id, title, content, timestamp, category);
                noteList.add(note);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return noteList;
    }

    public void updateNote(int id, String title, String content, String timestamp, int categoryId) {
        ContentValues note = new ContentValues();
        note.put(COLUMN_NOTES_TITLE, title);
        note.put(COLUMN_NOTES_CONTENT, content);
        note.put(COLUMN_NOTES_TIMESTAMP, timestamp);
        note.put(COLUMN_NOTES_CATEGORY_ID, categoryId);

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
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CATEGORY_ID));
        cursor.close();
        return new Note(noteId, title, content, timestamp, categoryId);

    }

    public ArrayList<Note> getNotesByCategory(int category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Note> notes = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_NOTES_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(category)}, null, null, COLUMN_NOTES_TIMESTAMP + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CONTENT));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TIMESTAMP));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CATEGORY_ID));

                notes.add(new Note(id, title, content, timestamp, categoryId));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return notes;
    }

    public void markNoteAsFavorite(int id, boolean isFavorite) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTES_IS_FAVORITE, isFavorite ? 1 : 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NOTES, values, COLUMN_NOTES_ID + " = ?", new String[]{String.valueOf(id)});
    }


    public void deleteNoteById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_NOTES_ID + " = ?", new String[]{String.valueOf(id)});
    }


    // Category

    public void insertCategory(String name) {
        ContentValues category = new ContentValues();
        category.put(COLUMN_CATEGORIES_NAME, name);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CATEGORIES, null, category);
    }

    public void updateCategory(int id, String name) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        values.put(COLUMN_CATEGORIES_NAME, name);
        db.update(TABLE_CATEGORIES, values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteCategoryById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, COLUMN_CATEGORIES_ID + " = ?", new String[]{String.valueOf(id)});
    }


    public List<Category> getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Category> categories = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIES_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIES_NAME));

                categories.add(new Category(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }
}
