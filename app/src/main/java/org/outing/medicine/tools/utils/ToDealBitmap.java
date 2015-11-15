package org.outing.medicine.tools.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 位图处理辅助类
 *
 * @author Sun Yu Lin
 */
public class ToDealBitmap {

    /**
     * 压缩图片到指定大小
     *
     * @param bitmap      要压缩的图片
     * @param newWidth    压缩后新的宽度
     * @param newHeight   压缩后新的高度
     * @param isKeepRatio 是否保持图片宽高比例不变
     * @return 压缩后的图片
     */
    public static Bitmap zipBitmap(Bitmap bitmap, double newWidth,
                                   double newHeight, boolean isKeepRatio) {
        // 获取这个图片的宽和高
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        if (isKeepRatio) {// 保持宽高比例
            if (scaleWidth < scaleHeight)
                matrix.postScale(scaleWidth, scaleWidth);
            else
                matrix.postScale(scaleHeight, scaleHeight);
        } else {// 放弃宽高比例
            matrix.postScale(scaleWidth, scaleHeight);
        }
        Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) width,
                (int) height, matrix, true);
        return newbitmap;
    }

    /**
     * 保持宽高比压缩图片至指定宽度
     */
    public static Bitmap zipBitmapFitX(Bitmap bitmap, double newWidth) {
        // 获取这个图片的宽和高
        float width = bitmap.getWidth();
        if (width <= newWidth)
            return bitmap;
        float height = bitmap.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float ratio = ((float) newWidth) / width;
        // 缩放图片动作
        matrix.postScale(ratio, ratio);
        Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) width,
                (int) height, matrix, true);
        return newbitmap;
    }

    /**
     * 将图片写入指定路径
     *
     * @param bitmap   要写入的图片
     * @param format   图片格式，只支持"png"与"jpg"。
     * @param filepath 要写入的文件绝对路径
     * @return 是否成功
     */
    public static boolean writeToFile(Bitmap bitmap, String format,
                                      String filepath) {
        if (bitmap == null) {// 空位图直接返回
            return false;
        }
        try {// 将位图保存为指定文件
            FileOutputStream out = new FileOutputStream(filepath);
            if (format.equals("jpg"))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            else if (format.equals("png"))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            else {
                out.close();
                return false;
            }
            out.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 将bitmap从文件中读取
     *
     * @param filepath 要写入的文件绝对路径
     * @return bitmap
     */
    public static Bitmap getFromFile(String filepath) {
//
//		Bitmap bitmap=null;
//		try {
//			FileInputStream in=new FileInputStream(filepath);
//			bitmap=in.available();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return bitmap;
        Bitmap bitmap = null;
        try {
            File file = new File(filepath);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(filepath);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }


        return bitmap;
    }

    /**
     * 根据指定URL解析图片(如果是网络图片，必须放在多线程中)
     *
     * @param url 要解析的图片URL
     * @return 解析后的图片
     */
    public static Bitmap getBitmapFromURL(String url) {
        int IO_BUFFER_SIZE = 4 * 1024;
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(),
                    IO_BUFFER_SIZE);

            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            byte[] b = new byte[IO_BUFFER_SIZE];
            int read;
            while ((read = in.read(b)) != -1) {
                out.write(b, 0, read);
            }
            out.flush();

            byte[] data = dataStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    options);
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return bitmap;
    }

    public static Bitmap getNetBitmap(String url_str) {
        try {
            URL url = new URL(url_str);
            InputStream is = url.openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param
     */
    public static Bitmap getCroppedRoundBitmap(Bitmap bitmap) {
        // 创建一个带有特定宽度、高度和颜色格式的位图。
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 20;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
