package com.kuaikan.app.scenecollection;

//import com.android.internal.telephony.Phone;
//import com.android.internal.telephony.PhoneFactory;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
//import com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.R.attr.tag;
import static com.kuaikan.app.scenecollection.Result.RAT_CDMA;

public class Util{

    public static final String COLLECT_TYPE = "dynamic";

    public static final String NETWORK_MODE_TYPE = "network_mode_type";
    public static final int TYPE_CMCC_GSM = 0;
    public static final int TYPE_CMCC_TDSCDMA = 1;
    public static final int TYPE_CMCC_LTE = 2;
    public static final int TYPE_CU_GSM = 3;
    public static final int TYPE_CU_WCDMA = 4;
    public static final int TYPE_CU_LTE = 5;
    public static final int TYPE_CDMA = 6;
    public static final int TYPE_TELECOM_LTE = 7;

    public static final String G2 = "0";
    public static final String G3 = "2";
    public static final String G4 = "7";

    public static final String[] CU_MNC = {"1"};
    public static final String[] CMCC_MNC = {"0"};
    public static final String[] TELECOM_MNC = {"11", "3"};

    public static void setGeneration(int generation, Message msg) throws Exception{
        String[] atCmd = new String[]{"AT+ERAT="+generation,"+ERAT"};
//        getPhone().invokeOemRilRequestStrings(atCmd, msg);
        invokeAT(atCmd, msg);
    }


    public static void getCellInfo(Message msg) throws Exception{
        String atStr[] = new String[]{"AT+ECELL","+ECELL"};

//        getPhone().invokeOemRilRequestStrings(atStr, msg);
        invokeAT(atStr, msg);
    }

//    public static Phone getPhone() throws Exception{
//        Class pf =  Class.forName("com.android.internal.telephony.PhoneFactory");
//        Method getPhone = pf.getDeclaredMethod("getDefaultPhone");
//
//        Log.i("gejun","p = " + getPhone.invoke(pf));
//        return (Phone)getPhone.invoke(pf);

//        Phone phone = PhoneFactory.getDefaultPhone();
//        if(phone instanceof LteDcPhoneProxy){
//            phone = ((LteDcPhoneProxy) phone).getLtePhone();
//        }
//        return phone;
//    }

    public static String getTime(long time){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(time);
        String t1=format.format(d1);
        return t1;
    }

    public static String getTime(){
        long time = System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(time);
        String t1=format.format(d1);
        return t1;
    }

    public static Object reflectPhone(){
        try {
            Class pf = Class.forName("com.android.internal.telephony.PhoneFactory");
            Method getPhone = pf.getDeclaredMethod("getDefaultPhone");
            Object phone1 = getPhone.invoke(pf);

            Class LteDcPhoneProxyC = Class.forName("com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy");
            Object phone2 = LteDcPhoneProxyC.cast(phone1);

            Method getLtePhoneM = LteDcPhoneProxyC.getDeclaredMethod("getLtePhone");
            Object phone3 = getLtePhoneM.invoke(phone2);
//            Log.i("gejun", "phone1 = " + phone1);
//            Log.i("gejun", "phone2 = " + phone2);
//            Log.i("gejun", "phone3 = " + phone3);

            return phone3;
        }catch (Exception exception){
            Log.i("gejun","exception = " + exception.toString());
            return null;
        }
    }

    public static Object reflectCDMAPhone(){
        try {
            Class pf = Class.forName("com.android.internal.telephony.PhoneFactory");
            Method getPhone = pf.getDeclaredMethod("getDefaultPhone");
            Object phone1 = getPhone.invoke(pf);

            Class LteDcPhoneProxyC = Class.forName("com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy");
            Object phone2 = LteDcPhoneProxyC.cast(phone1);

            Method getLtePhoneM = LteDcPhoneProxyC.getDeclaredMethod("getNLtePhone");
            Object phone3 = getLtePhoneM.invoke(phone2);
//            Log.i("gejun", "phone1 = " + phone1);
//            Log.i("gejun", "phone2 = " + phone2);
//            Log.i("gejun", "phone3 = " + phone3);

            return phone3;
        }catch (Exception exception){
            Log.i("gejun","exception = " + exception.toString());
            return null;
        }
    }

    public static void invokeAT4CDMA(String[] atCmd, Message msg){
        try {
            Object phone = Util.reflectCDMAPhone();
            Class pf = Class.forName("com.android.internal.telephony.Phone");
            Method m = pf.getDeclaredMethod("invokeOemRilRequestStrings", new Class[]{String[].class, Message.class});
            m.invoke(phone, new Object[]{atCmd, msg});
//            Log.e("gejun", "[Util][invokeAT4CDMA] atCmd: "+ Arrays.toString(atCmd));
        } catch (Exception e){
            Log.i("gejun","e = " + e.toString());
        }
    }

