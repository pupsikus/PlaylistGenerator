package com.playlist.playlist_generator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class FileManager_Activity extends ListActivity {
    final String LOG_TAG = "myLogs";
    private File currentDirectory = new File("/");
    private ArrayList<DirectoryList> directoryEntries = new ArrayList<DirectoryList>();
    private BoxAdapter boxAdapter;
    private String PathToMainFolder = "";
    private MainActivity MainSample = new MainActivity();

    //when new activity starts it uses some layout
    @Override
    public void onCreate(Bundle iNew) {
        Boolean FirstStart;
        super.onCreate(iNew);
        //set main layout
        setContentView(R.layout.activity_file_manager);
        FirstStart = getIntent().getBooleanExtra("FirstChoice",true);
        if(FirstStart){
            //browse to root directory
            browseTo(new File("/"));
        }
        else {
            //browse to last chosen directory
            PathToMainFolder = getIntent().getStringExtra("PathToMusicFolder");
            browseTo(new File(PathToMainFolder));
        }


    }

    //browse to file or directory
    private void browseTo(final File aDirectory){
        //if we want to browse directory
        if (aDirectory.isDirectory()){
            //fill list with files from this directory
            if (aDirectory.canRead()){
                this.currentDirectory = aDirectory;
                fill(aDirectory.listFiles());

                //set titleManager text
                TextView titleManager = (TextView) findViewById(R.id.titleManager);
                titleManager.setText(aDirectory.getAbsolutePath());

                boxAdapter = new BoxAdapter(this, directoryEntries);

                // настраиваем список
                ListView lvMain = (ListView) findViewById(android.R.id.list);
                lvMain.setAdapter(boxAdapter);
            }
        }
        else {
            //if we want to open file, show this dialog:
            //listener when YES button clicked
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
                    //or add something you want
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
    //fill list of directories
    private void fill(File[] files) {
        //clear list
        directoryEntries.clear();

        if (this.currentDirectory.getParent() != null){
            directoryEntries.add(new DirectoryList("..","..", R.drawable.ic_launcher, false));
        }
        //add every file into list
        for (File file : files) {
            if (file.canRead()){
                if(file.isFile()){
                    if(MainSample.trackChecker(file.getName())){
                        directoryEntries.add(new DirectoryList(file.getName(),file.getAbsolutePath(),R.drawable.ic_launcher, false));
                    }
                }
                else if(file.isDirectory()){
                    if(FolderWithMusic(file.getAbsolutePath())) {
                        directoryEntries.add(new DirectoryList(file.getName(),file.getAbsolutePath(),R.drawable.ic_launcher, false));
                    }
                }
            }
        }
    }

    //browse to parent directory
    private void upOneLevel(){
        if(this.currentDirectory.getParent() != null) {
            browseTo(currentDirectory.getParentFile());
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
            PathToMainFolder = this.currentDirectory.toString();
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

    //Search music in folders and subfolders
    private boolean FolderWithMusic(String directoryPath){
        Cursor cursor;
        String selection;
        String[] projection = {MediaStore.Audio.Media.IS_MUSIC};
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //Create query for searching media files in folder
        selection = MediaStore.Audio.Media.DATA + " like " + "'%" + directoryPath + "/%'";
        cursor = getContentResolver().query(uri, projection, selection, null, null);
        if (cursor != null) {
            boolean isDataPresent;
            isDataPresent = cursor.moveToFirst();
            return isDataPresent;
        }
        return false;
    }

    //when you clicked onto item
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected file name
        DirectoryList selectedFileString= directoryEntries.get(position);

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
