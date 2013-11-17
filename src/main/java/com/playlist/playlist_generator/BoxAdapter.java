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
import java.util.ArrayList;

/**
 * Created by PC_4i_7 on 9/23/13.
 */
public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<DirectoryList> objects;

    BoxAdapter(Context context, ArrayList<DirectoryList> items) {
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
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        DirectoryList p = getListPosition(position);

        // Fill list with view elements: Item Description and its image
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.ItemDesc);
        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);
        CheckBox cbChoose = (CheckBox) view.findViewById(R.id.cbBox);

        // присваиваем чекбоксу обработчик
        cbChoose.setOnCheckedChangeListener(myCheckChangList);
        // пишем позицию
        cbChoose.setTag(position);
        // заполняем данными из товаров: в корзине или нет
        cbChoose.setChecked(p.wasChecked);
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

    // обработчик для чекбоксов
    OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // меняем данные товара (в корзине или нет)
            getListPosition((Integer) buttonView.getTag()).wasChecked = isChecked;
        }
    };

}
