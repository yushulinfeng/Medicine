package org.outing.medicine.fun_remind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.outing.medicine.tools.file.FileTool;

import java.io.File;
import java.util.ArrayList;

public class RemindTool {
    private static final String REMIND_SD_PATH = "remind";
    private static final String REMIND_SAVE_PATH = "remind.drug";// 用药提醒列表
    private static final String TIMER_SAVE_PATH = "remind.timer_";// 闹钟列表（后面加id）
    private static final String REMIND_ICON_SD_PATH = "icon";//remind/icon

    /**
     * 重写提醒
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
     * 添加提醒
     */
    public static void addDrug(Context context, AnRemind new_drug) {
        if (new_drug == null)
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
     * 获取提醒
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
     * 获取提醒ID
     */
    public static ArrayList<String> getDrugID(Context context) {
        ArrayList<String> array = new ArrayList<String>();
        SharedPreferences pref = context.getSharedPreferences(
                REMIND_SAVE_PATH, Activity.MODE_PRIVATE);
        int count = pref.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String id = pref.getString("id" + i, "");
            array.add(id);
        }
        return array;
    }


    /**
     * 获取提醒数量
     */
    public static int getDrugCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                REMIND_SAVE_PATH, Activity.MODE_PRIVATE);
        int count = pref.getInt("count", 0);
        return count;
    }


    /**
     * 删除提醒
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
     * 修改提醒
     */
    public static void alterDrug(Context context, AnRemind drug) {
        ArrayList<AnRemind> array = getDrug(context);
        int index = -1;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getDrugId().equals(drug.getDrugId()))
                index = i;
        }
        if (index != -1) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(
                    REMIND_SAVE_PATH, Activity.MODE_PRIVATE).edit();//更快
            prefs.putString("id" + index, drug.getDrugId());
            prefs.putString("name" + index, drug.getDrugName());
            prefs.putString("text" + index, drug.getDrugText());
            prefs.commit();
        }
    }

    /**
     * 清空提醒
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


    ////////////////////////闹钟列表////////////////////////

    /**
     * 重写闹钟
     */
    public static void writeTimer(Context context, String drug_id, ArrayList<AnTimer> array) {
        int count = array.size();
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                TIMER_SAVE_PATH + drug_id, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count);
        AnTimer old_timer = null;
        for (int i = 0; i < count; i++) {
            old_timer = array.get(i);
            prefs.putString("id" + i, old_timer.getId());
            prefs.putString("name" + i, old_timer.getName());
            prefs.putString("text" + i, old_timer.getText());
            prefs.putInt("hour" + i, old_timer.getHour());
            prefs.putInt("min" + i, old_timer.getMinute());
            prefs.putInt("method" + i, old_timer.getMethod());
            prefs.putBoolean("enable" + i, old_timer.isEnable());
            prefs.putString("times" + i, old_timer.getTimes());
        }
        prefs.commit();
    }

    /**
     * 添加闹钟
     */
    public static void addTimer(Context context, String drug_id, AnTimer new_timer) {
        if (new_timer == null)
            return;
        ArrayList<AnTimer> array = getTimer(context, drug_id);
        int count = array.size();
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                TIMER_SAVE_PATH + drug_id, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count + 1);
        prefs.putString("id" + 0, new_timer.getId());// 最新放在最前面
        prefs.putString("name" + 0, new_timer.getName());
        prefs.putString("text" + 0, new_timer.getText());
        prefs.putInt("hour" + 0, new_timer.getHour());
        prefs.putInt("min" + 0, new_timer.getMinute());
        prefs.putInt("method" + 0, new_timer.getMethod());
        prefs.putBoolean("enable" + 0, new_timer.isEnable());
        prefs.putString("times" + 0, new_timer.getTimes());
        AnTimer old_timer = null;
        for (int i = 0; i < count; i++) {
            old_timer = array.get(i);
            prefs.putString("id" + (i + 1), old_timer.getId());
            prefs.putString("name" + (i + 1), old_timer.getName());
            prefs.putString("text" + (i + 1), old_timer.getText());
            prefs.putInt("hour" + (i + 1), old_timer.getHour());
            prefs.putInt("min" + (i + 1), old_timer.getMinute());
            prefs.putInt("method" + (i + 1), old_timer.getMethod());
            prefs.putBoolean("enable" + (i + 1), old_timer.isEnable());
            prefs.putString("times" + (i + 1), old_timer.getTimes());
        }
        prefs.commit();
    }

    /**
     * 获取闹钟
     */
    public static ArrayList<AnTimer> getTimer(Context context, String drug_id) {
        ArrayList<AnTimer> array = new ArrayList<AnTimer>();
        SharedPreferences pref = context.getSharedPreferences(
                TIMER_SAVE_PATH + drug_id, Activity.MODE_PRIVATE);
        int count = pref.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String id = pref.getString("id" + i, "");
            String name = pref.getString("name" + i, "");
            String text = pref.getString("text" + i, "");
            int hour = pref.getInt("hour" + i, -1);
            int min = pref.getInt("min" + i, -1);
            int method = pref.getInt("method" + i, 0);
            String times=pref.getString("times"+i,"0");
            AnTimer timer = new AnTimer(id, name, text, hour, min, method);
            timer.setEnable(pref.getBoolean("enable" + i, true));//默认可用
            timer.setTimes(times);
            array.add(timer);
        }
        return array;
    }

    /**
     * 删除闹钟
     */
    public static void deleteTimer(Context context, String drug_id, String timer_id) {
        ArrayList<AnTimer> array = getTimer(context, drug_id);
        int index = -1;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getId().equals(timer_id))
                index = i;
        }
        if (index != -1)
            array.remove(index);
        writeTimer(context, drug_id, array);
    }

    /**
     * 修改闹钟
     */
    public static void alterTimer(Context context, String drug_id, AnTimer timer) {
        ArrayList<AnTimer> array = getTimer(context, drug_id);
        int index = -1;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getId().equals(timer.getId()))
                index = i;
        }
        if (index != -1) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(
                    TIMER_SAVE_PATH + drug_id, Activity.MODE_PRIVATE).edit();
            prefs.putString("id" + index, timer.getId());// 最新放在最前面
            prefs.putString("name" + index, timer.getName());
            prefs.putString("text" + index, timer.getText());
            prefs.putInt("hour" + index, timer.getHour());
            prefs.putInt("min" + index, timer.getMinute());
            prefs.putInt("method" + index, timer.getMethod());
            prefs.putBoolean("enable" + index, timer.isEnable());
            prefs.putString("times" + index, timer.getTimes());
            prefs.commit();
        }
    }

    /**
     * 清空闹钟
     */
    public static void clearTimer(Context context, String drug_id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                TIMER_SAVE_PATH + drug_id, Activity.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.commit();
    }

    ////////////////////////刷新闹钟服务////////////////////////

    /**
     * 就是启动闹钟服务
     */
    public static void refreshTimer(Context context) {
        Intent intent = new Intent(context, ClockService.class);
        intent.putExtra("message", ClockService.REFRESH);
        context.startService(intent);
    }

}
