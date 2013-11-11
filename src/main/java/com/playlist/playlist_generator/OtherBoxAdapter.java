package com.playlist.playlist_generator;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by PC_4i_7 on 10/9/13.
 */
public class OtherBoxAdapter extends BaseAdapter{
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<OptionsList> objects;

    OtherBoxAdapter(Context context, ArrayList<OptionsList> items) {
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public OptionsList getItem(int position) {
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
            view = lInflater.inflate(R.layout.selected_item, parent, false);
        }

        OptionsList p = getListPosition(position);

        //описание и картинка
        ((TextView) view.findViewById(R.id.OptionDesc)).setText(p.OptionDesc);
        ((ImageView) view.findViewById(R.id.OptionImage)).setImageResource(p.image);
        ((EditText) view.findViewById(R.id.OptionSongCounter)).setText(p.SongCounter);

        return view;
    }
    // позиция
    OptionsList getListPosition(int position) {
        return ((OptionsList) getItem(position));
    }
}
