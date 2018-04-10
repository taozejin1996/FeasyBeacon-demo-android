package com.feasycom.fsybecon.Controler;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.feasycom.bean.BeaconBean;
import com.feasycom.bean.CommandBean;
import com.feasycom.bean.FeasyBeacon;
import com.feasycom.controler.FscBeaconApi;
import com.feasycom.controler.FscBeaconCallbacksImp;
import com.feasycom.fsybecon.Activity.MainActivity;
import com.feasycom.fsybecon.Activity.ParameterSettingActivity;
import com.feasycom.fsybecon.Activity.SetActivity;
import com.feasycom.fsybecon.Widget.InfoDialog;
import com.feasycom.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.feasycom.fsybecon.Activity.SetActivity.OPEN_TEST_MODE;


/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */

public class FscBeaconCallbacksImpParameter extends FscBeaconCallbacksImp {
    private WeakReference<ParameterSettingActivity> parameterSettingActivityWeakReference;
    private FscBeaconApi fscBeaconApi;
    private String moduleString;

    public FscBeaconCallbacksImpParameter(WeakReference<ParameterSettingActivity> parameterSettingActivityWeakReference, FscBeaconApi fscBeaconApi, String moduleString) {
        this.parameterSettingActivityWeakReference = parameterSettingActivityWeakReference;
        this.fscBeaconApi = fscBeaconApi;
        this.moduleString = moduleString;
    }

    @Override
    public void blePeripheralConnected(BluetoothGatt gatt, BluetoothDevice device) {
        if (parameterSettingActivityWeakReference.get().getPin2Connect() == null) {
            fscBeaconApi.startGetDeviceInfo(moduleString);
        }
    }

    @Override
    public void connectProgressUpdate(BluetoothDevice device, int status) {
        if (parameterSettingActivityWeakReference.get() == null) {
            return;
        }
        if (status == CommandBean.PASSWORD_CHECK) {
            parameterSettingActivityWeakReference.get().getConnectDialog().setInfo("check password...");
        } else if (status == CommandBean.PASSWORD_SUCCESSFULE) {
            parameterSettingActivityWeakReference.get().getConnectDialog().setInfo("password sucessful...");
            fscBeaconApi.startGetDeviceInfo(moduleString);
        } else if (status == CommandBean.PASSWORD_FAILED) {
            parameterSettingActivityWeakReference.get().getConnectDialog().setInfo("password failed...");
            parameterSettingActivityWeakReference.get().connectFailedHandler();
        } else if (status == CommandBean.PASSWORD_TIME_OUT) {
            parameterSettingActivityWeakReference.get().getConnectDialog().setInfo("timeout");
            parameterSettingActivityWeakReference.get().connectFailedHandler();
        }

    }

