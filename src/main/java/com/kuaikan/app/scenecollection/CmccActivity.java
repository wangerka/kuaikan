package com.kuaikan.app.scenecollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.util.Log;

//import com.android.internal.telephony.Phone;
//import com.android.internal.telephony.PhoneFactory;
import android.telephony.SubscriptionManager;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import java.util.List;
import android.os.Handler;
import android.os.Message;

//import android.os.AsyncResult;
//import com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy;

public class CmccActivity extends Activity implements OnClickListener{

    private Button g2;
    private Button g3;
    private Button g4;

//    private TextView info;
//
//    private TelephonyManager manager;
//    private Phone phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cmcc);

//        phone=PhoneFactory.getDefaultPhone();
//        if(phone instanceof LteDcPhoneProxy){
//            Log.i("gejun","init LteDcPhoneProxy");
//            phone = ((LteDcPhoneProxy) phone).getLtePhone();
//        }
//        Log.i("gejun","phone = " + phone);
//
//        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        g2 = (Button)findViewById(R.id.cmcc_gsm);
        g2.setOnClickListener(this);
        g3 = (Button)findViewById(R.id.tdscdma);
        g3.setOnClickListener(this);
        g4 = (Button)findViewById(R.id.cmcc_lte);
        g4.setOnClickListener(this);

//        info = (TextView)findViewById(R.id.info);
    }

    @Override
    public void onResume() {
        super.onResume();
//        manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO);
    }

    @Override
    public void onPause() {
        super.onPause();
//        manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

//    private static final int EVENT_SET_GENERATION = 0;
//    private static final int EVENT_SET_OP = 1;
//    private static final int EVENT_GET_NETWORKMODE = 2;
//    private static final int EVENT_GET_RSSI = 3;
//
//    private void setGeneration(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_GENERATION));
//    }
//
//    private void setOp(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_OP));
//    }
//
//    private void init(String[] g, String[] op){
//        setOp(op);
//        setGeneration(g);
//    }


    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        Intent result = new Intent(this, GsmResultActivity.class);
        switch(id){
            case R.id.cmcc_gsm:
//                init(new String[]{"AT+ERAT=0","+ERAT"},
//                        new String[]{"AT+COPS=1,2,46000","+COPS"});
                result.putExtra(Util.NETWORK_MODE_TYPE, Util.TYPE_CMCC_GSM);
                break;
            case R.id.tdscdma:
//                init(new String[]{"AT+ERAT=1","+ERAT"},
//                        new String[]{"AT+COPS=1,2,46000","+COPS"});
                result.putExtra(Util.NETWORK_MODE_TYPE, Util.TYPE_CMCC_TDSCDMA);
                break;
            case R.id.cmcc_lte:
//                init(new String[]{"AT+ERAT=3","+ERAT"},
//                        new String[]{"AT+COPS=1,2,46000","+COPS"});
                result.putExtra(Util.NETWORK_MODE_TYPE, Util.TYPE_CMCC_LTE);
                break;
        }
        startActivity(result);
    }

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case EVENT_SET_GENERATION:{
//                    AsyncResult ar = (AsyncResult) msg.obj;
//                    Log.i("gejun","exception = " + ar.exception);
//                    String atStr[] = {"AT+ECELL","+ECELL"};
//                    phone.invokeOemRilRequestStrings(atStr, mHandler
//                            .obtainMessage(EVENT_GET_NETWORKMODE));
////                    phone.invokeOemRilRequestStrings(new String[]{"AT+CSQ","+CSQ"}, mHandler
////                            .obtainMessage(EVENT_GET_RSSI));
//                    break;
//                }
//                case EVENT_GET_NETWORKMODE:{
//                    showResult(msg, "EVENT_GET_NETWORKMODE");
//                    break;
//                }
//                case EVENT_GET_RSSI:{
//                    showResult(msg, "EVENT_GET_NETWORKMODE");
//                    break;
//                }
//            }
//        }
//    };
//
//    private void showResult(Message msg, String tag){
//        AsyncResult ar1 = (AsyncResult)msg.obj;
//        String[] result1 = (String[]) ar1.result;
//        if(result1 == null){
//            Log.i("gejun","result null");
//            return;
//        }
//        int length1 = result1.length;
//        for(int i= 0;i<length1;i++){
//            Log.i("gejun",tag + i +" = " + result1[i]);
//            info.setText(result1[i]);
//        }
//
//        List<CellInfo> infos = manager.getAllCellInfo();
//        Log.i("gejun","infos = " + infos );
//        if(infos != null){
//            for(CellInfo info:infos){
//                Log.i("gejun","info = " + info);
//            }
//        }
//    }
//
//    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
//        @Override
//        public void onCellInfoChanged(List<CellInfo> cellInfo) {
//            Log.i("gejun","cellInfo = " + cellInfo);
//            if(cellInfo != null){
//                for(CellInfo info:cellInfo){
//                    Log.i("gejun","info = " + info);
//                }
//            }
//        }
//    };
}
