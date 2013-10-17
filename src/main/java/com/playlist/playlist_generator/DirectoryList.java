package com.playlist.playlist_generator;

import java.lang.String; /**
 * Created by PC_4i_7 on 9/23/13.
 */
public class DirectoryList {
    String ItemDesc;
    String ItemPath;
    int image;
    boolean wasChecked;

    DirectoryList(String _describe, String _path, int _image, boolean _wasChecked) {
        ItemDesc = _describe;
        ItemPath = _path;
        image = _image;
        wasChecked = _wasChecked;
    }

    String getName(){
        return ItemDesc;
    }

    String getPath(){
        return ItemPath;
    }
}
