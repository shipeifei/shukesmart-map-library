package com.shukesmart.maplibray.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SearchHistoryDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 3;                   //数据库版本号
    private static final String DB_NAME="searchHistory.db";    //数据库名
    public SearchHistoryDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS searchHistory(id INTEGER PRIMARY KEY AUTOINCREMENT,city TEXT,poiName TEXT,tag TEXT, searchAddress TEXT, count INTEGER,searchTime TEXT,longitude DOUBLE, latitude DOUBLE )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table searchHistory");
        db.execSQL("CREATE TABLE IF NOT EXISTS searchHistory(id INTEGER PRIMARY KEY AUTOINCREMENT,city TEXT,poiName TEXT,tag TEXT, searchAddress TEXT, count INTEGER,searchTime TEXT,longitude DOUBLE, latitude DOUBLE )");
    }
}

