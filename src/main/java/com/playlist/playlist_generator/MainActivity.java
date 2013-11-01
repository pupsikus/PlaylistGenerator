package com.playlist.playlist_generator;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MainActivity extends ListActivity implements OnClickListener {
    final int REQUEST_CODE_OPTION = 1;
    public ArrayList<OptionsList> MusicOptionsList=new ArrayList<OptionsList>();
    public OtherBoxAdapter OptionBoxAdapter;
    private static final String[] EXTENSIONS = { ".mp3", ".mid", ".wav", ".ogg", ".mp4" }; //Playable Extensions
    List<String> trackNames; //Playable Track Titles
    Collection<String> trackNamesCollection;
    Random random; //used for shuffle
    File path;
    Button btnSelectFolder; //Button Select Folder
    Button btnGeneratePL; //Button Generate Play List
    TextView tvName;
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
                    MusicOptionsList.add(new OptionsList("Option " + ListSize,PathList,R.drawable.ic_launcher));
                    OptionBoxAdapter = new OtherBoxAdapter(this, MusicOptionsList);

                    ListView lvMain = (ListView) findViewById(android.R.id.list);
                    lvMain.setAdapter(OptionBoxAdapter);
                    tvName=(TextView) findViewById(R.id.hello_world);
                    tvName.setText("Folders generated!");
                    checkList();
                    break;
            }
            // если вернулось не ОК
        } else {
            Toast.makeText(this, "Wrong result", Toast.LENGTH_SHORT).show();
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

    private void PLGenerator(){
        String ItemPath;
        trackNames = new ArrayList<String>();
        for(int i=0;i < MusicOptionsList.size();i++){
            for(int j=0; j < MusicOptionsList.get(i).getOptionPathSize(); j++){
                //MusicOptionsList.get(i);
                ItemPath = MusicOptionsList.get(i).getPath(j);
                addTracks(getTracks(ItemPath));
            }

        }
        random = new Random();

    }

    //Generate a String Array that represents all of the files found
    private ArrayList<String > getTracks(String MusicFolderPath){
        path = new File(MusicFolderPath);
        String[] temp = path.list();
        File[] files = path.listFiles();
        ArrayList<String> dirFiles = new ArrayList<String>();
        for(File file : files){
            dirFiles.add(file.getAbsolutePath());
        }

        return dirFiles;
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
            Toast.makeText(getBaseContext(), "Loaded " + Integer.toString(trackNames.size()) + " Tracks", Toast.LENGTH_SHORT).show();
        }
    }


    //Checks to make sure that the track to be loaded has a correct extenson
    private boolean trackChecker(String trackToTest){
        for(int j = 0; j < EXTENSIONS.length; j++){
            if(trackToTest.contains(EXTENSIONS[j])){
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