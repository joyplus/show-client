package com.joyplus.tvhelper.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.joyplus.network.filedownload.manager.DownloadManager;
import com.joyplus.tvhelper.entity.ApkInfo;
import com.joyplus.tvhelper.entity.MoviePlayHistoryInfo;
import com.joyplus.tvhelper.entity.PushedApkDownLoadInfo;
import com.joyplus.tvhelper.entity.PushedMovieDownLoadInfo;
import com.joyplus.tvhelper.utils.PackageUtils;
import com.joyplus.utils.Log;


public class DBServices {
	
	private static final String TAG = "DBServices";
	
	private static DBServices dao = null;
	private Context context;
	private DownloadManager dmg;

	private DBServices(Context context) {
		this.context = context;
		dmg = DownloadManager.getInstance(context);
	}

	public static DBServices getInstance(Context context) {
		if (dao == null) {
			dao = new DBServices(context);
		}
		return dao;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DBHelper(context).getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqliteDatabase;
	}
	
	
	public synchronized long insertApkInfo(PushedApkDownLoadInfo info){
		SQLiteDatabase db = getConnection();
		ContentValues values = new ContentValues();
		values.put(DBConstant.KEY_APK_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_APK_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_APK_INFO_FILE_PATH, info.getFile_path());
		values.put(DBConstant.KEY_APK_INFO_DOWNLOAD_STATE, info.getDownload_state());
		values.put(DBConstant.KEY_APK_INFO_DOWNLOADUUID, info.getTast().getUUId());
		values.put(DBConstant.KEY_APK_INFO_ISUSER, info.getIsUser());
		values.put(DBConstant.KEY_SYN_C1, info.getIcon_url());
		values.put(DBConstant.KEY_SYN_C2, info.getPackageName());
		long _id = db.insert(DBConstant.TABLE_APK_INFO, null, values);
		db.close();
        return _id;
	}
	
	public synchronized void updateApkInfo(PushedApkDownLoadInfo info){
        SQLiteDatabase db = getConnection();
        ContentValues values = new ContentValues();
		values.put(DBConstant.KEY_APK_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_APK_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_APK_INFO_FILE_PATH, info.getFile_path());
		values.put(DBConstant.KEY_APK_INFO_DOWNLOAD_STATE, info.getDownload_state());
		values.put(DBConstant.KEY_APK_INFO_DOWNLOADUUID, info.getTast().getUUId());
		values.put(DBConstant.KEY_APK_INFO_ISUSER, info.getIsUser());
		values.put(DBConstant.KEY_SYN_C1, info.getIcon_url());
		values.put(DBConstant.KEY_SYN_C2, info.getPackageName());
//
        int rows = db.update(DBConstant.TABLE_APK_INFO, values,
        		DBConstant.KEY_ID + " = ? ", new String[] {
        		info.get_id()+ ""
                });
        db.close();
        Log.d(TAG, rows + "--->update");
    }
	
	public synchronized void deleteApkInfo(PushedApkDownLoadInfo info) {
        SQLiteDatabase db = getConnection();
        int rows = db.delete(DBConstant.TABLE_APK_INFO,
        		DBConstant.KEY_ID + " = ? ", new String[] {
        		String.valueOf(info.get_id())
                });
        Log.i(TAG, rows + "rows deleted");
        dmg.deleteTask(info.getTast());
        db.close();
    }
	
