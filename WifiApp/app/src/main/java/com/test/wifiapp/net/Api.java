package com.test.wifiapp.net;

/**
 * Created by asus on 2017/6/5.
 */

public class Api {

    //动作请求
    private static final String url = "http://www.gdutzuo.top/community/";
    static final String userLogin = url + "wifiuser/login.do";
    static final String openWifi = url + "wificourse/openwifi.do";
    static final String closeWifi = url + "wificourse/closewifi.do";
    static final String checkin = url + "wifihistory/checkin.do";
    static final String checkedList = url + "wifihistory/checked.do";
//    static final String masterLogin = url + "admin/login.do";

    //用户信息
    static final String fieldPassword = "psw";
    static final String fieldUserName = "loginName";
    static final String fieldType = "type";
}
