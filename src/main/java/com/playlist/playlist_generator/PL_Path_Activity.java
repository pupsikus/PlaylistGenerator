package com.playlist.playlist_generator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PL_Path_Activity extends MyFileManager {
    private ArrayList<DirectoryList> dirEntries = new ArrayList<DirectoryList>();
    private String PathToPL;
    final String LOG_TAG = "myLogs";
    private Boolean isMainActivity;
    private Boolean isPL;
    private Settings_activity sa = new Settings_activity();
    private String DefaultPath;
    private MyDB mydb;

    //when new activity starts it uses some layout
    @Override
    public void onCreate(Bundle iNew) {
        super.onCreate(iNew);
        setContentView(R.layout.activity_pl_file_manager);
        mydb = new MyDB(this);
        PathToPL = getIntent().getStringExtra("PathToPL");
        isPL = getIntent().getBooleanExtra("PL", false);
        isMainActivity = getIntent().getBooleanExtra("MainActivity",false);

        if (isPL){
            DefaultPath = sa.GetDefaultPath("PL", mydb);
        }
        else{
            DefaultPath = sa.GetDefaultPath("Music", mydb);
            super.SetOnlyMusicFolders(true);
        }

        if(isMainActivity){
            if (PathToPL.equals("False")){
                //browse to root directory
                browseTo(new File(DefaultPath));
            }
            else{
                //browse to last chosen directory
                browseTo(new File(PathToPL));
            }
        }
        else {
            browseTo(new File(DefaultPath));
        }
        dirEntries = super.getDirEntries();
    }

    //When you click OK Button
    public void AddPathToList(View v){
        TextView TitleManager = (TextView)findViewById(R.id.titleManager);
        PathToPL = TitleManager.getText().toString();
        long rowID;

        ContentValues cv = new ContentValues();
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor c = db.query("plgTable",null,null,null,null,null,null);
        if (!isMainActivity && isPL){
            cv.put("pl_path", PathToPL);
            if (c.moveToFirst()) {
                rowID = db.update("plgTable", cv, "id=?", new String[]{"1"});
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
            }
            else{
                rowID = db.insert("plgTable", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
            }

        }
        else if (!isMainActivity && !isPL){
            cv.put("music_path", PathToPL);
            if (c.moveToFirst()) {
                rowID = db.update("plgTable", cv, "id=?", new String[]{"1"});
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
            }
            else{
                rowID = db.insert("plgTable", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
            }
        }
        c.close();

        Intent MainIntent = new Intent();
        MainIntent.putExtra("PathToPL",PathToPL);
        MainIntent.putExtra("MusicPath",PathToPL);
        setResult(RESULT_OK, MainIntent);
        finish();
     }

    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected file name
        DirectoryList selectedFileString = dirEntries.get(position);

        //if we select ".." then go upper
        if(selectedFileString.getPath().equals("..")){
            upOneLevel();
        }
        else {
            //browse to clicked file or directory using browseTo()
            File clickedFile;
            clickedFile = new File(selectedFileString.getPath());
            if (clickedFile != null)
                browseTo(clickedFile);

        }
    }
}
