package com.shukesmart.maplibray.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shukesmart.maplibray.utils.db.DBOpenHelper;
import com.shukesmart.maplibray.utils.db.DistanceBean;

public class DistanceInfoDao {

    private DBOpenHelper helper;

    private SQLiteDatabase db;

    public DistanceInfoDao(Context context) {

        helper = new DBOpenHelper(context);

    }

    public void insert(DistanceBean mDistanceInfo) {

        if (mDistanceInfo == null) {

            return;

        }

        db = helper.getWritableDatabase();

        String sql = "INSERT INTO milestone(distance,longitude,latitude) VALUES('"+ mDistanceInfo.getDistance() + "‘,’"+ mDistanceInfo.getLongitude() + "‘,’"+ mDistanceInfo.getLatitude() + "')";



        db.execSQL(sql);

        db.close();

    }
    public void del(int id) {


        db = helper.getWritableDatabase();

        String sql = "delete * from  milestone where id = "+ id;



        db.execSQL(sql);

        db.close();

    }
    public int getMaxId() {

        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT MAX(id) as id from milestone",null);

        if (cursor.moveToFirst()) {

            return cursor.getInt(cursor.getColumnIndexOrThrow("id"));

        }

        return -1;

    }



    public synchronized int insertAndGet(DistanceBean mDistanceInfo) {

        int result = -1;

        insert(mDistanceInfo);

        result = getMaxId();

        return result;

    }

    /**

     * 根据id获取

     * @param id

     * @return

     */

    public DistanceBean getById(int id) {

        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from milestone WHERE id = ?",new String[] { String.valueOf(id) });

        DistanceBean mDistanceInfo = null;

        if (cursor.moveToFirst()) {

            mDistanceInfo = new DistanceBean();

            mDistanceInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));

            mDistanceInfo.setDistance(cursor.getFloat(cursor.getColumnIndexOrThrow("distance")));

            mDistanceInfo.setLongitude(cursor.getFloat(cursor.getColumnIndexOrThrow("longitude")));

            mDistanceInfo.setLatitude(cursor.getFloat(cursor.getColumnIndexOrThrow("latitude")));

        }

        cursor.close();

        db.close();

        return mDistanceInfo;

    }



    public void updateDistance(DistanceBean mDistanceInfo) {

        if (mDistanceInfo == null) {

            return;

        }

        db = helper.getWritableDatabase();

        String sql = "update milestone set distance="+ mDistanceInfo.getDistance() +",longitude="+mDistanceInfo.getLongitude()+",latitude="+mDistanceInfo.getLatitude()+"where id = "+ mDistanceInfo.getId();

        db.execSQL(sql);

        db.close();

    }

}