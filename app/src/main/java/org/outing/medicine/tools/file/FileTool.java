package org.outing.medicine.tools.file;

import org.outing.medicine.logic.AnContact;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class FileTool {
	private static final String CONTACT_SAVE_PATH = "contact.info";
	private static final String CONTACT_IMG_SAVE_PATH = "contact_img";

	public static void saveContact(Context context, AnContact contact,
			int location) {
		String real_path = CONTACT_SAVE_PATH + location;
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				real_path, Activity.MODE_PRIVATE).edit();
		// prefs.putString("filename", filename);
		prefs.commit();
	}

	public static String getContact(Context context, int location) {
		String real_path = CONTACT_SAVE_PATH + location;
		SharedPreferences mPref = context.getSharedPreferences(real_path,
				Activity.MODE_PRIVATE);
		String filename = mPref.getString("filename", "");
		return filename;
	}
}