	public synchronized ArrayList<PushedApkDownLoadInfo> queryUserApkDownLoadInfo() {
        SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_APK_INFO, null,
        		DBConstant.KEY_APK_INFO_ISUSER + " = ? ", new String[] {
        		PushedApkDownLoadInfo.IS_USER + ""}, null, null, null);
        ArrayList<PushedApkDownLoadInfo> taskes = new ArrayList<PushedApkDownLoadInfo>();
        PushedApkDownLoadInfo info;
        while (cr.moveToNext()) {
        	
        	int _id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID));
        	String name = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_NAME));
        	Log.d(TAG, "PushedApkDownLoadInfo----------->" + name);
        	int push_id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_APK_INFO_PUSH_ID));
        	String file_path = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_FILE_PATH));
        	int download_statue = cr.getInt(cr.getColumnIndex(DBConstant.KEY_APK_INFO_DOWNLOAD_STATE));
        	String download_uuid = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_DOWNLOADUUID));
        	String icon_url = cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C1));
        	String packageName = cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C2));
        	
        	info = new PushedApkDownLoadInfo();
        	info.setIcon_url(icon_url);
        	info.set_id(_id);
        	info.setName(name);
        	info.setPush_id(push_id);
        	info.setPackageName(packageName);
        	if(download_statue == PushedApkDownLoadInfo.STATUE_DOWNLOADING){
        		download_statue = PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE;
        	}
        	if(download_statue == PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE||download_statue == PushedApkDownLoadInfo.STATUE_INSTALL_FAILE){
        		ApkInfo apkinfo = PackageUtils.getUnInstalledApkInfo(context, file_path);
        		if(apkinfo!=null){
        			info.setPackageName(apkinfo.getPackageName());
            		info.setIcon(apkinfo.getDrawble());
        		}
        	}
        	info.setDownload_state(download_statue);
        	info.setFile_path(file_path);
        	info.setIsUser(PushedApkDownLoadInfo.IS_USER);
        	info.setTast(dmg.findTaksByUUID(download_uuid));
        	
        	taskes.add(info);
        	
        }
        cr.close();
        db.close();
        return taskes;
    }
	
	public synchronized ArrayList<PushedApkDownLoadInfo> queryNotUserApkDownLoadInfo() {
        SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_APK_INFO, null,
        		DBConstant.KEY_APK_INFO_ISUSER + " = ? ", new String[] {
        		PushedApkDownLoadInfo.IS_NOT_USER + ""}, null, null, null);
        ArrayList<PushedApkDownLoadInfo> taskes = new ArrayList<PushedApkDownLoadInfo>();
        PushedApkDownLoadInfo info;
        while (cr.moveToNext()) {
        	int _id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID));
        	String name = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_NAME));
        	Log.d(TAG, "not user PushedApkDownLoadInfo----------->" + name);
        	int push_id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_APK_INFO_PUSH_ID));
        	String file_path = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_FILE_PATH));
        	int download_statue = cr.getInt(cr.getColumnIndex(DBConstant.KEY_APK_INFO_DOWNLOAD_STATE));
        	String download_uuid = cr.getString(cr.getColumnIndex(DBConstant.KEY_APK_INFO_DOWNLOADUUID));
        	
        	
        	info = new PushedApkDownLoadInfo();
        	info.set_id(_id);
        	info.setName(name);
        	info.setPush_id(push_id);
        	info.setDownload_state(download_statue);
        	info.setFile_path(file_path);
        	info.setIsUser(PushedApkDownLoadInfo.IS_NOT_USER); 
        	info.setTast(dmg.findTaksByUUID(download_uuid));
        	if(download_statue == PushedApkDownLoadInfo.STATUE_DOWNLOAD_COMPLETE||download_statue == PushedApkDownLoadInfo.STATUE_INSTALL_FAILE){
        		//。。。
        		deleteApkInfo(info);
        	}else{
        		info.setDownload_state(PushedApkDownLoadInfo.STATUE_WAITING_DOWNLOAD);
        	}
        	taskes.add(info);
        }
        cr.close();
        db.close();
        return taskes;
    }
	
	public synchronized long insertMovieDownLoadInfo(PushedMovieDownLoadInfo info){
		SQLiteDatabase db = getConnection();
		ContentValues values = new ContentValues();
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH, info.getFile_path());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE, info.getDownload_state());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL, info.getPush_url());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID, info.getTast().getUUId());
		long _id = db.insert(DBConstant.TABLE_MOVIE_INFO, null, values);
		db.close();
        return _id;
	}
	
	public synchronized void deleteMovieDownLoadInfo(PushedMovieDownLoadInfo info) {
		SQLiteDatabase db = getConnection();
		int rows = db.delete(DBConstant.TABLE_MOVIE_INFO,
				DBConstant.KEY_ID + " = ? ", new String[] {
				String.valueOf(info.get_id())
		});
		Log.i(TAG, rows + "rows deleted");
		dmg.deleteTask(info.getTast());
		db.close();
	}
	
	public synchronized void updateMovieDownLoadInfo(PushedMovieDownLoadInfo info){
        SQLiteDatabase db = getConnection();
        ContentValues values = new ContentValues();
        values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH, info.getFile_path());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE, info.getDownload_state());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL, info.getPush_url());
		values.put(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID, info.getTast().getUUId());
