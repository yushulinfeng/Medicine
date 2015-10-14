package org.outing.medicine.start;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.outing.medicine.tools.utils.DealPass;

/**
 * 用户名与密码存储与读取类
 */
public class UserTool {
    private static final String FIRSTRUN_SAVE_PATH = "all.firstrun";
    private static final String USER_SAVE_PATH = "all.User";//密码相对存储位置


    /**
     * 判断用户是否为首次运行
     */
    public static boolean isFirstRun(Context context) {
        SharedPreferences mPref = context.getSharedPreferences(FIRSTRUN_SAVE_PATH,
                Activity.MODE_PRIVATE);
        boolean first = mPref.getBoolean("first", true);
        writeNotFirstRun(context);
        return first;
    }

    /**
     * 存储为非首次运行
     */
    private static void writeNotFirstRun(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                FIRSTRUN_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putBoolean("first", false);
        prefs.commit();
    }

    /////////////////////////用户名与密码处理//////////////////////////////

    /**
     * 存储用户名与密码（MD5）
     */
    public static void saveUser(Context context, String name, String pass) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                USER_SAVE_PATH, Activity.MODE_PRIVATE).edit();
//        String md5_pass = DealPass.getMD5(pass);
        String safe_pass = DealPass.encodePass(pass, name);
        prefs.putString("name", name);
        prefs.putString("safe_pass", safe_pass);
        prefs.commit();
    }

    /**
     * 从本地的存储中读取出用户名，没有存储返回""
     */
    public static String getUserName(Context context) {
        SharedPreferences mPref = context.getSharedPreferences(USER_SAVE_PATH,
                Activity.MODE_PRIVATE);
        String name = mPref.getString("name", "");
        return name;
    }

    /**
     * 从本地的存储中读取出密码（MD5）
     */
    public static String getUserPass(Context context) {
        SharedPreferences mPref = context.getSharedPreferences(USER_SAVE_PATH,
                Activity.MODE_PRIVATE);
        String name = mPref.getString("name", "");
        String safe_pass = mPref.getString("safe_pass", "");
        String pass = DealPass.decodePass(safe_pass, name);
        return pass;
    }

    /**
     * 从本地的存储中读取出临时的的密码，用于登录失败后
     */
    public static String getTempPass(Context context) {
        String pass = getUserPass(context);
        if (pass == null || pass.equals("")) {
            return "";
        } else {
            try {
                pass = pass.substring(16);// 密码处理
            } catch (Exception e) {
                pass = "";
            }
        }
        return pass;
    }

}
