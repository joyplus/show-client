package com.joyplus.wificonfig;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;


public class WifiConfiger extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static final String TAG  = "WifiConfiger";
	private Context mContext;
//	private ContentResolver resolver;
	
	private List<ScanResultInfo> list = null;
	private WifiManager manager = null;
	private WifiReceiver receiver;
	
	private static final int LEVELNUM = 5;
	private static final int MAX_RSSI = -45;
	private static final int MIN_RSSI = -105;
	 
	private static final int SECURITY_NONE = 0;
	private static final int SECURITY_WEP = 1;
	private static final int SECURITY_PSK = 2;
	private static final int SECURITY_EAP = 3;
    
    
    private static final int WIFI_AP_STATE_UNKNOWN = -1;
    private static final int WIFI_AP_STATE_DISABLING = 10;  
    private static final int WIFI_AP_STATE_DISABLED = 11;  
    private static final int WIFI_AP_STATE_ENABLING = 12;  
    private static final int WIFI_AP_STATE_ENABLED = 13;  
    private static final int WIFI_AP_STATE_FAILED = 14;
    
    private static final int MESSAGE_SHOW_DAILOG = 100;
    private static final int MESSAGE_DISMISS_DAILOG = 101;
    
    private Handler mHandler;
	
//    private HttpServletResponse resp;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		int type = Integer.valueOf( req.getParameter("type"));
		resp.setContentType("text/html");
		switch (type) {
		case 0:
			getWifiInfo(req, resp);
			break;
		case 1:
			connetectWifi(req, resp);
			break;

		default:
			break;
		}
	}
	
	private void connetectWifi(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		int security = Integer.valueOf(req.getParameter("security"));
		String ssid = req.getParameter("SSID");
		String password = req.getParameter("password");
		Log.d(TAG, "connect--------->"+ssid);
		connect(ssid, password, security);
	}
	
	private void getWifiInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String str = "";
		try {
			List<ScanResultInfo> scanList = list;
			JSONObject responseObj = new JSONObject();
			JSONArray array = new JSONArray();
			if(scanList==null){
				readWifiInfoFromFile();
//				scanList = new ArrayList<ScanResultInfo>();
			}
			Log.d(TAG, "list size = " + scanList.size());
			if(scanList.size()>1){
				Collections.sort(scanList, new LevelComparator());
			}
			Log.d(TAG, "list size = " + scanList.size());
			for(int i=0;i<scanList.size(); i++){
				ScanResultInfo localScanResult = scanList.get(i);
				if ((localScanResult.SSID == null) || (localScanResult.SSID.length() == 0) || (localScanResult.capabilities.contains("[IBSS]")))
			          continue;
				JSONObject obj = new JSONObject();
				obj.put("SSID", localScanResult.SSID);
				obj.put("capabilities", localScanResult.capabilities);
				obj.put("level", localScanResult.level);
				obj.put("level_4", calculateSignalLevel(localScanResult.level, LEVELNUM));
				obj.put("security", getSecurity(localScanResult));
				if(localScanResult.capabilities.contains("WEP")
						||localScanResult.capabilities.contains("PSK")
						||localScanResult.capabilities.contains("EAP")
						||localScanResult.capabilities.contains("WAPI-KEY")
						||localScanResult.capabilities.contains("WAPI-CERT")){
					obj.put("isLocked", 1);
				}else{
					obj.put("isLocked", 0);
				}
				array.put(obj);
			}
//			try {
//				Thread.sleep(4000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			responseObj.put("code", 200);
			responseObj.put("items", array);
			
			str = responseObj.toString();
			manager.startScan();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
	//			JSONObject errorObj = new JSONObject();
	//			errorObj.put("code", 201);
	//			errorObj.put("message", "Json error");
				str = e.getMessage();
	//			e.printStackTrace();
			}
		OutputStream out = resp.getOutputStream();
		out.write(str.getBytes(), 0, str.getBytes().length);
		out.close();
	}
	
	
	private void setWifiApEnabled(boolean enabled) {
        WifiConfiguration configuration;
        try {
                Method method = WifiManager.class.getMethod("getWifiApConfiguration");
                configuration = (WifiConfiguration) method.invoke(manager); 
                Method method1 = manager.getClass().getMethod("setWifiApEnabled",
                WifiConfiguration.class, boolean.class);
                method1.invoke(manager, configuration, enabled); // true
//                Toast.makeText(c,"setWifiApEnabled"+enabled,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
                // TODO Auto-generated catch block
//            Toast.makeText(c,"error",Toast.LENGTH_LONG).show(); 
                e.printStackTrace();
        }
}
	
	private int getWifiApState() {  
        try {  
            Method method = manager.getClass().getMethod("getWifiApState");  
            return (Integer) method.invoke(manager);  
        } catch (Exception e) {  
            Log.e(TAG, "Cannot get WiFi AP state", e);  
            return WIFI_AP_STATE_FAILED;  
        }  
    }  
      
    private boolean isApEnabled() {  
        int state = getWifiApState();  
        return WIFI_AP_STATE_ENABLING == state || WIFI_AP_STATE_ENABLED == state;  
    }
    
    private boolean isApOn() {  
        int state = getWifiApState();  
        return WIFI_AP_STATE_ENABLED == state;  
    }
    
    private boolean isApOff() {  
        int state = getWifiApState();  
        return WIFI_AP_STATE_DISABLED == state;  
    }
	
	private void connect(String ssid, String password, int security){
		Log.d(TAG, "conneting------------>"+ssid);
		Message msg = mHandler.obtainMessage();
		msg.what = MESSAGE_SHOW_DAILOG;
		msg.obj = ssid;
		mHandler.sendMessage(msg);
		boolean isLastAp = false;
		int lasWifiIndex = -1;
		if(isApOn()){
			Log.d(TAG, "ap is on");
			isLastAp = true;
			setWifiApEnabled(false);
		}
		while(!isApOff()){
			Log.d(TAG, "ap is not off");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setWifiApEnabled(false);
		}
		Log.d(TAG, "ap is off");
		if(manager.getWifiState()==WifiManager.WIFI_STATE_ENABLED){
			WifiInfo wifo = manager.getConnectionInfo();
			lasWifiIndex = wifo.getNetworkId();
		}else{
			manager.setWifiEnabled(true);
			while(manager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED){
				try {
					Log.d(TAG, "wifi is not on");
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Log.d(TAG, "wifi is  on");
//		List<WifiConfiguration> list = manager.getConfiguredNetworks();
//		if(list!=null){
//			String configSsid = "\"" + ssid + "\"";
//			for(int i=0; i<list.size(); i++){
//				WifiConfiguration config = list.get(i);
//				if(config.SSID.equals(configSsid)){
//					 manager.enableNetwork(config.networkId, true);
//					 checkIsconnect(isLastAp, lasWifiIndex,config.networkId);
//					 return ;
//				}
//			}
//		}
		
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\"" + ssid + "\"";
		 switch (security) {
         case SECURITY_NONE:
             config.allowedKeyManagement.set(KeyMgmt.NONE);

         case SECURITY_WEP:
             config.allowedKeyManagement.set(KeyMgmt.NONE);
             config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
             config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
             if (password!=null && password.length() != 0) {
                 int length = password.length();
                 // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                 if ((length == 10 || length == 26 || length == 58) &&
                         password.matches("[0-9A-Fa-f]*")) {
                     config.wepKeys[0] = password;
                 } else {
                     config.wepKeys[0] = '"' + password + '"';
                 }
             }

         case SECURITY_PSK:
             config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
             if (password!=null && password.length() != 0) {
                 if (password.matches("[0-9A-Fa-f]{64}")) {
                     config.preSharedKey = password;
                 } else {
                     config.preSharedKey = '"' + password + '"';
                 }
             }

         case SECURITY_EAP:
             config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
             config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
//             config.eap.setValue((String) mEapMethod.getSelectedItem());
//
//             config.phase2.setValue((mPhase2.getSelectedItemPosition() == 0) ? "" :
//                     "auth=" + mPhase2.getSelectedItem());
//             config.ca_cert.setValue((mEapCaCert.getSelectedItemPosition() == 0) ? "" :
//                     KEYSTORE_SPACE + Credentials.CA_CERTIFICATE +
//                     (String) mEapCaCert.getSelectedItem());
//             config.client_cert.setValue((mEapUserCert.getSelectedItemPosition() == 0) ? "" :
//                     KEYSTORE_SPACE + Credentials.USER_CERTIFICATE +
//                     (String) mEapUserCert.getSelectedItem());
//             config.private_key.setValue((mEapUserCert.getSelectedItemPosition() == 0) ? "" :
//                     KEYSTORE_SPACE + Credentials.USER_PRIVATE_KEY +
//                     (String) mEapUserCert.getSelectedItem());
//             config.identity.setValue((mEapIdentity.length() == 0) ? "" :
//                     mEapIdentity.getText().toString());
//             config.anonymous_identity.setValue((mEapAnonymous.length() == 0) ? "" :
//                     mEapAnonymous.getText().toString());
//             if (mPassword.length() != 0) {
//                 config.password.setValue(mPassword.getText().toString());
//             }
//             return config;
		 }
		 if (config != null) {
             int networkId = manager.addNetwork(config);
             if (networkId != -1) {
            	 manager.enableNetwork(networkId, false);
            	 manager.saveConfiguration();
                 config.networkId = networkId;
                 manager.enableNetwork(networkId, true);
//                 manager.reconnect();
//                 connect(networkId);
//                 if (mDialog.edit || requireKeyStore(config)) {
//                     saveNetworks();
//                 } else {
//                     connect(networkId);
//                 }
             }else{
            	 if(isLastAp){
    				 openAp();
    				 return;
    			 }
             }
             checkIsconnect(isLastAp, lasWifiIndex,networkId);
		 }
		 
	}
	
	private void checkIsconnect(boolean isLastAp,int lasWifiIndex,int netId){
			try {
				Thread.sleep(20000);
				Log.d(TAG, "sleping---------------------->");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
//			WifiInfo info = manager.getConnectionInfo();
			ConnectivityManager connetivitManager = (ConnectivityManager) mContext.getSystemService("connectivity");
			NetworkInfo info = connetivitManager.getActiveNetworkInfo();
			if(info!=null&&info.isAvailable()&&info.isConnected()){
				Log.d(TAG, "isConneted");
				Message msg = new Message();
				msg.what = MESSAGE_DISMISS_DAILOG;
				msg.arg1 = 0;
				mHandler.sendMessage(msg);
				return;
			}else{
				Log.d(TAG, "Conneted failed");
				Message msg = new Message();
				msg.what = MESSAGE_DISMISS_DAILOG;
				msg.arg1 = 1;
				mHandler.sendMessage(msg);
//				Toast.makeText(mContext, "连接失败", Toast.LENGTH_LONG).show();
//				if(netId!= -1){
				manager.removeNetwork(netId);
//				}
				if(isLastAp){
					openAp();
				 }else{
					if(manager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED){
						manager.setWifiEnabled(true);
						while(manager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED){
							try {
								Log.d(TAG, "wifi is not on");
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					manager.enableNetwork(lasWifiIndex, true);
				 }
			}
	}
	
	private void openAp(){
		 manager.setWifiEnabled(false);
		 while(manager.getWifiState()!=WifiManager.WIFI_STATE_DISABLED){
			 try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 setWifiApEnabled(true);
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
              if(isApOn()){
                  return;
              }
              if(isApOff()){
                  setWifiApEnabled(true);
              }
              retryCount--;
           }
	}
	
	private int getSecurity(ScanResultInfo result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
//		android.content.ContentResolver resolver = (android.content.ContentResolver) config.getServletContext().getAttribute("org.mortbay.ijetty.contentResolver");
//		resolver = (android.content.ContentResolver) config.getServletContext().getAttribute("org.mortbay.ijetty.contentResolver");
//	    android.content.Context androidContext = (android.content.Context) config.getServletContext().getAttribute("org.mortbay.ijetty.context");
	    mContext = (android.content.Context) config.getServletContext().getAttribute("org.mortbay.ijetty.context");
	    manager = (WifiManager) mContext.getSystemService(android.content.Context.WIFI_SERVICE);
	    receiver = new WifiReceiver();
//	    mContext
	    mHandler = new Handler(Looper.getMainLooper())
	    {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case MESSAGE_SHOW_DAILOG:
					Toast.makeText(mContext, "正在连接到"+(String)msg.obj, Toast.LENGTH_LONG).show();
//					if(mpDialog!=null&&mpDialog.isShowing()){
//						mpDialog.dismiss();
//					}
//					mpDialog = null;
//					mpDialog = new ProgressDialog(mContext);
//			        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
////			        mpDialog.setTitle("正在连接");//设置标题  
//			        mpDialog.setMessage("正在连接到" + (String)msg.obj);  
//			        mpDialog.show();
					break;
				case MESSAGE_DISMISS_DAILOG:
//					if(mpDialog!=null&&mpDialog.isShowing()){
//						mpDialog.dismiss();
//					}
					Log.d(TAG, "arg1 = " + msg.arg1);
					if(msg.arg1==0){
						Toast.makeText(mContext, "连接成功", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(mContext, "连接失败", Toast.LENGTH_LONG).show();
					}
					
					break;
				}
			}
	    	
	    };
	    mContext.registerReceiver(receiver, new IntentFilter(  
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    if(manager.getScanResults()!=null&&manager.getScanResults().size()>=0){
	    	Log.d(TAG, "manager.getScanResults()  =" + manager.getScanResults().size());
	    	generateList(manager.getScanResults());
	    }else{
	    	readWifiInfoFromFile();
	    }
	    manager.startScan();
	}
	
	private void generateList(List<ScanResult> wifiList){
		if(list == null){
			list = new ArrayList<WifiConfiger.ScanResultInfo>();
		}else{
			list.clear();
		}
		for(ScanResult s: wifiList){
			ScanResultInfo info = new ScanResultInfo();
			info.SSID = s.SSID;
			info.level = s.level;
			info.capabilities = s.capabilities;
			list.add(info);
		}
	}
	
	
	private void readWifiInfoFromFile() {
		// TODO Auto-generated method stub
		Log.d(TAG, "read from file");
		List<ScanResultInfo> wifiList = new ArrayList<ScanResultInfo>();
		Context c = null;
		try {
			c = mContext.createPackageContext("com.joyplus.configuration", Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "read wifi from file error");
			e.printStackTrace();	
		} 
		if(c!=null){
			SharedPreferences p = c.getSharedPreferences("wifiList", Context.MODE_WORLD_WRITEABLE);
			String str = p.getString("wifiList", "");
			Log.d(TAG, str);
			try{
				if(str.length()>0){
					JSONObject json = new JSONObject(str);
					JSONArray arry = json.getJSONArray("items");
					for(int i = 0; i<arry.length(); i++){
						JSONObject obj = arry.getJSONObject(i);
						ScanResultInfo result = new ScanResultInfo();
						result.SSID = obj.getString("SSID");
						result.level = obj.getInt("level");
						result.capabilities = obj.getString("capabilities");
						wifiList.add(result);
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
				Log.d(TAG, "SharedPreferences get error");
				e.printStackTrace();
			}
			
		}else{
			Log.e(TAG, "c is null error");
		}
		list = wifiList;
	}
	
	class ScanResultInfo{
		public String SSID;
		public int level;
		public String capabilities;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(receiver);
		super.destroy();
	}

	public int calculateSignalLevel(int rssi, int numLevels) {
		if (rssi <= MIN_RSSI) {
			return 0;
		} else if (rssi >= MAX_RSSI) {
			return numLevels - 1;
		} else {
			int partitionSize = (MAX_RSSI - MIN_RSSI) / (numLevels - 1);
			return (int) (Math.ceil((rssi - MIN_RSSI) / partitionSize));
		}
	}
	
	
	class LevelComparator implements Comparator {

		@Override
		public int compare(Object first, Object second) {
			// TODO Auto-generated method stub
			int first_level = ((ScanResultInfo) first).level;
			int second_level = ((ScanResultInfo) second).level;
			if (first_level - second_level < 0) {
				return 1;
			} else if(first_level - second_level > 0){
				return -1;
			} else{
				return 0;
			}
		}
	}
	
	class WifiReceiver extends BroadcastReceiver {  
		  
        public void onReceive(Context c, Intent intent) { 
        	Log.d(TAG, "-------------------------------------->");
        	list.clear();
        	if(manager.getScanResults()==null){
    	    	readWifiInfoFromFile();
    	    }else{
    	    	generateList(manager.getScanResults());
    	    	saveList(manager.getScanResults(), mContext);
    	    }
//        	list = manager.getScanResults();
        	Log.d(TAG, "list size = " + list.size());
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
            try {
    			c = mContext.createPackageContext("com.joyplus.configuration", Context.CONTEXT_IGNORE_SECURITY);
    		} catch (NameNotFoundException e) {
    			// TODO Auto-generated catch block
    			Log.d(TAG, "read wifi from file error");
    			e.printStackTrace();
    		} 
    		if(c!=null){
    			SharedPreferences s = c.getSharedPreferences("", Context.MODE_WORLD_WRITEABLE);
	            Editor editor = s.edit();
	            editor.putString("wifiList",json.toString());
	            editor.commit();
    		}else{
    			Log.d(TAG, "c is  null --------------------------");
    		}
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
