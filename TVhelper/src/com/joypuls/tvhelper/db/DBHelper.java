package com.joypuls.tvhelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public DBHelper(Context context) {
		super(context, "apk.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table apk_info(_id integer PRIMARY KEY AUTOINCREMENT,  "
				+ "name char, push_id char, url char, fileSize integer, compeleteSize integer, download_state integer, file_path char)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}