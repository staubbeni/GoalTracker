package com.example.goaltracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GoalTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Táblák
    private static final String TABLE_USERS = "users";
    private static final String TABLE_GOALS = "goals";

    // Users tábla
    private static final String COL_USER_ID = "id";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    // Goals tábla
    private static final String COL_GOAL_ID = "id";
    private static final String COL_USER_ID_FK = "user_id";
    private static final String COL_GOAL_NAME = "name";
    private static final String COL_GOAL_DESC = "description";
    private static final String COL_GOAL_COMPLETED = "completed";
    private static final String COL_GOAL_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users tábla létrehozása
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Goals tábla létrehozása
        String createGoalsTable = "CREATE TABLE " + TABLE_GOALS + " (" +
                COL_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID_FK + " INTEGER, " +
                COL_GOAL_NAME + " TEXT, " +
                COL_GOAL_DESC + " TEXT, " +
                COL_GOAL_COMPLETED + " INTEGER, " +
                COL_GOAL_DATE + " TEXT, " +
                "FOREIGN KEY(" + COL_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createGoalsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        onCreate(db);
    }

    // Felhasználó hozzáadása
    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Bejelentkezés ellenőrzése
    public int checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + COL_EMAIL + "=? AND " + COL_PASSWORD + "=?", new String[]{email, password});
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        cursor.close();
        return -1;
    }

    // "Goal" hozzáadása
    public boolean insertGoal(int userId, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID_FK, userId);
        values.put(COL_GOAL_NAME, name);
        values.put(COL_GOAL_DESC, description);
        values.put(COL_GOAL_COMPLETED, 0);
        values.put(COL_GOAL_DATE, String.valueOf(System.currentTimeMillis()));
        long result = db.insert(TABLE_GOALS, null, values);
        return result != -1;
    }

    // "Goal" lekérdezése
    public Cursor getGoals(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GOALS + " WHERE " + COL_USER_ID_FK + "=?", new String[]{String.valueOf(userId)});
    }

    // "Goal" törlése
    public boolean deleteGoal(int goalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_GOALS, COL_GOAL_ID + "=?", new String[]{String.valueOf(goalId)}) > 0;
    }

    // "Goal" teljesített
    public boolean completeGoal(int goalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GOAL_COMPLETED, 1);
        return db.update(TABLE_GOALS, values, COL_GOAL_ID + "=?", new String[]{String.valueOf(goalId)}) > 0;
    }

    // Statisztika
    public int getCompletedGoalsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_GOALS + " WHERE " +
                COL_USER_ID_FK + "=? AND " + COL_GOAL_COMPLETED + "=1", new String[]{String.valueOf(userId)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }


    public int getTotalGoalsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_GOALS + " WHERE " +
                COL_USER_ID_FK + "=?", new String[]{String.valueOf(userId)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}