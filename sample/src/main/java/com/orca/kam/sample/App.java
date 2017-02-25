package com.orca.kam.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author kam6512
 * @Create on 2017-02-26.
 */
public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(this);
    }
}
