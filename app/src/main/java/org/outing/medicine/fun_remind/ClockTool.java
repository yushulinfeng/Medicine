package org.outing.medicine.fun_remind;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ClockTool {
    private static final String RING_SAVE_PATH = "remind.ring";// 就用这个名字
    private static final String TEMP_SAVE_PATH = "remind.temp";
    private static final String STATE_SAVE_PATH = "remind.state";
    private static final String LOG_SAVE_PATH = "remind.log";//程序内部存储
    //记录可能较多，建议只是临时存储，正式存储还是SD卡（建议本地只存储log即可）
    //断网活该策略，建议本地什么都不存了

    /////////////////////闹钟提醒/////////////////////

    /**
     * 重写闹钟提醒
     */
    public static void writeRing(Context context, ArrayList<AnRing> array) {
        int count = array.size();
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                RING_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count);
        AnRing old_drug = null;
        for (int i = 0; i < count; i++) {
            old_drug = array.get(i);
            prefs.putString("id_remind" + i, old_drug.remind.getDrugId());
            prefs.putString("name_remind" + i, old_drug.remind.getDrugName());
            prefs.putString("text_remind" + i, old_drug.remind.getDrugText());
            prefs.putString("id" + i, old_drug.timer.getId());
            prefs.putString("name" + i, old_drug.timer.getName());
            prefs.putString("text" + i, old_drug.timer.getText());
            prefs.putInt("hour" + i, old_drug.timer.getHour());
            prefs.putInt("min" + i, old_drug.timer.getMinute());
            prefs.putInt("method" + i, old_drug.timer.getMethod());
            prefs.putBoolean("enable" + i, old_drug.timer.isEnable());
            prefs.putString("times" + i, old_drug.timer.getTimes());
        }
        prefs.commit();
    }

    /**
     * 获取闹钟提醒
     */
    public static ArrayList<AnRing> getRing(Context context) {
        ArrayList<AnRing> array = new ArrayList<AnRing>();
        SharedPreferences pref = context.getSharedPreferences(
                RING_SAVE_PATH, Activity.MODE_PRIVATE);
        int count = pref.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String id = pref.getString("id_remind" + i, "");
            String name = pref.getString("name_remind" + i, "");
            String text = pref.getString("text_remind" + i, "");
            AnRemind remind = new AnRemind(id, name, text);
            String id0 = pref.getString("id" + i, "");
            String name0 = pref.getString("name" + i, "");
            String text0 = pref.getString("text" + i, "");
            int hour = pref.getInt("hour" + i, -1);
            int min = pref.getInt("min" + i, -1);
            int method = pref.getInt("method" + i, AddTimer.REMIND_DEFAULT);
            String times = pref.getString("times" + i, "0");
            AnTimer timer = new AnTimer(id0, name0, text0, hour, min, method);
            timer.setEnable(pref.getBoolean("enable" + i, true));//默认可用
            timer.setTimes(times);
            array.add(new AnRing(remind, timer));
        }
        return array;
    }

    /**
     * 清空提醒
     */
    public static void clearRing(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                RING_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.commit();
    }


    /////////////////////10分钟后提醒/////////////////////

    public static void writeTempRing(Context context, ArrayList<AnRing> array) {
        int count = array.size();
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                TEMP_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count);
        AnRing old_drug = null;
        for (int i = 0; i < count; i++) {
            old_drug = array.get(i);
            prefs.putString("id_remind" + i, old_drug.remind.getDrugId());
            prefs.putString("name_remind" + i, old_drug.remind.getDrugName());
            prefs.putString("text_remind" + i, old_drug.remind.getDrugText());
            prefs.putString("id" + i, old_drug.timer.getId());
            prefs.putString("name" + i, old_drug.timer.getName());
            prefs.putString("text" + i, old_drug.timer.getText());
            prefs.putInt("hour" + i, old_drug.timer.getHour());
            prefs.putInt("min" + i, old_drug.timer.getMinute());
            prefs.putInt("method" + i, old_drug.timer.getMethod());
            prefs.putBoolean("enable" + i, old_drug.timer.isEnable());
            prefs.putString("times" + i, old_drug.timer.getTimes());
        }
        prefs.commit();
    }

    public static ArrayList<AnRing> getTempRing(Context context) {
        ArrayList<AnRing> array = new ArrayList<AnRing>();
        SharedPreferences pref = context.getSharedPreferences(
                TEMP_SAVE_PATH, Activity.MODE_PRIVATE);
        int count = pref.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String id = pref.getString("id_remind" + i, "");
            String name = pref.getString("name_remind" + i, "");
            String text = pref.getString("text_remind" + i, "");
            AnRemind remind = new AnRemind(id, name, text);
            String id0 = pref.getString("id" + i, "");
            String name0 = pref.getString("name" + i, "");
            String text0 = pref.getString("text" + i, "");
            int hour = pref.getInt("hour" + i, -1);
            int min = pref.getInt("min" + i, -1);
            int method = pref.getInt("method" + i, AddTimer.REMIND_DEFAULT);
            String times = pref.getString("times" + i, "0");
            AnTimer timer = new AnTimer(id0, name0, text0, hour, min, method);
            timer.setEnable(pref.getBoolean("enable" + i, true));//默认可用
            timer.setTimes(times);
            array.add(new AnRing(remind, timer));
        }
        return array;
    }

    public static void deleteTempRing(Context context, ArrayList<String> del_timer_ids) {
        ArrayList<AnRing> array = getTempRing(context);
        String ring_id_temp = "";
        for (int i = 0; i < array.size(); i++) {
            ring_id_temp = array.get(i).timer.getId();
            for (int j = 0; j < del_timer_ids.size(); j++) {//只比较timer_id即可，理论上不会重复
                if (ring_id_temp.equals(del_timer_ids.get(j))) {
                    array.remove(i);
                    del_timer_ids.remove(j);//减少运算量
                    break;
                }
            }
        }
        clearTempRing(context);
        writeTempRing(context, array);
    }

    public static void clearTempRing(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                TEMP_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.commit();
    }

    /////////////////////用药LOG/////////////////////

    private static void saveCardFile(Context context, String filepath,
                                     String string) {
        FileOutputStream fos = null;
        try {// 写入空排行
            fos = context.openFileOutput(filepath, Activity.MODE_PRIVATE);
            fos.write((string).getBytes());
            fos.flush();// 清除缓存
            fos.close();// 一般关闭要写在finally中
        } catch (Exception e) {
        }
    }

    private static String getCardFile(Context context, String filepath) {
        FileInputStream fis = null;
        String string = " ";
        byte[] buff = null;
        try {
            fis = context.openFileInput(filepath);
            buff = new byte[fis.available()];
            fis.read(buff);
            fis.close();// 这个建议写在finally中，我为了省力
            string = new String(buff);// 转换为字符串
        } catch (Exception e) {
            string = " ";// 出错就用空记录
        }
        return string;
    }

    private static void cleanCardFile(Context context, String filepath) {
        FileOutputStream fos = null;
        try {// 写入空排行
            fos = context.openFileOutput(filepath, Activity.MODE_PRIVATE);
            fos.write((" ").getBytes());
            fos.flush();// 清除缓存
            fos.close();// 一般关闭要写在finally中
        } catch (Exception e) {
        }
    }

    public static void saveLog(Context context, String log) {
        String history = getLog(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        String time = sdf.format(new Date());
        String string = history + "\n\n" + time + "\n" + log;
        saveCardFile(context, LOG_SAVE_PATH, string);
    }

    public static String getLog(Context context) {
        return getCardFile(context, LOG_SAVE_PATH);
    }

    public static void cleanLog(Context context) {
        cleanCardFile(context, LOG_SAVE_PATH);
    }

}
