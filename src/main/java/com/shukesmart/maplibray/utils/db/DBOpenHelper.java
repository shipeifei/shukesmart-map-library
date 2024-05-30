package com.shukesmart.maplibray.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;                   //数据库版本号

    private static final String DB_NAME="distance.db";    //数据库名

    public DBOpenHelper(Context context){                   //创建数据库

        super(context, DB_NAME, null, VERSION);

    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  //版本号发生改变的时

        db.execSQL("drop table milestone");

        db.execSQL("CREATE TABLE IF NOT EXISTS milestone(id INTEGER PRIMARY KEY AUTOINCREMENT, distance INTEGER,longitude DOUBLE, latitude DOUBLE )");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS milestone(id INTEGER PRIMARY KEY AUTOINCREMENT, distance INTEGER,longitude DOUBLE, latitude DOUBLE )");

    }
}