package com.test.wifiapp.entity;

/**
 * Created by asus on 2017/6/5.
 */

public class MasterLoginRec {

    private MasterLoginRec.DataBean data;
    private boolean success;
    private String info;

    public MasterLoginRec.DataBean getData() {
        return data;
    }

    public void setData(MasterLoginRec.DataBean data) {
        this.data = data;
    }

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

    public static class DataBean {
        private String loginName;
        private String psw;

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getPsw() {
            return psw;
        }

        public void setPsw(String psw) {
            this.psw = psw;
        }
    }

}
