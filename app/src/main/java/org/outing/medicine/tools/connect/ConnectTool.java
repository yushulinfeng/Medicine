package org.outing.medicine.tools.connect;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ConnectTool {
	private static final String COOKIE_SAVE_PATH = "connect_cookie";

	public static void saveCookie(Context context, String cookie) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				COOKIE_SAVE_PATH, Activity.MODE_PRIVATE).edit();
		prefs.putString("cookie", cookie);
		prefs.commit();
	}

	public static String getCookie(Context context) {
		SharedPreferences mPref = context.getSharedPreferences(
				COOKIE_SAVE_PATH, Activity.MODE_PRIVATE);
		String cookie = mPref.getString("cookie", null);
		return cookie;
	}

}
