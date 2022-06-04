package com.cindea.pothub.entities;

public class Pothole {

    private double latitude;
    private double longitude;
    private String address;
    private String user;
    private int intensity;
    private String timestamp;
    private int action;

    public Pothole(double latitude, double longitude, String address, String user, int intensity, String timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.user = user;
        this.intensity = intensity;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Pothole{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", user='" + user + '\'' +
                ", intensity=" + intensity +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}