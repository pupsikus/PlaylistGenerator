package com.playlist.playlist_generator;

import java.util.ArrayList;

public class OptionsList {
    String OptionDesc;
    ArrayList<String> OptionPath = new ArrayList<String>();
    int image;
    String SongCounter;

    OptionsList(String _describe, ArrayList<String> _path, int _image, String _SongCounter) {
        OptionDesc = _describe;
        OptionPath = _path;
        image = _image;
        SongCounter=_SongCounter;
    }

    String getName(){
        return OptionDesc;
    }

    int getOptionPathSize(){
        return OptionPath.size();
    }

    String getSongCounterSize(){
        return SongCounter;
    }

    String getPath(int index){
        return OptionPath.get(index);
    }
}
