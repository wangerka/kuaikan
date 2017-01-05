package com.kuaikan.app.scenecollection;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by zhaozehong on 16-12-8.
 */

public class DataOption {

    private static DataOption mOption;
    private Context mContext;

    private ArrayList<String> result = new ArrayList<String>();
    private ArrayList<Integer> opList = new ArrayList<Integer>();
    private ArrayList<Integer> generations = new ArrayList<Integer>();
    private static boolean isSerchOver = true;

    public synchronized static DataOption getIntence(Context context) {
        if (mOption == null) {
            Log.e("gejun", "[DataOption][getIntence] init DataOption");
            mOption = new DataOption(context);
        }
        return mOption;
    }

    public static boolean isInit() {
        if (isSerchOver) {
            mOption = null;
        }
        return mOption != null;
    }

    private DataOption(Context context) {
        mContext = context.getApplicationContext();
    }

    public void initGs() {
        isSerchOver = false;
        Log.e("gejun", "[DataOption][initGs] ");
        generations.clear();
        generations.add(0);
        generations.add(1);
        generations.add(3);
    }

    public void switchCdma() {
        isSerchOver = false;
        Log.e("gejun", "[DataOption][switchCdma] ");
        generations.clear();
        generations.add(3);
        generations.add(4);
        Util.invokeAT(new String[]{"AT+ERAT=3,0", "+ERAT"},
                mHandler.obtainMessage(EVENT_SET_GENERATION));
    }

    public void startSetOPService(){
        Log.e("gejun", "[DataOption][startSetOPService] ");
        OpSetting.getIntentce().startSetOPService(mContext, new OpSetting.ServiceConnectionCallBack() {
            @Override
            public void onServiceConnected() {
                OpSetting.getIntentce().switchSimCard(mContext, OpSetting.OP_CURR_SIMINFO);
                oneKey();
            }

            @Override
            public void onServiceDisconnected() {

            }
        });
    }

    public void stopSetOPService(){
        Log.e("gejun", "[DataOption][stopSetOPService] ");
        OpSetting.getIntentce().stopSetOPService(mContext);
    }

    public void oneKey() {
        startSearchTime = System.currentTimeMillis();
        Log.e("gejun", "[DataOption][oneKey] startSearchTime: "+startSearchTime);
        mHandler.sendEmptyMessage(EVENT_GET_CELLINFO);
    }

    public void sendResult() {
        Log.e("gejun", "[DataOption][sendResult] ");
        ArrayList<String> retList = new ArrayList<String>(result);
        Intent it = new Intent("com.kuaikan.send_result");
        it.putStringArrayListExtra("result", retList);
        mContext.sendBroadcast(it);
    }

