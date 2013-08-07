package com.joyplus.tvhelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public DBHelper(Context context) {
		super(context, "helper.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {	
		db.execSQL("create table "+	DBConstant.TABLE_APK_INFO	+"(" +
				DBConstant.KEY_ID									+ " integer PRIMARY KEY AUTOINCREMENT,  "+ 
				DBConstant.KEY_APK_INFO_NAME 						+ " char, " +
				DBConstant.KEY_APK_INFO_PUSH_ID 					+ " char, " +
				DBConstant.KEY_APK_INFO_DOWNLOAD_STATE				+ " integer, " +
				DBConstant.KEY_APK_INFO_FILE_PATH					+ " char, " +
				DBConstant.KEY_APK_INFO_DOWNLOADUUID				+ " char," +
				DBConstant.KEY_APK_INFO_ISUSER						+ " integer," +
				DBConstant.KEY_SYN1									+ " integer," +
				DBConstant.KEY_SYN2									+ " integer," +
				DBConstant.KEY_SYN3									+ " integer," +
				DBConstant.KEY_SYN4									+ " integer," +
				DBConstant.KEY_SYN_C1								+ " char," +
				DBConstant.KEY_SYN_C2								+ " char," +
				DBConstant.KEY_SYN_C3								+ " char," +
				DBConstant.KEY_SYN_C4								+ " char" +
				")");
		db.execSQL("create table "+	DBConstant.TABLE_MOVIE_INFO	+"(" +
				DBConstant.KEY_ID									+ " integer PRIMARY KEY AUTOINCREMENT,  "+ 
				DBConstant.KEY_MOVIE_DOWNLOAD_INFO_NAME				+ " char, " +
				DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_ID			+ " char, " +
				DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOAD_STATE	+ " integer, " +
				DBConstant.KEY_MOVIE_DOWNLOAD_INFO_PUSH_URL			+ " char, " +
				DBConstant.KEY_MOVIE_DOWNLOAD_INFO_FILE_PATH		+ " char, " +
				DBConstant.KEY_MOVIE_DOWNLOAD_INFO_DOWNLOADUUID		+ " char," +
				DBConstant.KEY_SYN1									+ " integer," +
				DBConstant.KEY_SYN2									+ " integer," +
				DBConstant.KEY_SYN3									+ " integer," +
				DBConstant.KEY_SYN4									+ " integer," +
				DBConstant.KEY_SYN_C1								+ " char," +
				DBConstant.KEY_SYN_C2								+ " char," +
				DBConstant.KEY_SYN_C3								+ " char," +
				DBConstant.KEY_SYN_C4								+ " char" +
				")");
		db.execSQL("create table "+	DBConstant.TABLE_PLAY_INFO	+"(" +
				DBConstant.KEY_ID									+ " integer PRIMARY KEY AUTOINCREMENT,  "+  
				DBConstant.KEY_PLAY_INFO_NAME						+ " char, " +
				DBConstant.KEY_PLAY_INFO_TYPE						+ " integer, " +//本地与在线区别
				DBConstant.KEY_PLAY_INFO_PUSH_ID					+ " char, " +
				DBConstant.KEY_PLAY_INFO_PUSH_URL					+ " char, " +
				DBConstant.KEY_PLAY_INFO_PLAY_BACK_TIME				+ " integer, " +
				DBConstant.KEY_PLAY_INFO_FILE_PATH					+ " char, " +
				DBConstant.KEY_PLAY_INFO_DOWNLOADUUID				+ " char," +
				DBConstant.KEY_SYN1									+ " integer," +
				DBConstant.KEY_SYN2									+ " integer," +
				DBConstant.KEY_SYN3									+ " integer," +
				DBConstant.KEY_SYN4									+ " integer," +
				DBConstant.KEY_SYN_C1								+ " char," +
				DBConstant.KEY_SYN_C2								+ "	char," +
				DBConstant.KEY_SYN_C3								+ " char," +
				DBConstant.KEY_SYN_C4								+ "	char" +
				")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}