package com.playlist.playlist_generator;

import java.util.ArrayList;

/**
 * Created by PC_4i_7 on 9/27/13.
 */
public class OptionsList {
    String OptionDesc;
    ArrayList<String> OptionPaths = new ArrayList<String>();
    int image;

    OptionsList(String _describe, ArrayList<String> _path, int _image) {
        OptionDesc = _describe;
        OptionPaths = _path;
        image = _image;
    }

    String getName(){
        return OptionDesc;
    }

    String getPath(int index){
        return OptionPaths.get(index);
    }
}
