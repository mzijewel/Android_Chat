package com.example.nayan.chatappupdated.tools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.nayan.chatappupdated.R;
import com.example.nayan.chatappupdated.activity.TabActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dev on 1/17/2018.
 */

public class Utils {
    public static final String APP_NAME = "Chat App";

    // save data to sharedPreference
    public static void savePref(String name, String value) {
        SharedPreferences pref = MainApplication.getInstance().getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(name, value);
        editor.apply();
    }

    // get data from shared preference
    public static String getPref(String name, String defaultValue) {
        SharedPreferences pref = MainApplication.getInstance().getContext().getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return pref.getString(name, defaultValue);
    }

    public static String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String day = sdf.format(new Date());
        return day;
    }

    public static void createNotify(String name, String content, int id) {
        Intent activityIntent = new Intent(MainApplication.getInstance().getContext(), TabActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainApplication.getInstance().getContext(), 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(MainApplication.getInstance().getContext())
                .setSmallIcon(R.drawable.default_avata)
                .setContentTitle(name)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) MainApplication.getInstance().getContext().getSystemService(
                        Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        notificationManager.notify(id,
                notificationBuilder.build());
    }

    public static void log(String s) {
        Log.e("CHAT", s);
    }

    public static void showTost(String msg) {
        Toast.makeText(MainApplication.getInstance().getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
