package com.playlist.playlist_generator;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppDonate implements View.OnClickListener {
    private final static String APP_TITLE = "Playlist Generator";

    private final static int DAYS_UNTIL_PROMPT = 5;
    private final static int LAUNCHES_UNTIL_PROMPT = 7;
    private Dialog dialog;

    public void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("appdonate", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showDonateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public void showDonateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        dialog = new Dialog(mContext);
        dialog.setTitle(mContext.getResources().getString(R.string.Donate_title));
        dialog.setContentView(R.layout.app_donate);
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.ll_appdonate);

        ImageButton btnFacebook = (ImageButton) ll.findViewById(R.id.appdonate_btnFacebook);
        ImageButton btnGoogle = (ImageButton) ll.findViewById(R.id.appdonate_btnGoogle);
        TextView tvText = (TextView) ll.findViewById(R.id.appdonate_tvText);
        tvText.setText(mContext.getResources().getString(R.string.Donate_text));

        btnFacebook.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        tvText.setOnClickListener(this);

        //tvText.setWidth(240);
        dialog.show();

    }

    public static Intent getOpenFacebookIntent(Context context) {
        String pageId = "256347111219009";
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + pageId));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + pageId));
        }
    }

    @Override
    public void onClick(View v) {
        Context context;
        context = dialog.getContext();
        SharedPreferences prefs = context.getSharedPreferences("appdonate", 0);
        SharedPreferences.Editor editor = prefs.edit();
        switch (v.getId()){
            case R.id.appdonate_btnFacebook:
                context.startActivity(getOpenFacebookIntent(context));
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
                break;
            case R.id.appdonate_btnGoogle:
                String GPlusPath = "https://plus.google.com/100179954398225067202/posts";
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GPlusPath)));
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                break;
        }
    }
}
