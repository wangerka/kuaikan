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
import android.widget.ListView;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.FileOutputStream;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;
import java.util.UUID;
import java.lang.reflect.Field;

public class OneKeyActivity extends Activity{

    private RelativeLayout bar;
    private ListView list1;
    private ListView list2;
    private ListView list3;
    private ListView list4;
    private ScrollView sv;

    private TextView start;
    private TextView end;

    private int flag = 0;
    private int flagB = 0;


    ///private List<GsmResult> resultList;
    private List<GsmResult> result1;
    private List<GsmResult> result2;
    private List<GsmResult> result3;
    private List<CdmaResult> result4;
    private List<GsmResult> totalList;

    private DataAdapter mDataAdapter1;
    private DataAdapter mDataAdapter2;
    private DataAdapter mDataAdapter3;
    private DataAdapter mDataAdapter4;


//    private Phone phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_key);
        if(savedInstanceState != null){
            flagB = savedInstanceState.getInt("flagb");
        }

        list1 = (ListView) findViewById(R.id.list1);
        list2 = (ListView) findViewById(R.id.list2);
        list3 = (ListView) findViewById(R.id.list3);
        Log.i("gejun","list1 = "+ list1);
        Log.i("gejun","list2 = "+ list2);
        Log.i("gejun","list3 = "+ list3);
//        list4 = (ListView) findViewById(R.id.list4);
        bar = (RelativeLayout) findViewById(R.id.progress);
        sv = (ScrollView) findViewById(R.id.sv);

        start = (TextView) findViewById(R.id.startTime);
        start.setText(Util.getTime());
        end = (TextView) findViewById(R.id.endTime);

//        try {
//            phone = PhoneFactory.getDefaultPhone();
//        } catch (Exception exception) {
//            Toast.makeText(this, R.string.phone_not_avaliable, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        if(phone instanceof LteDcPhoneProxy){
//            Log.i("gejun","init LteDcPhoneProxy");
//            phone = ((LteDcPhoneProxy) phone).getLtePhone();
//        }



//        resultList = new ArrayList<GsmResult>();
        result1 = new ArrayList<GsmResult>();
        result2 = new ArrayList<GsmResult>();
        result3 = new ArrayList<GsmResult>();
        result4 = new ArrayList<CdmaResult>();
        totalList = new ArrayList<GsmResult>();

        mDataAdapter1 = new DataAdapter(this);
        mDataAdapter1.setData(result1);
        list1.setAdapter(mDataAdapter1);

        mDataAdapter2 = new DataAdapter(this);
        mDataAdapter2.setData(result2);
        list2.setAdapter(mDataAdapter2);

