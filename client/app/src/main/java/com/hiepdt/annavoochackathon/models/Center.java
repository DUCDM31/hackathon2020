package com.hiepdt.annavoochackathon.models;

public class Center {
    private String url;
    private String name;
    private String address;
    private String phone;
    private String open;
    private double lat;
    private double lon;


    public Center(){}

    public Center(String url, String name, String address, String phone, String open, double lat, double lon) {
        this.url = url;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.open = open;
        this.lat = lat;
        this.lon = lon;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
