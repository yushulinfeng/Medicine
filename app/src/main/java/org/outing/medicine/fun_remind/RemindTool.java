package org.outing.medicine.fun_remind;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.outing.medicine.tools.file.FileTool;

import java.io.File;
import java.util.ArrayList;

public class RemindTool {
    private static final String REMIND_SAVE_PATH = "remind.drug";// 用药提醒列表
    private static final String REMIND_SD_PATH = "remind";
    private static final String REMIND_ICON_SD_PATH = "icon";//remind/icon

    /**
     * 重写收藏
     */
    public static void writeDrug(Context context, ArrayList<AnRemind> array) {
        int count = array.size();
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                REMIND_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count);
        AnRemind old_drug = null;
        for (int i = 0; i < count; i++) {
            old_drug = array.get(i);
            prefs.putString("id" + i, old_drug.getDrugId());
            prefs.putString("name" + i, old_drug.getDrugName());
            prefs.putString("text" + i, old_drug.getDrugText());
        }
        prefs.commit();
    }

    /**
     * 添加收藏
     */
    public static void addDrug(Context context, AnRemind new_drug) {
        if (new_drug == null || new_drug.equals(""))
            return;
        ArrayList<AnRemind> array = getDrug(context);
        int count = array.size();
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                REMIND_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count + 1);
        prefs.putString("id" + 0, new_drug.getDrugId());// 最新放在最前面
        prefs.putString("name" + 0, new_drug.getDrugName());// 最新放在最前面
        prefs.putString("text" + 0, new_drug.getDrugText());// 最新放在最前面
        AnRemind old_drug = null;
        for (int i = 0; i < count; i++) {
            old_drug = array.get(i);
            prefs.putString("id" + (i + 1), old_drug.getDrugId());
            prefs.putString("name" + (i + 1), old_drug.getDrugName());
            prefs.putString("text" + (i + 1), old_drug.getDrugText());
        }
        prefs.commit();
    }

    /**
     * 获取收藏
     */
    public static ArrayList<AnRemind> getDrug(Context context) {
        ArrayList<AnRemind> array = new ArrayList<AnRemind>();
        SharedPreferences pref = context.getSharedPreferences(
                REMIND_SAVE_PATH, Activity.MODE_PRIVATE);
        int count = pref.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String id = pref.getString("id" + i, "");
            String name = pref.getString("name" + i, "");
            String text = pref.getString("text" + i, "");
            array.add(new AnRemind(id, name, text));
        }
        return array;
    }


    /**
     * 删除收藏
     */
    public static boolean deleteDrug(Context context, AnRemind drug) {
        ArrayList<AnRemind> array = getDrug(context);
        int index = -1;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getDrugId().equals(drug.getDrugId()))
                index = i;
        }
        if (index != -1)
            array.remove(index);
        writeDrug(context, array);
        return false;
    }

    /**
     * 清空收藏
     */
    public static void clearDrug(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                REMIND_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.commit();
    }

    /**
     * 获取图像存储路径
     */
    public static File getIconSDPath() {
        File root = FileTool.getBaseSDCardPath();
        if (root == null) return null;
        File path = new File(root, REMIND_SD_PATH);
        File icon_path = new File(path, REMIND_ICON_SD_PATH);
        try {
            if (!icon_path.exists()) icon_path.mkdirs();
        } catch (Exception e) {
            return null;
        }
        return icon_path;
    }
}
