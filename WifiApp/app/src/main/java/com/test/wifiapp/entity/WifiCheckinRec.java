package com.test.wifiapp.entity;

/**
 * Created by asus on 2017/6/5.
 */

public class WifiCheckinRec {
    private String num;

    private boolean success;
    private String info;
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
