package org.outing.medicine.tools;

import org.outing.medicine.tools.utils.DealPass;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用户名与密码存储与读取类
 * 
 * @author Sun Yu Lin
 */
public class ToDealUser {
	/** 密码相对存储位置 */
	private static final String USER_SAVE_PATH = "all.User";

	/**
	 * 存储用户名与密码（MD5）
	 */
	public static void saveUser(Context context, String name, String pass) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				USER_SAVE_PATH, Activity.MODE_PRIVATE).edit();
		String md5_pass = DealPass.getMD5(pass);
		String safe_pass = DealPass.encodePass(md5_pass, name);
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
