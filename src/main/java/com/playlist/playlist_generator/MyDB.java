package com.playlist.playlist_generator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDB extends SQLiteOpenHelper {
    private String LOG_TAG = "MyLog:";
    public MyDB(Context context) {
        // конструктор суперкласса
        super(context, "plgDB", null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table plgTable ("
                + "id integer primary key autoincrement,"
                + "pl_path text,"
                + "music_path text" + ");");

        db.execSQL("create table Extensions ("
                + "id integer primary key autoincrement,"
                + "file_extensions text," + "check integer" + ");");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == 2 && oldVersion == 1){
            db.execSQL("create table Extensions ("
                    + "id integer primary key autoincrement,"
                    + "file_extensions text," + "check integer" + ");");
        }
    }
}

