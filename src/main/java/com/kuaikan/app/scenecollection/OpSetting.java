package com.kuaikan.app.scenecollection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.settings.SetDefaultOPAidl;

/**
 * Created by gejun on 16-12-6.
 */

class OpSetting {

    public static final String OP_SWITCH = "switch";
    public static final String OP_CURR_SIMINFO = "get_current_siminfo";
    public static final String OP_CMCC = "cmcc";
    public static final String OP_CU = "cu";
    public static final String OP_TELCOM = "telcom";

    private OpServiceConnection mConnect;
    private SetDefaultOPAidl mAidl;

    private static OpSetting mOpSetting;

    public synchronized static OpSetting getIntentce() {
        if (mOpSetting == null) {
            mOpSetting = new OpSetting();
        }
        return mOpSetting;
    }

    private OpSetting() {

    }

    public synchronized void startSetOPService(Context context, ServiceConnectionCallBack mCallBack) {
        if (mConnect == null) {
            Log.e("gejun", "[OpSetting][startSetOPService] ");
            mConnect = new OpServiceConnection();
            mConnect.setServiceConnectionCallBack(mCallBack);

            Intent intent = new Intent();
            intent.setPackage("com.android.settings");
            intent.setAction("com.freeme.action.setop");
            context.bindService(intent, mConnect, Context.BIND_AUTO_CREATE);
        } else {
            if (mCallBack != null) {
                mCallBack.onServiceConnected();
            }
        }
    }

    public void stopSetOPService(Context context) {
        Log.e("gejun", "[OpSetting][stopSetOPService] unbindService");
        context.unbindService(mConnect);
        mConnect = null;
    }

    public void switchSimCard(Context context, final String flag) {
        Log.i("gejun","----------------------->switchSimCard " + flag + ", mConnect = " + mConnect
        + ", mAidl = " + mAidl);
        if (mConnect == null) {
            startSetOPService(context, new ServiceConnectionCallBack() {
                @Override
                public void onServiceConnected() {
                    Log.e("gejun", "[OpSetting][onServiceConnected] ");
                    try {
                        Log.i("gejun","mConnect == null mAidl = " + mAidl);
                        if (mAidl != null) {
                            mAidl.switchSimCard(flag);
                        }
                    } catch (RemoteException e) {
                    }
                }

                @Override
                public void onServiceDisconnected() {

                }
            });
        } else {
            try {
                Log.i("gejun","mAidl = " + mAidl);
                if (mAidl != null) {
                    mAidl.switchSimCard(flag);
                }
            } catch (RemoteException e) {
                Log.i("gejun","e = " + e.toString());
            }
        }
    }

    private class OpServiceConnection implements ServiceConnection {

        private ServiceConnectionCallBack mCallBack;

        private void setServiceConnectionCallBack(ServiceConnectionCallBack mCallBack) {
            this.mCallBack = mCallBack;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("gejun", "[OpServiceConnection][onServiceConnected]");
            mAidl = SetDefaultOPAidl.Stub.asInterface(service);
            if (mCallBack != null) {
                mCallBack.onServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("gejun", "[OpServiceConnection][onServiceDisconnected]");
            mConnect = null;
            if (mCallBack != null) {
                mCallBack.onServiceDisconnected();
            }
        }
    }

    interface ServiceConnectionCallBack {

        void onServiceConnected();

        void onServiceDisconnected();
    }
}
