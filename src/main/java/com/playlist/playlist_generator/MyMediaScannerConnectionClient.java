package com.playlist.playlist_generator;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class MyMediaScannerConnectionClient implements MediaScannerConnection.MediaScannerConnectionClient {
    private String mFilename;
    private String mMimetype;
    private MediaScannerConnection mConn;
    public MyMediaScannerConnectionClient (Context ctx, File file, String mimetype) {
        this.mFilename = file.getAbsolutePath();
        mConn = new MediaScannerConnection(ctx, this);
        mConn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mConn.scanFile(mFilename, mMimetype);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        Log.i("ExternalStorage", "Scanned " + path + ":");
        Log.i("ExternalStorage", "-> uri=" + uri);
        mConn.disconnect();
    }
}
