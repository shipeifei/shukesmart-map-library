package com.shukesmart.maplibray.utils.utils;

public class uploadlog {

    public static  void uploadLog(String filePath){
        //上传日志
        new Thread( new FileUploadTask(filePath,"https://swmap.azurewebsites.net/upload_file")).start();
    }
}
