package org.outing.medicine.start.tool;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.outing.medicine.tools.utils.ToDealPass;

public class UserTool {
	private static final String FIRSTRUN_SAVE_PATH = "all.firstrun";
	private static final String USER_SAVE_PATH = "all.user";

	/**
	 * 判断用户是否为首次运行
	 */
	public static boolean isFirstRun(Context context) {
		SharedPreferences mPref = context.getSharedPreferences(
				FIRSTRUN_SAVE_PATH, Activity.MODE_PRIVATE);
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

	/**
	 * 保存账户密码（传入原始密码即可，方法中将进行加密）
	 */
	public static void saveUser(Context context, String name, String pass) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				USER_SAVE_PATH, Activity.MODE_PRIVATE).edit();
		pass = ToDealPass.encodePass(pass, name);// 使用用户名加密
		prefs.putString("name", name);
		prefs.putString("pass", pass);
		prefs.commit();
	}

	/**
	 * 获取账户密码
	 */
	public static String[] getUser(Context context) {
		SharedPreferences mPref = context.getSharedPreferences(USER_SAVE_PATH,
				Activity.MODE_PRIVATE);
		String name = mPref.getString("name", "");// 判断""
		String pass = mPref.getString("pass", "");
		if (!name.equals("") && !pass.equals(""))
			pass = ToDealPass.decodePass(pass, name);
		return new String[] { name, pass };
	}

}
