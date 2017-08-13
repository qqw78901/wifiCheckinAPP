package com.test.wifiapp.entity;


import java.text.SimpleDateFormat;

public class SignIn {

    private String name;
    private String createDt;
    private String mac;
    private String createIp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDt() {
        SimpleDateFormat format =new SimpleDateFormat("MM-dd HH:mm:ss");
        Long time=Long.valueOf(createDt);
        String d = format.format(time);
        return d;
    }

    public void setCreateDt(String createDt) {

        this.createDt = createDt;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }
}