    public static void invokeAT(String[] atCmd, Message msg){
        try {
            Object phone = Util.reflectPhone();
            Class pf = Class.forName("com.android.internal.telephony.Phone");
            Method m = pf.getDeclaredMethod("invokeOemRilRequestStrings", new Class[]{String[].class, Message.class});
            m.invoke(phone, new Object[]{atCmd, msg});
//            Log.e("gejun", "[Util][invokeAT] atCmd: "+ Arrays.toString(atCmd));
        } catch (Exception e){
            Log.i("gejun","e = " + e.toString());
        }
    }

    public static void startSetOPService(Context c, String op){
        Intent intent = new Intent();
        intent.setPackage("com.android.settings");
        intent.setAction("com.freeme.action.setop");
        intent.putExtra("op", op);
        c.startService(intent);
    }

    //xml保存
    public static final String START_RTT_RADIO_INFO = "+ECENGINFO:\"1xRTT_Radio_Info\"";
    public static final String START_RTT_INFO = "+ECENGINFO:\"1xRTT_Info\"";
    public static final String START_RTT_SERVING_NEIGHBR_SET_INFO = "+ECENGINFO:\"1xRTT_Serving_Neighbr_Set_Info\"";
    public static final String START_ECELL = "+ECELL";
    public static final String START_VLOCINFO = "+VLOCINFO";

    public static final String FILE_PATH = "/storage/sdcard0/";

    public static List<Result> parseResults(List<String> resultList){
        List<Result> cellinfos = new ArrayList<Result>();
        //cdma
        if(hasCDMAInfos(resultList)){
            parseCDMA(resultList, cellinfos);
        }

        //gsm
        parseGsm(resultList, cellinfos);
        return cellinfos;
    }

    public static void parseGsm(List<String> resultList, List<Result> cell){
        for(String currentCell : resultList){
            if(currentCell.startsWith("+ECELL")){
                String[] cellArrays = currentCell.split(",");
                String g = cellArrays[1];
                String mnc = cellArrays[5];
                String array1 = cellArrays[0].substring(cellArrays[0].length() - 1);
                int count = Integer.parseInt(array1);

                for(int j=0;j<count;j++){
                    GsmResult item = new GsmResult();
                    item.setAct(cellArrays[j * 13 + 1]);
                    item.setCellId(cellArrays[j * 13 + 2]);
                    item.setLac(cellArrays[j * 13 + 3]);
                    item.setMcc(cellArrays[j * 13 + 4]);
                    item.setMnc(cellArrays[j * 13 + 5]);
                    item.setPsc_or_pci(cellArrays[j * 13 + 6]);
                    item.setSig1(cellArrays[j * 13 + 7]);
                    item.setSig2(cellArrays[j * 13 + 8]);
                    item.setSig1_in_dbm(cellArrays[j * 13 + 9]);
                    item.setSig2_in_dbm(cellArrays[j * 13 + 10]);
                    cell.add(item);
                }
            }
        }
    }

    public static boolean hasCDMAInfos(List<String> resultList){
        for(String item : resultList){
            if(item.startsWith("+VLOCINFO") || item.startsWith("+ECENGINFO")){
                return true;
            }
        }
        return false;
    }

    public static void parseCDMA(List<String> resultList, List<Result> cell){
        String mcc = "";
        String mnc = "";
        String bid = "";
        String sid = "";
        String nid = "";
        int rx_power1 = 0;
        for(String item : resultList){
            if(item.startsWith("+VLOCINFO")){
                String[] info1 = item.split(",");
                mcc = info1[1];
                mnc = info1[2];
                sid = info1[3];
                nid = info1[4];
                bid = info1[5];
            }
        }

        for(String item : resultList){
            if(item.startsWith("+ECENGINFO:\"1xRTT_Radio_Info\"")){
                rx_power1 = Integer.parseInt(item.split(",")[4]);
            }
        }

        for(String item : resultList){
            if(item.startsWith("+ECENGINFO:\"1xRTT_Serving_Neighbr_Set_Info\"")){
                String[] info2 = item.split(",");
                int cand_set_count = Integer.parseInt(info2[5]);
                int cellcount = Integer.parseInt(info2[6 + cand_set_count * 3]) + 1;
                for(int i = 0;i<cellcount;i++){
                    CdmaResult cdma = new CdmaResult();
                    if(i == 0){
                        cdma.setSid(sid);
                        cdma.setNid(nid);
                        cdma.setBid(bid);
                        cdma.setPn(info2[2]);
                        cdma.setRx(""+ (rx_power1 + Integer.parseInt(info2[3])/-2));
                    } else {
                        cdma.setSid(sid);
                        cdma.setNid(nid);
                        cdma.setBid(bid);
                        cdma.setPn(info2[6 + cand_set_count * 3 + 1 + (i-1) * 3]);
                        int rx = Integer.parseInt(info2[6 + cand_set_count * 3 + 1 + ((i-1) * 3) + 1]);
                        cdma.setRx("" + (rx_power1 + rx/-2));
                    }
                    cell.add(cdma);
                }
            }
        }
    }

