package com.test.wifiapp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.test.wifiapp.utils.WifiApUtil;

public class WifiInfoActivity extends AppCompatActivity {

    private String TAG = "WifiInfoActivity";
    private TextView mTVWifiInfo;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_info);
        mTVWifiInfo =(TextView) findViewById(R.id.wifi_info);
        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        if(null != mWifiInfo && null != mWifiInfo.getSSID()){
            String mac = "";
            try{
                        mac =  WifiApUtil.getInstance(WifiInfoActivity.this).getLocalMac();
            }catch (Exception e){
                mac = mWifiInfo.getMacAddress();
            }
            String info = "getSSID()="+mWifiInfo.getSSID()+"\n"
                    +"getBSSID()="+mWifiInfo.getBSSID()+"\n"
                    +"getHiddenSSID()="+mWifiInfo.getHiddenSSID()+"\n"
                    +"getLinkSpeed()="+mWifiInfo.getLinkSpeed()+"\n"
                    +"getMacAddress()="+mac+"\n"
                    +"getNetworkId()="+mWifiInfo.getNetworkId()+"\n"
                    +"getRssi()="+mWifiInfo.getRssi()+"\n"
                    +"getSupplicantState()="+mWifiInfo.getSupplicantState()+"\n"
                    +"getDetailedStateOf()="+mWifiInfo.getDetailedStateOf(mWifiInfo.getSupplicantState());
            mTVWifiInfo.setText(info);
        }else {
            mTVWifiInfo.setText("没有连接到wifi");
        }
        Log.i(TAG,"iswifiEnable = "+mWifiManager.isWifiEnabled());
    }
}
