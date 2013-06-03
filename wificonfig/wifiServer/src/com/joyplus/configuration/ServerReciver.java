package com.joyplus.configuration;

import java.lang.reflect.Method;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.ijetty.util.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ServerReciver extends BroadcastReceiver
{
    private static final int WIFI_AP_STATE_UNKNOWN = -1;
    private static final int WIFI_AP_STATE_DISABLING = 10;  
    private static final int WIFI_AP_STATE_DISABLED = 11;  
    private static final int WIFI_AP_STATE_ENABLING = 12;  
    private static final int WIFI_AP_STATE_ENABLED = 13;  
    private static final int WIFI_AP_STATE_FAILED = 14;
    
    public static final String __PORT = "org.mortbay.ijetty.port";
    public static final String __NIO = "org.mortbay.ijetty.nio";
    public static final String __SSL = "org.mortbay.ijetty.ssl";
    
    public static final String __CONSOLE_PWD = "org.mortbay.ijetty.console";
    public static final String __PORT_DEFAULT = "8080";
    public static final boolean __NIO_DEFAULT = true;
    public static final boolean __SSL_DEFAULT = false;
    public static final String __CONSOLE_PWD_DEFAULT = "admin";
    
    private static final String TAG = "ServerReciver";
    private String ipAddress;
//    public static List<ScanResult> wifiList;
//    private static boolean isInit = false;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        String action = intent.getAction();
//        Toast.makeText(context,"boot ok",Toast.LENGTH_LONG).show();
        if("android.intent.action.BOOT_COMPLETED".equals(action)){
//            try
//            {
//                Thread.sleep(3000);
//            }
//            catch (InterruptedException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            isInit = false;
//            Toast.makeText(context,"boot ok",Toast.LENGTH_LONG).show();
            Log.d(TAG,"boot complete ------------------------------------->");
            WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
            if(isApOn(context)){
                setWifiApEnabled(false,context);
                do{
                    waiting(); 
                }while(!isApOff(context));
            }
            wifiManager.setWifiEnabled(true);
            do{
                waiting();
            }while(wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED);
            wifiManager.startScan();
            while (wifiManager.getScanResults()==null)
            {
                waiting();
            }
            List<ScanResult> wifiList = wifiManager.getScanResults();
//            Toast.makeText(context,"size = " + wifiList.size(),Toast.LENGTH_LONG).show();
            Log.d(TAG,"boot complete ----------------wifiList---->"+wifiList.size());
            saveList(wifiList,context);
//            isInit = true;
            try
            {
                Log.d(TAG,"sleeping 2 second");
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG,"sleeping 2 second error");
            }
            ConnectivityManager connetivitManager = (ConnectivityManager)context.getSystemService("connectivity");
            NetworkInfo info = connetivitManager.getActiveNetworkInfo();
            if(info!=null&&info.isAvailable()&&info.isConnectedOrConnecting()){
                Log.d(TAG,"boot complete -------------have wifi ------------->");
//                Toast.makeText(context,"have wifi",Toast.LENGTH_LONG).show();
                
                restartServer(context);
//                while(!isApOn(context)){
//                    waiting();
//                } 
                return ;
            }else{
              wifiManager.setWifiEnabled(false);
              while(wifiManager.getWifiState()!=WifiManager.WIFI_STATE_DISABLED){
                  waiting();  
              }
              waiting();
              setWifiApEnabled(true,context);
              int retryCount = 5;
              while (retryCount>0)
              {
                  try
                  {
                      Thread.sleep(2000);
                  }
                  catch (InterruptedException e)
                  {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }
                  if(isApOn(context)){
                      return;
                  }
                  if(isApOff(context)){
                      setWifiApEnabled(true,context);
                  }
                  retryCount--;
               }
            }
//            Toast.makeText(context,"boot ok2",Toast.LENGTH_LONG).show();
          restartServer(context);
        }else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
            if(getLocalIpAddress(context)!=null){
                ConnectivityManager connetivitManager = (ConnectivityManager)context.getSystemService("connectivity");
                NetworkInfo info = connetivitManager.getActiveNetworkInfo();
                if(info != null&&info.isAvailable()&&info.isConnected()){
                    if(!getLocalIpAddress(context).equals(ipAddress)){
                         restartServer(context);
                     }
                }
                ipAddress = getLocalIpAddress(context);
            }
            
          }
          else if("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())){
              try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
              if(isApOn(context)){
                  restartServer(context);
              }
          }

    }
    
    private void waiting(){
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void setWifiApEnabled(boolean enabled,Context c) {
        WifiConfiguration configuration;
        WifiManager wifiManager = (WifiManager)c.getSystemService("wifi");
        try {
                Method method = WifiManager.class.getMethod("getWifiApConfiguration");
                configuration = (WifiConfiguration) method.invoke(wifiManager); 
                Method method1 = wifiManager.getClass().getMethod("setWifiApEnabled",
                WifiConfiguration.class, boolean.class);
                method1.invoke(wifiManager, configuration, enabled); // true
//                Toast.makeText(c,"setWifiApEnabled"+enabled,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
                // TODO Auto-generated catch block
//            Toast.makeText(c,"error",Toast.LENGTH_LONG).show(); 
                e.printStackTrace();
        }
}
    
    public String getLocalIpAddress(Context c) {  
        WifiManager wifiManager = (WifiManager)c.getSystemService("wifi");
        WifiInfo info = wifiManager.getConnectionInfo();
               if(info!=null){
                       int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                       if(isApEnabled(c)){
                           return null;
                       }
                       return intToIp(ipAddress);
               }else{
                       return null;
               }
           
           
    }

    private String intToIp(int i)
     {
       return (i & 0xFF) + "." + (i >> 8 & 0xFF) + "." + (i >> 16 & 0xFF) + "." + (i >> 24 & 0xFF);
     }
    
    private boolean isApOn(Context c) {  
        int state = getWifiApState(c);  
        return WIFI_AP_STATE_ENABLED == state;  
    }
    
    private boolean isApOff(Context c) {  
        int state = getWifiApState(c);  
        return WIFI_AP_STATE_DISABLED == state;  
    }
    
    private boolean isApEnabled(Context c) {  
        int state = getWifiApState(c);  
        return WIFI_AP_STATE_ENABLING == state || WIFI_AP_STATE_ENABLED == state;   
    }
    
    private int getWifiApState(Context c) {  
        try {  
            WifiManager wifiManager = (WifiManager)c.getSystemService("wifi");
            Method method = wifiManager.getClass().getMethod("getWifiApState");  
            return (Integer) method.invoke(wifiManager);  
        } catch (Exception e) {  
            Log.e(TAG, "Cannot get WiFi AP state", e);  
            return WIFI_AP_STATE_FAILED;  
        }  
    } 
    
    private void  saveList(List<ScanResult> list,Context c){
        try
        {
            JSONObject json = new JSONObject();
            JSONArray arry = new JSONArray();
            if(list!=null){
                for(int i = 0; i<list.size(); i++){
                    JSONObject item = new JSONObject();
                    ScanResult scanResult = list.get(i);
                    item.put("SSID",scanResult.SSID);
                    item.put("level",scanResult.level);
                    item.put("capabilities",scanResult.capabilities);
                    arry.put(item);
                }
            }
            json.put("items",arry);
            SharedPreferences s = c.getSharedPreferences("wifiList",Context.MODE_WORLD_WRITEABLE);
            Editor editor = s.edit();
            editor.putString("wifiList",json.toString());
            editor.commit();
            Log.d(TAG,"save success!");
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    private synchronized void restartServer(Context c){
        Log.d(TAG,"restart called----");
//        if(!isInit){
//            Log.d(TAG,"not init");
//            return;
//        }
        c.stopService(new Intent(c,IJettyService.class));
        Intent intent1 = new Intent(c,IJettyService.class);
        intent1.putExtra(__PORT,__PORT_DEFAULT);
        intent1.putExtra(__NIO,__NIO_DEFAULT);
        intent1.putExtra(__SSL,__SSL_DEFAULT);
        intent1.putExtra(__CONSOLE_PWD,__CONSOLE_PWD_DEFAULT);
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        c.startService(intent1);
    }
//    private void startServer(){
//        Intent intent1 = new Intent(c,IJettyService.class);
//        intent1.putExtra(__PORT,__PORT_DEFAULT);
//        intent1.putExtra(__NIO,__NIO_DEFAULT);
//        intent1.putExtra(__SSL,__SSL_DEFAULT);
//        intent1.putExtra(__CONSOLE_PWD,__CONSOLE_PWD_DEFAULT);
//        c.startService(intent1);
//    }

}
