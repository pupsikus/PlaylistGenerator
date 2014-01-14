package com.playlist.playlist_generator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class PL_Path_BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<DirectoryList> objects;

    PL_Path_BoxAdapter(Context context, ArrayList<DirectoryList> items) {
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.pl_item, parent, false);
        }

        DirectoryList p = getListPosition(position);

        // Fill list with view elements: Item Description and its image
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.ItemDesc);
        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);

        return view;
    }
    // товар по позиции
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
}
