package com.example.guans.arrivied.application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by shiqian.guan on 2017/7/20.
 */

public class ArrivedApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
