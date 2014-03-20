package com.playlist.playlist_generator;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ListActivity implements OnClickListener {
    final String LOG_TAG = "myLogs";
    final int REQUEST_CODE_OPTION_FM = 1;
    final int REQUEST_CODE_OPTION_PL_FM = 2;
    final int SDK_VERSION = Integer.valueOf(android.os.Build.VERSION.SDK);
    public ArrayList<OptionsList> MusicOptionsList=new ArrayList<OptionsList>();
    public OtherBoxAdapter OptionBoxAdapter;
    private static final String[] EXTENSIONS = { ".mp3", ".mid", ".wav", ".ogg", ".mp4", ".aac", ".flac", ".m4a" }; //Playable Extensions
    private String PathToPL;
    private String PathToMusicFolder = "";
    private File file;
    private MyDB mydb;

    private Intent IntentVar;

    List<String> trackNames; //Playable Track Titles
    Button btnSelectFolder; //Button Select Folder
    Button btnGeneratePL; //Button Generate Play List
    Button btnExitApp; //Exit application
    Button btnPathToPL;

    TextView tvPathToPL;
    EditText etPLName;
    CheckBox cbPathToPL;
    TableLayout tlPLName;
    LinearLayout LL_PathToPL;
    int ListSize;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Search button by ID
        btnSelectFolder = (Button) findViewById(R.id.button_select_folder);
        btnGeneratePL = (Button) findViewById(R.id.generate);
        btnExitApp = (Button) findViewById(R.id.ExitApp);
        btnPathToPL=(Button) findViewById(R.id.btnPathToPL);

        //Button click
        btnSelectFolder.setOnClickListener(this);
        btnGeneratePL.setOnClickListener(this);
        btnExitApp.setOnClickListener(this);
        btnPathToPL.setOnClickListener(this);

        tlPLName=(TableLayout) findViewById(R.id.tlPLName);
        tvPathToPL = (TextView) findViewById(R.id.tvPathToPL);
        cbPathToPL =(CheckBox)findViewById(R.id.cbPL_path);
        LL_PathToPL=(LinearLayout)findViewById(R.id.LLPathToPL);

        mydb = new MyDB(this);
        GetDefaultPLPtah(btnPathToPL);

        AppRater RateMe = new AppRater();
        RateMe.app_launched(this);
        //RateMe.showRateDialog(this,null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_select_folder:
                //Start Music File Manager activity
                IntentVar = new Intent(this, FileManager_Activity.class);
                if(PathToMusicFolder.equals("")){
                    IntentVar.putExtra("FirstChoice",true);
                    IntentVar.putExtra("PathToMusicFolder",PathToMusicFolder);
                }
                else
                {
                    IntentVar.putExtra("FirstChoice",false);
                    IntentVar.putExtra("PathToMusicFolder",PathToMusicFolder);
                }
                startActivityForResult(IntentVar, REQUEST_CODE_OPTION_FM);
                break;
            case R.id.generate:
                PLGenerator_Button();
                break;
            case R.id.ExitApp:
                ExitApp();
                break;
            case R.id.btnPathToPL:
                //Start Path File Manager activity
                IntentVar = new Intent(this, PL_Path_Activity.class);
                String PathToPL = btnPathToPL.getText().toString();
                if (!PathToPL.equals(getString(R.string.String_PathToPL))){
                    //Last choice
                    IntentVar.putExtra("PathToPL",PathToPL);
                }
                else {
                    //First choice
                    IntentVar.putExtra("PathToPL","False");
                }
                IntentVar.putExtra("MainActivity",true);
                IntentVar.putExtra("PL",true);
                startActivityForResult(IntentVar, REQUEST_CODE_OPTION_PL_FM);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        //If result is positive
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_OPTION_FM:
                    //TODO Auto-generated method stub
                    ArrayList<String> PathList = data.getStringArrayListExtra("ArrayMusicDirList");
                    PathToMusicFolder = data.getStringExtra("MainFolderPath");

                    String Option = AddOption(PathList);
                    ListSize=MusicOptionsList.size()+1;
                    //MusicOptionsList.add(new OptionsList("Option " + ListSize,PathList,R.drawable.ic_launcher,""));
                    MusicOptionsList.add(new OptionsList(Option,PathList,R.drawable.ic_launcher,""));
                    OptionBoxAdapter = new OtherBoxAdapter(this, MusicOptionsList);

                    ListView lvMain = (ListView) findViewById(android.R.id.list);
                    lvMain.setAdapter(OptionBoxAdapter);
                    lvMain.setItemsCanFocus(true);
                    checkList();
                    break;
                case REQUEST_CODE_OPTION_PL_FM:
                    PathToPL = data.getStringExtra("PathToPL");
                    btnPathToPL.setText(PathToPL);
                    break;
            }
        }
    }

    //Check if list exists. If so - unhide button Generate
    private void checkList(){
        if(MusicOptionsList.size()!=0){
            btnGeneratePL.setVisibility(View.VISIBLE);
            tlPLName.setVisibility(View.VISIBLE);
            btnPathToPL.setVisibility(View.VISIBLE);
            tvPathToPL.setVisibility(View.VISIBLE);
            LL_PathToPL.setVisibility(View.VISIBLE);
        }
        else{
            btnGeneratePL.setVisibility(View.GONE);
            tlPLName.setVisibility(View.GONE);
            btnPathToPL.setVisibility(View.GONE);
            tvPathToPL.setVisibility(View.GONE);
            LL_PathToPL.setVisibility(View.GONE);
        }
    }

    //Generate playlist
    private void PLGenerator_Button(){
        String ItemPath;
        Boolean IsMusicOption = true;
        Intent UpdateMediaIntent;

        if (btnPathToPL.getText().toString().equals(getResources().getString(R.string.btnPathToPL))){
            Toast.makeText(getBaseContext(), getResources().getString(R.string.ChoosePathToPL), Toast.LENGTH_SHORT).show();
            return;
        }
        UpdateMediaIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));

        if(cbPathToPL.isChecked()){
            UpdateDefaultPLPath(btnPathToPL.getText().toString());
        }

        trackNames = new ArrayList<String>();
        ArrayList<ArrayList<String>> dirFiles = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> dirAllFiles = new ArrayList<ArrayList<String>>();
        dirAllFiles.add(new ArrayList<String>());
        //Looking for directories with songs from Folder Lists
        for(int i=0;i < MusicOptionsList.size();i++){
            dirFiles.add(new ArrayList<String>());
            for(int j=0; j < MusicOptionsList.get(i).getOptionPathSize(); j++){
                ItemPath = MusicOptionsList.get(i).getPath(j);
                addTracks(getTracks(ItemPath, dirFiles.get(i)));
                addTracks(getTracks(ItemPath, dirAllFiles.get(0)));
            }
            if (dirFiles.get(i).size()>0) {Collections.shuffle(dirFiles.get(i));}
            else {IsMusicOption=false;}
        }
        if (dirAllFiles.get(0).size()>0) {Collections.shuffle(dirAllFiles.get(0));}
        else {IsMusicOption=false;}

        if (IsMusicOption == true){
            if (IsSimplePL(dirFiles)){
                CreatePList(dirAllFiles);
            }
            else{
                CreatePList(dirFiles);
            }

            //Updates Media Files indexes in memory
            if (SDK_VERSION < 19){sendBroadcast(UpdateMediaIntent);}

            Toast.makeText(getBaseContext(), getResources().getString(R.string.Done), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getBaseContext(), getResources().getString(R.string.NotDone), Toast.LENGTH_SHORT).show();
        }
    }

    private void CreatePList(ArrayList<ArrayList<String>> OptionsFilesList){
        //Try code and catch exceptions
        String PLName;
        ArrayList<Integer> SongCounterList = new ArrayList<Integer>();
        long NumOfSongs[] = new long[OptionsFilesList.size()];
        long NumOfIterations = 0;
        long tempVar = 0;
        long index = 0;
        int arr_of_indexes[][]; //[0][1]=3 - SongCounterList.get(0) - start position will be 3
        //[0][0]=2 - SongCounterList.get(0) = 2, num song per option. The same as SongCounterList
        int counter;

        arr_of_indexes = new int [OptionsFilesList.size()][2];
        try {
            PLName=PLName();
            //new file for Playlist
            //file = new File(Environment.getExternalStorageDirectory() + "/Music",PLName);
            file = new File(PathToPL,PLName);
            PrintWriter writer = new PrintWriter(file, "utf-8");

            // Save song counters values and calculate number of iterations as minimum of all possible iterations
            for (int i=0;i<OptionsFilesList.size();i++){
                SongCounterList.add(SongCounter(OptionsFilesList,i));
                NumOfSongs[i]=OptionsFilesList.get(i).size();
                arr_of_indexes[i][0]=SongCounterList.get(i);
                arr_of_indexes[i][1]=0;
                if (arr_of_indexes[i][0]==0){
                    tempVar = 0;
                }
                else{
                    tempVar = NumOfSongs[i]/arr_of_indexes[i][0];
                }

                if (NumOfIterations == 0){
                    NumOfIterations = tempVar;
                }
                else if(NumOfIterations > 0 && tempVar != 0 && NumOfIterations > tempVar){
                    NumOfIterations = tempVar;
                }
            }

            while (index!=NumOfIterations){
                index=index+1;
                for (int i=0; i<OptionsFilesList.size(); i++){
                    counter=0;
                    if (SongCounterList.get(i)!=0){
                        for(int j=arr_of_indexes[i][1]; j<OptionsFilesList.get(i).size(); j++){
                            // Write path to song to the file
                            writer.println(OptionsFilesList.get(i).get(j)+"\r");
                            counter=counter+1;
                            if(counter==arr_of_indexes[i][0]){ //num of songs per option equals counter
                                arr_of_indexes[i][1]=j+1;
                                break;
                            }
                        }
                    }
                }
            }
           /* ensure that everything is
            * really written out and close */
            writer.flush();
            writer.close();

        }
        catch (IOException ioe)
        {
            Log.d(LOG_TAG, "Файл является null или путь к файлу равен null ");
            ioe.printStackTrace();
        }
    }

    private int SongCounter(ArrayList<ArrayList<String>> OptionsFilesList,int index){
        ListView lvMain = (ListView) findViewById(android.R.id.list);
        View view;
        EditText etSongCounter;
        Integer SongCounter=0;

        view = lvMain.getChildAt(index);
        etSongCounter=(EditText) view.findViewById(R.id.OptionSongCounter);
        try {
            SongCounter = Integer.parseInt(etSongCounter.getText().toString());
            if (SongCounter == 0){
                Log.d(LOG_TAG, "Счетчик песен равен 0. Ошибка ");
            }
            else if(SongCounter > OptionsFilesList.get(index).size()){
                SongCounter = OptionsFilesList.get(index).size();
            }
        }
        catch (NumberFormatException ioe)
        {
            Log.d(LOG_TAG, "Не удалось конвертировать счестчик песен в тип Long ");
            SongCounter = OptionsFilesList.get(index).size();
        }
        catch (NullPointerException npe){
            SongCounter = OptionsFilesList.get(index).size();
        }
        return SongCounter;
    }

    private boolean IsSimplePL(ArrayList<ArrayList<String>> OptionsFilesList){
        ListView lvMain = (ListView) findViewById(android.R.id.list);
        View view;
        EditText etSongCounter;
        Integer SongCounter=0;
        boolean IsTrue = false;
        for (int i=0; i<OptionsFilesList.size(); i++){
            view = lvMain.getChildAt(i);
            etSongCounter=(EditText) view.findViewById(R.id.OptionSongCounter);
            try {
                SongCounter = Integer.parseInt(etSongCounter.getText().toString());
                if (SongCounter == 0){
                    Log.d(LOG_TAG, "Счетчик песен равен 0. Ошибка ");
                    return false;
                }
                else if(SongCounter > 0){
                    return false;
                }
            }
            catch (NumberFormatException ioe)
            {
                Log.d(LOG_TAG, "Не удалось конвертировать счестчик песен в тип Long ");
                SongCounter = OptionsFilesList.get(i).size();
                IsTrue = true;
            }
            catch (NullPointerException npe){
                SongCounter = OptionsFilesList.get(i).size();
                return false;
            }
        }
        return IsTrue;
    }

    public void DelListElemButton(View v){
        int itemToRemove  = getListView().getPositionForView(v);
        MusicOptionsList.remove(itemToRemove);
        OptionBoxAdapter = new OtherBoxAdapter(this, MusicOptionsList);

        ListView lvMain = (ListView) findViewById(android.R.id.list);
        lvMain.setAdapter(OptionBoxAdapter);
        lvMain.setItemsCanFocus(true);
        checkList();
    }

    //Generate a String Array that represents all of the files found
    private ArrayList<String> getTracks(String directoryName, ArrayList<String> files) {
        File directory = new File(directoryName);

        //directory may contain only way to file
        if (directory.isDirectory()){
            // get all the files from a directory
            try{
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
            }
            catch (NullPointerException ex){
                return files;
            }

        }
        else if(directory.isFile()){
            if(trackChecker(directory.getName())){
                files.add(directory.getAbsolutePath());
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
                }
            }
        }
    }

    //Checks to make sure that the track to be loaded has a correct extension
    public boolean trackChecker(String trackToTest){
        for (String EXTENSION : EXTENSIONS) {
            if (trackToTest.contains(EXTENSION)) {
                return true;
            }
        }
        return false;
    }

    private void UpdateDefaultPLPath(String PathToPL){
        long rowID;
        if (!PathToPL.equals(getResources().getString(R.string.btnPathToPL))){
            ContentValues cv = new ContentValues();
            SQLiteDatabase db = mydb.getWritableDatabase();
            Cursor c = db.query("plgTable",null,null,null,null,null,null);
            cv.put("pl_path", PathToPL);
            if (c.moveToFirst()) {
                rowID = db.update("plgTable", cv, "id=?", new String[]{"1"});
                Log.d(LOG_TAG, "row updated, ID = " + rowID);
            }
            else{
                rowID = db.insert("plgTable", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
            }
            c.close();
        }
    }

    private void GetDefaultPLPtah(Button btnPathToPL){
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor c = db.query("plgTable",null,null,null,null,null,null);
        if (c.moveToFirst()) {
            int PathToPL_ColIndex = c.getColumnIndex("pl_path");
            String defaultPlPath = c.getString(PathToPL_ColIndex);
            if (defaultPlPath!=null && !defaultPlPath.equals("")){
                btnPathToPL.setText(defaultPlPath);
                PathToPL = defaultPlPath;
            }
        }
        c.close();
    }

    private String PLName(){
        String PLName;
        //Check for mounted SD and create name for PL
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
        }
        etPLName=(EditText) findViewById(R.id.etPLName);
        if(etPLName.getText().toString().length() == 0){
            PLName = getString(R.string.etPLName);
        }
        else{
            PLName = etPLName.getText().toString();
        }
        PLName = PLName + ".m3u";
        return PLName;
    }

    private String AddOption(ArrayList<String> PathList){
        String Option = getResources().getString(R.string.Option);
        for(String OptionPath : PathList){
            String SubOption = "";
            for(int i = OptionPath.length(); i > -1; i--){
                if(OptionPath.indexOf("/", i)>=0){
                    if(SubOption.equals("")){
                        SubOption = OptionPath.substring(i);
                    }
                    else{
                        SubOption = ".." + SubOption;
                        break;
                    }
                }
            }
            Option = Option + " " + SubOption + ";";
        }
        return Option;
    }

    private void ExitApp(){
        mydb.close();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                IntentVar = new Intent(this, Settings_activity.class);
                startActivity(IntentVar);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
