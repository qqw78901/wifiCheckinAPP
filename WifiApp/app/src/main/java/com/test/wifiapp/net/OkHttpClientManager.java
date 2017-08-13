package com.test.wifiapp.net;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.test.wifiapp.entity.LoginUserRec;
import com.test.wifiapp.entity.WifiCheckedRec;
import com.test.wifiapp.entity.WifiCheckinRec;
import com.test.wifiapp.entity.WifiCourseRec;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by asus on 2017/6/5.
 * 网络处理管理类
 */

public class OkHttpClientManager {

    private Handler mHandler;
    private Gson mGson;
    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private String session = "";

    //一般用户登录
    public static void userLogin(String userName, String password, String mac, ResultCallback<LoginUserRec> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(Api.fieldUserName, userName);
        map.put(Api.fieldPassword, password);
        map.put(Api.fieldType, "2");
        map.put("mac", mac);
        getInstance().enqueuePost(Api.userLogin, callback, map);
    }

    //开启wifi热点
    public static void openWifiCallBack(String ssid, String bssid, ResultCallback<WifiCourseRec> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("ssid", ssid);
        map.put("mac", bssid);
        getInstance().enqueuePost(Api.openWifi, callback, map);
    }

    //关闭wifi热点
    public static void closeWifiCallBack(ResultCallback<WifiCourseRec> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("ssid", "1");

        getInstance().enqueuePost(Api.closeWifi, callback, map);
    }

    //学生签到
    public static void checkinRequest(String localMac, String assid, String ssid, ResultCallback<WifiCheckinRec> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("mac", localMac);
        map.put("assid", assid);
        map.put("ssid", ssid);
        getInstance().enqueuePost(Api.checkin, callback, map);
    }

    //管理员登录
    public static void masterLogin(String masterName, String password, String mac, ResultCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put(Api.fieldUserName, masterName);
        map.put(Api.fieldPassword, password);
        map.put(Api.fieldType, "1");
        map.put("mac",mac);
        getInstance().enqueuePost(Api.userLogin, callback, map);
    }

    //获取已签到列表
    public static void getChecked(String courseId , ResultCallback<WifiCheckedRec> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        getInstance().enqueuePost(Api.checkedList, callback, map);
    }

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                .writeTimeout(5000L, TimeUnit.MILLISECONDS)
                .build();
        mHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    //多项表单数据的异步post
    private void enqueuePost(String url, final ResultCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    //单项表单数据的post
    private void enqueuePost(String url, ResultCallback callback, Param param) {
        RequestBody body = new FormBody.Builder()
                .add(param.key, param.value)
                .build();


        Request request = new Request.Builder()
                .url(url)
                .addHeader("cookie", session)
                .post(body)
                .build();
        deliveryResult(callback, request);

    }

    //泛型处理类
    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }

    //请求时的表单对参数
    private static class Param {
        Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    //处理请求结果
    private void deliveryResult(final ResultCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(call.request(), e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                Headers headers = response.headers();
                List<String> cookies = headers.values("Set-Cookie");
                if (cookies.size() != 0) {
                    String s = cookies.get(0);
                    session = s.substring(0, s.indexOf(";"));
                }

                try {
                    final String string = response.body().string();
                    if (callback.mType == Response.class) {
                        sendSuccessResultCallback(response, callback);
                    } else {
                        Object o = mGson.fromJson(string, callback.mType);
                        sendSuccessResultCallback(o, callback);
                    }


                } catch (IOException | com.google.gson.JsonParseException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                }
                response.close();
            }
        });
    }

    //请求失败时调用
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    //请求成功时调用
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    //将map转化为表单对
    private Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();

        int i = 0;

        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());

        }
        return res;
    }

    private Request buildPostRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .addHeader("Cookie", session)
                .url(url)
                .post(requestBody)
                .build();

    }

}
