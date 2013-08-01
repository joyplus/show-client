package com.joyplus.tvhelper.helper;

import com.joyplus.tvhelper.utils.Log;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class NetWorkHelper {

	private static String LOG_TAG = "NetWorkHelper";
	public static Uri uri = Uri.parse("content://telephony/carriers");

	public static boolean isMobileDataEnable(Context context) throws Exception {
		return ((ConnectivityManager) context.getSystemService("connectivity"))
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager localConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");

		if (localConnectivityManager == null) {

			Log.w(LOG_TAG, "couldn't get connectivity manager");
		} else {

			NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager
					.getAllNetworkInfo();
			for (int i = 0; i < arrayOfNetworkInfo.length; i++) {

				if (arrayOfNetworkInfo[i].isAvailable()) {

					return true;
				}
			}
		}

		return false;
	}

	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager localConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");

		if (localConnectivityManager == null) {

			Log.w(LOG_TAG, "couldn't get connectivity manager");
		} else {

			NetworkInfo localNetworkInfo = localConnectivityManager
					.getActiveNetworkInfo();
			if (localNetworkInfo != null && (localNetworkInfo.getType() == 0)) {

				TelephonyManager localTelephonyManager = (TelephonyManager) context
						.getSystemService("phone");

				if ((localTelephonyManager != null)
						&& (localTelephonyManager.isNetworkRoaming())) {

					Log.d(LOG_TAG, "network is roaming");
					return true;
				}
			}
		}

		Log.d(LOG_TAG, "not using mobile network");
		return false;
	}

	public static boolean isWifiDataEnable(Context context) throws Exception {
		
		return ((ConnectivityManager) context.getSystemService("connectivity"))
				.getNetworkInfo(ConnectivityManager.DEFAULT_NETWORK_PREFERENCE)
				.isConnectedOrConnecting();
	}

}
