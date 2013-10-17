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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends ListActivity implements OnClickListener {
    final int REQUEST_CODE_OPTION = 1;
    public ArrayList<OptionsList> MusicOptionsList=new ArrayList<OptionsList>();
    private OtherBoxAdapter OptionBoxAdapter;
    private static final String[] EXTENSIONS = { ".mp3", ".mid", ".wav", ".ogg", ".mp4" }; //Playable Extensions
    List<String> trackNames; //Playable Track Titles
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

        //Button click
        //btnSelectFolder.setOnClickListener(OnClickBtnSelectFolder);
        btnSelectFolder.setOnClickListener(this);
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
                trackNames = new ArrayList<String>();
                random = new Random();
                addTracks(getTracks());
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

    //Generate a String Array that represents all of the files found
    private String[] getTracks(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            path = Environment.getExternalStorageDirectory();
            String[] temp = path.list();
            return temp;
        }
        else {
            Toast.makeText(getBaseContext(), "SD Card is either mounted elsewhere or is unusable", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    //Adds the playable files to the trackNames List
    private void addTracks(String[] temp){
        if(temp != null){
            for(int i = 0; i < temp.length; i++){
                //Only accept files that have one of the extensions in the EXTENSIONS array
                if(trackChecker(temp[i])){
                    trackNames.add(temp[i]);
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
