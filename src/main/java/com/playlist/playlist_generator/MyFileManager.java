package com.playlist.playlist_generator;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MyFileManager extends ListActivity {
    private File currentDirectory = new File("/");
    private ArrayList<DirectoryList> directoryEntries = new ArrayList<DirectoryList>();
    private MainActivity MainSample = new MainActivity();
    private PL_Path_BoxAdapter boxAdapter;
    private boolean OnlyMusicFolders = false;

    //browse to file or directory
    public void browseTo(final File aDirectory){
        //if we want to browse directory
        if (aDirectory.isDirectory()){
            //fill list with files from this directory
            if (aDirectory.canRead()){
                this.currentDirectory = aDirectory;
                fill(aDirectory.listFiles());

                //set titleManager text
                TextView titleManager = (TextView) findViewById(R.id.titleManager);
                titleManager.setText(aDirectory.getAbsolutePath());
            }
            boxAdapter = new PL_Path_BoxAdapter(this, directoryEntries);
            // настраиваем список
            ListView lvMain = (ListView) findViewById(android.R.id.list);
            lvMain.setAdapter(boxAdapter);
        }
    }

    //fill list
    public void fill(File[] files) {
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
                else if(file.isDirectory() && file.canRead()){
                    if (OnlyMusicFolders){
                        if(FolderWithMusic(file.getAbsolutePath())) {
                            directoryEntries.add(new DirectoryList(file.getName(),file.getAbsolutePath(),R.drawable.ic_launcher, false));
                        }
                    }
                    else{
                        directoryEntries.add(new DirectoryList(file.getName(),file.getAbsolutePath(),R.drawable.ic_launcher, false));
                    }
                }
            }
        }
    }

    //browse to parent directory
    public void upOneLevel(){
        if(this.currentDirectory.getParent() != null) {
            browseTo(currentDirectory.getParentFile());
        }
    }

    //Search music in folders and subfolders
    public boolean FolderWithMusic(String directoryPath){
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

    public void SetOnlyMusicFolders(Boolean Flag){
        OnlyMusicFolders = Flag;
    }

    public ArrayList<DirectoryList> getDirEntries(){
        return directoryEntries;
    }

    public void SetCurrentDirectory(File CurDirPath){
            currentDirectory = CurDirPath;
    }
    public File GetCurrentDirectory(){
        return currentDirectory;
    }
}
