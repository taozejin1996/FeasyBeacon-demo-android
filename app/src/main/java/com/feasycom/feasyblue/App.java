package com.feasycom.feasyblue;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ly","开启bugly");
        CrashReport.initCrashReport(getApplicationContext(), "8557035c67", false);

    }


}
