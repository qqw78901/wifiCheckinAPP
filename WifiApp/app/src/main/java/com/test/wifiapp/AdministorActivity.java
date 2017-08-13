package com.test.wifiapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.test.wifiapp.entity.Static;
import com.test.wifiapp.entity.WifiCourseRec;
import com.test.wifiapp.net.OkHttpClientManager;
import com.test.wifiapp.utils.WifiApUtil;

import okhttp3.Request;

public class AdministorActivity extends AppCompatActivity {

    private Button mCheckSignIn;
    private String TAG = "WifiApActivity";
    public final static boolean DEBUG = true;
    private Button mBtStartWifiAp, mBtStopWifiAp;
    private EditText mWifiSsid, mWifiPassword;
    private RadioGroup mRgWifiSerurity;
    private RadioButton mRdNo, mRdWpa, mRdWpa2;
    private TextView mWifiApState;
    private String courseId="";
    private WifiApUtil.WifiSecurityType mWifiType = WifiApUtil.WifiSecurityType.WIFICIPHER_NOPASS;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (DEBUG) Log.i(TAG, "WifiApActivity message.what=" + msg.what);
            switch (msg.what) {
                case WifiApUtil.MESSAGE_AP_STATE_ENABLED:
                    try {
                        String ssid = WifiApUtil.getInstance(AdministorActivity.this).getValidApSsid();
                        String mac = "";
                        String pw = WifiApUtil.getInstance(AdministorActivity.this).getValidPassword();
                        try {
                            mac = WifiApUtil.getInstance(AdministorActivity.this).getLocalMac();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mWifiApState.setText("wifi热点开启成功" + "\n"
                                + "SSID：" + ssid + "\n"
                                + "密码：" + pw + "\n"
                                + "Mac地址：" + mac + "\n");
                        //将ssid和mac发送到服务器服务器处理开启课程;
                        try {
                            OkHttpClientManager.openWifiCallBack(ssid, mac,//回调
                                    new OkHttpClientManager.ResultCallback<WifiCourseRec>() {
                                        @Override
                                        public void onError(Request request, Exception e) {
//                                            mWifiApState.append("连接服务器失败\n");
                                            Toast.makeText(AdministorActivity.this, "开启wifi热点过程连接服务器失败", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onResponse(WifiCourseRec response) {
                                            mWifiApState.append(response.getInfo() + "\n");
                                            try {
                                                courseId = response.getCourseId();
                                            }catch (Exception e){
                                                Toast.makeText(AdministorActivity.this, "无法获取courseId", Toast.LENGTH_SHORT).show();

                                                e.printStackTrace();
                                            }
                                            Toast.makeText(AdministorActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                        }


                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case WifiApUtil.MESSAGE_AP_STATE_FAILED:
                    mWifiApState.setText("wifi热点已关闭\n" + "正在反馈到服务器\n");
                    try {
                        OkHttpClientManager.closeWifiCallBack(
                                //关闭wifi发送请求后的回调

                                new OkHttpClientManager.ResultCallback<WifiCourseRec>() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        mWifiApState.setText("wifi热点已关闭\n 反馈到服务器失败，请重新尝试");

                                        Toast.makeText(AdministorActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onResponse(WifiCourseRec response) {
                                        if (response.isSuccess()) {
                                            mWifiApState.setText("wifi热点已关闭\n 已成功反馈到服务器\n本次课程共" + response.getNum() +
                                                    "人签到");
                                        } else {
                                            Toast.makeText(AdministorActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                        }

//                                        Toast.makeText(AdministorActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administor);
        mCheckSignIn = (Button) findViewById(R.id.check_sign_in);
        WifiApUtil.getInstance(getApplicationContext());
        WifiApUtil.getInstance(this).regitsterHandler(mHandler);
        mBtStartWifiAp = (Button) findViewById(R.id.bt_start_wifiap);
        mWifiSsid = (EditText) findViewById(R.id.et_ssid);
        mWifiPassword = (EditText) findViewById(R.id.et_password);
        mRgWifiSerurity = (RadioGroup) findViewById(R.id.rg_security);
        mRdNo = (RadioButton) findViewById(R.id.rd_no);
        mRdWpa = (RadioButton) findViewById(R.id.rd_wpa);
        mRdWpa2 = (RadioButton) findViewById(R.id.rd_wpa2);
        mWifiApState = (TextView) findViewById(R.id.tv_state);
        mBtStopWifiAp = (Button) findViewById(R.id.bt_stop_wifiap);

        mCheckSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministorActivity.this, CheckSignIn.class);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRgWifiSerurity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {

                if (arg1 == mRdNo.getId()) {
                    mWifiType = WifiApUtil.WifiSecurityType.WIFICIPHER_NOPASS;
                } else if (arg1 == mRdWpa.getId()) {
                    mWifiType = WifiApUtil.WifiSecurityType.WIFICIPHER_WPA;
                } else if (arg1 == mRdWpa2.getId()) {
                    mWifiType = WifiApUtil.WifiSecurityType.WIFICIPHER_WPA2;
                }
                if (DEBUG) Log.i(TAG, "radio check mWifiType = " + mWifiType);
            }
        });
        mBtStartWifiAp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String ssid = mWifiSsid.getText().toString();
                Static.ssid = ssid;
                String password = mWifiPassword.getText().toString();
                if (DEBUG) Log.d(TAG, "ssid = " + ssid + "password = " + password);
                if (null == ssid || "".equals(ssid)) {
                    Toast.makeText(AdministorActivity.this, "请输入ssid", Toast.LENGTH_SHORT).show();
                    return;
                }
                mWifiApState.setText("正在开启");
                WifiApUtil.getInstance(AdministorActivity.this)
                        .turnOnWifiAp(ssid, password, mWifiType);

            }
        });

        mBtStopWifiAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiApUtil.getInstance(AdministorActivity.this).closeWifiAp();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (DEBUG) Log.i(TAG, "WifiApActivity onBackPressed");
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (DEBUG) Log.i(TAG, "WifiApActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.i(TAG, "WifiApActivity onDestroy");
        WifiApUtil.getInstance(this).unregitsterHandler();
    }


}