//
        int rows = db.update(DBConstant.TABLE_MOVIE_INFO, values,
        		DBConstant.KEY_ID + " = ? ", new String[] {
        		info.get_id()+ ""
                });
        db.close();
        Log.d(TAG, rows + "--->update");
    }
	
	
	public synchronized ArrayList<PushedMovieDownLoadInfo> queryMovieDownLoadInfos() {
        SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_MOVIE_INFO, null,
        		DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE + " <? ", new String[] {
        		PushedMovieDownLoadInfo.STATUE_DOWNLOAD_COMPLETE + ""}, null, null, null);
        ArrayList<PushedMovieDownLoadInfo> taskes = new ArrayList<PushedMovieDownLoadInfo>();
        PushedMovieDownLoadInfo info;
        while (cr.moveToNext()) {
        	
        	int _id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID));
        	String name = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME));
        	int push_id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID));
        	String file_path = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH));
        	int download_statue = cr.getInt(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE));
        	String download_uuid = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID));
        	String push_url = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL));
        	Log.d(TAG, "PushedMovieDownLoadInfo---url-------->" + push_url);
        	
        	info = new PushedMovieDownLoadInfo();
        	info.set_id(_id);
        	info.setName(name);
        	info.setPush_id(push_id);
        	download_statue = PushedApkDownLoadInfo.STATUE_DOWNLOAD_PAUSE;
        	info.setDownload_state(download_statue);
        	info.setFile_path(file_path);
        	info.setPush_url(push_url);
        	info.setTast(dmg.findTaksByUUID(download_uuid));
        	taskes.add(info);
        }
        cr.close();
        db.close();
        return taskes;
    }
	
	public synchronized ArrayList<PushedMovieDownLoadInfo> queryMovieDownLoadedInfos() {
        SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_MOVIE_INFO, null,
        		DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE + " =? ", new String[] {
        		PushedMovieDownLoadInfo.STATUE_DOWNLOAD_COMPLETE + ""}, null, null, null);
        ArrayList<PushedMovieDownLoadInfo> taskes = new ArrayList<PushedMovieDownLoadInfo>();
        PushedMovieDownLoadInfo info;
        while (cr.moveToNext()) {
        	
        	int _id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID));
        	String name = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME));
        	int push_id = cr.getInt(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID));
        	String file_path = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH));
        	int download_statue = cr.getInt(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE));
        	String download_uuid = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID));
        	String push_url = cr.getString(cr.getColumnIndex(DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL));
        	
        	info = new PushedMovieDownLoadInfo();
        	info.set_id(_id);
        	info.setName(name);
        	info.setPush_id(push_id);
        	info.setDownload_state(download_statue);
        	info.setFile_path(file_path);
        	info.setPush_url(push_url);
        	info.setTast(dmg.findTaksByUUID(download_uuid));
        	taskes.add(info);
        }
        cr.close();
        db.close();
        return taskes;
    }

	public synchronized long insertMoviePlayHistory(MoviePlayHistoryInfo info){
		SQLiteDatabase db = getConnection();
		ContentValues values = new ContentValues();
		values.put(DBConstant.KEY_PLAY_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_PLAY_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_PLAY_INFO_FILE_PATH, info.getPic_url());
		values.put(DBConstant.KEY_PLAY_INFO_PUSH_URL, info.getPush_url());
		values.put(DBConstant.KEY_PLAY_INFO_TYPE, info.getPlay_type());
		values.put(DBConstant.KEY_PLAY_INFO_PLAY_BACK_TIME, info.getPlayback_time());
		values.put(DBConstant.KEY_SYN1, info.getDuration());
		values.put(DBConstant.KEY_SYN2, info.getDefination());
		values.put(DBConstant.KEY_SYN3, info.getCreat_time());
		
//		values.put(DBConstant.KEY_SYN_C1, info.getDownload_url());
		values.put(DBConstant.KEY_SYN_C2, info.getRecivedDonwLoadUrls());
		values.put(DBConstant.KEY_SYN_C3, info.getTime_token());
		values.put(DBConstant.KEY_SYN_C4, info.getBtEpisodesString());
		
		long _id = db.insert(DBConstant.TABLE_PLAY_INFO, null, values);
		db.close();
        return _id;
	}
	
	public synchronized void updateMoviePlayHistory(MoviePlayHistoryInfo info){
        SQLiteDatabase db = getConnection();
        ContentValues values = new ContentValues();
		values.put(DBConstant.KEY_PLAY_INFO_NAME, info.getName());
		values.put(DBConstant.KEY_PLAY_INFO_PUSH_ID, info.getPush_id());
		values.put(DBConstant.KEY_PLAY_INFO_FILE_PATH, info.getPic_url());
		values.put(DBConstant.KEY_PLAY_INFO_PUSH_URL, info.getPush_url());
		values.put(DBConstant.KEY_PLAY_INFO_TYPE, info.getPlay_type());
		values.put(DBConstant.KEY_SYN2, info.getDefination());
		values.put(DBConstant.KEY_SYN3, info.getCreat_time());
		values.put(DBConstant.KEY_SYN_C3, info.getTime_token());
		values.put(DBConstant.KEY_SYN_C4, info.getBtEpisodesString());
		if(info.getPlayback_time()>0){
			values.put(DBConstant.KEY_PLAY_INFO_PLAY_BACK_TIME, info.getPlayback_time());
		}
		if(info.getDuration()>0){
			values.put(DBConstant.KEY_SYN1, info.getDuration());
		}
//		if(info.getDownload_url()!=null){
//			values.put(DBConstant.KEY_SYN_C1, info.getDownload_url());
//		}
		
		if(info.getRecivedDonwLoadUrls()!=null){
			values.put(DBConstant.KEY_SYN_C2, info.getRecivedDonwLoadUrls());
		}
//
		int rows = db.update(DBConstant.TABLE_PLAY_INFO, values,
        		DBConstant.KEY_ID + " = ? ", new String[] {
        		info.getId()+ ""
                });;
//		if(info.getPlay_type() == MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
//			 rows = db.update(DBConstant.TABLE_PLAY_INFO, values,
//		        		DBConstant.KEY_PLAY_INFO_FILE_PATH + " = ? ", new String[] {
//		        		info.getLocal_url()+ ""
//		                });
//		}else{
//			 rows = db.update(DBConstant.TABLE_PLAY_INFO, values,
//	        		DBConstant.KEY_PLAY_INFO_PUSH_URL + " = ? ", new String[] {
//	        		info.getPush_url()+ ""
//	                }); 
//		}
       
        db.close();
        Log.d(TAG, rows + "--->update");
    }
	
	public synchronized void deleteMoviePlayHistory(MoviePlayHistoryInfo info){
		SQLiteDatabase db = getConnection();
		int rows = db.delete(DBConstant.TABLE_PLAY_INFO,
				DBConstant.KEY_ID + " = ? ", new String[] {
				String.valueOf(info.getId())
		});
		Log.i(TAG, rows + "rows deleted");
		db.close();
	}
	public synchronized void deleteMoviePlayHistory(int id){
		SQLiteDatabase db = getConnection();
		int rows = db.delete(DBConstant.TABLE_PLAY_INFO,
				DBConstant.KEY_ID + " = ? ", new String[] {
				String.valueOf(id)
		});
		Log.i(TAG, rows + "rows deleted");
		db.close();
	}
	
	
	public synchronized List<MoviePlayHistoryInfo> queryMoviePlayHistoryList(){
		SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_PLAY_INFO, null,
        		DBConstant.KEY_PLAY_INFO_TYPE+" >=? ", new String[] {
                		"0"}, null, null, DBConstant.KEY_SYN3 +" desc,"+DBConstant.KEY_ID +" desc");
        ArrayList<MoviePlayHistoryInfo> taskes = new ArrayList<MoviePlayHistoryInfo>();
        
        MoviePlayHistoryInfo info;
        while (cr.moveToNext()) {
    		info = new MoviePlayHistoryInfo();
    		
    		info.setId(cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID)));
    		info.setPlay_type(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_TYPE)));
    		info.setPush_id(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_ID)));
    		info.setPlayback_time(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PLAY_BACK_TIME)));
    		info.setDuration(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN1)));
    		info.setDefination(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN2)));
    		info.setCreat_time(cr.getLong(cr.getColumnIndex(DBConstant.KEY_SYN3)));
    		info.setName(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_NAME)));
    		info.setPush_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_URL)));
    		info.setPic_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_FILE_PATH)));
