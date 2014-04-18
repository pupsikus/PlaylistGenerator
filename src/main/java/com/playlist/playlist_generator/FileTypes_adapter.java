package com.playlist.playlist_generator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class FileTypes_adapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<File_types> objects;
    MyDB mydb;
    String LOG_TAG = "My logs:";

    FileTypes_adapter(Context context, ArrayList<File_types> items) {
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mydb = new MyDB(context);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //List item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get view elements
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        File_types p = getListPosition(position);

        // Fill list with view elements: Item Description and its image
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.ItemDesc);
        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);
        CheckBox cbChoose = (CheckBox) view.findViewById(R.id.cbBox);

        //Write position
        cbChoose.setTag(position);
        //fill checkboxes
        cbChoose.setChecked(p.wasChecked);
        //Checkbox listener
        cbChoose.setOnCheckedChangeListener(myCheckChangList);
        return view;
    }

    File_types getListPosition(int position) {
        return ((File_types) getItem(position));
    }

    // List info
    ArrayList<File_types> getBox() {
        ArrayList<File_types> box = new ArrayList<File_types>();
        for (File_types p : objects) {
            //if was checked
            if (p.wasChecked)
                box.add(p);
        }
        return box;
    }

    // Checkbox listener
    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            Integer iPosition = (Integer) buttonView.getTag();
            String file_ext = objects.get(iPosition).ItemDesc;

            SQLiteDatabase db = mydb.getWritableDatabase();
            try{
                String columns[]={"file_extensions", "check_field"};
                String selection1;
                selection1 = "file_extensions = ?";
                String[] selection2 = {file_ext};
                Cursor c = db.query("Extensions",columns,selection1,selection2,null,null,null);
                if (c.moveToFirst()) {
                    //Extension exists
                    ContentValues cv = new ContentValues();
                    int Extensions_ColIndex = c.getColumnIndex("file_extensions");
                    int Check_field_ColIndex = c.getColumnIndex("check_field");
                    Log.d(LOG_TAG, "Extension = " + c.getString(Extensions_ColIndex));
                    int check_value = c.getInt(Check_field_ColIndex);
                    Log.d(LOG_TAG, "Check value = " + check_value);
                    if(isChecked){
                        cv.put("check_field", 1);
                        int rowID = db.update("Extensions",cv,selection1,selection2);
                        Log.d(LOG_TAG, "UpdatedRowId = " + rowID);
                    }
                    else{
                        cv.put("check_field", 0);
                        int rowID = db.update("Extensions",cv,selection1,selection2);
                        Log.d(LOG_TAG, "UpdatedRowId = " + rowID);
                    }
                }
                else {
                    Log.d(LOG_TAG, "Extensiion: " + file_ext + " wasn't found in database. Error");
                }
                c.close();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }


            //<ark checkbox on change
            getListPosition((Integer) buttonView.getTag()).wasChecked = isChecked;
        }
    };

    public String getExtItemDesc(int position){

        String ItemExtDesc =  objects.get(position).ItemDesc;
        return ItemExtDesc;
    }
}
