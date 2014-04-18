package com.playlist.playlist_generator;

public class File_types {
    String ItemDesc;
    int image;
    boolean wasChecked;

    File_types(String _describe, int _image, boolean _wasChecked) {
        ItemDesc = _describe;
        image = _image;
        wasChecked = _wasChecked;
    }

    String getName(){
        return ItemDesc;
    }
}