    public static String[] saveToXml(Context c, List<Result> cellInfos){
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString().trim().replaceAll("-", "");
        String fileName = "attach_" + uuidString +".xml";

        try {
            FileOutputStream outStream = new FileOutputStream(FILE_PATH + fileName);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(outStream, "UTF-8");
            serializer.startDocument("UTF-8", false);
            serializer.startTag(null, Result.DATA);

            for (Result item : cellInfos) {
                if(item instanceof GsmResult){
                    saveGsm(serializer, (GsmResult) item);
                } else if(item instanceof  CdmaResult){
                    saveCDMA(serializer, (CdmaResult)item);
                }
            }

            serializer.endTag(null, Result.DATA);
            serializer.endDocument();
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(c, R.string.save_success, Toast.LENGTH_SHORT).show();
        return new String[]{uuidString, FILE_PATH + fileName};
    }

    public static void saveGsm(XmlSerializer serializer, GsmResult result){
        String lac = result.getLac();
        lac = lac.substring(1, lac.length()-1);
        if(!isRightCellInfo(lac)) return;

        String mnc = result.getMnc();
        String rat = result.getAct();
        try {
            if(rat.equals(G2)){
                serializer.startTag(null, Result.RAT_2G);
            } else if(rat.equals(G3)
                    && Arrays.asList(CU_MNC).contains(mnc)) {
                serializer.startTag(null, Result.RAT_3G_CU);
            } else if(rat.equals(G3)
                    && Arrays.asList(CMCC_MNC).contains(mnc)){
                serializer.startTag(null, Result.RAT_3G_CMCC);
            } else if (rat.equals(G4)){
                serializer.startTag(null, Result.RAT_4G);
            }

            fillItem(serializer, Result.CREATE_TIME, Util.getTime());
            fillItem(serializer, Result.LAC, Integer.parseInt(lac, 16)+"");
            String ci = result.getCellId();
            ci = ci.substring(1, ci.length()-1);
            fillItem(serializer, Result.CID, Integer.parseInt(ci, 16)+"");
            fillItem(serializer, Result.MCC, result.getMcc());
            fillItem(serializer, Result.MNC, result.getMnc());
            fillItem(serializer, Result.PSC_PCI, result.getPsc_or_pci());
            fillItem(serializer, Result.SIG1, result.getSig1());
            fillItem(serializer, Result.SIG1DBM, result.getSig1_in_dbm());
            fillItem(serializer, Result.SIG2, result.getSig2());
            fillItem(serializer, Result.SIG2DBM, result.getSig2_in_dbm());

            if(rat.equals(G2)){
                serializer.endTag(null, Result.RAT_2G);
            } else if(rat.equals(G3)
                    && Arrays.asList(CU_MNC).contains(mnc)) {
                serializer.endTag(null, Result.RAT_3G_CU);
            } else if(rat.equals(G3)
                    && Arrays.asList(CMCC_MNC).contains(mnc)){
                serializer.endTag(null, Result.RAT_3G_CMCC);
            } else if (rat.equals(G4)){
                serializer.endTag(null, Result.RAT_4G);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCDMA(XmlSerializer serializer, CdmaResult result){
        try {
            serializer.startTag(null, Result.RAT_CDMA);

            fillItem(serializer, Result.CREATE_TIME, Util.getTime());
            fillItem(serializer, Result.NID, result.getNid());
            fillItem(serializer, Result.BID, result.getBid());
            fillItem(serializer, Result.SID, result.getSid());
            fillItem(serializer, Result.RX, result.getRx());
            fillItem(serializer, Result.PN, result.getPn());

            serializer.endTag(null, Result.RAT_CDMA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isRightCellInfo(String lac){
        try{
            toEight(lac);
        } catch (Exception exception){
            return false;
        }
        return true;
    }

    public static String toEight(String shiliu) {
        shiliu = shiliu.substring(1, shiliu.length() - 1);
        return Integer.parseInt(shiliu, 16) + "";
    }

    public static void fillItem(XmlSerializer serializer, String key, String value){
        try {
            serializer.startTag(null, key);
            serializer.text(value);
            serializer.endTag(null, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