//    		info.setDownload_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C1)));
    		info.setRecivedDonwLoadUrls(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C2)));
    		info.setTime_token(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C3)));
			info.setBtEpisodes(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C4)));
        	taskes.add(info);
        }
        cr.close();
        db.close();
        return taskes;
	}
	
	public synchronized MoviePlayHistoryInfo queryMoviePlayHistoryByLoaclUrl(String localUrl){
		SQLiteDatabase db = getConnection();
        Cursor cr = db.query(DBConstant.TABLE_PLAY_INFO, null,
        		DBConstant.KEY_PLAY_INFO_FILE_PATH + " =? ", new String[] {
        		localUrl}, null, null, null);
        MoviePlayHistoryInfo info = null;
        while (cr.moveToNext()) {
    		info = new MoviePlayHistoryInfo();
    		
    		info.setId(cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID)));
    		info.setPlay_type(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_TYPE)));
    		info.setPush_id(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_ID)));
    		info.setPlayback_time(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PLAY_BACK_TIME)));
    		info.setDuration(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN1)));
    		info.setDefination(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN2)));
    		info.setCreat_time(cr.getLong(cr.getColumnIndex(DBConstant.KEY_SYN3)));
    		info.setName(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_NAME)));
    		info.setPush_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_URL)));
    		info.setPic_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_FILE_PATH)));
