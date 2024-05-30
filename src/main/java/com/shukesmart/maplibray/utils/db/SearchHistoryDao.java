package com.shukesmart.maplibray.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shukesmart.maplibray.utils.db.SearchHistoryBean;
import com.shukesmart.maplibray.utils.db.SearchHistoryDBHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SearchHistoryDao {

    private SearchHistoryDBHelper helper;

    private SQLiteDatabase db;

    public SearchHistoryDao(Context context) {

        helper = new SearchHistoryDBHelper(context);

    }

    public void insert(SearchHistoryBean mDistanceInfo) {

        if (mDistanceInfo == null) {

            return;

        }

        //判断之前是否有相同的poiName
        SearchHistoryBean searchHistoryBean = isPoinameExist(mDistanceInfo.getPoiName());
        if (searchHistoryBean != null) {
            updateCount(mDistanceInfo);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDateTime = LocalDateTime.now();
            String dateTimeString = currentDateTime.format(formatter);
            db = helper.getWritableDatabase();

            String sql = "INSERT INTO searchHistory(searchTime,longitude,latitude,searchAddress,tag,count,city,poiName) VALUES('" + dateTimeString + "'," + mDistanceInfo.getLongitude() + "," + mDistanceInfo.getLatitude() + ",'" + mDistanceInfo.getSearchAddress() + "','" + mDistanceInfo.getTag() + "',1,'" + mDistanceInfo.getCity() + "','" + mDistanceInfo.getPoiName() + "')";


            db.execSQL(sql);

            db.close();
        }

    }

    public void del(int id) {


        db = helper.getWritableDatabase();

        String sql = "delete  from  searchHistory where id = " + id;


        db.execSQL(sql);

        db.close();

    }

    public int getMaxId() {

        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT MAX(id) as id from searchHistory", null);

        if (cursor.moveToFirst()) {

            return cursor.getInt(cursor.getColumnIndexOrThrow("id"));

        }

        return -1;

    }


    public synchronized int insertAndGet(SearchHistoryBean mDistanceInfo) {

        int result = -1;

        insert(mDistanceInfo);

        result = getMaxId();

        return result;

    }

    /**
     * 根据id获取
     *
     * @param id
     * @return
     */

    public SearchHistoryBean getById(int id) {

        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from searchHistory WHERE id = ?", new String[]{String.valueOf(id)});

        SearchHistoryBean mDistanceInfo = null;

        if (cursor.moveToFirst()) {

            mDistanceInfo = new SearchHistoryBean();

            mDistanceInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            mDistanceInfo.setCount(cursor.getInt(cursor.getColumnIndexOrThrow("count")));
            mDistanceInfo.setTag(cursor.getString(cursor.getColumnIndexOrThrow("tag")));
            mDistanceInfo.setSearchAddress(cursor.getString(cursor.getColumnIndexOrThrow("searchAddress")));
            mDistanceInfo.setSearchTime(cursor.getString(cursor.getColumnIndexOrThrow("searchTime")));
            mDistanceInfo.setLongitude(cursor.getFloat(cursor.getColumnIndexOrThrow("longitude")));
            mDistanceInfo.setPoiName(cursor.getString(cursor.getColumnIndexOrThrow("poiName")));
            mDistanceInfo.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));

            mDistanceInfo.setLatitude(cursor.getFloat(cursor.getColumnIndexOrThrow("latitude")));

        }

        cursor.close();

        db.close();

        return mDistanceInfo;

    }

    public SearchHistoryBean isPoinameExist(String poiName) {
        Log.e("isPoinameExist", "isPoinameExist: " + poiName);

        db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from searchHistory WHERE poiName = ?", new String[]{poiName});

        SearchHistoryBean mDistanceInfo = null;

        if (cursor.moveToFirst()) {

            mDistanceInfo = new SearchHistoryBean();

            mDistanceInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            mDistanceInfo.setCount(cursor.getInt(cursor.getColumnIndexOrThrow("count")));
            mDistanceInfo.setTag(cursor.getString(cursor.getColumnIndexOrThrow("tag")));
            mDistanceInfo.setSearchAddress(cursor.getString(cursor.getColumnIndexOrThrow("searchAddress")));
            mDistanceInfo.setSearchTime(cursor.getString(cursor.getColumnIndexOrThrow("searchTime")));
            mDistanceInfo.setLongitude(cursor.getFloat(cursor.getColumnIndexOrThrow("longitude")));
            mDistanceInfo.setPoiName(cursor.getString(cursor.getColumnIndexOrThrow("poiName")));
            mDistanceInfo.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));

            mDistanceInfo.setLatitude(cursor.getFloat(cursor.getColumnIndexOrThrow("latitude")));

        }

        cursor.close();

        db.close();

        return mDistanceInfo;

    }

    /**
     * 更新记录
     *
     * @param account 要更新的对象
     */
    public void update(SearchHistoryBean account) {
        // 获取SQLiteDatabase对象
        SQLiteDatabase db = helper.getWritableDatabase();
        // 执行一条SQL语句
        String sql = "update searchHistory set searchAddress=?,tag=? where _id=?";
        db.execSQL(sql, new Object[]{account.getSearchAddress(), account.getTag(), account.getId()});
        // 关闭
        db.close();
    }


    public void updateCount(SearchHistoryBean mDistanceInfo) {

        if (mDistanceInfo == null) {

            return;

        }

        db = helper.getWritableDatabase();

        String sql = "update searchHistory set count=count+1 where id = " + mDistanceInfo.getId();

        db.execSQL(sql);

        db.close();

    }

    public ArrayList<SearchHistoryBean> getList(int pageNum) {
        ArrayList<SearchHistoryBean> list = new ArrayList<SearchHistoryBean>();
        String index = (pageNum - 1) * 20 + ""; // 翻页时的起始索引
        String count = 20 + ""; // 查询多少条数据

        db = helper.getReadableDatabase();

        String sql = "select * from searchHistory order by searchTime desc limit ?,?";
        Cursor cursor = db.rawQuery(sql, new String[]{index, count});

        while (cursor.moveToNext()) {

            SearchHistoryBean mDistanceInfo = new SearchHistoryBean();

            mDistanceInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            mDistanceInfo.setCount(cursor.getInt(cursor.getColumnIndexOrThrow("count")));

            mDistanceInfo.setTag(cursor.getString(cursor.getColumnIndexOrThrow("tag")));
            mDistanceInfo.setSearchAddress(cursor.getString(cursor.getColumnIndexOrThrow("searchAddress")));
            mDistanceInfo.setSearchTime(cursor.getString(cursor.getColumnIndexOrThrow("searchTime")));
            mDistanceInfo.setLongitude(cursor.getFloat(cursor.getColumnIndexOrThrow("longitude")));
            mDistanceInfo.setPoiName(cursor.getString(cursor.getColumnIndexOrThrow("poiName")));
            mDistanceInfo.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
            mDistanceInfo.setLatitude(cursor.getFloat(cursor.getColumnIndexOrThrow("latitude")));
            list.add(mDistanceInfo);
        }

        cursor.close();

        db.close();

        return list;

    }

    /**
     * 查询所有记录的总数
     *
     * @return 所有记录总数
     */
    public int queryCount() {
        int count = -1;
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select count(*) from searchHistory";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

}