        mDataAdapter3 = new DataAdapter(this);
        mDataAdapter3.setData(result3);
        list3.setAdapter(mDataAdapter3);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(flagB == 0) {
            OpSetting.getIntentce().switchSimCard(this, OpSetting.OP_CU);
            init(new String[]{"AT+ERAT=0,0", "+ERAT"},
                    new String[]{"AT+COPS=1,2,46001", "+COPS"});
        } else {
            OpSetting.getIntentce().switchSimCard(this, OpSetting.OP_CMCC);
            init(new String[]{"AT+ERAT=0,0", "+ERAT"},
                    new String[]{"AT+COPS=1,2,46001", "+COPS"});
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_GET_CELLINFO);
    }

    private static final int EVENT_SET_GENERATION = 0;
    private static final int EVENT_SET_OP = 1;
    private static final int EVENT_GET_NETWORKMODE = 2;
    private static final int EVENT_GET_RSSI = 3;
    private static final int EVENT_GET_CELLINFO = 4;


    private void setGeneration(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_GENERATION));
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_SET_GENERATION:{
//                    AsyncResult ar = (AsyncResult) msg.obj;
//                    Log.i("gejun","exception = " + ar.exception);
                    mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
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

                    }
                    mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
                    break;
                }
            }
        }
    };

    private void showResult(Message msg, String tag){
//        AsyncResult ar1 = (AsyncResult)msg.obj;
//        String[] result1 = (String[]) ar1.result;
//        if(result1 == null || result1.length == 0){
//            Log.i("gejun","result null");
//            return;
//        }
//
//        extraData(result1[0]);
//
//        int length1 = result1.length;
//        for(int i= 0;i<length1;i++){
//            Log.i("gejun",tag + i +" = " + result1[i]);
//        }

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
        List<GsmResult> resultList = new ArrayList<GsmResult>();

        GsmResult row1 = new GsmResult();
        String mnc = allItems[4];
        if(mnc.equals("0")){
            if(flag == 0){
                row1.setLac(getResources().getString(R.string.cmcc_gsm));
            } else if(flag == 1){
                row1.setLac(getResources().getString(R.string.tdscdma));
            } else if(flag == 2){
                row1.setLac(getResources().getString(R.string.cmcc_lte));
            }
        } else if(mnc.equals("1")) {
            if(flag == 0){
                row1.setLac(getResources().getString(R.string.cu_gsm));
            } else if(flag == 1){
                row1.setLac(getResources().getString(R.string.wcdma));
            } else if(flag == 2){
                row1.setLac(getResources().getString(R.string.cu_lte));
            }
        } else if(mnc.equals("11")){
            if(flag == 2){
                row1.setLac(getResources().getString(R.string.telecom_lte));
            }
        }
        resultList.add(row1);

        GsmResult row2 = new GsmResult();
        row2.setLac(getResources().getString(R.string.lac));
        row2.setCellId(getResources().getString(R.string.cell_id));
        row2.setSig1(getResources().getString(R.string.rssi));
        resultList.add(row2);

        for(int i=0;i<count;i++){
            GsmResult item = new GsmResult();
            if(allItems[i*13 + 2].equals("\"FFFF\"")) continue;
//            Log.i("gejun","act = " + allItems[0]);
            if(flag == 0 && !allItems[0].equals("0")){
                return;
            }
            if(flag == 1 && !allItems[0].equals("2")){
                return;
            }
            if(flag == 2 && !allItems[0].equals("7")){
                return;
            } else if(flag == 3 && flagB == 1 ){
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

        if(flag == 0){
            Log.i("gejun","resutl size111 = " + resultList.size());
            totalList.addAll(resultList);
            mDataAdapter1.setData(resultList);
            setListViewHeightBasedOnChildren(list1);
            mDataAdapter1.notifyDataSetChanged();
            flag++;
            mHandler.removeMessages(EVENT_GET_CELLINFO);
            init(new String[]{"AT+ERAT=1,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46001","+COPS"});
        } else if (flag == 1){
            Log.i("gejun","resutl size222 = " + resultList.size());
            totalList.addAll(resultList);
            mDataAdapter2.setData(resultList);
            setListViewHeightBasedOnChildren(list2);
            mDataAdapter2.notifyDataSetChanged();
            flag++;
            mHandler.removeMessages(EVENT_GET_CELLINFO);
            init(new String[]{"AT+ERAT=3,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46001","+COPS"});
        } else if(flag == 2){
            Log.i("gejun","resutl size333 = " + resultList.size());
            totalList.addAll(resultList);
            mDataAdapter3.setData(resultList);
            setListViewHeightBasedOnChildren(list3);
            mDataAdapter3.notifyDataSetChanged();
            flag++;
        } else if(flag == 3){
//            result4 = resultList;
            //flag ++;
            OpSetting.getIntentce().switchSimCard(this, OpSetting.OP_CMCC);
            init(new String[]{"AT+ERAT=0,0","+ERAT"},
                    new String[]{"AT+COPS=1,2,46001","+COPS"});
            flag = 0;
            flagB = 1;
        }

        Log.i("gejun","flag = " + flag + ", flagB = " + flagB);
        if(flag == 3 && flagB == 1){
            mHandler.removeMessages(EVENT_GET_CELLINFO);

            bar.setVisibility(View.GONE);
            end.setText(Util.getTime());

            sv.scrollTo(0, 0);
        }

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

        private List<GsmResult> result;
        public void setData(List<GsmResult> list){
            result = list;
        }
        private Context context;
        public DataAdapter(Context context)
        {
            this.context = context;
        }
        @Override
        public int getCount() {
            return result.size();
        }

        @Override
        public Object getItem(int position) {
            return result.get(position);
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
            if(position == 0){
                view.setBackgroundResource(R.drawable.top_bg);
                cellId.setVisibility(View.GONE);
                rssi.setVisibility(View.GONE);
            } else if(position == getCount() -1){
                view.setBackgroundResource(R.drawable.bottom_bg);
                cellId.setVisibility(View.VISIBLE);
                rssi.setVisibility(View.VISIBLE);
            } else {
                view.setBackgroundResource(R.drawable.mid_bg);
                cellId.setVisibility(View.VISIBLE);
                rssi.setVisibility(View.VISIBLE);
            }

            GsmResult resultItem = result.get(position);
            Log.i("gejun","LAC = " + resultItem.getLac());
            if(position == 0 || position == 1){
                lac.setText(resultItem.getLac());
                cellId.setText(resultItem.getCellId());
            } else {
                lac.setText(toEight(resultItem.getLac()));
                cellId.setText(toEight(resultItem.getCellId()));
            }
            bcch.setText(resultItem.getBcch());
            bsic.setText(resultItem.getBsic());
            rssi.setText(resultItem.getSig1());

            return view;
        }

    }

    private String toEight(String shiliu){
        shiliu = shiliu.substring(1, shiliu.length()-1);
        return Integer.parseInt(shiliu, 16)+"";
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            GsmResult item = (GsmResult) listAdapter.getItem(i);
            Log.i("gejun","item.LAC = " + item.getLac());
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        Log.i("gejun","ListHeight = " + params.height);
        listView.setLayoutParams(params);
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
            try {
                save(totalList);
                Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
            } catch (Exception exception){
                //
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void save(List<GsmResult> retults)
            throws Exception, IllegalStateException, IOException {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString().trim().replaceAll("-", "");
        String fileName = "attach_" + uuidString +".xml";

        FileOutputStream outStream = new FileOutputStream("/storage/sdcard0/"+fileName);
        XmlSerializer serializer = Xml.newSerializer();//获取XML写入信息的序列化对象
        serializer.setOutput(outStream, "UTF-8");//设置要写入的OutputStream
        serializer.startDocument("UTF-8", false);//设置文档标签
        serializer.startTag(null, "datas");//设置开始标签，第一个参数为namespace
//        serializer.startTag(null, "gsminfos");
        for (GsmResult result : retults) {

            String lac = result.getLac();
            lac = lac.substring(1, lac.length()-1);
            if(!isRightCellInfo(lac)) continue;
//            serializer.attribute(null, "id", "11111111111111111111111");

            if(result.getAct().equals("0")){
                serializer.startTag(null, "gsm");
            } else if(result.getAct().equals("2")
                    && result.getMnc().equals("1")) {
                serializer.startTag(null, "wcdma");
            } else if(result.getAct().equals("2")
                    && result.getMnc().equals("0")){
                serializer.startTag(null, "tdscdma");
            } else if (result.getAct().equals("7")){
                serializer.startTag(null, "lte");
            } else {
                serializer.startTag(null, "unknown");
            }

            fillItem(serializer, "createtime", Util.getTime());

            fillItem(serializer, "attachid", uuidString);

//            String lac = result.getLac();
//            lac = lac.substring(1, lac.length()-1);
            fillItem(serializer, "lac", Integer.parseInt(lac, 16)+"");

            String ci = result.getCellId();
            ci = ci.substring(1, ci.length()-1);
            fillItem(serializer, "cellid", Integer.parseInt(ci, 16)+"");

            fillItem(serializer, "mcc", result.getMcc());

            fillItem(serializer, "mnc", result.getMnc());

            fillItem(serializer, "psc_or_pci", result.getPsc_or_pci());

            fillItem(serializer, "sig1", result.getSig1());

            fillItem(serializer, "sig1_in_dbm", result.getSig1_in_dbm());

            fillItem(serializer, "sig2", result.getSig2());

            fillItem(serializer, "sig2_in_dbm", result.getSig2_in_dbm());

            if(result.getAct().equals("0")){
                serializer.endTag(null, "gsm");
            } else if(result.getAct().equals("2")
                    && result.getMnc().equals("1")) {
                serializer.endTag(null, "wcdma");
            } else if(result.getAct().equals("2")
                    && result.getMnc().equals("0")){
                serializer.endTag(null, "tdscdma");
            } else if (result.getAct().equals("7")){
                serializer.endTag(null, "lte");
            } else {
                serializer.endTag(null, "unknown");
            }
        }
//        serializer.endTag(null, "gsminfos");
        serializer.endTag(null, "datas");
        serializer.endDocument();
        outStream.flush();
        outStream.close();
    }

    private boolean isRightCellInfo(String lac){
        try{
            toEight(lac);
        } catch (Exception exception){
            return false;
        }
        return true;
    }

    private void fillItem(XmlSerializer serializer, String key, String value)
            throws Exception, IllegalStateException, IOException{
        serializer.startTag(null, key);
        serializer.text(value);
        serializer.endTag(null, key);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("flagb", flagB);
    }
}
