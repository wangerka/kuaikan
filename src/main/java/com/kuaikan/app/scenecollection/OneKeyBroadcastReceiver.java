package com.kuaikan.app.scenecollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zhaozehong on 16-12-8.
 */
public class OneKeyBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_SEARCH_OVER = "com.kuaikan.action.SEARCH_OVER";
    private static final String ACTION_GET_CURRENT_SIM = "com.kuaikan.action.GET_SIM_INFO";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("gejun", "OneKeyBroadcastReceiver action: " + action);
        if (action.equals(ACTION_SEARCH_OVER)) {
            Log.i("gejun", "search over!!!!");
            DataOption.getIntence(context).sendResult();
        } else if (action.equals(ACTION_GET_CURRENT_SIM)) {
            Log.i("gejun", "init cdma!!!!");
            DataOption.getIntence(context).switchCdma();
        }
    }
}
