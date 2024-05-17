package com.example.a71plostfoundapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lostandfound.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_POST_TYPE = "post_type";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LOCATION = "location";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_TYPE + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_LOCATION + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public void insertItem(String postType, String name, String phone, String description, String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_TYPE, postType);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_LOCATION, location);
        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public void updateItem(int id, String postType, String name, String phone, String description, String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_TYPE, postType);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_LOCATION, location);
        db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String postType = cursor.getString(cursor.getColumnIndex(COLUMN_POST_TYPE));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
                items.add(new Item(id, postType, name, phone, description, date, location));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }


    public Item getItem(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, COLUMN_ID + "=?", new String[]{String.valueOf(itemId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Item item = new Item(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_POST_TYPE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
            cursor.close();
            db.close();
            return item;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }
    }

    public void deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[] { String.valueOf(itemId) });
        db.close();
    }
}
