package com.playlist.playlist_generator;

import java.util.ArrayList;

/**
 * Created by PC_4i_7 on 9/27/13.
 */
public class OptionsList {
    String OptionDesc;
    ArrayList<String> OptionPath = new ArrayList<String>();
    int image;

    OptionsList(String _describe, ArrayList<String> _path, int _image) {
        OptionDesc = _describe;
        OptionPath = _path;
        image = _image;
    }

    String getName(){
        return OptionDesc;
    }

    int getOptionPathSize(){
        return OptionPath.size();
    }

    String getPath(int index){
        return OptionPath.get(index);
    }
}
