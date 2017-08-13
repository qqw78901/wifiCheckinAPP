package com.test.wifiapp.entity;

import java.util.List;

/**
 * Created by asus on 2017/6/5.
 */

public class WifiCheckedRec {

    private boolean success;
    private String info;
    private String num;
    private List<SignIn> data;


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


    public List<SignIn> getData() {
        return data;
    }

    public void setData(List<SignIn> data) {
        this.data = data;
    }
}
