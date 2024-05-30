package com.shukesmart.maplibray.utils.db;

public class RouteBean {
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;
    public String getLengthInMeters() {
        return lengthInMeters;
    }

    public void setLengthInMeters(String lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    private String lengthInMeters;

    public String getTravelTimeInSeconds() {
        return travelTimeInSeconds;
    }

    public void setTravelTimeInSeconds(String travelTimeInSeconds) {
        this.travelTimeInSeconds = travelTimeInSeconds;
    }

    private String travelTimeInSeconds;
}