    @Override
    public void atCommandCallBack(String command, String param, String status) {
        if (parameterSettingActivityWeakReference.get() == null) {
            return;
        }
        /**
         * get module information and save module information are through the AT command to configure
         * after saving the module information you can do something
         */
        if (CommandBean.COMMAND_FINISH.equals(status) && "save...".equals(parameterSettingActivityWeakReference.get().getConnectDialog().getInfo())) {
            /**
             *  if you want to switch activity please wait a moment for release service connection
             */
            parameterSettingActivityWeakReference.get().getConnectDialog().setInfo("finish...");
            fscBeaconApi.disconnect();
            parameterSettingActivityWeakReference.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    parameterSettingActivityWeakReference.get().getConnectDialog().dismiss();
                    if (OPEN_TEST_MODE) {
                        SetActivity.actionStart(parameterSettingActivityWeakReference.get().getActivity());
                    } else {
                        MainActivity.actionStart(parameterSettingActivityWeakReference.get().getActivity());
                    }
                    parameterSettingActivityWeakReference.get().finishActivity();
                    Log.i("finish", "1");
                }
            }, InfoDialog.INFO_DIAOLOG_SHOW_TIME);
        }

        if (CommandBean.COMMAND_TIME_OUT.equals(status) && "save...".equals(parameterSettingActivityWeakReference.get().getConnectDialog().getInfo())) {
            /**
             *  if you want to switch activity please wait a moment for release service connection
             */
            parameterSettingActivityWeakReference.get().getConnectDialog().setInfo("timeout...");
            fscBeaconApi.disconnect();
            parameterSettingActivityWeakReference.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    parameterSettingActivityWeakReference.get().getConnectDialog().dismiss();
                    if (OPEN_TEST_MODE) {
                        SetActivity.actionStart(parameterSettingActivityWeakReference.get().getActivity());
                    } else {
                        MainActivity.actionStart(parameterSettingActivityWeakReference.get().getActivity());
                    }
                    parameterSettingActivityWeakReference.get().finishActivity();
                    Log.i("timeout", "1");
                }
            }, InfoDialog.INFO_DIAOLOG_SHOW_TIME);
        }

        if (CommandBean.COMMAND_FINISH.equals(status) && "connected".equals(parameterSettingActivityWeakReference.get().getConnectDialog().getInfo())) {
            parameterSettingActivityWeakReference.get().getConnectDialog().dismiss();
            parameterSettingActivityWeakReference.get().getHandler().removeCallbacks(parameterSettingActivityWeakReference.get().getCheckConnect());
            if(OPEN_TEST_MODE){
                parameterSettingActivityWeakReference.get().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parameterSettingActivityWeakReference.get().getConnectDialog().setInfo("save...");
                        parameterSettingActivityWeakReference.get().getConnectDialog().show();
                        parameterSettingActivityWeakReference.get().getFscBeaconApi().saveBeaconInfo();
                    }
                }, 2000);
            }

        } else {
            /**
             * parameter modification success or failure here to deal with
             * eg command  AT+NAME=123  status COMMAND_SUCCESSFUL
             */
            if (CommandBean.COMMAND_TIME_OUT.equals(status)) {
//                Log.i("timeout", command);
            } else if (CommandBean.COMMAND_FAILED.equals(status)) {
//                Log.i("failed", command);
            } else if (CommandBean.COMMAND_SUCCESSFUL.equals(status)) {
//                Log.i("success", command);
            } else if (CommandBean.COMMAND_NO_NEED.equals(status)) {
//                Log.i("no_need", command);
            }
        }
    }


    @Override
    public void deviceInfo(final String parameterName, final Object parameter) {
        if (parameterSettingActivityWeakReference.get() == null) {
            return;
        }
        Log.i("device", parameterName);
        if (CommandBean.COMMAND_BEGIN.equals(parameterName)) {
            parameterSettingActivityWeakReference.get().getConnectDialog().setInfo("connected");
        } else if (CommandBean.COMMAND_MODEL.equals(parameterName)) {
            parameterSettingActivityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parameterSettingActivityWeakReference.get().getModule().setText((String) parameter, false);
                }
            });
        } else if (CommandBean.COMMAND_VERSION.equals(parameterName)) {
            parameterSettingActivityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parameterSettingActivityWeakReference.get().getVersion().setText((String) parameter, false);
                }
            });
        } else if (CommandBean.COMMAND_NAME.equals(parameterName)) {
            parameterSettingActivityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parameterSettingActivityWeakReference.get().getName().setText((String) parameter, true);
                }
            });
        } else if (CommandBean.COMMAND_ADVIN.equals(parameterName)) {
            Log.i("advin", (String) parameter);
            parameterSettingActivityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parameterSettingActivityWeakReference.get().getInterval().setText((String) parameter, true);
                }
            });
        } else if (CommandBean.COMMAND_BWMODE.equals(parameterName)) {
            /**
             * if use the password ,open the connectable directly
             */
            if (FeasyBeacon.BLE_KEY_WAY.equals(parameterSettingActivityWeakReference.get().getEncryptWay())) {
                parameterSettingActivityWeakReference.get().getConnectable().setCheck(true);
            } else if (CommandBean.COMMAND_BWMODE_OPEN.equals((String) parameter)) {
                parameterSettingActivityWeakReference.get().getConnectable().setCheck(true);
            } else {
                parameterSettingActivityWeakReference.get().getConnectable().setCheck(false);
            }
        } else if (CommandBean.COMMAND_PIN.equals(parameterName)) {
            Log.i("pin", "command call back");
            parameterSettingActivityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String temp = (String) parameter;
                    if (temp.length() == 1) {
                        parameterSettingActivityWeakReference.get().getPIN().setText("00000" + (String) parameter, true);
                    } else if (temp.length() == 2) {
                        parameterSettingActivityWeakReference.get().getPIN().setText("0000" + (String) parameter, true);
                    } else if (temp.length() == 3) {
                        parameterSettingActivityWeakReference.get().getPIN().setText("000" + (String) parameter, true);
                    } else if (temp.length() == 4) {
                        parameterSettingActivityWeakReference.get().getPIN().setText("00" + (String) parameter, true);
                    } else if (temp.length() == 5) {
                        parameterSettingActivityWeakReference.get().getPIN().setText("0" + (String) parameter, true);
                    } else {
                        parameterSettingActivityWeakReference.get().getPIN().setText((String) parameter, true);
                    }
                }
            });
        } else if (CommandBean.COMMAND_TX_POWER.equals(parameterName)) {
            parameterSettingActivityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parameterSettingActivityWeakReference.get().getTxPower().setSelect(Integer.valueOf((String) parameter).intValue() + 1);
                }
            });
        } else if (CommandBean.COMMAND_BADVDATA.equals(parameterName)) {
            /**
             *  after each connection is successful SDK internal cache  broadcast information
             *  please ensure that your operation on the broadcast information is cached data for the SDK
             */
            parameterSettingActivityWeakReference.get().getAdapter().updateAllBeacon((ArrayList<BeaconBean>) parameter);
            parameterSettingActivityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fscBeaconApi.isBeaconInfoFull()) {
                        parameterSettingActivityWeakReference.get().addBeaconEnable(false);
                    }
                    parameterSettingActivityWeakReference.get().getAdapter().notifyDataSetChanged();
                }
            });
        } else if (CommandBean.COMMAND_EXTEND.equals(parameterName)) {
            Log.i("extend", (String) parameter);
            parameterSettingActivityWeakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parameterSettingActivityWeakReference.get().getExtEnd().setText((String) parameter);
                }
            });

        }
    }
}
