package com.feasycom.fsybecon;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.feasycom.fsybecon.Service.BluetoothService;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {
    private static final String TAG = "App";
    public static App instance;
    public BluetoothService mBluetoothService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected: 绑定服务成功" );
            mBluetoothService = ((BluetoothService.LocalBinder) service)
                    .getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceConnected: 销毁服务" );
            mBluetoothService = null;

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "360d4f05fa", false);
        instance = this;
        Intent btServiceIntent = new Intent(this, BluetoothService.class);
        bindService(btServiceIntent, connection, Context.BIND_AUTO_CREATE);

    }

}