//    		info.setDownload_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C1)));
    		info.setRecivedDonwLoadUrls(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C2)));
    		info.setTime_token(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C3)));
			info.setBtEpisodes(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C4)));
        }
        cr.close();
        db.close();
        return info;
	}
	public synchronized MoviePlayHistoryInfo queryMoviePlayHistoryById(int id){
		SQLiteDatabase db = getConnection();
		Cursor cr = db.query(DBConstant.TABLE_PLAY_INFO, null,
				DBConstant.KEY_ID + " =? ", new String[] {
				String.valueOf(id)}, null, null, null);
		MoviePlayHistoryInfo info = null;
		while (cr.moveToNext()) {
			info = new MoviePlayHistoryInfo();
			
			info.setId(cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID)));
    		info.setPlay_type(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_TYPE)));
    		info.setPush_id(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_ID)));
    		info.setPlayback_time(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PLAY_BACK_TIME)));
    		info.setDuration(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN1)));
    		info.setDefination(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN2)));
    		info.setCreat_time(cr.getLong(cr.getColumnIndex(DBConstant.KEY_SYN3)));
    		info.setName(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_NAME)));
    		info.setPush_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_URL)));
    		info.setPic_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_FILE_PATH)));
//    		info.setDownload_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C1)));
    		info.setRecivedDonwLoadUrls(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C2)));
    		info.setTime_token(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C3)));
			info.setBtEpisodes(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C4)));
		}
		cr.close();
		db.close();
		return info;
	}
	
	public synchronized MoviePlayHistoryInfo hasMoviePlayHistory(int type,String url){
		SQLiteDatabase db = getConnection();
		Cursor cr;
		MoviePlayHistoryInfo info = null;
		if(type==MoviePlayHistoryInfo.PLAY_TYPE_LOCAL){
			cr = db.query(DBConstant.TABLE_PLAY_INFO, null,
					DBConstant.KEY_PLAY_INFO_FILE_PATH + " =? ", new String[] {
					url}, null, null, null);
		}else{
			cr = db.query(DBConstant.TABLE_PLAY_INFO, null,
					DBConstant.KEY_PLAY_INFO_PUSH_URL + " =? ", new String[] {
					url}, null, null, null);
		}
		if(cr!=null){
			if(cr.moveToNext()){
				info = new MoviePlayHistoryInfo();
				
				info.setId(cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID)));
	    		info.setPlay_type(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_TYPE)));
	    		info.setPush_id(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_ID)));
	    		info.setPlayback_time(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PLAY_BACK_TIME)));
	    		info.setDuration(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN1)));
	    		info.setDefination(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN2)));
	    		info.setCreat_time(cr.getLong(cr.getColumnIndex(DBConstant.KEY_SYN3)));
	    		info.setName(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_NAME)));
	    		info.setPush_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_URL)));
	    		info.setPic_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_FILE_PATH)));
