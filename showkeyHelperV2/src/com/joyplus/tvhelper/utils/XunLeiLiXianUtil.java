package com.joyplus.tvhelper.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.joyplus.sub.SUBTYPE;
import com.joyplus.sub.SubURI;
import com.joyplus.tvhelper.entity.SharpnessEnum;
import com.joyplus.tvhelper.entity.VideoPlayUrl;
import com.joyplus.tvhelper.entity.XLLXFileInfo;
import com.joyplus.tvhelper.entity.XLLXUserInfo;
import com.joyplus.tvhelper.helper.HttpClientHelper;
import com.joyplus.tvhelper.https.HttpResult;
import com.joyplus.tvhelper.https.HttpUtils;

public class XunLeiLiXianUtil {

	private static final String TAG = "XunLeiLiXianUtil";

	public static final String XL_PREFERENCES = "joy_xl";

	public static final String COOKIE = "cookie";
	public static final String COOKIE_URL = "xunlei.com";

	public static final String LIST_URL = "http://i.vod.xunlei.com/req_history_play_list/req_num/30/req_offset/0";

	public static final String SESSIONID = "sessionid";
	public static final String USERID = "userid";
	private static final String USRNAME = "usrnmae";

	public static final String LOGOUT_URL = "http://login.xunlei.com/unregister";
	public static final String PARSE_URL = "http://i.vod.xunlei.com/req_get_method_vod";
	public static final String REFERER = "http://vod.xunlei.com/client/cplayer.html";
	public static final String USERINFO_URL = "http://dynamic.vip.xunlei.com/login/asynlogin_contr/asynProxy/";

	private static final String CHECK_RESULT = "check_result";
	private static final String GET_PLAY_URL_1 = "http://i.vod.xunlei.com/vod_dl_all";
	private static final String GET_PLAY_URL_2 = "http://i.vod.xunlei.com/req_get_method_vod";
	private static final String LOGIN_URL = "http://login.xunlei.com/sec2login/";
	private static final String VER_CODE_URL = "http://login.xunlei.com/check";
	
	public static int CACHE_NUM = 7;

	public static int Login(Context context,String username,String password,String verifyCode) {
		
		return Login(context,username,password,verifyCode,false);
	}

	public static int Login(Context context, String username, String password,String verifycode,boolean isMd5) {

		int i = -1;// 登陆状态
		boolean isVerifyCodeNull = true;
		HttpResult localHttpResult = null;
		Log.i(TAG, "verifycode--->" + verifycode);
		if (verifycode == null || verifycode.equals("")) {
			isVerifyCodeNull = true;

			NameValuePair[] arrayOfNameValuePair = new NameValuePair[2];
			arrayOfNameValuePair[0] = new BasicNameValuePair("u", username);
			arrayOfNameValuePair[1] = new BasicNameValuePair("cachetime",
					String.valueOf(System.currentTimeMillis()));
			localHttpResult = HttpClientHelper.get(VER_CODE_URL,
					null, arrayOfNameValuePair);

			Cookie localCookie = null;

			if (localHttpResult != null) {
				Log.i(TAG, "localHttpResult--->" + localHttpResult.toString());
				localCookie = localHttpResult.getCookie("CHECK_RESULT");
				Log.i(TAG, "localCookie--->" + localCookie);
				if (localCookie != null) {
					String cookieValue = localCookie.getValue();
					Log.i(TAG,"localCookie.getValue()--->"+ localCookie.getValue());

					if (cookieValue != null && cookieValue.length() >= 1) {
						Log.i(TAG,"cookieValue.charAt(0)--->"+ cookieValue.charAt(0));
						if (cookieValue.charAt(0) == '1') {// 暂时不知道是0能够获取，还是非0能够获取
							i = 1;// 获取验证码失败，请稍后再试
						} else {
							// [version: 0][name:
							// check_result][value:0:!ZRw][domain:
							// xunlei.com][path: /][expiry:null]
							if (cookieValue.length() >= 2) {
								verifycode = cookieValue.substring(2);// [value:0:!ZRw] // 去冒号以后的值
							}
						}
					}
				}
			}
		} else {
			isVerifyCodeNull = false;
		}

		Log.i(TAG, "username--->" + username);
		// verifycode = "!2AW";网上获取，密码与预想一样
		if (verifycode != null && !verifycode.equals("")) {// 成功获取验证码

			NameValuePair[] arrayOfNameValuePair2 = new NameValuePair[5];
			arrayOfNameValuePair2[0] = new BasicNameValuePair("u", username);
			arrayOfNameValuePair2[1] = new BasicNameValuePair("login_enable","1");
			arrayOfNameValuePair2[2] = new BasicNameValuePair("login_hour","720");

			if (!isMd5) {
				arrayOfNameValuePair2[3] = new BasicNameValuePair("p",
						MD5Util.getMD5String(MD5Util.getMD5String(MD5Util
								.getMD5String(password))
								+ verifycode.toUpperCase()));
			} else {
				arrayOfNameValuePair2[3] = new BasicNameValuePair("p",
						MD5Util.getMD5String(MD5Util.getMD5String(password)
								+ verifycode.toUpperCase()));
			}

			// Log.i(TAG, "UserPassword"+ password +
			// " MD5 Password---->"+ arrayOfNameValuePair2[3]);
			arrayOfNameValuePair2[4] = new BasicNameValuePair("verifycode",
					verifycode);
			HttpResult httpResult = null;
			if (isVerifyCodeNull) {

				// post发送密码
				if(localHttpResult != null){
					
					httpResult = HttpClientHelper.post(LOGIN_URL, null,
							arrayOfNameValuePair2, localHttpResult.getCookies());
				}
			} else {

				// post发送密码
				Header[] arrayOfHeader = new Header[1];
				arrayOfHeader[0] = new BasicHeader("cookie",
						getVerifyCookie(context));
				httpResult = HttpClientHelper.post(LOGIN_URL, arrayOfHeader,
						arrayOfNameValuePair2, null);
			}
			
			if (httpResult != null && httpResult.getCookies() != null
					&& httpResult.getCookies().length > 0) {

				return getLoginFlag(context, httpResult.getCookies());
			}
		}

		return 1;// 获取验证码失败
	}
	