    private static final int EVENT_GET_CELLINFO = 0;
    private static final int EVENT_GET_NETWORKMODE = 1;
    private static final int EVENT_SET_GENERATION = 2;
    private static final int EVENT_GET_CDMA_INFO = 3;
    private static final int EVENT_GET_ACTIVE_CDMA = 4;
    private static final int EVENT_GET_NEIGHBOR_CDMA = 5;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_GET_CELLINFO: {
                    try {
                        Util.getCellInfo(mHandler.obtainMessage(EVENT_GET_NETWORKMODE));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("gejun", "e = " + e.toString());
                    }
                    mHandler.removeMessages(EVENT_GET_CELLINFO);
                    mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
                    break;
                }
                case EVENT_GET_NETWORKMODE: {
                    dealRestult(msg);
                    break;
                }
                case EVENT_GET_CDMA_INFO: {
                    Util.invokeAT4CDMA(new String[]{"AT+VLOCINFO?", "+VLOCINFO"},
                            mHandler.obtainMessage(EVENT_GET_ACTIVE_CDMA));

                    Util.invokeAT4CDMA(new String[]{"AT+ECENGINFO=1,19", "+ECENGINFO"},
                            mHandler.obtainMessage(EVENT_GET_NEIGHBOR_CDMA));
                    break;
                }
                case EVENT_GET_ACTIVE_CDMA:
                case EVENT_GET_NEIGHBOR_CDMA: {
                    showCDMAResult(msg, "EVENT" + msg.what);
                    break;
                }
            }
        }
    };

    private void showCDMAResult(Message msg, String tag) {
        String[] arr = null;
        try {
            Class arC = Class.forName("android.os.AsyncResult");
            Field result = arC.getDeclaredField("result");
            Object resultString = result.get(msg.obj);

            Field exception = arC.getDeclaredField("exception");
            Object ex = exception.get(msg.obj);
            Log.i("gejun", tag + "ex:" + ex);
            Log.i("gejun", tag + "resultString = " + resultString);
            if (resultString == null) return;
            arr = (String[]) resultString;
            for (int i = 0; i < arr.length; i++) {
                Log.i("gejun", tag + "show Result = " + arr[i]);
            }

        } catch (Exception e) {
            Log.i("gejun", tag + " e = " + e.toString());
        }
        if (arr != null) {
            extraData1(arr);
        }
    }

    private String mcc;
    private String mnc;
    private String sid;
    private String bid;
    private String nid;
    private int rx_power1;

    private void extraData1(String[] o) {
        String vlocinfo = null;
        if (o.length == 0) return;
        if (o[0].startsWith("+VLOCINFO")) {
            String[] info1 = o[0].split(",");
            mcc = info1[1];
            mnc = info1[2];
            sid = info1[3];
            nid = info1[4];
            bid = info1[5];
            Log.i("gejun", "mcc: " + mcc + ", mnc: " + mnc + ", sid: " + sid + ", nid: " + nid + ", bid: " + bid);
            if(mnc.equals("460")) vlocinfo = o[0];
            return;
        }

        if (o[0].startsWith("+ECENGINFO:\"1xRTT_Radio_Info\"")) {
            rx_power1 = Integer.parseInt(o[0].split(",")[4]);
            Log.i("gejun", "rx_power1: " + rx_power1);
        }

        if (o[2].split(",")[1].equals("0")) {
            mHandler.sendEmptyMessage(EVENT_GET_CDMA_INFO);
            return;
        }
        Log.i("gejun", "************save result*************");
        mHandler.removeMessages(EVENT_GET_CDMA_INFO);
//        cdmaResult = o;
        if(vlocinfo != null) result.add(vlocinfo);
        for (int i = 0; i < o.length; i++) {
//            cdmaResultList.add(o[i]);
            result.add(o[i]);
        }
        opList.add(Integer.parseInt(mnc));
        Log.i("gejun", mnc + " has search over!");
        if (opList.size() >= 2) {
            Log.i("gejun", "search over!");
            mHandler.removeMessages(EVENT_GET_CELLINFO);
            sendResult();
            stopSetOPService();

            result.clear();
            opList.clear();
            generations.clear();
            isSerchOver = true;

            if (mCallBack != null) {
                mCallBack.searchOver();
            }
        } else {
            initGs();
            //switch sim
            OpSetting.getIntentce().switchSimCard(mContext, OpSetting.OP_SWITCH);
        }
    }

    private void dealRestult(Message msg) {
        String[] arr = null;
        try {
            Class arC = Class.forName("android.os.AsyncResult");
            Field result = arC.getDeclaredField("result");
            Object resultString = result.get(msg.obj);
            if (resultString == null) return;
            arr = (String[]) resultString;
        } catch (Exception e) {
            Log.i("gejun", "e = " + e.toString());
        }

        if (arr != null) {
            extraData(arr);
        }
    }

    private long startSearchTime;
    private int currentG;

    private void extraData(String[] p) {
        if (opList.size() > 2) {
            mHandler.removeMessages(EVENT_GET_CELLINFO);
        }
        String o = p[0];
        Log.i("gejun", "extraData o : " + o);
        String[] subItems = o.split(",");
        if (subItems.length < 2) {
            return;
        }
        String rat = subItems[1];
        int mnc = Integer.parseInt(subItems[5]);
        int g = 0;
        if (rat.equals("0")) {//2g
            g = 0;
        } else if (rat.equals("2")) {//3g
            g = 1;
        } else if (rat.equals("7")) {//4g
            g = 3;
        }

        //log
        if (generations != null && generations.size() > 0) {
            for (int i = 0; i < generations.size(); i++) {
                Log.i("gejun", "g" + i + ":" + generations.get(i));
            }
        }
        if (opList != null && opList.size() > 0) {
            for (int i = 0; i < opList.size(); i++) {
                Log.i("gejun", "op" + i + ":" + opList.get(i));
            }
        }

        Log.i("gejun", "g = " + g + ", mnc = " + mnc);
        if (!generations.contains(g) || opList.contains(mnc)) {//this g had got
            Log.i("gejun", "extraData be give up");
            long lastTime = System.currentTimeMillis() - startSearchTime;
            Log.i("gejun", "lasttime = " + lastTime);
            if (startSearchTime != 0 && lastTime > 30000) {//search fail
                Log.i("gejun", "**********************fail search op: g = " + currentG + ", mnc = " + mnc);
                generations.remove(new Integer(currentG));
                if (generations.size() > 0) {
                    if (generations.get(0) == 4) {
                        startSearchTime = 0;
                        mHandler.sendEmptyMessage(EVENT_GET_CDMA_INFO);
                        return;
                    }
                    startSearchTime = System.currentTimeMillis();
                    currentG = generations.remove(0);
                    Util.invokeAT(new String[]{"AT+ERAT=" + currentG + ",0", "+ERAT"},
                            mHandler.obtainMessage(EVENT_SET_GENERATION));
                } else {
                    Log.i("gejun", mnc + " has search over!switch another or end!");
                    opList.add(mnc);
                    if (opList.size() >= 2) {
                        sendResult();

                        mHandler.removeMessages(EVENT_GET_CELLINFO);
                        stopSetOPService();

                        result.clear();
                        opList.clear();
                        generations.clear();
                        isSerchOver = true;

                        if (mCallBack != null) {
                            mCallBack.searchOver();
                        }
                        Log.i("gejun", "search end!!!!!!!");
                        //search over
                    } else {
                        initGs();
                        //switch sim
                        OpSetting.getIntentce().switchSimCard(mContext, OpSetting.OP_SWITCH);
                    }
                }
            }
            return;
        } else {
            result.add(o);//add new result
            generations.remove(new Integer(g));
            //search other g
            if (generations.size() > 0) {
                if (generations.get(0) == 4) {
                    startSearchTime = 0;
                    mHandler.sendEmptyMessage(EVENT_GET_CDMA_INFO);
                    return;
                }
                startSearchTime = System.currentTimeMillis();
                currentG = generations.get(0);
                Log.i("gejun", "set nextG = " + currentG);
                Util.invokeAT(new String[]{"AT+ERAT=" + currentG + ",0", "+ERAT"},
                        mHandler.obtainMessage(EVENT_SET_GENERATION));
            } else {
                Log.i("gejun", mnc + " has search over, change another or end!!!");
                opList.add(mnc);
                if (opList.size() >= 2) {
                    mHandler.removeMessages(EVENT_GET_CELLINFO);
                    sendResult();
                    stopSetOPService();

                    result.clear();
                    opList.clear();
                    generations.clear();
                    isSerchOver = true;

                    if (mCallBack != null) {
                        mCallBack.searchOver();
                    }
                    Log.i("gejun", "search over!!!");
                } else {
                    initGs();
                    //switch sim
                    OpSetting.getIntentce().switchSimCard(mContext, OpSetting.OP_SWITCH);
                }
            }
        }
    }

    private SearchOverCallBack mCallBack;
    public void setSearchOverCallBack(SearchOverCallBack callback){
        mCallBack = callback;
    }
    interface SearchOverCallBack{
        void searchOver();
    }
}