//	    		info.setDownload_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C1)));
	    		info.setRecivedDonwLoadUrls(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C2)));
	    		info.setTime_token(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C3)));
				info.setBtEpisodes(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C4)));
			}
		}
		cr.close();
		db.close();
		return info;
	}
	
	public synchronized MoviePlayHistoryInfo hasMoviePushHistory(String time){
		SQLiteDatabase db = getConnection();
		Cursor cr;
		MoviePlayHistoryInfo info = null;
//		String sql = "select * from " + DBConstant.TABLE_PLAY_INFO + " where " + DBConstant.KEY_SYN_C3 + " like " + time+",";
		cr = db.query(DBConstant.TABLE_PLAY_INFO, null,
				DBConstant.KEY_SYN_C3 + " like ? ", new String[] {
				"%"+time+",%"}, null, null, null);
		if(cr!=null){
			if(cr.moveToNext()){
				info = new MoviePlayHistoryInfo();
				
				info.setId(cr.getInt(cr.getColumnIndex(DBConstant.KEY_ID)));
				info.setPlay_type(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_TYPE)));
				info.setPush_id(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_ID)));
				info.setPlayback_time(cr.getInt(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PLAY_BACK_TIME)));
				info.setDuration(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN1)));
				info.setDefination(cr.getInt(cr.getColumnIndex(DBConstant.KEY_SYN2)));
				info.setCreat_time(cr.getLong(cr.getColumnIndex(DBConstant.KEY_SYN3)));
				info.setName(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_NAME)));
				info.setPush_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_PUSH_URL)));
				info.setPic_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_PLAY_INFO_FILE_PATH)));
//	    		info.setDownload_url(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C1)));
				info.setRecivedDonwLoadUrls(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C2)));
				info.setBtEpisodes(cr.getString(cr.getColumnIndex(DBConstant.KEY_SYN_C4)));
			}
		}
		cr.close();
		db.close();
		return info;
	}
}