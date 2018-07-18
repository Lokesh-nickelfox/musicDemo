package com.nickelfox.music;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class ApplicationController extends Application
{
    private static ApplicationController instance;
    private SharedPreferences prefs;

    public static  boolean isAppLive = false;

    public void onCreate() {
        super.onCreate();
        instance = this;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static synchronized ApplicationController getInstance() {
        ApplicationController applicationController;
        synchronized (ApplicationController.class) {
            applicationController = instance;
        }
        return applicationController;
    }

    public SharedPreferences getPrefs() {
        return this.prefs;
    }



}
