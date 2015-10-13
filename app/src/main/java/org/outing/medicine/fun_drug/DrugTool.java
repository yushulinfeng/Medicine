package org.outing.medicine.fun_drug;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.outing.medicine.tools.ToDealZip;
import org.outing.medicine.tools.file.FileTool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DrugTool {
    private static final int HISTORY_MAX_COUNT = 20;// 历史保存20条即可
    private static final String HISTORY_SAVE_PATH = "drug.history";// 历史
    private static final String COLLECT_SAVE_PATH = "drug.collect";// 收藏
    private static final String UNZIP_SAVE_PATH = "drug";// 前后不加/

    // //////////////////////////历史// //////////////////////////

    /**
     * 添加历史
     */
    public static void addHistory(Context context, AnDrug new_drug) {
        if (new_drug == null || new_drug.getName().equals(""))
            return;// 说明：调用时getCommonName()默认为""。
        ArrayList<AnDrug> array = getHistory(context);
        int count = array.size();
        for (int i = 0; i < count; i++) {
            if (array.get(i).getName().equals(new_drug.getName())) {
                return;//懒得排序了，直接返回吧
            }
        }
        if (count >= HISTORY_MAX_COUNT)
            count = HISTORY_MAX_COUNT - 1;// 确保不超过最大值，即舍弃时间最靠前的。
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                HISTORY_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count + 1);
        prefs.putString("name" + 0, new_drug.getName());// 最新放在最前面
        prefs.putString("com_name" + 0, new_drug.getCommonName());
        AnDrug old_drug = null;
        for (int i = 0; i < count; i++) {
            old_drug = array.get(i);
            prefs.putString("name" + (i + 1), old_drug.getName());
            prefs.putString("com_name" + (i + 1), old_drug.getCommonName());
        }
        prefs.commit();
    }

    /**
     * 获取历史
     */
    public static ArrayList<AnDrug> getHistory(Context context) {
        ArrayList<AnDrug> array = new ArrayList<AnDrug>();
        SharedPreferences pref = context.getSharedPreferences(
                HISTORY_SAVE_PATH, Activity.MODE_PRIVATE);
        int count = pref.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String name = pref.getString("name" + i, "");
            String com_name = pref.getString("com_name" + i, "");
            array.add(new AnDrug(name, com_name));
        }
        return array;
    }

    /**
     * 清空历史
     */
    public static void clearHistory(Context context) {// 暂时没用到
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                HISTORY_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.commit();
    }

    // //////////////////////////收藏// //////////////////////////

    /**
     * 重写收藏
     */
    public static void writeCollect(Context context, ArrayList<AnDrug> array) {
        int count = array.size();
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                COLLECT_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count);
        AnDrug old_drug = null;
        for (int i = 0; i < count; i++) {
            old_drug = array.get(i);
            prefs.putString("name" + i, old_drug.getName());
            prefs.putString("com_name" + i, old_drug.getCommonName());
        }
        prefs.commit();
    }

    /**
     * 添加收藏
     */
    public static void addCollect(Context context, AnDrug new_drug) {
        if (new_drug == null || new_drug.getName().equals(""))
            return;
        ArrayList<AnDrug> array = getHistory(context);
        int count = array.size();
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                COLLECT_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.putInt("count", count + 1);
        prefs.putString("name" + 0, new_drug.getName());// 最新放在最前面
        prefs.putString("com_name" + 0, new_drug.getCommonName());
        AnDrug old_drug = null;
        for (int i = 0; i < count; i++) {
            old_drug = array.get(i);
            prefs.putString("name" + (i + 1), old_drug.getName());
            prefs.putString("com_name" + (i + 1), old_drug.getCommonName());
        }
        prefs.commit();
    }

    /**
     * 获取收藏
     */
    public static ArrayList<AnDrug> getCollect(Context context) {
        ArrayList<AnDrug> array = new ArrayList<AnDrug>();
        SharedPreferences pref = context.getSharedPreferences(
                COLLECT_SAVE_PATH, Activity.MODE_PRIVATE);
        int count = pref.getInt("count", 0);
        for (int i = 0; i < count; i++) {
            String name = pref.getString("name" + i, "");
            String com_name = pref.getString("com_name" + i, "");
            array.add(new AnDrug(name, com_name));
        }
        return array;
    }

    /**
     * 是否已经收藏
     */
    public static boolean isCollected(Context context, AnDrug new_drug) {
        ArrayList<AnDrug> array = getHistory(context);
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getName().equals(new_drug.getName())
                    && array.get(i).getCommonName()
                    .equals(new_drug.getCommonName()))
                return true;
        }
        return false;
    }

    /**
     * 删除收藏
     */
    public static boolean deleteCollect(Context context, AnDrug drug) {
        ArrayList<AnDrug> array = getHistory(context);
        int index = -1;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getName().equals(drug.getName())
                    && array.get(i).getCommonName()
                    .equals(drug.getCommonName()))
                index = i;
        }
        if (index != -1)
            array.remove(index);
        writeCollect(context, array);
        return false;
    }

    /**
     * 清空收藏
     */
    public static void clearCollect(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                COLLECT_SAVE_PATH, Activity.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.commit();
    }

    // //////////////////////////文件解压缩// //////////////////////////
    public static File getUnzipFile(Context context, boolean only_father_path) {
        try {
            File root_path = FileTool.getBaseSDCardPath();
            if (root_path == null) return null;
            File unzip_path = new File(root_path, UNZIP_SAVE_PATH);
            File unzip_file = new File(unzip_path, "drug.xml");
            if (only_father_path)
                return unzip_path;
            return unzip_file;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean writeUnzip(Context context) {
        try {
            File parent = getUnzipFile(context, true);
            if (!parent.exists())
                parent.mkdirs();
            File copy_file = new File(parent, "drug.zip");
            copyFile(context, "drug/drug.zip", copy_file);
            ToDealZip.unzipFile(copy_file.getAbsolutePath(),
                    getUnzipFile(context, true).getAbsolutePath());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // 将文件从assets复制到存储卡，不处理异常
    private static void copyFile(Context context, String resName,
                                 File targetFile) throws IOException {
        int temp = 0;
        InputStream in = null;
        FileOutputStream out = null;
        in = context.getResources().getAssets().open(resName);
        out = new FileOutputStream(targetFile);
        BufferedInputStream is = new BufferedInputStream(in);
        BufferedOutputStream os = new BufferedOutputStream(out);
        /* 个人吐槽一句，自从写了这个程序，终于明白缓冲区的神奇作用了！复制了一个2M的文件用不用缓冲区时间上大约是10倍的关系 */
        while ((temp = is.read()) != -1) {
            os.write(temp);
        }
        is.close();
        os.close();
        in.close();
        out.close();
    }
}
