package com.shukesmart.maplibray.utils.utils;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.shukesmart.maplibray.utils.utils.MapPoint;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//使用方式
//GraphUtils.isPointInPolygon(MapPoint point,List<MapPoint> boundaryPoints)
public class GraphUtils {
    private static double EARTH_RADIUS = 6378137;
    private final int discard = 1;   //Volatile修饰的成员变量在每次被线程访问时，都强迫从共享内存中重读该成员变量的值。

    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getMacAddress();
    }

    public static boolean isLocationOnPath(double latitude, double longitude, List<LatLng> boundaryPoints, int tolerance) {
        return PolyUtil.isLocationOnPath(new LatLng(latitude, longitude), boundaryPoints, true, tolerance);
    }

    /**
     * @param jsonArray
     * @return
     */
    public static ArrayList<JSONObject> copyJsonArray(JSONArray jsonArray) {
        ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                arrayList.add(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public static String formatFriendlyDate(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar otherDate = Calendar.getInstance();
        otherDate.setTime(date);

        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat todayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String todayDate = todayFormat.format(today.getTime());
        String otherDateDate = todayFormat.format(otherDate.getTime());

        if (otherDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && otherDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return otherDateDate;
        } else {
            return fullFormat.format(date);
        }
    }

    public static boolean navigation(TextView instructionAddressView,
                                     JSONArray instructions,
                                     int currentInstructionIndex,
                                     int instructionTotalSize,
                                     int distanceToCurrentInstructionDestination,
                                     TextView uint,
                                     TextView instructionKmView,
                                     TTS tts,
                                     JSONObject currentInstruction) throws JSONException {


        //行驶中
        if (distanceToCurrentInstructionDestination > 2) {
            if (distanceToCurrentInstructionDestination < 1000) {
                uint.setText("m");
                instructionKmView.setText(String.valueOf(distanceToCurrentInstructionDestination));
            } else {
                instructionKmView.setText(String.valueOf(Math.round((float) distanceToCurrentInstructionDestination / 1000)));
                uint.setText("km");
            }
            return false;
        } else {
            //进入下一段路程
            if (currentInstructionIndex < instructionTotalSize - 1) {
                currentInstructionIndex += 1;
                currentInstruction = (JSONObject) instructions.get(currentInstructionIndex);

                //获取上一个instruction的信息
                JSONObject lastInstruction = (JSONObject) instructions.get(currentInstructionIndex - 1);
                //计算下一段路程，当前instruction-上一个instrction
                int routeOffsetInMeters = currentInstruction.getInt("routeOffsetInMeters") - lastInstruction.getInt("routeOffsetInMeters");
                String ttsText = "";
                if (routeOffsetInMeters < 1000) {
                    uint.setText("m");
                    instructionKmView.setText(String.valueOf(routeOffsetInMeters));
                    ttsText += routeOffsetInMeters + "meters";
                } else {
                    instructionKmView.setText(String.valueOf(routeOffsetInMeters / 1000));

                    uint.setText("km");
                    ttsText += routeOffsetInMeters + "kilometers";

                }

                instructionAddressView.setText(currentInstruction.getString("message"));


                if (currentInstruction.has("combinedMessage")) {
                    ttsText += currentInstruction.getString("combinedMessage");

                } else {
                    ttsText += currentInstruction.getString("message");

                }
                tts.startSpeed(ttsText, true);
                return true;

            }

            return true;
        }
    }


    public static String paddingZero(int val) {
        if (val < 10) {
            return "0" + val;
        }
        return val + "";
    }

    public static String getMac() {
        String macAddress = "";
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (intf.getName().equalsIgnoreCase("wlan0")) {
                    byte[] mac = intf.getHardwareAddress();
                    if (mac == null) {
                        macAddress = "";
                    } else {
                        StringBuilder buf = new StringBuilder();
                        for (byte aMac : mac) {
                            buf.append(String.format("%02X:", aMac));
                        }
                        if (buf.length() > 0) {
                            buf.deleteCharAt(buf.length() - 1);
                        }
                        macAddress = buf.toString();
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return macAddress;

    }

    /**
     * 判断点是否在多边形内(基本思路是用交点法)
     *
     * @param point
     * @param boundaryPoints
     * @return
     */
    public static boolean isPointInPolygon(MapPoint point, MapPoint[] boundaryPoints) {
        // 防止第一个点与最后一个点相同
        if (boundaryPoints != null && boundaryPoints.length > 0
                && boundaryPoints[boundaryPoints.length - 1].equals(boundaryPoints[0])) {
            boundaryPoints = Arrays.copyOf(boundaryPoints, boundaryPoints.length - 1);
        }
        int pointCount = boundaryPoints.length;

        // 首先判断点是否在多边形的外包矩形内，如果在，则进一步判断，否则返回false
        if (!isPointInRectangle(point, boundaryPoints)) {
            return false;
        }

        // 如果点与多边形的其中一个顶点重合，那么直接返回true
        for (int i = 0; i < pointCount; i++) {
            if (point.equals(boundaryPoints[i])) {
                return true;
            }
        }

        /**
         * 基本思想是利用X轴射线法，计算射线与多边形各边的交点，如果是偶数，则点在多边形外，否则在多边形内。还会考虑一些特殊情况，如点在多边形顶点上
         * ， 点在多边形边上等特殊情况。
         */
        // X轴射线与多边形的交点数
        int intersectPointCount = 0;
        // X轴射线与多边形的交点权值
        float intersectPointWeights = 0;
        // 浮点类型计算时候与0比较时候的容差
        double precision = 2e-10;
        // 边P1P2的两个端点
        MapPoint point1 = boundaryPoints[0], point2;
        // 循环判断所有的边
        for (int i = 1; i <= pointCount; i++) {
            point2 = boundaryPoints[i % pointCount];

            /**
             * 如果点的y坐标在边P1P2的y坐标开区间范围之外，那么不相交。
             */
            if (point.getLat() < Math.min(point1.getLat(), point2.getLat())
                    || point.getLat() > Math.max(point1.getLat(), point2.getLat())) {
                point1 = point2;
                continue;
            }
            /**
             * 此处判断射线与边相交
             */
            if (point.getLat() > Math.min(point1.getLat(), point2.getLat())
                    // 如果点的y坐标在边P1P2的y坐标开区间内
                    && point.getLat() < Math.max(point1.getLat(), point2.getLat())) {
                // 若边P1P2是垂直的
                if (point1.getLng() == point2.getLng()) {
                    if (point.getLng() == point1.getLng()) {
                        // 若点在垂直的边P1P2上，则点在多边形内
                        return true;
                    } else if (point.getLng() < point1.getLng()) {
                        // 若点在在垂直的边P1P2左边，则点与该边必然有交点
                        ++intersectPointCount;
                    }
                } else {// 若边P1P2是斜线
                    // 点point的x坐标在点P1和P2的左侧
                    if (point.getLng() <= Math.min(point1.getLng(), point2.getLng())) {
                        ++intersectPointCount;
                    }
                    // 点point的x坐标在点P1和P2的x坐标中间
                    else if (point.getLng() > Math.min(point1.getLng(), point2.getLng())
                            && point.getLng() < Math.max(point1.getLng(), point2.getLng())) {
                        double slopeDiff = getSlopeDiff(point, point1, point2);
                        if (slopeDiff > 0) {
                            // 由于double精度在计算时会有损失，故匹配一定的容差。经试验，坐标经度可以达到0.0001
                            if (slopeDiff < precision) {
                                // 点在斜线P1P2上
                                return true;
                            } else {
                                // 点与斜线P1P2有交点
                                intersectPointCount++;
                            }
                        }
                    }
                }
            } else {
                // 边P1P2水平
                if (point1.getLat() == point2.getLat()) {
                    if (checkPointInLine(point, point1, point2)) {
                        return true;
                    }
                }
                /**
                 * 判断点通过多边形顶点
                 */
                if (((point.getLat() == point1.getLat() && point.getLng() < point1.getLng()))
                        || (point.getLat() == point2.getLat() && point.getLng() < point2.getLng())) {
                    if (point2.getLat() < point1.getLat()) {
                        intersectPointWeights += -0.5;
                    } else if (point2.getLat() > point1.getLat()) {
                        intersectPointWeights += 0.5;
                    }
                }
            }
            point1 = point2;
        }
        // 偶数在多边形外
        if ((intersectPointCount + Math.abs(intersectPointWeights)) % 2 == 0) {
            return false;
        } else { // 奇数在多边形内
            return true;
        }
    }

    private static double getSlopeDiff(MapPoint point, MapPoint point1, MapPoint point2) {
        double slopeDiff = 0.0d;
        if (point1.getLat() > point2.getLat()) {
            slopeDiff = (point.getLat() - point2.getLat()) / (point.getLng() - point2.getLng())
                    - (point1.getLat() - point2.getLat()) / (point1.getLng() - point2.getLng());
        } else {
            slopeDiff = (point.getLat() - point1.getLat()) / (point.getLng() - point1.getLng())
                    - (point2.getLat() - point1.getLat()) / (point2.getLng() - point1.getLng());
        }
        return slopeDiff;
    }

    private static boolean checkPointInLine(MapPoint point, MapPoint point1, MapPoint point2) {
        if (point.getLng() <= Math.max(point1.getLng(), point2.getLng())
                && point.getLng() >= Math.min(point1.getLng(), point2.getLng())) {
            // 若点在水平的边P1P2上，则点在多边形内
            return true;
        }
        return false;
    }

    /**
     * 判断点是否在矩形内在矩形边界上，也算在矩形内(根据这些点，构造一个外包矩形)
     *
     * @param point          点对象
     * @param boundaryPoints 矩形边界点
     * @return
     */
    public static boolean isPointInRectangle(MapPoint point, MapPoint[] boundaryPoints) {
        // 西南角点
        MapPoint southWestPoint = getSouthWestPoint(boundaryPoints);
        // 东北角点
        MapPoint northEastPoint = getNorthEastPoint(boundaryPoints);
        return (point.getLng() >= southWestPoint.getLng() && point.getLng() <= northEastPoint.getLng()
                && point.getLat() >= southWestPoint.getLat() && point.getLat() <= northEastPoint.getLat());

    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lat1 纬度1
     * @param lng1 经度1
     * @param lat2 纬度2
     * @param lng2 经度2
     * @return 距离
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        //double len=s/EARTH_SEA;
        //s = s / 1000;
        return s;
    }

    /**
     * 根据这组坐标，画一个矩形，然后得到这个矩形西南角的顶点坐标
     *
     * @param vertexs
     * @return
     */
    private static MapPoint getSouthWestPoint(MapPoint[] vertexs) {
        double minLng = vertexs[0].getLng(), minLat = vertexs[0].getLat();
        for (MapPoint bmapPoint : vertexs) {
            double lng = bmapPoint.getLng();
            double lat = bmapPoint.getLat();
            if (lng < minLng) {
                minLng = lng;
            }
            if (lat < minLat) {
                minLat = lat;
            }
        }
        return new MapPoint(minLng, minLat);
    }

    /**
     * 根据这组坐标，画一个矩形，然后得到这个矩形东北角的顶点坐标
     *
     * @param vertexs
     * @return
     */
    private static MapPoint getNorthEastPoint(MapPoint[] vertexs) {
        double maxLng = 0.0d, maxLat = 0.0d;
        for (MapPoint bmapPoint : vertexs) {
            double lng = bmapPoint.getLng();
            double lat = bmapPoint.getLat();
            if (lng > maxLng) {
                maxLng = lng;
            }
            if (lat > maxLat) {
                maxLat = lat;
            }
        }
        return new MapPoint(maxLng, maxLat);
    }

    /**
     * 格式化经纬度保留5位小数
     ***/
    public static Double formatLatLngKeepFive(Double value) {
        DecimalFormat format2 = new DecimalFormat("#.#####");
        String str2 = format2.format(value);
        return Double.parseDouble(str2);

    }

    public static String secondsToDaysHoursMinutes(int seconds) {
        if (seconds < 60) {
            return seconds + " s";
        }
        int days = seconds / 86400; // 1天有86400秒
        int hours = (seconds % 86400) / 3600; // 1小时有3600秒
        int minutes = (seconds % 3600) / 60; // 1分钟有60秒
        String result = "";
        if (days > 0) {
            result += days + "d ";
        }

        if (hours > 0) {
            result += hours + "h ";
        }

        if (minutes > 0) {
            result += minutes + "m ";
        }

        return result;
    }

    //计算多个点之间的总距离
    public static double calculateTotalDistance(List<LatLng> points) {
        System.out.print(points);
        double totalDistance = 0;
        LatLng prevPoint = null;

        for (LatLng point : points) {
            if (prevPoint != null) {
                totalDistance += SphericalUtil.computeDistanceBetween(prevPoint, point);
            }
            prevPoint = point;
        }

        System.out.print("calculateTotalDistance:" + totalDistance);

        return totalDistance; // 返回总距离，单位为米
    }

    /**
     * 检测是否在原地不动
     *
     * @param distance
     * @return
     */
    public static boolean isMove(Double distance) {

        if (distance < 0.01) {

            return false;

        }

        return true;

    }

    /**
     * 检测获取的数据是否是正常的
     *
     * @return
     */
    public static boolean checkProperLocation(double latitude, double longitude) {
        return latitude != 0 && longitude != 0;
    }

}


