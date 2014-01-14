package com.playlist.playlist_generator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PL_Path_Activity extends MyFileManager {
    private ArrayList<DirectoryList> dirEntries = new ArrayList<DirectoryList>();
    private String PathToPL;

    //when new activity starts it uses some layout
    @Override
    public void onCreate(Bundle iNew) {
        super.onCreate(iNew);
        setContentView(R.layout.activity_pl_file_manager);
        PathToPL = getIntent().getStringExtra("PathToPL");
        if (PathToPL.equals("False")){
            //browse to root directory
            browseTo(new File("/"));
        }
        else{
            //browse to last chosen directory
            browseTo(new File(PathToPL));
        }
        dirEntries = super.getDirEntries();
    }

    //When you click OK Button
    public void AddPathToList(View v){
        TextView TitleManager = (TextView)findViewById(R.id.titleManager);
        PathToPL = TitleManager.getText().toString();
        Intent MainIntent = new Intent();

        MainIntent.putExtra("PathToPL",PathToPL);
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
