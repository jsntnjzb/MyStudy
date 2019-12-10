package com.home.mystudy.Model;

public class WeatherInfo {
    private String cityid;
    private String city;
    private String temp;
    private String WD;
    private String WS;
    private String SD;
    private String AP;
    private String njd;
    private String WSE;
    private String time;
    private String sm;
    private int isRadar;
    private String Radar;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
