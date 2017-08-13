package com.test.wifiapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.test.wifiapp.adapter.SignInAdapter;
import com.test.wifiapp.entity.SignIn;
import com.test.wifiapp.entity.WifiCheckedRec;
import com.test.wifiapp.entity.WifiCourseRec;
import com.test.wifiapp.net.OkHttpClientManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class CheckSignIn extends AppCompatActivity {

    private List<SignIn> list = new ArrayList<>();
    private String courseId="";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sign_in);
        initData();
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        Toast.makeText(CheckSignIn.this, courseId, Toast.LENGTH_SHORT).show();
        OkHttpClientManager.getChecked(courseId,
                //获取lsit请求后的回调

                new OkHttpClientManager.ResultCallback<WifiCheckedRec>() {
                    @Override
                    public void onError(Request request, Exception e) {

                        Toast.makeText(CheckSignIn.this, "连接服务器失败", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(WifiCheckedRec response) {
                        if (response.isSuccess()) {
                            try {
                                list = response.getData();
                                SignInAdapter adapter = new SignInAdapter(list);
                                recyclerView.setAdapter(adapter);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(CheckSignIn.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                        }

//                                        Toast.makeText(AdministorActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void initData() {

    }


}
