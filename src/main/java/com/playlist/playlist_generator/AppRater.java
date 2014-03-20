package com.playlist.playlist_generator;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppRater implements View.OnClickListener{
    private final static String APP_TITLE = "Playlist Generator";
    private final static String APP_PNAME = "com.playlist.playlist_generator";

    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 5;
    private Dialog dialog;

    public void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
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
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        double MaxButtonWidth = 0.0;
        dialog = new Dialog(mContext);
        dialog.setTitle(mContext.getResources().getString(R.string.RateMe_rate) + " " + APP_TITLE);
        dialog.setContentView(R.layout.app_rate);
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.ll_apprate);

        Button btnRate = (Button) ll.findViewById(R.id.apprate_btnRateMe);
        Button btnRemind = (Button) ll.findViewById(R.id.apprate_btnRemind);
        Button btnNo = (Button) ll.findViewById(R.id.apprate_btnNo);
        TextView tvText = (TextView) ll.findViewById(R.id.apprate_tvText);
        tvText.setText(mContext.getResources().getString(R.string.RateMe_text1) + " " + APP_TITLE + " " + mContext.getResources().getString(R.string.RateMe_text2));

        MaxButtonWidth = (double) btnRate.getWidth();
        if(MaxButtonWidth < (double) btnRemind.getWidth()){
            MaxButtonWidth = (double) btnRemind.getWidth();
        }
        else if(MaxButtonWidth < (double) btnNo.getWidth()) {
            MaxButtonWidth = (double) btnNo.getWidth();
        }
        btnRate.setWidth((int)MaxButtonWidth);
        btnRemind.setWidth((int)MaxButtonWidth);
        btnNo.setWidth((int)MaxButtonWidth);

        btnRate.setOnClickListener(this);
        btnRemind.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        tvText.setOnClickListener(this);

        //tvText.setWidth(240);
        dialog.show();


    }

    @Override
    public void onClick(View v) {
        Context context;
        context = dialog.getContext();
        SharedPreferences prefs = context.getSharedPreferences("apprater", 0);
        SharedPreferences.Editor editor = prefs.edit();
        switch (v.getId()){
            case R.id.apprate_btnRateMe:
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
                break;
            case R.id.apprate_btnRemind:
                dialog.dismiss();
                break;
            case R.id.apprate_btnNo:
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
                break;
        }
    }

}
