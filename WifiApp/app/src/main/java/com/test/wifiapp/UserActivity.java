package com.test.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.test.wifiapp.entity.WifiCheckinRec;
import com.test.wifiapp.net.OkHttpClientManager;
import com.test.wifiapp.utils.WifiApUtil;

import okhttp3.Request;

public class UserActivity extends AppCompatActivity {

    private Button mBtWifiScan,checkinBtn;
    private TextView result;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mBtWifiScan = (Button) findViewById(R.id.bt_scan_wifi);
        checkinBtn = (Button) findViewById(R.id.btn_check_in);
        result = (TextView) findViewById(R.id.check_in_result);
        //签到按钮功能实现
        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//忘记获取服务了
                mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//                判断是否已经连上了wifi 如果没有连上，禁止发请求
                mWifiInfo = mWifiManager.getConnectionInfo();
                if(null != mWifiInfo && null != mWifiInfo.getSSID()){
                    String mac = "",BSSID="",SSID="";
//                    换用成功率高的获取mac方法
                    try{
                        mac =  WifiApUtil.getInstance(UserActivity.this).getLocalMac();
                    }catch (Exception e){
                        mac = mWifiInfo.getMacAddress();
                    }
                    BSSID = mWifiInfo.getBSSID();
                    SSID = mWifiInfo.getSSID();
                    result.setText(BSSID+SSID);

                    OkHttpClientManager.checkinRequest(mac,BSSID,SSID,
                            //关闭wifi发送请求后的回调
                            new OkHttpClientManager.ResultCallback<WifiCheckinRec>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    result.setText("签到失败，请重新尝试");
                                    Toast.makeText(UserActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();

                                }
                                @Override
                                public void onResponse(WifiCheckinRec response) {
                                    result.setText(response.getInfo());
//                                    result.setText("您已成功签到！\n目前共"+response.getNum()+"人签到");
//                                    Toast.makeText(UserActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    result.setText("没有连接到wifi");
                }

//                String localMac,String assid,


            }
        });
        mBtWifiScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent in = new Intent(UserActivity.this, WifiListActivity.class);
                startActivity(in);
            }
        });
    }
}
