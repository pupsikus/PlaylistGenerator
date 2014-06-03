package com.playlist.playlist_generator;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings_activity extends MyFileManager {
    final String LOG_TAG = "my_logs:";
    static final ArrayList<HashMap<String,String>> list =
            new ArrayList<HashMap<String,String>>();
    final int REQUEST_CODE_MUSIC_PATH = 1;
    final int REQUEST_CODE_PL_PATH = 2;
    final int REQUEST_CODE_EXTENSIONS = 3;
    View vew;
    //String DefaultPath = "/";
    String DefaultMusicPath;
    String DefaultPLPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setDbPath();
    }
    private void fillList() {
        HashMap<String,String> Back_row = new HashMap<String,String>();
        Back_row.put("header",getResources().getString(R.string.Settings_Back));
        //temp.put("description", DefaultPath);
        list.add(Back_row);

        HashMap<String,String> Music_path_row = new HashMap<String,String>();
        Music_path_row.put("header",getResources().getString(R.string.Settings_Music_Path));
        Music_path_row.put("description", DefaultMusicPath);
        list.add(Music_path_row);

        HashMap<String,String> PL_path_row = new HashMap<String,String>();
        PL_path_row.put("header",getResources().getString(R.string.Settings_PL_Path));
        PL_path_row.put("description", DefaultPLPath);
        list.add(PL_path_row);

        HashMap<String,String> File_types = new HashMap<String,String>();
        File_types.put("header","Music file types");
        list.add(File_types);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent IntentVar;
        switch (position){
            case 0: //Back
                Intent MainIntent = new Intent();
                setResult(RESULT_OK, MainIntent);
                finish();
                break;
            case 1: //Music path
                IntentVar = new Intent(this, PL_Path_Activity.class);
                IntentVar.putExtra("manager",true);
                startActivityForResult(IntentVar, REQUEST_CODE_MUSIC_PATH);
                break;
            case 2: //PL path
                IntentVar = new Intent(this, PL_Path_Activity.class);
                IntentVar.putExtra("PL",true);
                startActivityForResult(IntentVar, REQUEST_CODE_PL_PATH);
                break;
            case 3: //File types
                IntentVar = new Intent(this, Extensions.class);
                startActivityForResult(IntentVar, REQUEST_CODE_EXTENSIONS);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // запишем в лог значения requestCode и resultCode
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        //If result is positive
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_MUSIC_PATH:
                    setDbPath();
                    break;
                case REQUEST_CODE_PL_PATH:
                    setDbPath();
                    break;
                case REQUEST_CODE_EXTENSIONS:
                    break;
            }
        }
    }

    public String GetDefaultPath(String choice, MyDB mydb){
        String defaultPath = "";
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor c = db.query("plgTable",null,null,null,null,null,null);
        if (c.moveToFirst()) {
            if (choice.equals("Music")){
                int PathToMusic_ColIndex = c.getColumnIndex("music_path");
                defaultPath = c.getString(PathToMusic_ColIndex);
            }
            else if (choice.equals("PL")) {
                int PathToPL_ColIndex = c.getColumnIndex("pl_path");
                defaultPath = c.getString(PathToPL_ColIndex);
            }
        }
        if (defaultPath == null){
            defaultPath = "/";
        }
        else if (defaultPath.equals("")){
            defaultPath = "/";
        }
        c.close();
        return defaultPath;
    }

    public void buttonReset(View v){
        MyDB mydb = new MyDB(this);
        ContentValues cv = new ContentValues();
        String defaultPath = "";
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor c = db.query("plgTable",null,null,null,null,null,null);
        ArrayList<String> ExtensionsList = new ArrayList<String>();
        Extensions ExtObject = new Extensions();

        cv.put("pl_path", defaultPath);
        cv.put("music_path", defaultPath);
        if (c.moveToFirst()) {
            int rowID = db.update("plgTable", cv, "id=?", new String[]{"1"});
            Log.d(LOG_TAG, "row updated, ID = " + rowID);
        }
        c.close();

        int clearCount = db.delete("Extensions", null, null);
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);
        ExtensionsList = ExtObject.ExtFirstLaunch(mydb);

        mydb.close();

        setDbPath();
    }

    private void setDbPath(){
        MyDB mydb = new MyDB(this);
        DefaultMusicPath = GetDefaultPath("Music",mydb);
        DefaultPLPath = GetDefaultPath("PL",mydb);
        list.clear();
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.settings_row,
                new String[] {"header","description"},
                new int[] {R.id.settings_item,R.id.settings_sub_item}
        );
        fillList();
        mydb.close();
        setListAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent MainIntent = new Intent();
            setResult(RESULT_OK, MainIntent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
