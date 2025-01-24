package com.example.notes.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTES_ID = "id";
    public static final String COLUMN_NOTES_TITLE = "title";
    public static final String COLUMN_NOTES_CONTENT = "content";
    public static final String COLUMN_NOTES_CATEGORY_ID = "category_id";
    public static final String COLUMN_NOTES_TIMESTAMP = "timestamp";


    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORIES_ID = "id";
    public static final String COLUMN_CATEGORIES_NAME = "name";



    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTES_TITLE + " TEXT, " +
                COLUMN_NOTES_CONTENT + " TEXT, " +
                COLUMN_NOTES_TIMESTAMP + " LONG, " +
                COLUMN_NOTES_CATEGORY_ID + " INTEGER DEFAULT -1" +
                ")";
        db.execSQL(createNotesTable);

        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COLUMN_CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORIES_NAME + " TEXT UNIQUE" +
                ")";
        db.execSQL(createCategoriesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    // CRUD operations for categories
    public void insertCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORIES_NAME, name);
        db.insert(TABLE_CATEGORIES, null, values);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, COLUMN_CATEGORIES_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIES_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIES_NAME))
                );
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return categories;
    }

    public int getCategoryIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{COLUMN_CATEGORIES_ID}, COLUMN_CATEGORIES_NAME + " = ?", new String[]{name}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIES_ID));
            cursor.close();
            return id;
        }

        if (cursor != null) {
            cursor.close();
        }
        return -1; // no category found
    }

    public boolean isCategoryInUse(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_NOTES_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)}, null, null, null);

        boolean isInUse = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }
        return isInUse;
    }

    public boolean safeDeleteCategoryById(int categoryId) {
        if (!isCategoryInUse(categoryId)) {
            deleteCategoryById(categoryId);
            return true;
        }
        return false;
    }

    public void deleteCategoryById(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, COLUMN_CATEGORIES_ID + " = ?", new String[]{String.valueOf(categoryId)});
    }

    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, COLUMN_CATEGORIES_ID + " = ?", new String[]{String.valueOf(categoryId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Category category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIES_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIES_NAME))
            );
            cursor.close();
            return category;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null; // category not found
    }

    // CRUD operations for notes
    public void insertNote(String title, String content, String timestamp, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTES_TITLE, title);
        values.put(COLUMN_NOTES_CONTENT, content);
        values.put(COLUMN_NOTES_TIMESTAMP, timestamp);
        values.put(COLUMN_NOTES_CATEGORY_ID, categoryId);
        db.insert(TABLE_NOTES, null, values);
    }

    public ArrayList<Note> getAllNotes(String sortBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Note> noteList = new ArrayList<>();
        String orderBy = COLUMN_NOTES_TIMESTAMP + " DESC"; // default

        if ("title".equals(sortBy)) {
            orderBy = COLUMN_NOTES_TITLE + " ASC";
        } else if ("category".equals(sortBy)) {
            orderBy = "categories." + COLUMN_CATEGORIES_NAME + " COLLATE NOCASE ASC";
        } else if ("timestamp".equals(sortBy)) {
            orderBy = COLUMN_NOTES_TIMESTAMP + " DESC";
        }

        String query = "SELECT notes.* FROM " + TABLE_NOTES + " AS notes" +
                " LEFT JOIN " + TABLE_CATEGORIES + " AS categories" +
                " ON notes." + COLUMN_NOTES_CATEGORY_ID + " = categories." + COLUMN_CATEGORIES_ID +
                " ORDER BY " + orderBy;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TITLE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CONTENT));
                    String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TIMESTAMP));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CATEGORY_ID));

                    Note note = new Note(id, title, content, timestamp, categoryId);
                    noteList.add(note);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error while fetching all notes from the database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return noteList;
    }

    public Note getNoteById(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, COLUMN_NOTES_ID + " = ?", new String[]{String.valueOf(noteId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Note note = new Note(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTES_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_CONTENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES_TIMESTAMP)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIES_ID))
            );
            cursor.close();
            return note;
        }
        return null;
    }

    public void updateNote(int noteId, String title, String content, String timestamp, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTES_TITLE, title);
        values.put(COLUMN_NOTES_CONTENT, content);
        values.put(COLUMN_NOTES_TIMESTAMP, timestamp);
        values.put(COLUMN_NOTES_CATEGORY_ID, categoryId);
        db.update(TABLE_NOTES, values, "id = ?", new String[]{String.valueOf(noteId)});
    }

    public void deleteNoteById(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, "id = ?", new String[]{String.valueOf(noteId)});
    }
}
