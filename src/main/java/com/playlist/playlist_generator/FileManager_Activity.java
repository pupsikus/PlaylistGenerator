package com.playlist.playlist_generator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class FileManager_Activity extends MyFileManager {
    final String LOG_TAG = "myLogs";
    private ArrayList<DirectoryList> dirEntries = new ArrayList<DirectoryList>();
    private BoxAdapter boxAdapter;
    private String PathToMainFolder = "";
    private Settings_activity sa = new Settings_activity();
    private String DefaultPath;
    private MyDB mydb;

    //when new activity starts it uses some layout
    @Override
    public void onCreate(Bundle iNew) {
        Boolean Bool_FirstStart;
        super.onCreate(iNew);
        //set main layout
        setContentView(R.layout.activity_file_manager);
        SetOnlyMusicFolders(true);
        mydb = new MyDB(this);
        DefaultPath = sa.GetDefaultPath("Music",mydb);
        Bool_FirstStart = getIntent().getBooleanExtra("FirstChoice",true);
        if(Bool_FirstStart){
            //browse to root directory
            browseTo(new File(DefaultPath));
        }
        else {
            //browse to last chosen directory
            PathToMainFolder = getIntent().getStringExtra("PathToMusicFolder");
            browseTo(new File(PathToMainFolder));
        }
    }
    //browse to file or directory
    @Override
    protected void browseTo(final File aDirectory){
        //if we want to browse directory
        if (aDirectory.isDirectory()){
            //fill list with files from this directory
            if (aDirectory.canRead()){
                super.SetCurrentDirectory(aDirectory);
                fill(aDirectory.listFiles());
                dirEntries = super.getDirEntries();
                //set titleManager text
                TextView titleManager = (TextView) findViewById(R.id.titleManager);
                titleManager.setText(aDirectory.getAbsolutePath());

                boxAdapter = new BoxAdapter(this, dirEntries);

                // настраиваем список
                ListView lvMain = (ListView) findViewById(android.R.id.list);
                lvMain.setAdapter(boxAdapter);
            }
        }
        else {
            //open file dialog:
            DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {
                    //intent to navigate file
                    Intent OpenFileIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("file://" + aDirectory.getAbsolutePath()));
                    //start this activity
                    startActivity(OpenFileIntent);
                }
            };
            //listener when NO button clicked
            DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    //do nothing
                }
            };

            //create dialog
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.AcceptChoice)) //title
                    .setMessage(getResources().getString(R.string.OpenFileMessage) + "'" + aDirectory.getName() + "' ?") //message
                    .setPositiveButton(getResources().getString(R.string.PositiveButton), okButtonListener) //positive button
                    .setNegativeButton(getResources().getString(R.string.NegativeButton), cancelButtonListener) //negative button
                    .show(); //show dialog
        }
    }

    //When you click OK Button
    public void AddPathToList(View v){
        ArrayList<String> FoldersList = new ArrayList<String>();
        for(DirectoryList dl : boxAdapter.getBox()){
            //If item is checked then boolean wasChecked = true
            if(dl.wasChecked){
                FoldersList.add(dl.getPath());
            }
        }
        if (FoldersList.size()!=0){
            PathToMainFolder = GetCurrentDirectory().toString();
            Intent MainIntent = new Intent();
            MainIntent.putExtra("ArrayMusicDirList", FoldersList);
            MainIntent.putExtra("MainFolderPath", PathToMainFolder);
            setResult(RESULT_OK, MainIntent);
            this.finish();

        }
        else{
            /*
            create dialog when no one where checked, but button OK was pressed
            It means to take current folder
            listener when OK button clicked
            */
            DialogInterface.OnClickListener OkMusicButtonListener = new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {
                    ArrayList<String> NewFoldersList = new ArrayList<String>();
                    TextView titleManager;
                    String FolderPath;


                    titleManager = (TextView) findViewById(R.id.titleManager);
                    try {
                        PathToMainFolder = titleManager.getText().toString();
                    }
                    catch (NullPointerException npe){
                        Log.d(LOG_TAG, " TextView filed (title) doesn't contains text. Null pointer exception.");
                        PathToMainFolder = "";
                    }

                    FolderPath = titleManager.getText().toString();
                    NewFoldersList.add(FolderPath);
                    Intent MainIntent = new Intent();

                    MainIntent.putExtra("ArrayMusicDirList", NewFoldersList);
                    MainIntent.putExtra("MainFolderPath", PathToMainFolder);
                    setResult(RESULT_OK, MainIntent);
                    finish();

                }
            };
            //listener when NO button clicked
            DialogInterface.OnClickListener CancelMusicButtonListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    //do nothing
                    //or add something you want
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.SetTitleChooseFolder)); //title
                builder.setMessage(getResources().getString(R.string.SetMessageChooseFolder)); //message
                builder.setPositiveButton(getResources().getString(R.string.PositiveButton), OkMusicButtonListener); //positive button
                builder.setNegativeButton(getResources().getString(R.string.NegativeButton), CancelMusicButtonListener); //nsegative button
                builder.show(); //show dialog
        }
    }

    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected file name
        DirectoryList selectedFileString= dirEntries.get(position);

        //if we select ".." then go upper
        if(selectedFileString.getPath().equals("..")){
            upOneLevel();
        } else {
            //browse to clicked file or directory using browseTo()
            File clickedFile;
            clickedFile = new File(selectedFileString.getPath());
            if (clickedFile != null)
                browseTo(clickedFile);
        }
    }
}
