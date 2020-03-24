package com.feasycom.fsybecon.Controler;

import android.util.Log;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.controler.FscBeaconCallbacksImp;
import com.feasycom.fsybecon.Activity.ParameterSettingActivity;
import com.feasycom.fsybecon.Activity.SensorActivity;
import com.feasycom.fsybecon.Activity.SetActivity;

import java.lang.ref.WeakReference;

import static com.feasycom.fsybecon.Activity.SetActivity.OPEN_TEST_MODE;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class FscBeaconCallbacksImpSensor extends FscBeaconCallbacksImp {
    private static final String TAG = "FscBeaconCallbacksImpSe";
    private WeakReference<SensorActivity> weakReference;
    private boolean testDeviceFound = false;

    public FscBeaconCallbacksImpSensor(WeakReference<SensorActivity> weakReference) {
        this.weakReference = weakReference;
    }

    @Override
    public void blePeripheralFound(BluetoothDeviceWrapper device, int rssi, byte[] record) {

        /**
         * BLE search speed is fast,please pay attention to the life cycle of the device object ,directly use the final type here
         */
        if (OPEN_TEST_MODE) {
            if ((device.getName() != null) && device.getName().contains("2beacon")) {
                weakReference.get().getFscBeaconApi().stopScan();
                if (!testDeviceFound) {
                    testDeviceFound = true;
                } else {
                    return;
                }
                weakReference.get().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ParameterSettingActivity.actionStart(weakReference.get(), device, "000000");
                        weakReference.get().finishActivity();
                    }
                }, 3000);
            }
        } else {
//            if ((null != device.getgBeacon()) || (null != device.getiBeacon()) || (null != device.getAltBeacon())) {
            if ((weakReference.get() != null) && (weakReference.get().getDeviceQueue().size() < 350) && device.getMonitor() != null) {
                Log.e(TAG, "blePeripheralFound: " + device.getAddress());
                weakReference.get().getDeviceQueue().offer(device);
            }
//            }
        }

    }
}
