package com.test.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.test.wifiapp.entity.Fields;
import com.test.wifiapp.entity.LoginUserRec;
import com.test.wifiapp.entity.MasterLoginRec;
import com.test.wifiapp.net.NetworkState;
import com.test.wifiapp.net.OkHttpClientManager;
import com.test.wifiapp.utils.SharefreferenceUtil;
import com.test.wifiapp.utils.WifiApUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    private Button signInButton;
    private RadioGroup radioGroup;
    private EditText etAccount;
    private EditText etPassword;
    private Intent intent;
    private ProgressBar pbrLogin;
    private boolean isUserLogin;
    private String userName = null;
    private String passWord = null;
    private Context mContext;
    private String mac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvents();
    }

    private void initEvents() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbn_user:
                        isUserLogin = true;
                        postLogin();


                        break;
                    case R.id.rbn_administor:
                        isUserLogin = false;
                        postLogin();
                        break;
                }
            }
        });
    }

    private void initView() {
        signInButton = (Button) findViewById(R.id.singIn);
        radioGroup = (RadioGroup) findViewById(R.id.rgp_select);
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassword = (EditText) findViewById(R.id.et_password);
        pbrLogin = (ProgressBar) findViewById(R.id.pbr_login);
        intent = new Intent();
        mContext = getApplicationContext();
    }

    //开始登录
    private void postLogin() {
        try {
            mac = MainActivity.getMac();
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"无法获取mac",Toast.LENGTH_SHORT).show();
            return;
        }
        userName = etAccount.getText().toString();
        passWord = etPassword.getText().toString();
        pbrLogin.setVisibility(View.VISIBLE);
        keepView();
        if (NetworkState.networkConnected(this)) {
            if (isUserLogin) {
                userLogin(userName, passWord);
            } else {
                masterLogin(userName, passWord);
            }
        } else {
            restoreView();
            pbrLogin.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
        }
    }

    //处理登录请求时保持界面
    private void keepView() {
        etAccount.setEnabled(false);
        etPassword.setEnabled(false);
        signInButton.setClickable(false);
        radioGroup.setEnabled(false);
    }

    private void userLogin(String userName, String passWord) {
        OkHttpClientManager.userLogin(userName, passWord,mac, new OkHttpClientManager.ResultCallback<LoginUserRec>() {
            @Override
            public void onError(Request request, Exception e) {
                restoreView();
                pbrLogin.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(LoginUserRec response) {
                pbrLogin.setVisibility(View.INVISIBLE);
                if (!response.isSuccess()) {
                    restoreView();
                    Toast.makeText(MainActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                } else {
                    userLoginSuccess(response);
                }
            }
        });
    }

    private void masterLogin(String userName, String passWord) {
        OkHttpClientManager.masterLogin(userName, passWord, mac , new OkHttpClientManager.ResultCallback<MasterLoginRec>() {
            @Override
            public void onError(Request request, Exception e) {
                restoreView();
                pbrLogin.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(MasterLoginRec response) {
                pbrLogin.setVisibility(View.INVISIBLE);
                if (!response.isSuccess()) {
                    restoreView();
                    Toast.makeText(MainActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                } else {
                    masterLoginSuccess(response);
                }
            }


        });
    }

    //登录失败时恢复界面可编辑
    private void restoreView() {
        etAccount.setEnabled(true);
        etPassword.setEnabled(true);
        signInButton.setClickable(true);
        radioGroup.setEnabled(false);
    }

    //登录成功的处理
    private void userLoginSuccess(LoginUserRec response) {
        SharefreferenceUtil.clearSharePreference(this);

        LoginUserRec.DataBean data = response.getData();

        //普通用户登录
        SharefreferenceUtil.setUserInfo(this, Fields.USER_TYPE_NORMAL_USER,
                data.getLoginName(), data.getPsw());
        intent = new Intent(MainActivity.this, UserActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void masterLoginSuccess(MasterLoginRec rec) {
        MasterLoginRec.DataBean data = rec.getData();

        //管理员登录
        SharefreferenceUtil.setMasterInfo(this, Fields.USER_TYPE_MASTER, data.getLoginName(), data.getPsw());
        Intent intent = new Intent(MainActivity.this, AdministorActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //判断NetworkInfo对象是否为空 并且类型是否为WIFI
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return networkInfo.isAvailable();
        }
        return false;
    }
    public static String getMac(){
        String macSerial = "";
        try {
//                最有效的方法 ： https://my.oschina.net/u/1455799/blog/335704
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {
                macSerial += line.trim();
            }

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return macSerial;
    }

}