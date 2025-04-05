package com.example.goaltracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GoalTracker.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String TABLE_GOALS = "goals";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_GOALS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, name TEXT, description TEXT, completed INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        onCreate(db);
    }

    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public int checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        cursor.close();
        return -1;
    }

    public boolean insertGoal(int userId, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("name", name);
        values.put("description", description);
        values.put("completed", 0);
        long result = db.insert(TABLE_GOALS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getGoals(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GOALS + " WHERE user_id = ?", new String[]{String.valueOf(userId)});
    }

    public void updateGoal(int goalId, boolean completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completed", completed ? 1 : 0);
        db.update(TABLE_GOALS, values, "id = ?", new String[]{String.valueOf(goalId)});
        db.close();
    }

    public void deleteGoal(int goalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GOALS, "id = ?", new String[]{String.valueOf(goalId)});
        db.close();
    }

    public int getTotalGoalsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_GOALS + " WHERE user_id = ?", new String[]{String.valueOf(userId)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getCompletedGoalsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_GOALS + " WHERE user_id = ? AND completed = 1", new String[]{String.valueOf(userId)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}