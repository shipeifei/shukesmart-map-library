package com.shukesmart.maplibray.utils.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploadTask implements Runnable {
    private String filePath;
    private String uploadUrl;

    public FileUploadTask(String filePath, String uploadUrl) {
        this.filePath = filePath;
        this.uploadUrl = uploadUrl;
    }

    @Override
    public void run() {
        System.out.println(filePath);
        File file = new File(filePath);
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true); // 设置为输出模式
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + "*****");
            connection.setRequestProperty("file", file.getName());

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes("--" + "*****" + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + "\r\n");
            dos.writeBytes("Content-Type: " + "application/octet-stream" + "\r\n");
            dos.writeBytes("\r\n");

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int count;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes("\r\n--" + "*****" + "--\r\n");
            dos.flush();
            dos.close();

            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("文件上传成功");
                // 文件上传成功
            } else {
                // 文件上传失败
                System.out.println("文件上传失败");

            }
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("文件删除成功");
                } else {
                    System.out.println("文件删除失败");
                }
            } else {
                System.out.println("文件不存在");
            }
            connection.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}