	//获取验证码
	public static Bitmap getVerifyCodeBitmap(Context context,String username){
		
		Header[] arrayOfHeader = new Header[2];
		arrayOfHeader[0] = new BasicHeader("cookie", getVerifyCookie(context));
		arrayOfHeader[1] = new BasicHeader("Referer","http://vod.xunlei.com/home.html");
		byte[] bytes = null;
		
		HttpResult localHttpResult = HttpClientHelper.get("http://verify.xunlei.com/image?cachetime="
				+ System.currentTimeMillis(),arrayOfHeader, null, null, 0);
		
		if ((localHttpResult != null)
				&& (localHttpResult.getStatuCode() == 200)) {
			
			bytes = localHttpResult.getResponse();
			saveVerifyCookies(context, localHttpResult.getCookies());
//			Log.d("HttpUtils", "localHttpResult bitmap--->" + localHttpResult.toString());
		}

		if (bytes != null) {
			Log.i(TAG, "bytes length-->" + bytes.length);
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
					bytes.length);
			return bitmap;
		}
			
		return null;
	}

	public static XLLXUserInfo getUser(Context context, Header header) {

		Header[] arrayOfHeader = { header };
		String userInfo = HttpUtils.getContent(USERINFO_URL, arrayOfHeader,
				null);

		// Log.d(TAG, "getUser--->userInfo:" + userInfo + "   header--->" +
		// header);

		try {
			if (userInfo != null) {

				if (userInfo.indexOf("{") != -1) {

					XLLXUserInfo xllxUserInfo = new XLLXUserInfo();

					userInfo = userInfo.substring(userInfo.indexOf("{"));
					JSONObject localJSONObject = (JSONObject) new JSONTokener(
							userInfo).nextValue();
					xllxUserInfo.autopay = Integer.parseInt(localJSONObject
							.getString("autopay"));
					xllxUserInfo.daily = Integer.parseInt(localJSONObject
							.getString("daily"));
					xllxUserInfo.expiredate = localJSONObject
							.getString("expiredate");
					xllxUserInfo.growvalue = Integer.parseInt(localJSONObject
							.getString("growvalue"));
					xllxUserInfo.isvip = Integer.parseInt(localJSONObject
							.getString("isvip"));
					xllxUserInfo.isyear = Integer.parseInt(localJSONObject
							.getString("isyear"));
					xllxUserInfo.level = Integer.parseInt(localJSONObject
							.getString("level"));
					xllxUserInfo.nickname = localJSONObject
							.getString("nickname");
					xllxUserInfo.usrname = localJSONObject.getString("usrname");
					xllxUserInfo.payname = localJSONObject.getString("payname");

					saveUserInfo(context, xllxUserInfo);

					return xllxUserInfo;
				}

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static ArrayList<XLLXFileInfo> getVideoList(Context context,
			int cacheNum, int pageIndex) {

		ArrayList<XLLXFileInfo> list = new ArrayList<XLLXFileInfo>();
		try {

			BasicHeader localBasicHeader = new BasicHeader("cookie",
					getCookie(context));

			String listUrl = "http://i.vod.xunlei.com/req_history_play_list/req_num/"
					+ cacheNum + "/req_offset/" + cacheNum * (pageIndex - 1);

			Header[] arrayOfHeader = { localBasicHeader };
			NameValuePair[] arrayOfNameValuePair = new NameValuePair[3];
			arrayOfNameValuePair[0] = new BasicNameValuePair("type", "all");
			arrayOfNameValuePair[1] = new BasicNameValuePair("order", "create");
			arrayOfNameValuePair[2] = new BasicNameValuePair("t",
					String.valueOf(System.currentTimeMillis()));

			String listContent = HttpUtils.getContent(listUrl, arrayOfHeader,
					arrayOfNameValuePair);
			if (listContent != null) {

				String decodeListContent = URLDecoder.decode(listContent);
				Log.d(TAG, "getVideoList--->json= " + decodeListContent);

				JSONObject respJsonObject = ((JSONObject) new JSONTokener(
						decodeListContent).nextValue()).getJSONObject("resp");

				int record_num = respJsonObject.getInt("record_num");
				Log.d("XunleiBiz", "getVideoList--->record_num= " + record_num);

				JSONArray historyPlayListJsonObject = respJsonObject
						.getJSONArray("history_play_list");

				for (int j = 0; j < historyPlayListJsonObject.length(); j++) {
					XLLXFileInfo xllxFileInfo = new XLLXFileInfo();
					JSONObject jsonObject = historyPlayListJsonObject
							.getJSONObject(j);
					if(jsonObject.has("file_name"))
						xllxFileInfo.file_name = jsonObject.getString("file_name");
					if(jsonObject.has("src_url"))
						xllxFileInfo.src_url = jsonObject.getString("src_url");
					if(jsonObject.has("createtime"))
						xllxFileInfo.createTime = jsonObject.getString("createtime");
					if(jsonObject.has("duration"))
						xllxFileInfo.duration = jsonObject.getString("duration");
					if(jsonObject.has("file_size"))
						xllxFileInfo.filesize = jsonObject.getString("file_size");
					if(jsonObject.has("userid"))
						xllxFileInfo.userid = jsonObject.getString("userid");
					if(jsonObject.has("gcid"))
						xllxFileInfo.gcid = jsonObject.getString("gcid");
					xllxFileInfo.recordeNum = record_num;
					if (xllxFileInfo.src_url.contains("bt://")) {
						xllxFileInfo.isDir = true;
					}
					list.add(xllxFileInfo);
				}
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return list;
	}

	private static int getLoginFlag(Context context, Cookie[] cookies) {

		int loginFlag = 1;

		if (cookies != null && cookies.length > 0) {

			String sessionidStr = null;
			String useridStr = null;
			String usrnameStr = null;

			for (int i = 0; i < cookies.length; i++) {

				if (cookies[i] != null) {

					Cookie tempCookie = cookies[i];

					Log.i(TAG,
							"tempCookie.toString()--->" + tempCookie.toString());
					if (tempCookie.getName() != null) {

						if (tempCookie.getName().equals("sessionid")) {

							// saveSessionid(context, tempCookie.getValue());
							sessionidStr = tempCookie.getValue();

						} else if (tempCookie.getName().equalsIgnoreCase(
								"userid")) {

							// saveUID(context, tempCookie.getValue());
							useridStr = tempCookie.getValue();
						} else if (tempCookie.getName().equalsIgnoreCase(
								"usrname")) {

							// saveUsrname(context, tempCookie.getValue());
							usrnameStr = tempCookie.getValue();
						} else if (tempCookie.getName().equalsIgnoreCase(
								"blogresult")) {// 暂时不知道用来干什么的

							Log.i(TAG,
									"blogresult---->" + tempCookie.getValue());
							try {
								loginFlag = Integer.valueOf(tempCookie
										.getValue());
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}

			if (loginFlag == 0) {

				saveUsrname(context, usrnameStr);
				saveUID(context, useridStr);
				saveSessionid(context, sessionidStr);
				saveCookies(context, cookies);
			}

		}

		return loginFlag;
	}
	
	//get 1000 data default 30
	public static XLLXFileInfo[] getSubFile(Context context,
			XLLXFileInfo xllxFileInfo) {
		
		try {
			
			BasicHeader localBasicHeader = new BasicHeader("cookie",
					getCookie(context));
			
			String btStr = HttpUtils.getContent(
					"http://i.vod.xunlei.com/req_subBT/info_hash/"
							+ xllxFileInfo.src_url.substring(5)
							+ "/req_num/1000/req_offset/0",
					new Header[] { localBasicHeader }, null);
			Log.i(TAG,"getSubFile---->" + btStr);
			if(btStr != null && !btStr.equals("")) {
				
				JSONArray localJSONArray = ((JSONObject) new JSONTokener(
						URLDecoder.decode(btStr)).nextValue()).
						getJSONObject("resp").getJSONArray("subfile_list");
				
				XLLXFileInfo[] arrayOfXLLXFileInfo = new XLLXFileInfo[localJSONArray.length()];
				for(int i=0;i< localJSONArray.length();i++) {
					
					JSONObject localJSONObject = localJSONArray.getJSONObject(i);
					arrayOfXLLXFileInfo[i] = new XLLXFileInfo();
					arrayOfXLLXFileInfo[i].file_name = localJSONObject
							.getString("name");
					arrayOfXLLXFileInfo[i].src_url = (xllxFileInfo.src_url
							+ "/" + localJSONObject.getString("index"));
					arrayOfXLLXFileInfo[i].createTime = xllxFileInfo.createTime;
					arrayOfXLLXFileInfo[i].duration = localJSONObject
							.getString("duration");
					arrayOfXLLXFileInfo[i].filesize = localJSONObject
							.getString("file_size");
					arrayOfXLLXFileInfo[i].gcid = localJSONObject.getString("gcid");
					arrayOfXLLXFileInfo[i].userid = xllxFileInfo.userid;
				}
				
				xllxFileInfo.btFiles = arrayOfXLLXFileInfo;
				
				return arrayOfXLLXFileInfo;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<VideoPlayUrl> getPlayUrl(String urlStr,
			Header[] header, NameValuePair[] nameValuePair) {

		ArrayList<VideoPlayUrl> localArrayList = new ArrayList<VideoPlayUrl>();
		;
		try {

			String str = HttpUtils.getContent(urlStr, header, nameValuePair);

			if (TextUtils.equals(str, null) || str.equals("")) {

				return localArrayList;
			}

			JSONArray localJSONArray = ((JSONObject) new JSONTokener(str)
					.nextValue()).getJSONObject("resp").getJSONArray(
					"vodinfo_list");

			for (int i = 0; i < localJSONArray.length(); i++) {

				VideoPlayUrl localVideoPlayUrl = new VideoPlayUrl();
				localVideoPlayUrl.playurl = localJSONArray.getJSONObject(i)
						.getString("vod_url");
				localVideoPlayUrl.sharp = SharpnessEnum.getSharp(i);
				localArrayList.add(localVideoPlayUrl);
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return localArrayList;
	}
	
	public static ArrayList<VideoPlayUrl> getLXPlayUrl(Context context,
			XLLXFileInfo xllxFileInfo) {
		ArrayList<VideoPlayUrl> localArrayList = getLXPlayUrl4Vod_dl_all(context,xllxFileInfo);//can drag
		ArrayList<VideoPlayUrl> tempArrayList = getLXPlayUrlReferer(context, xllxFileInfo);//can't drag
		for(VideoPlayUrl playUrl:tempArrayList){
			localArrayList.add(playUrl);
			Log.d(TAG, "playUrl  sharp"+ playUrl.sharp);
		}
//		localArrayList.addAll(tempArrayList);

//		if (localArrayList.size() > 0) {
			
			return localArrayList;
//		}
			
//		return getLXPlayUrlReferer(context, xllxFileInfo);
	}
	
	public static byte[] getSubtitle(Context context,XLLXFileInfo xllxFileInfo){
		
		Log.i(TAG, "getSubtitle--->xllxFileInfo lx_gcid:" + xllxFileInfo.lx_gcid);
		
		Header[] arrayOfHeader = { new BasicHeader("cookie",getCookie(context)) };
		
		NameValuePair[] firstNameValuePair = new NameValuePair[4];
		firstNameValuePair[0]= new BasicNameValuePair("gcid", xllxFileInfo.lx_gcid);
		firstNameValuePair[1]= new BasicNameValuePair("cid", xllxFileInfo.lx_cid);
		firstNameValuePair[2]= new BasicNameValuePair("userid", getUID(context));
		firstNameValuePair[3]= new BasicNameValuePair("t", System.currentTimeMillis() + "");
		
		String autoloadStr = HttpUtils.getContent(
				"http://i.vod.xunlei.com/subtitle/autoload", arrayOfHeader,
				firstNameValuePair);
		Log.i(TAG, "getSubtitle--->autoloadStr:" + autoloadStr);
		if(autoloadStr != null && !autoloadStr.equals("")){
			
			try {
				JSONObject subtitleJsonObject = ((JSONObject) new JSONTokener(autoloadStr)
					.nextValue()).getJSONObject("subtitle");
				if(subtitleJsonObject != null){
					
					String subTitleUrl = subtitleJsonObject.getString("surl");
					if(subTitleUrl != null && !subTitleUrl.equals("")){
						
						byte[] subTitle = HttpUtils.getBinary(subTitleUrl, null,null);
//						Log.i(TAG, "getSubtitle--->subTitle:" + subTitle);
						return subTitle;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		NameValuePair[] arrayOfNameValuePair = new NameValuePair[1];
//		arrayOfNameValuePair[0] = new BasicNameValuePair("scid","11702C01977EA74E629A2D56B31843CFDC642E7D");

		
		return null;
	}
	
	public static List<SubURI> getSubtitleList(Context context,XLLXFileInfo xllxFileInfo){
		
		Log.i(TAG, "getSubtitle--->xllxFileInfo lx_gcid:" + xllxFileInfo.lx_gcid);
		List<SubURI> list = new ArrayList<SubURI>();
		Header[] arrayOfHeader = { new BasicHeader("cookie",getCookie(context)) };
		
		NameValuePair[] firstNameValuePair = new NameValuePair[4];
		firstNameValuePair[0]= new BasicNameValuePair("gcid", xllxFileInfo.lx_gcid);
		firstNameValuePair[1]= new BasicNameValuePair("cid", xllxFileInfo.lx_cid);
		firstNameValuePair[2]= new BasicNameValuePair("userid", getUID(context));
		firstNameValuePair[3]= new BasicNameValuePair("t", System.currentTimeMillis() + "");
		
		String subtitleList = HttpUtils.getContent(
				"http://i.vod.xunlei.com/subtitle/list", arrayOfHeader,
				firstNameValuePair);
		Log.i(TAG, "getSubtitle--->autoloadStr:" + subtitleList);
		if(subtitleList != null && !subtitleList.equals("")){
			
			try {
				JSONArray subtitleJsonArray = ((JSONObject) new JSONTokener(subtitleList)
					.nextValue()).getJSONArray("sublist");
				if(subtitleJsonArray != null && subtitleJsonArray.length() > 0){
					for(int i=0;i<subtitleJsonArray.length();i++){
						
						JSONObject subtitleJsonObject = subtitleJsonArray.getJSONObject(i);
						if(subtitleJsonObject != null){
							String subTitleUrl = subtitleJsonObject.getString("surl");
							if(subTitleUrl != null && !subTitleUrl.equals("")
									&& URLUtil.isNetworkUrl(subTitleUrl)){
//								if(subTitleUrl.contains("scid=")){
//									
//									list.add(subTitleUrl);
//								}else {
//									if(subTitleUrl.length() == subTitleUrl.indexOf(".srt") + 4){
//										list.add(subTitleUrl);
//									}
//								}
								SubURI subURI = new SubURI();
								subURI.SubType = SUBTYPE.NETWORK;
								subURI.Uri = subTitleUrl;
								list.add(subURI);
//								byte[] subTitle = HttpUtils.getBinary(subTitleUrl, null,null);
//							Log.i(TAG, "getSubtitle--->subTitle:" + subTitle);
							}
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	
	public static List<SubURI> getSubtitle4Push(String url,String appkey){
		Log.i(TAG, "getSubtitle4Push---url--->" + url);
		List<SubURI> list = new ArrayList<SubURI>();
		Header[] arrayOfHeader = { new BasicHeader("app_key",appkey) };
		
		String subTitlesStr = HttpUtils.getContent(url, arrayOfHeader, null);
		Log.i(TAG, "getSubtitle4Push--->subTitlesStr:" + subTitlesStr);
		
		if(subTitlesStr != null && !subTitlesStr.equals("")){
			
			try {
				JSONObject subtitlesJsonObject = (JSONObject) new JSONTokener(subTitlesStr).nextValue();
				
				if(subtitlesJsonObject.has("error")){
					
					if(!subtitlesJsonObject.getBoolean("error")
							&& subtitlesJsonObject.has("subtitles")){
						
						JSONArray subtitleContents = subtitlesJsonObject.getJSONArray("subtitles");
						if(subtitleContents != null && subtitleContents.length() > 0){
							
//							String subTitleUrl = subtitleContents.getString(0);
							for(int i=0;i<subtitleContents.length();i++){
								
								String subTitleUrl = subtitleContents.getString(i);
								if(subTitleUrl != null && !subTitleUrl.equals("")
										&& URLUtil.isNetworkUrl(subTitleUrl)){
//									list.add(subTitleUrl);
									SubURI subURI = new SubURI();
									subURI.SubType = SUBTYPE.NETWORK;
									subURI.Uri = subTitleUrl;
									list.add(subURI);
//								byte[] subTitle = HttpUtils.getBinary(subTitleUrl, null,null);
//								return subTitle;
								}
							}
						}
						
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return list;
	}

	public static ArrayList<VideoPlayUrl> getLXPlayUrl4Vod_dl_all(
			Context context, XLLXFileInfo xllxFileInfo) {

		ArrayList<VideoPlayUrl> localArrayList = new ArrayList<VideoPlayUrl>();

		if (xllxFileInfo.isDir) {

			return localArrayList;
		}

		try {

			Header[] arrayOfHeader = { new BasicHeader("cookie",
					getCookie(context)) };
			NameValuePair[] arrayOfNameValuePair = new NameValuePair[3];

			arrayOfNameValuePair[0] = new BasicNameValuePair("userid",
					getUID(context));
			arrayOfNameValuePair[1] = new BasicNameValuePair("gcid",
					xllxFileInfo.gcid);
			arrayOfNameValuePair[2] = new BasicNameValuePair("filename",
					URLEncoder.encode(xllxFileInfo.src_url));

			String urlAllStrs = HttpUtils.getContent(
					"http://i.vod.xunlei.com/vod_dl_all", arrayOfHeader,
					arrayOfNameValuePair);

			Log.d(TAG, "urlAllStrs--->" + urlAllStrs);

			if (urlAllStrs != null && !urlAllStrs.equals("")) {

				JSONObject allJsonObject = (JSONObject) new JSONTokener(
						urlAllStrs).nextValue();

				JSONObject fullHDJSONObject = allJsonObject
						.getJSONObject("Full_HD");
				if ((fullHDJSONObject.has("url"))
						&& (!fullHDJSONObject.isNull("url"))) {
					VideoPlayUrl localVideoPlayUrl3 = new VideoPlayUrl();
					localVideoPlayUrl3.playurl = fullHDJSONObject
							.getString("url");
					localVideoPlayUrl3.sharp = SharpnessEnum.getSharp(3);
					localVideoPlayUrl3.isCanDrag = true;
					localArrayList.add(localVideoPlayUrl3);
				}

				JSONObject hdJSONObject = allJsonObject.getJSONObject("HD");
				if ((hdJSONObject.has("url")) && (!hdJSONObject.isNull("url"))) {
					VideoPlayUrl localVideoPlayUrl2 = new VideoPlayUrl();
					localVideoPlayUrl2.playurl = hdJSONObject.getString("url");
					localVideoPlayUrl2.sharp = SharpnessEnum.getSharp(2);
					localVideoPlayUrl2.isCanDrag = true;
					localArrayList.add(localVideoPlayUrl2);
				}

				JSONObject sdJSONObject = allJsonObject.getJSONObject("SD");
				if ((sdJSONObject.has("url")) && (!sdJSONObject.isNull("url"))) {
					VideoPlayUrl localVideoPlayUrl1 = new VideoPlayUrl();
					localVideoPlayUrl1.playurl = sdJSONObject.getString("url");
					localVideoPlayUrl1.sharp = SharpnessEnum.getSharp(0);
					localVideoPlayUrl1.isCanDrag = true;
					localArrayList.add(localVideoPlayUrl1);

				}

				return localArrayList;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return localArrayList;
	}

	public static ArrayList<VideoPlayUrl> getLXPlayUrlReferer(Context context,
			XLLXFileInfo xllxFileInfo) {

		ArrayList<VideoPlayUrl> localArrayList = new ArrayList<VideoPlayUrl>();

		if (xllxFileInfo.isDir || xllxFileInfo.file_name == null) {

			return localArrayList;
		}

		if (xllxFileInfo.src_url != null) {

			try {
				for (int i = 2; i >= 0; i--) {

					Header[] arrayOfHeader = new Header[2];
					arrayOfHeader[0] = new BasicHeader("cookie",getCookie(context));
					arrayOfHeader[1] = new BasicHeader("Referer","http://vod.xunlei.com/client/cplayer.html");

					NameValuePair[] arrayOfNameValuePair = new NameValuePair[8];
					arrayOfNameValuePair[0] = new BasicNameValuePair("url",
							URLEncoder.encode(xllxFileInfo.src_url));
					arrayOfNameValuePair[1] = new BasicNameValuePair("video_name",
							URLEncoder.encode(xllxFileInfo.file_name));
					arrayOfNameValuePair[2] = new BasicNameValuePair("platform", i + "");
					arrayOfNameValuePair[3] = new BasicNameValuePair("userid",
							getUID(context));
//							xllxFileInfo.userid);
					arrayOfNameValuePair[4] = new BasicNameValuePair("vip", "1");
					arrayOfNameValuePair[5] = new BasicNameValuePair("sessionid", getSessionid(context));
					arrayOfNameValuePair[6] = new BasicNameValuePair("cache",System.currentTimeMillis() + "");
					arrayOfNameValuePair[7] = new BasicNameValuePair("from","vlist");

					String str = HttpUtils.getContent(
							"http://i.vod.xunlei.com/req_get_method_vod",
							arrayOfHeader, arrayOfNameValuePair);
					Log.d("XunleiBiz", "getLXPlayUrlReferer--->" + str);

					if (str != null && !str.equals("")) {

						JSONObject jsonObject = ((JSONObject) new JSONTokener(
								str).nextValue()).getJSONObject("resp");

						if (jsonObject != null && jsonObject.has("src_info")) {

							JSONArray localJSONArray = jsonObject
									.getJSONArray("vodinfo_list");
							for (int j = 0; j < localJSONArray.length(); j++) {

								VideoPlayUrl localVideoPlayUrl = new VideoPlayUrl();
								VideoPlayUrl localVideoPlayUrl_1 = new VideoPlayUrl();
								localVideoPlayUrl.playurl = localJSONArray
										.getJSONObject(j).getString("vod_url_dt17");
								localVideoPlayUrl_1.playurl = localJSONArray
										.getJSONObject(j).getString("vod_url");
//								localVideoPlayUrl.playurl = localJSONArray
//										.getJSONObject(j).getString("vod_url");
								localVideoPlayUrl.sharp = SharpnessEnum.getSharp(j+1);
								localVideoPlayUrl_1.sharp = SharpnessEnum.getSharp(j+1);
								localVideoPlayUrl.isCanDrag = true;
								localVideoPlayUrl_1.isCanDrag = true;
								localArrayList.add(localVideoPlayUrl);
								localArrayList.add(localVideoPlayUrl_1);
							}
							
							//subtitle
							JSONObject src_infoJsonObject = jsonObject.getJSONObject("src_info");
							if(src_infoJsonObject != null){
								
								xllxFileInfo.lx_gcid = src_infoJsonObject.getString("gcid");
								xllxFileInfo.lx_cid = src_infoJsonObject.getString("cid");
							}
//							return localArrayList;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return localArrayList;

	}

	public static void Logout(Context paramContext) {

		SharedPreferences.Editor localEditor = paramContext
				.getSharedPreferences(XL_PREFERENCES, 0).edit();

		localEditor.remove("userid");
		localEditor.remove("sessionid");
		localEditor.remove("cookie");
		localEditor.remove("usrnmae");
		localEditor.remove("autopay");
		localEditor.remove("daily");
		localEditor.remove("expiredate");
		localEditor.remove("isvip");
		localEditor.remove("level");
		localEditor.remove("nickname");
		localEditor.remove("usrname");
		localEditor.remove("payname");

		localEditor.commit();
	}

	public static void saveCookies(Context context, Cookie[] cookies) {

		SharedPreferences localSharedPreferences = context
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE);

		String str = null;
		StringBuilder localStringBuilder = null;

		if ((cookies != null) && (cookies.length > 0)) {

			localStringBuilder = new StringBuilder();
			for (int i = 0; i < cookies.length; i++) {

				if (cookies[i] != null) {

					String[] tempStrs = new String[2];
					tempStrs[0] = cookies[i].getName();
					tempStrs[1] = cookies[i].getValue();
					localStringBuilder.append(String.format("%s=%s", tempStrs));
					if (i != cookies.length - 1) {

						localStringBuilder.append(";");
					}

				}
			}

			str = localStringBuilder.toString();
			SharedPreferences.Editor localEditor = localSharedPreferences
					.edit();
			localEditor.putString("cookie", str);
			localEditor.commit();
		}
	}
	
	public static void clearCookies(Context context){
		SharedPreferences localSharedPreferences = context
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor localEditor = localSharedPreferences
				.edit();
		localEditor.putString("cookie", null);
		localEditor.commit();
	}

	public static String getCookie(Context context) {

		return context.getSharedPreferences(XL_PREFERENCES,
				Context.MODE_PRIVATE).getString("cookie", null);
	}
	
	public static String getCookie(Cookie[] cookies){
		
		String str = null;
		StringBuilder localStringBuilder = null;

		if ((cookies != null) && (cookies.length > 0)) {
			
			localStringBuilder = new StringBuilder();
			for (int i = 0; i < cookies.length; i++) {
				Log.i(TAG, "cookies[]--->" +  cookies[i].toString());
				if (cookies[i] != null) {

					String[] tempStrs = new String[2];
					tempStrs[0] = cookies[i].getName();
					tempStrs[1] = cookies[i].getValue();
					localStringBuilder.append(String.format("%s=%s", tempStrs));
					if (i != cookies.length - 1) {

						localStringBuilder.append(";");
					}

				}
			}

			str = localStringBuilder.toString();
		}
		Log.i(TAG, "cookies---->" + str);
		
		return str;
	}
	
	public static void saveVerifyCookies(Context context, Cookie[] cookies) {

		SharedPreferences localSharedPreferences = context
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE);
		String cookie = getCookie(cookies);
		Log.i(TAG, "head cookie--->" + cookie);
		if(cookie != null && !cookie.equals("")){
			
			 SharedPreferences.Editor localEditor = localSharedPreferences
			 .edit();
			 localEditor.putString("verifycookie", cookie);
			 localEditor.commit();
		}
	}
	
	public static void saveVerifyCookies(Context context, String cookie) {

		SharedPreferences localSharedPreferences = context
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE);
		Log.i(TAG, "head cookie--->" + cookie);
		if(cookie != null && !cookie.equals("")){
			
			 SharedPreferences.Editor localEditor = localSharedPreferences
			 .edit();
			 localEditor.putString("verifycookie", cookie);
			 localEditor.commit();
		}else {
			
			 SharedPreferences.Editor localEditor = localSharedPreferences
			 .edit();
			 localEditor.putString("verifycookie", "");
			 localEditor.commit();
		}
	}

	public static String getVerifyCookie(Context context) {

		return context.getSharedPreferences(XL_PREFERENCES,
				Context.MODE_PRIVATE).getString("verifycookie", null);
	}

	private static void saveUsrname(Context context, String usrname) {

		SharedPreferences.Editor localEditor = context.getSharedPreferences(
				XL_PREFERENCES, Context.MODE_PRIVATE).edit();
		localEditor.putString("usrnmae", usrname);
		localEditor.commit();
	}

	public static String getUsrname(Context context) {

		return context.getSharedPreferences(XL_PREFERENCES,
				Context.MODE_PRIVATE).getString("usrnmae", null);
	}

	private static void saveUID(Context context, String userid) {

		SharedPreferences.Editor localEditor = context.getSharedPreferences(
				XL_PREFERENCES, Context.MODE_PRIVATE).edit();
		localEditor.putString("userid", userid);
		localEditor.commit();
	}

	public static String getUID(Context context) {

		return context.getSharedPreferences(XL_PREFERENCES,
				Context.MODE_PRIVATE).getString("userid", null);
	}

	private static void saveSessionid(Context context, String sessionid) {

		SharedPreferences.Editor localEditor = context.getSharedPreferences(
				XL_PREFERENCES, Context.MODE_PRIVATE).edit();
		localEditor.putString("sessionid", sessionid);
		localEditor.commit();
	}

	public static String getSessionid(Context context) {

		return context.getSharedPreferences(XL_PREFERENCES,
				Context.MODE_PRIVATE).getString("sessionid", null);
	}

	public static XLLXUserInfo getUserInfoFromLocal(Context paramContext) {

		SharedPreferences localSharedPreferences = paramContext
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE);
		XLLXUserInfo localXLLXUserInfo = new XLLXUserInfo();
		localXLLXUserInfo.autopay = localSharedPreferences
				.getInt("autopay", -1);
		localXLLXUserInfo.daily = localSharedPreferences.getInt("daily", -1);
		localXLLXUserInfo.expiredate = localSharedPreferences.getString(
				"expiredate", "");
		localXLLXUserInfo.growvalue = localSharedPreferences.getInt(
				"growvalue", -1);
		localXLLXUserInfo.isvip = localSharedPreferences.getInt("isvip", -1);
		localXLLXUserInfo.isyear = localSharedPreferences.getInt("isyear", -1);
		localXLLXUserInfo.level = localSharedPreferences.getInt("level", -1);
		localXLLXUserInfo.nickname = localSharedPreferences.getString(
				"nickname", "");
		localXLLXUserInfo.usrname = localSharedPreferences.getString("usrname",
				"");
		localXLLXUserInfo.payname = localSharedPreferences.getString("payname",
				"");
		return localXLLXUserInfo;
	}

	private static void saveUserInfo(Context paramContext,
			XLLXUserInfo paramXLLXUserInfo) {

		SharedPreferences.Editor localEditor = paramContext
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE)
				.edit();
		localEditor.putInt("autopay", paramXLLXUserInfo.autopay);
		localEditor.putInt("daily", paramXLLXUserInfo.daily);
		localEditor.putString("expiredate", paramXLLXUserInfo.expiredate);
		localEditor.putInt("isvip", paramXLLXUserInfo.isvip);
		localEditor.putInt("level", paramXLLXUserInfo.level);
		localEditor.putString("nickname", paramXLLXUserInfo.nickname);
		localEditor.putString("usrname", paramXLLXUserInfo.usrname);
		localEditor.putString("payname", paramXLLXUserInfo.payname);
		localEditor.commit();
	}

	public static Header getCookieHeader(Context context) {

		Header header = null;

		String cookieStr = getCookie(context);

		if (cookieStr != null && !cookieStr.equals("")) {

			header = new BasicHeader("cookie", cookieStr);
		}

		return header;
	}
	
	//存储登陆录账号名
	public static void saveLoginUserName(Context context,String loginUserName) {
		
		SharedPreferences.Editor localEditor = context
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE)
				.edit();
		localEditor.putString("loginUserName", loginUserName);
		localEditor.commit();
	}
	
	public static String getLoginUserName(Context context) {
		
		SharedPreferences preferences = context
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE);
		return preferences.getString("loginUserName", "");
	}
	
	//存储登陆录密码
	public static void saveLoginUserPasswd(Context context,String loginUserPasswd) {
		SharedPreferences.Editor localEditor = context
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE)
				.edit();
		localEditor.putString("loginUserPasswd", loginUserPasswd);
		localEditor.commit();
	}
	
	public static String getLoginUserPasswd(Context context) {
		
		SharedPreferences preferences = context
				.getSharedPreferences(XL_PREFERENCES, Context.MODE_PRIVATE);
		return preferences.getString("loginUserPasswd", "");
	}

}
