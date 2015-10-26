package org.outing.medicine.fun_tools;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

import org.outing.medicine.tools.ToDealBitmap;

import java.io.File;
import java.lang.reflect.Method;

//没用的先留着，不要删除
//可以写个锁屏，但是……再议。
public class WidgetImage {
    private static final String WALL_BACKUP_NAME = "wall.jpg";
    private static final String LOCK_BACKUP_NAME = "lock.jpg";

    public boolean showTextOnWallPaper(Context context, String line1, String line2) {
        boolean is_show = WidgetTool.getImageState(context);
        if (is_show)
            return false;
        Bitmap bitmap = getWallPaper(context);
        ToDealBitmap.writeToFile(bitmap, "jpg", getFileName(WALL_BACKUP_NAME));
        bitmap = drawText(context, bitmap, line1, line2);
        boolean state = setWallPaper(context, bitmap);
        WidgetTool.saveImageState(context, state);
        return state;
    }

    public boolean hideTextOnWallPaper(Context context) {
        boolean is_show = WidgetTool.getImageState(context);
        if (!is_show)
            return false;
        Bitmap bitmap = BitmapFactory.decodeFile(getFileName(WALL_BACKUP_NAME));
        boolean state = setWallPaper(context, bitmap);
        WidgetTool.saveImageState(context, !state);//!state
        return state;//state
    }

    public boolean showTextOnLockPaper(Context context, String line1, String line2) {
        boolean is_show = WidgetTool.getLockImageState(context);
        if (is_show)
            return false;
        Bitmap bitmap = getLockWallPaper(context);
        Log.e("EEE", "EEEEE" + "0000000");
        ToDealBitmap.writeToFile(bitmap, "jpg", getFileName(LOCK_BACKUP_NAME));
        Log.e("EEE", "EEEEE" + "11111");
        bitmap = drawText(context, bitmap, line1, line2);
        Log.e("EEE", "EEEEE" + "22222");
        boolean state = setLockWallPaper(context, bitmap);
        Log.e("EEE", "EEEEE" + "33333");
        WidgetTool.saveLockImageState(context, state);
        Log.e("EEE", "EEEEE" + "44444");
        return state;
    }

    public boolean hideTextOnLockPaper(Context context) {
        boolean is_show = WidgetTool.getLockImageState(context);
        if (!is_show)
            return false;
        Bitmap bitmap = BitmapFactory.decodeFile(getFileName(LOCK_BACKUP_NAME));
        boolean state = setLockWallPaper(context, bitmap);
        WidgetTool.saveLockImageState(context, !state);//!state
        return state;//state
    }

    ///////////////////////////分界线//////////////////////////////
    //暂时保留，以后可能用到
    private Bitmap getBitmap(Context context, String text) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Bitmap cacheBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels,
                Bitmap.Config.ARGB_8888);//全屏大小
        //这里最好先读取原来的图片
        int text_size = 40;
        int text_x = dm.widthPixels / 2 - text_size * text.length() / 2;
        int text_y = dm.heightPixels / 2;
        Canvas cacheCanvas = new Canvas();
        cacheCanvas.setBitmap(cacheBitmap);
        // cacheCanvas.drawColor(Color.WHITE);//全白背景
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(text_size);
        cacheCanvas.drawText(text, text_x, text_y, paint);
        return cacheBitmap;
    }

    private Bitmap drawText(Context context, Bitmap bitmap, String line1, String line2) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        //这里最好先读取原来的图片
        int text_size = 40;
        int text1_x = bitmap.getWidth() / 2 - text_size * line1.length() / 2;
        int text2_x = bitmap.getWidth() / 2 - text_size * line2.length() / 2;
        int text1_y = bitmap.getHeight() / 4;
        int text2_y = text1_y + text_size;
        Canvas cacheCanvas = new Canvas();
        Paint paint = new Paint();
        Bitmap bitmap_temp = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth(), bitmap.getHeight(), false);//复制备份
        bitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);//原图大小
        cacheCanvas.drawColor(Color.BLACK);//全黑背景
        cacheCanvas.setBitmap(bitmap);//图片太大会报错，所以使用全黑过渡
        try {
            cacheCanvas.drawBitmap(bitmap_temp, 0, 0, paint);
        } catch (Exception e) {
            cacheCanvas.drawColor(Color.BLACK);//全黑背景
        }
        paint.setColor(Color.WHITE);
        paint.setTextSize(text_size);
        cacheCanvas.drawText(line1, text1_x, text1_y, paint);
        cacheCanvas.drawText(line2, text2_x, text2_y, paint);
        return bitmap;
    }

    private Bitmap getWallPaper(Context context) {
        // 获取壁纸管理器
        WallpaperManager wallpaperManager = WallpaperManager
                .getInstance(context);
        // 获取当前桌面壁纸
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        // 将Drawable,转成Bitmap
        Bitmap bitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();
        return bitmap;
    }

    private Bitmap getLockWallPaper(Context context) {
        //锁屏壁纸难以获取，就截取当前桌面壁纸的第一帧吧
        Bitmap bitmap = getWallPaper(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (bitmap.getWidth() >= dm.widthPixels && bitmap.getHeight() >= dm.heightPixels)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, dm.widthPixels, dm.heightPixels);
        return bitmap;
    }

    private boolean setWallPaper(Context context, Bitmap bitmap) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            wallpaperManager.setBitmap(bitmap);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean setLockWallPaper(Context context, Bitmap bitmap) {
        //锁屏壁纸的设置却无法直接调用WallpaperManager，需要用到反射调用。
        try {
            WallpaperManager mWallManager = WallpaperManager.getInstance(context);
            Log.e("EEE", "EEEEE" + "AAAAAAA");
            Class class1 = mWallManager.getClass();//获取类名
            Log.e("EEE", "EEEEE" + "BBBBBBB");
            //获取设置锁屏壁纸的函数
            Method setWallPaperMethod = class1.getMethod("setBitmapToLockWallpaper", Bitmap.class);
            Log.e("EEE", "EEEEE" + "CCCCCCC");
            setWallPaperMethod.invoke(mWallManager, bitmap);
            Log.e("EEE", "EEEEE" + "DDDDDDD");
            return true;
        } catch (Exception e) {
            Log.e("EEE", "EEEEE" + "EEEEEEEEEE");
            return false;
        }
    }

    private String getFileName(String name) {
        return new File(WidgetTool.getSDSavepath(), name).getAbsolutePath();
    }
}
