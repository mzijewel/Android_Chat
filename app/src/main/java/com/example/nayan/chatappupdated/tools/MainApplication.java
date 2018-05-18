package com.example.nayan.chatappupdated.tools;

import android.app.Application;
import android.content.Context;

/**
 * Created by Dev on 1/17/2018.
 */

public class MainApplication extends Application {
    private static MainApplication instance;


    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public Context getContext() {
        return instance.getApplicationContext();
    }
}
