package com.example.lastbite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "last_bite.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(),
                DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Each pantry item has its own ID, name, quantity and unit.
        db.execSQL(
                "CREATE TABLE pantry (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Add data-preserving migrations when the database version changes.
    }

    public long addIngredient(String name, double quantity, String unit) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);

        return db.insert("pantry", null, values);
    }

    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                "pantry",
                null,
                null,
                null,
                null,
                null,
                "name COLLATE NOCASE ASC, _id ASC"
        );
    }

    public Cursor getIngredient(long id) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                "pantry",
                null,
                "_id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    public int updateIngredient(long id, String name,
                                double quantity, String unit) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);

        return db.update(
                "pantry",
                values,
                "_id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteIngredient(long id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                "pantry",
                "_id = ?",
                new String[]{String.valueOf(id)}
        );
    }
}