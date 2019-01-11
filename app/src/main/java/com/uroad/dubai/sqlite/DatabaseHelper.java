package com.uroad.dubai.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "rescue.db";
    public static final int DATABASE_VERSION_1 = 1;

    private static final String APP_USER_INFORMATION = "CREATE TABLE IF NOT EXISTS [user_information] ([_id] INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "[userid] NVARCHAR,"
            + "[name] NVARCHAR,"
            + "[nickname] NVARCHAR,"
            + "[sex] NVARCHAR,"
            + "[avatar] NVARCHAR,"
            + "[type] NVARCHAR,"
            + "[age] NVARCHAR);";


    private static DatabaseHelper instance;

    public synchronized static DatabaseHelper getInstance(Context c) {
        if (null == instance) {
            instance = new DatabaseHelper(c);
        }
        return instance;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION_1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(APP_USER_INFORMATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
