package com.test.wifiapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.test.wifiapp.entity.Fields;

/**
 * Created by asus on 2017/6/5.
 */

public class SharefreferenceUtil {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static SharedPreferences getUserPreferences(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(Fields.UsersharePreferenceName, 0);
        }
        return preferences;
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        if (editor == null) {
            editor = getUserPreferences(context).edit();
        }
        return editor;
    }



    /**
     * 获取一般用户的用户id或者管理员id
     *
     * @return
     */
    public static int getId(Context context) {
        return getUserPreferences(context).getInt(Fields.Id, 0);
    }

    public static void setUserInfo(Context context, @Nullable int userType,
                                   @Nullable String username, @Nullable String password) {
        SharedPreferences.Editor editor = getEditor(context);
        if (userType != 0)
            editor.putInt(Fields.UserTypeLabel, userType);
        if (username != null)
            editor.putString(Fields.account, username);
        if (password != null)
            editor.putString(Fields.password, password);
        editor.apply();

    }


    public static void setMasterInfo(Context context, int userType, String username, String password) {
        SharedPreferences.Editor editor = getEditor(context);
        if (userType != 0)
            editor.putInt(Fields.UserTypeLabel, userType);
        if (username != null)
            editor.putString(Fields.account, username);
        if (password != null)
            editor.putString(Fields.password, password);
        editor.apply();
    }


    /**
     * 获取一般用户或管理员的用户名
     *
     * @return
     */
    public static String getAccount(Context context) {
        return getUserPreferences(context).getString(Fields.account, "");
    }


    public static int getUserType(Context context) {
        return getUserPreferences(context).getInt(Fields.UserTypeLabel, 0);
    }


    public static String getPassword(Context context) {
        return getUserPreferences(context).getString(Fields.password, "");
    }

    public static void clearSharePreference(Context context) {
        getEditor(context).clear().apply();
    }

}
