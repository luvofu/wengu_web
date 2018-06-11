package com.wg.common.utils;

import com.wg.common.model.NearbyRange;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class LngLatUtils {
    public static final long NEARBY_RADIUS = 60000;//附近半径60千米
    private static final double EARTH_RADIUS = 6378.137;// 单位千米

    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    //经纬度距离
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;// 两点纬度差
        double b = getRadian(lng1) - getRadian(lng2);// 两点的经度差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s * 1000;
    }

    //附近范围
    public static NearbyRange getNearbyRange(double longitude, double latitude, double radius) {
        double dtLng = Math.abs(1.0 / (111000 * Math.cos(latitude)) * radius);
        double fromLng = longitude - dtLng;
        double toLng = longitude + dtLng;
        double dtLat = Math.abs(1.0 / 111000 * radius);
        double fromLat = latitude - dtLat;
        double toLat = latitude + dtLat;
        NearbyRange nearbyRange = new NearbyRange(longitude, latitude, fromLng, toLng, fromLat, toLat);
        return nearbyRange;
    }
}
