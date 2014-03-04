package com.playlist.playlist_generator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<DirectoryList> objects;
    MainActivity ma = new MainActivity();

    BoxAdapter(Context context, ArrayList<DirectoryList> items) {
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        String ItemDesc;
        File file;

        //Get view elements
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        DirectoryList p = getListPosition(position);

        // Fill list with view elements: Item Description and its image
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.ItemDesc);
        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);
        CheckBox cbChoose = (CheckBox) view.findViewById(R.id.cbBox);

        ItemDesc = p.ItemDesc;
        if (ItemDesc.equals("..")){
            cbChoose.setClickable(false);
            cbChoose.setVisibility(View.GONE);
        }
        else{
            file = new File(p.ItemPath);
            if(!file.isDirectory()){
                if(!ma.trackChecker(p.ItemDesc)){
                    cbChoose.setClickable(false);
                    cbChoose.setVisibility(View.GONE);
                }
            }
        }

        //Checkbox listener
        cbChoose.setOnCheckedChangeListener(myCheckChangList);
        //Write position
        cbChoose.setTag(position);
        //fill checkboxes
        cbChoose.setChecked(p.wasChecked);
        return view;
    }

    DirectoryList getListPosition(int position) {
        return ((DirectoryList) getItem(position));
    }

    // List info
    ArrayList<DirectoryList> getBox() {
        ArrayList<DirectoryList> box = new ArrayList<DirectoryList>();
        for (DirectoryList p : objects) {
            //if was checked
            if (p.wasChecked)
                box.add(p);
        }
        return box;
    }

    // Checkbox listener
    OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //<ark checkbox on change
            getListPosition((Integer) buttonView.getTag()).wasChecked = isChecked;
        }
    };

}
