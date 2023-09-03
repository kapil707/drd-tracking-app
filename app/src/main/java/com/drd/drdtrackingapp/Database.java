package com.drd.drdtrackingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	public Database(Context context) {
		super(context, "drd_trackingapp_db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table tbl_user_loc(_id Integer Primary Key autoincrement,user_code Text,firebase_token Text,latitude Text,longitude Text,getdate Text,gettime Text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("create table tbl_user_loc(_id Integer Primary Key autoincrement,user_code Text,firebase_token Text,latitude Text,longitude Text,getdate Text,gettime Text)");
	}
}
