package com.kuaikan.app.scenecollection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import com.android.internal.telephony.Phone;
//import com.android.internal.telephony.PhoneFactory;
//import android.os.AsyncResult;
//import com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy;

public class GsmResultActivity extends Activity implements OnClickListener{

//    private Button g2;
//    private Button g3;
//    private Button g4;
//
//    private TextView info;

//    private TelephonyManager manager;
//    private Phone phone;

    private int networkModeType;
    List<Result> resultList;
    private ListView list;
    private DataAdapter mDataAdapter;
    private String mmc;
    private String mnc;
    private RelativeLayout bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gsm_list);

        networkModeType = getIntent().getIntExtra(Util.NETWORK_MODE_TYPE, 0);
        Log.i("gejun","networkModeType = " + networkModeType);
        if(savedInstanceState == null) {
            if (networkModeType == Util.TYPE_CMCC_GSM || networkModeType == Util.TYPE_CMCC_TDSCDMA || networkModeType == Util.TYPE_CMCC_LTE) {
//                OpSetting.getIntentce().switchSimCard(this, OpSetting.OP_CMCC);
                Util.startSetOPService(this, OpSetting.OP_CMCC);
            } else if (networkModeType == Util.TYPE_CU_GSM || networkModeType == Util.TYPE_CU_LTE || networkModeType == Util.TYPE_CU_WCDMA) {
//                OpSetting.getIntentce().switchSimCard(this, OpSetting.OP_CU);
                Util.startSetOPService(this, OpSetting.OP_CU);
            } else if (networkModeType == Util.TYPE_TELECOM_LTE) {
//                OpSetting.getIntentce().switchSimCard(this, OpSetting.OP_TELCOM);
                Util.startSetOPService(this, OpSetting.OP_TELCOM);
            }
        }

        resultList = new ArrayList<Result>();

        list = (ListView) findViewById(R.id.list);
        bar = (RelativeLayout) findViewById(R.id.progress);

        Util.invokeAT(new String[]{"AT+EGMR=0,5", "+EGMR"}, mHandler.obtainMessage(100));
    }

    @Override
    public void onResume() {
        super.onResume();
//        Util.invokeAT(new String[]{"AT+COPS=?", "+COPS?"}, mHandler.obtainMessage(EVENT_COPS));

        mDataAdapter = new DataAdapter(this);
        list.setAdapter(mDataAdapter);

        if(networkModeType == Util.TYPE_CU_GSM){
            init(new String[]{"AT+ERAT=0,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46001","+COPS"});
            setTitle(R.string.cu_gsm);
        } else if (networkModeType == Util.TYPE_CU_WCDMA){
            init(new String[]{"AT+ERAT=1,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46001","+COPS"});
            setTitle(R.string.wcdma);
        } else if (networkModeType == Util.TYPE_CU_LTE){
            init(new String[]{"AT+ERAT=3,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46001","+COPS"});
            setTitle(R.string.cu_lte);
        } else if (networkModeType == Util.TYPE_CMCC_GSM){
            init(new String[]{"AT+ERAT=0,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46000","+COPS"});
            setTitle(R.string.cmcc_gsm);
        } else if (networkModeType == Util.TYPE_CMCC_TDSCDMA){
            init(new String[]{"AT+ERAT=1,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46000","+COPS"});
            setTitle(R.string.tdscdma);
        } else if (networkModeType == Util.TYPE_CMCC_LTE){
            init(new String[]{"AT+ERAT=3,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46000","+COPS"});
            setTitle(R.string.cmcc_lte);
        } else if (networkModeType == Util.TYPE_TELECOM_LTE){
            init(new String[]{"AT+ERAT=3,0", "+ERAT"},
                    new String[]{"AT+COPS=1,2,46011","+COPS"});
            setTitle(R.string.telecom_lte);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("gejun","GSMResultActivity onPause remove EVENT_GET_CELLINFO");
        mHandler.removeMessages(EVENT_GET_CELLINFO);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("com.freeme.colse_signal");
        sendBroadcast(intent);
    }

    private static final int EVENT_SET_GENERATION = 0;
    private static final int EVENT_SET_OP = 1;
    private static final int EVENT_GET_NETWORKMODE = 2;
    private static final int EVENT_GET_RSSI = 3;
    private static final int EVENT_GET_CELLINFO = 4;
    private static final int EVENT_COPS = 5;


    private void setGeneration(String[] atCmd){
        Log.i("gejun","rat = " + atCmd[0]);
        Util.invokeAT(atCmd, mHandler.obtainMessage(EVENT_SET_GENERATION));
    }

    private void setOp(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_OP));
    }

    private void init(String[] g, String[] op){
        setOp(op);
        setGeneration(g);
    }


    @Override
    public void onClick(View arg0) {
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_SET_GENERATION:{
                    mHandler.sendEmptyMessage(EVENT_GET_CELLINFO);
                    break;
                }
                case EVENT_GET_NETWORKMODE:{
                    showResult(msg, "EVENT_GET_NETWORKMODE");
                    break;
                }
                case EVENT_GET_RSSI:{
//                    showResult(msg, "EVENT_GET_NETWORKMODE");
                    break;
                }
                case EVENT_GET_CELLINFO:{
                    try {
                        Util.getCellInfo(mHandler.obtainMessage(EVENT_GET_NETWORKMODE));
                    } catch (Exception e){
                        Log.i("gejun","e = "+ e.toString());
                    }
                    mHandler.removeMessages(EVENT_GET_CELLINFO);
                    mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
                    break;
                }
//                case EVENT_COPS:{
//                    showResult(msg, "EVENT_COPS");
//                    break;
//                }
                case 100:{
                    showResult(msg, "###############");
                    break;
                }
            }
        }
    };

    private void showResult(Message msg, String tag){
        try {
            Class arC = Class.forName("android.os.AsyncResult");
            Field result = arC.getDeclaredField("result");
            Object resultString = result.get(msg.obj);
            if(resultString == null) return;
            String[] arr = (String[]) resultString;
            for(int i =0;i<arr.length;i++){
                Log.i("gejun","show Result = " + arr[i]);
            }
            extraData(arr[0]);
        } catch (Exception e){
            Log.i("gejun","e = " + e.toString());
        }
    }

    private void extraData(String o){
//        String tmp = o.substring("+ECELL");
        String tmp = o.replaceAll(" ", "");
//        Log.i("gejun","tmp = " + tmp);
        if(tmp.length() <= 8) return;
        tmp = tmp.substring(7);
        int count = Integer.parseInt(tmp.substring(0, 1));
//        Log.i("gejun","tmp1111 = " + tmp);
        tmp = tmp.substring(2);
//        Log.i("gejun","tmp = " + tmp);
        String[] allItems = tmp.split(",");
        resultList.clear();
        for(int i=0;i<count;i++){
            GsmResult item = new GsmResult();
            if(allItems[i*13 + 2].equals("\"FFFF\"")) continue;
//            Log.i("gejun","act = " + allItems[0]);
            if((networkModeType == Util.TYPE_CMCC_GSM
                    || networkModeType == Util.TYPE_CMCC_LTE
                    || networkModeType == Util.TYPE_CMCC_TDSCDMA)
                    && !allItems[4].equals("0")){
                return;
            }
            if((networkModeType == Util.TYPE_CU_GSM
                    || networkModeType == Util.TYPE_CU_LTE
                    || networkModeType == Util.TYPE_CU_WCDMA)
                    && !allItems[4].equals("1")){
                return;
            }
            if(networkModeType == Util.TYPE_TELECOM_LTE
                    && !allItems[4].equals("11")){
                return;
            }
            if((networkModeType == Util.TYPE_CMCC_GSM
                    || networkModeType == Util.TYPE_CU_GSM) && !allItems[0].equals("0")){
                return;
            }
            if((networkModeType == Util.TYPE_CMCC_TDSCDMA
                    || networkModeType ==Util.TYPE_CU_WCDMA) && !allItems[0].equals("2")){
                return;
            }
            if((networkModeType == Util.TYPE_TELECOM_LTE
                    || networkModeType ==Util.TYPE_CMCC_LTE || networkModeType == Util.TYPE_CU_LTE) && !allItems[0].equals("7")){
                return;
            }
///            Log.i("gejun","tmp = " + allItems[i * 13 + 1]);
//            Log.i("gejun","tmp = " + allItems[i * 13 + 2]);
            item.setAct(allItems[i * 13]);
            item.setCellId(allItems[i * 13 + 1]);
            item.setLac(allItems[i * 13 + 2]);
            item.setMcc(allItems[i * 13 + 3]);
            item.setMnc(allItems[i * 13 + 4]);
            item.setPsc_or_pci(allItems[i * 13 + 5]);
            item.setSig1(allItems[i * 13 + 6]);
            item.setSig1_in_dbm(allItems[i * 13 + 8]);
            item.setSig2(allItems[i * 13 + 7]);
            item.setSig2_in_dbm(allItems[i * 13 + 9]);
            resultList.add(item);
        }

        Intent intent;
        if(allItems[4].equals("0")){
            intent = new Intent("com.freeme.open_cmcc");
            sendBroadcast(intent);
        } else if(allItems[4].equals("1")){
            intent = new Intent("com.freeme.open_cu");
            sendBroadcast(intent);
        }

        bar.setVisibility(View.GONE);
        mDataAdapter.notifyDataSetChanged();
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            Log.i("gejun","cellInfo = " + cellInfo);
            if(cellInfo != null){
                for(CellInfo info:cellInfo){
                    Log.i("gejun","info = " + info);
                }
            }
        }
    };

    class DataAdapter extends BaseAdapter {
        private Context context;
        public DataAdapter(Context context)
        {
            this.context = context;
        }
        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.gsm_item, null);
            TextView lac = (TextView)view.findViewById(R.id.lac);
            TextView cellId = (TextView)view.findViewById(R.id.cell_id);
            TextView rssi = (TextView)view.findViewById(R.id.rssi);
            TextView bcch = (TextView)view.findViewById(R.id.bcch);
            bcch.setVisibility(View.GONE);
            TextView bsic = (TextView)view.findViewById(R.id.bsic);
            bsic.setVisibility(View.GONE);

            GsmResult resultItem = (GsmResult) resultList.get(position);
            lac.setText(toEight(resultItem.getLac()));
            cellId.setText(toEight(resultItem.getCellId()));
            bcch.setText(resultItem.getBcch());
            bsic.setText(resultItem.getBsic());
            rssi.setText(resultItem.getSig1());

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.save){
            if(bar.getVisibility() == View.VISIBLE){
                Toast.makeText(this, R.string.none_info, Toast.LENGTH_SHORT).show();
                return true;
            }
            Util.saveToXml(this, resultList);
        }
        return super.onOptionsItemSelected(item);
    }

    private String toEight(String shiliu){
        shiliu = shiliu.substring(1, shiliu.length()-1);
        return Integer.parseInt(shiliu, 16)+"";
    }

    private Object reflectPhone(){
        try {
            Class pf = Class.forName("com.android.internal.telephony.PhoneFactory");
            Method getPhone = pf.getDeclaredMethod("getDefaultPhone");
            Object phone1 = getPhone.invoke(pf);

            Class LteDcPhoneProxyC = Class.forName("com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy");
            Object phone2 = LteDcPhoneProxyC.cast(phone1);

            Method getLtePhoneM = LteDcPhoneProxyC.getDeclaredMethod("getLtePhone");
            Object phone3 = getLtePhoneM.invoke(phone2);
            Log.i("gejun", "phone1 = " + phone1);
            Log.i("gejun", "phone2 = " + phone2);
            Log.i("gejun", "phone3 = " + phone3);

            return phone3;
        }catch (Exception exception){
            Log.i("gejun","exception = " + exception.toString());
            return null;
        }
    }

}
