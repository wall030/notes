package com.example.notes.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notes.Category;
import com.example.notes.Note;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_CATEGORIES = "categories";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabelle für Notizen
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "timestamp LONG, " +
                "category_id INTEGER DEFAULT -1" +
                ")";
        db.execSQL(createNotesTable);

        // Tabelle für Kategorien
        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE" +
                ")";
        db.execSQL(createCategoriesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    // CRUD-Operationen für Kategorien
    public void insertCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert(TABLE_CATEGORIES, null, values);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "name ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name"))
                );
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return categories;
    }
    public List<String> getAllCategoriesAsStringList() {
        List<String> categoryNames = new ArrayList<>();
        List<Category> categories = getAllCategories(); // Ruft die bestehende Methode auf
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }
        return categoryNames;
    }

    public int getCategoryIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
            return id;
        }

        if (cursor != null) {
            cursor.close();
        }
        return -1; // Keine Kategorie gefunden
    }

    public boolean isCategoryInUse(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, "category_id = ?", new String[]{String.valueOf(categoryId)}, null, null, null);

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
        db.delete(TABLE_CATEGORIES, "id = ?", new String[]{String.valueOf(categoryId)});
    }

    public void updateCategory(int categoryId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.update(TABLE_CATEGORIES, values, "id = ?", new String[]{String.valueOf(categoryId)});
    }

    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, "id = ?", new String[]{String.valueOf(categoryId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Category category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
            cursor.close();
            return category;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null; // Kategorie wurde nicht gefunden
    }

    // CRUD-Operationen für Notizen
    public void insertNote(String title, String content, long timestamp, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("timestamp", timestamp);
        values.put("category_id", categoryId);
        db.insert(TABLE_NOTES, null, values);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, null, null, null, null, "timestamp DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("content")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))
                );
                notes.add(note);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return notes;
    }

    public Note getNoteById(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, "id = ?", new String[]{String.valueOf(noteId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Note note = new Note(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))
            );
            cursor.close();
            return note;
        }
        return null;
    }

    public void updateNote(int noteId, String title, String content, long timestamp, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("timestamp", timestamp);
        values.put("category_id", categoryId);
        db.update(TABLE_NOTES, values, "id = ?", new String[]{String.valueOf(noteId)});
    }

    public void deleteNoteById(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, "id = ?", new String[]{String.valueOf(noteId)});
    }

    public int getLastInsertedNoteId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid() AS id", null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
            return id;
        }
        return -1; // Keine ID gefunden
    }
}
