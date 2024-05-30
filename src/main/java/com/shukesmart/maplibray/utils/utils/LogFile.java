package com.shukesmart.maplibray.utils.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogFile {
    private   String logFileName="";

    public LogFile(String name,Context context) {
        String dir = getSDCardPrivateCacheDir(context);
        //创建目录
        File folder = new File(dir + "/shukesmartNavigation");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = dateFormat.format(new Date());
        logFileName = folder+"/" +date + "-" + name +"-"+ Build.BRAND + "-" + Build.MODEL.replaceAll(" ","-") +".txt";
        System.out.println(logFileName);


    }
    public String getLogFileName(){
        return logFileName;
    }
    public static String getSDCardPrivateCacheDir(Context context) {
        if (null != context.getExternalCacheDir()) {
            return context.getExternalCacheDir().getAbsolutePath();
        }
        return null;
    }
    void writeTxt(String fileName, String content) {
        if(logFileName.isEmpty()){
            System.out.println("没有初始化日志文件名称，无法创建日志");

        }
        else {
            try {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = sdf.format(calendar.getTime());
                //要指定编码方式，否则会出现乱码
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileName, true), "utf-8");
                osw.write(content + "--------------" + currentDate + "\n");
                osw.flush();
                osw.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public void delFile(){
        File file = new File(logFileName);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("文件删除成功");
            } else {
                System.out.println("文件删除失败");
            }
        } else {
            System.out.println("文件不存在");
        }
    }
    public void log(String message) {
        writeTxt(logFileName,message);
    }
}
