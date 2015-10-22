package org.outing.medicine.tools.file;

import android.os.Environment;

import java.io.File;

/**
 * 总的文件管理类
 */
public class FileTool {
    //SharedPreferences注册

    //
    private static final String BASE_SD_PATH = "OldFriend";

    /**
     * 获取本软件在存储卡的基本路径
     */
    public static File getBaseSDCardPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File root_path = new File(
                        Environment.getExternalStorageDirectory(),
                        BASE_SD_PATH);// 存在无卡、无权风险
                if (!root_path.exists())
                    root_path.mkdirs();
                return root_path;
            } catch (Exception e) {
                return null;//禁止权限在此处捕获
            }
        }
        return null;
    }
}
