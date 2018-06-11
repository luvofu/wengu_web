package com.wg.common.model;

/**
 * Created by Administrator on 2017/2/22 0022.
 */
public class NearbyRange {
    private double longitude;
    private double latitude;
    private double fromLng;
    private double toLng;
    private double fromLat;
    private double toLat;

    public NearbyRange(double longitude, double latitude, double fromLng, double toLng, double fromLat, double toLat) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.fromLng = fromLng;
        this.toLng = toLng;
        this.fromLat = fromLat;
        this.toLat = toLat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getFromLng() {
        return fromLng;
    }

    public void setFromLng(double fromLng) {
        this.fromLng = fromLng;
    }

    public double getToLng() {
        return toLng;
    }

    public void setToLng(double toLng) {
        this.toLng = toLng;
    }

    public double getFromLat() {
        return fromLat;
    }

    public void setFromLat(double fromLat) {
        this.fromLat = fromLat;
    }

    public double getToLat() {
        return toLat;
    }

    public void setToLat(double toLat) {
        this.toLat = toLat;
    }
}
