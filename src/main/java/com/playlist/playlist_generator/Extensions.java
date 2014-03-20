package com.playlist.playlist_generator;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class Extensions extends ListActivity {
    private MyDB mydb;
    private  ArrayList<String> ExtList;
    private String LOG_TAG = "Extensions class:";
    private final String[] EXTENSIONS = { ".mp3", ".mid", ".wav", ".ogg", ".mp4", ".aac", ".flac", ".m4a" }; //Playable Extensions

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_extensions);

        mydb = new MyDB(this);
        ExtList = getExtensions(mydb);

    }

    private ArrayList<String> getExtensions(MyDB extdb){
        ArrayList<String> Extensions = new ArrayList<String>();
        SQLiteDatabase db = extdb.getWritableDatabase();
        try{
            Cursor c = db.query("plgTable",null,null,null,null,null,null);

            if (c.moveToFirst()) {
                int i = 0;
                int Extensions_ColIndex = c.getColumnIndex("extensions");
                do {
                    Log.d(LOG_TAG, "Extension = " + c.getString(Extensions_ColIndex));
                    Extensions.add(c.getString(Extensions_ColIndex));
                    i++;
                } while (c.moveToNext());
                c.close();
            }
        }catch (NullPointerException e){
            Log.d(LOG_TAG, "Table plgTable wasn't found, details:");
            e.printStackTrace();
        }
        if(Extensions.size() == 0) Extensions = ExtFirstLaunch(extdb);

        return Extensions;
    }

    private ArrayList<String> ExtFirstLaunch(MyDB extdb){
        Long rowID;
        ArrayList<String> Extensions = new ArrayList<String>();
        SQLiteDatabase db = extdb.getWritableDatabase();

        try{
            Cursor c = db.query("Extensions",null,null,null,null,null,null);
            ContentValues cv = new ContentValues();
            for(String ext : EXTENSIONS){
                Extensions.add(ext);
                cv.put("file_extensions", ext);
                cv.put("check", 1);
                rowID = db.insert("Extensions", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
            }
        }catch (NullPointerException e){
            Log.d(LOG_TAG, "Table Extensions wasn't found, details:");
            e.printStackTrace();
        }

        return Extensions;
    }
}
