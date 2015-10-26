package org.outing.medicine.fun_tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.outing.medicine.tools.file.FileTool;

import java.io.File;

public class WidgetTool {
    private static final String WID_IMAGE_STATE_SAVE_PATH = "wid.image_state";
    private static final String WID_TEXT_SAVE_PATH = "wid.text";
    private static final String WID_SDCARD_SAVE_PATH = "wid_image";

    public static File getSDSavepath() {
        File root_path = FileTool.getBaseSDCardPath();
        if (root_path == null)
            return null;
        File save_path = new File(root_path, WID_SDCARD_SAVE_PATH);
        if (!save_path.exists())
            save_path.mkdirs();
        return save_path;
    }

    public static boolean getImageState(Context context) {
        SharedPreferences mPref = context.getSharedPreferences(WID_IMAGE_STATE_SAVE_PATH,
                Activity.MODE_PRIVATE);
        boolean first = mPref.getBoolean("state", false);
        return first;
    }

    public static void saveImageState(Context context, boolean state) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                WID_IMAGE_STATE_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putBoolean("state", state);
        prefs.commit();
    }


    public static boolean getLockImageState(Context context) {
        SharedPreferences mPref = context.getSharedPreferences(WID_IMAGE_STATE_SAVE_PATH,
                Activity.MODE_PRIVATE);
        boolean first = mPref.getBoolean("lock_state", false);
        return first;
    }

    public static void saveLockImageState(Context context, boolean state) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                WID_IMAGE_STATE_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putBoolean("lock_state", state);
        prefs.commit();
    }

    public static String getWidText(Context context) {
        SharedPreferences mPref = context.getSharedPreferences(WID_TEXT_SAVE_PATH,
                Activity.MODE_PRIVATE);
        String text = mPref.getString("text", "");
        return text;
    }

    public static void saveWidText(Context context, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                WID_TEXT_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putString("text", text);
        prefs.commit();
    }
}
