package com.playlist.playlist_generator;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ListActivity implements OnClickListener {
    final String LOG_TAG = "myLogs";
    final int REQUEST_CODE_OPTION = 1;
    public ArrayList<OptionsList> MusicOptionsList=new ArrayList<OptionsList>();
    public OtherBoxAdapter OptionBoxAdapter;
    private static final String[] EXTENSIONS = { ".mp3", ".mid", ".wav", ".ogg", ".mp4" }; //Playable Extensions
    private static long NumOfTracks;
    List<String> trackNames; //Playable Track Titles
    Button btnSelectFolder; //Button Select Folder
    Button btnGeneratePL; //Button Generate Play List
    EditText etSongCounter;
    int ListSize;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Search button by ID
        btnSelectFolder = (Button) findViewById(R.id.button_select_folder);
        btnGeneratePL = (Button) findViewById(R.id.generate);
        //Button click
        btnSelectFolder.setOnClickListener(this);
        btnGeneratePL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_select_folder:
                //Trying to create new activity fir file manager
                Intent intent = new Intent(this, FileManager_Activity.class);
                startActivityForResult(intent, REQUEST_CODE_OPTION);
                break;
            case R.id.generate:
                // TODO Auto-generated method stub
                PLGenerator();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // запишем в лог значения requestCode и resultCode
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        // если пришло ОК
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_OPTION:
                    ArrayList<String> PathList = data.getStringArrayListExtra("ArrayMusicDirList");
                    ListSize=MusicOptionsList.size()+1;
                    MusicOptionsList.add(new OptionsList("Option " + ListSize,PathList,R.drawable.ic_launcher,"1"));
                    OptionBoxAdapter = new OtherBoxAdapter(this, MusicOptionsList);

                    ListView lvMain = (ListView) findViewById(android.R.id.list);
                    lvMain.setAdapter(OptionBoxAdapter);
                    lvMain.setItemsCanFocus(true);
                    checkList();
                    break;
            }
            // если вернулось не ОК
        } else {
            //Toast.makeText(this, "Wrong result", Toast.LENGTH_SHORT).show();
        }
    }

    //Check if list exists. If so - unhide button Generate
    private void checkList(){
        if(MusicOptionsList.size()!=0){
            btnGeneratePL=(Button) findViewById(R.id.generate);
            btnGeneratePL.setVisibility(View.VISIBLE);
        }
        else{
            btnGeneratePL.setVisibility(View.GONE);
        }
    }

    //Generate playlist
    private void PLGenerator(){
        String ItemPath;
        trackNames = new ArrayList<String>();
        ArrayList<ArrayList<String>> dirFiles = new ArrayList<ArrayList<String>>();
        //Looking for directories with songs from Folder Lists
        for(int i=0;i < MusicOptionsList.size();i++){
            dirFiles.add(new ArrayList<String>());
            for(int j=0; j < MusicOptionsList.get(i).getOptionPathSize(); j++){
                ItemPath = MusicOptionsList.get(i).getPath(j);
                addTracks(getTracks(ItemPath, dirFiles.get(i)));
            }
            Collections.shuffle(dirFiles.get(i));
        }
        CreatePList(dirFiles);
        Toast.makeText(getBaseContext(), "Loaded " + Long.toString(NumOfTracks) + " Tracks", Toast.LENGTH_SHORT).show();

    }

    private void CreatePList(ArrayList<ArrayList<String>> OptionsFilesList){
        //Try code and catch exceptions
        try {
            //Check for mounted SD
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
                return;
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/Music","Test.m3u");
            PrintWriter writer = new PrintWriter(file, "UTF-8");

            // Write path to song to the file
            for (int i=0;i<OptionsFilesList.size();i++){
                for(int j=0;j<OptionsFilesList.get(i).size();j++){
                    writer.println(OptionsFilesList.get(i).get(j)+"\r");
                }
            }
           /* ensure that everything is
            * really written out and close */
            writer.flush();
            writer.close();
        }
        catch (IOException ioe)
        {ioe.printStackTrace();}
    }

    //Generate a String Array that represents all of the files found
    private ArrayList<String> getTracks(String directoryName, ArrayList<String> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if(trackChecker(file.getName())){
                    files.add(file.getAbsolutePath());
                }
            } else if (file.isDirectory()) {
                getTracks(file.getAbsolutePath(), files);
            }
        }
        return files;
    }

    //Adds the playable files to the trackNames List
    private void addTracks(ArrayList<String>  dirFiles){
        if(dirFiles != null){
            for(int i = 0; i < dirFiles.size(); i++){
                //Only accept files that have one of the extensions in the EXTENSIONS array

                if(trackChecker(dirFiles.get(i))){
                    trackNames.add(dirFiles.get(i));
                    //trackNamesCollection.add(temp[i]);
                }
            }
            //Toast.makeText(getBaseContext(), "Loaded " + Integer.toString(trackNames.size()) + " Tracks", Toast.LENGTH_SHORT).show();
            NumOfTracks= trackNames.size();
        }
    }

    //Checks to make sure that the track to be loaded has a correct extenson
    private boolean trackChecker(String trackToTest){
        for (String EXTENSION : EXTENSIONS) {
            if (trackToTest.contains(EXTENSION)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
