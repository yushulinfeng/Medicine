package org.outing.medicine.tools.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 带斜线的TextView控件<br/>
 * 宽、高建议相等的定值<br/>
 * 通过   android:drawableTop="#e87a19" 指定斜线颜色（不指定默认橙色）<br/>
 * 通过   android:drawableBottom="#000" 指定文字颜色（不指定默认黑色）<br/>
 * 通过   android:drawablePadding="4dp" 指定文字偏移（不指定默认为0dp）<br/>
 * textColor属性被禁用，设置了也没用。<br/>
 * 1-3个字。0个字的话，将隐藏控件。多了的话，截取前三个。
 */
public class SlopeTextView extends TextView {
    public SlopeTextView(Context context) {
        super(context);
    }

    public SlopeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlopeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String text_temp = getText().toString();
        if (text_temp.length() > 3) text_temp = text_temp.substring(0, 3);
        setTextColor(Color.argb(0, 0, 0, 0));
        if (text_temp.length() == 0) return;
        // setText("");//将造成循环

        int x1 = 0, y1 = 0, x2 = getWidth(), y2 = getHeight();
        int x3 = x2, y3 = y2 / 2, x4 = x2 / 2, y4 = y1;

        int pad = getCompoundDrawablePadding();
        //画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);//反锯齿
        paint.setStyle(Paint.Style.FILL);
        try {//斜线颜色
            Drawable drawableTop = getCompoundDrawables()[1];
            paint.setColor(((ColorDrawable) drawableTop).getColor());//强制转换
        } catch (Exception e) {
            paint.setColor(Color.rgb(232, 122, 25));
        }

        //绘图
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.close();
        canvas.drawPath(path, paint);

        try {//文字颜色
            Drawable drawableBottom = getCompoundDrawables()[3];
            paint.setColor(((ColorDrawable) drawableBottom).getColor());//强制转换
        } catch (Exception e) {
            paint.setColor(Color.BLACK);
        }
        paint.setTextSize(getTextSize());
        int text_length = text_temp.length();
        switch (text_length) {//0就不用处理了
            case 1:
                canvas.drawText(text_temp, x2 / 2 + pad, y2 / 2 - pad, paint);
                break;
            case 2:
                canvas.drawText(text_temp.charAt(0) + "", x2 / 3 + pad, y2 / 3 - pad, paint);
                canvas.drawText(text_temp.charAt(1) + "", x2 / 3 * 2 + pad, y2 / 3 * 2 - pad, paint);
                break;
            case 3:
                canvas.drawText(text_temp.charAt(0) + "", x2 / 4 + pad, y2 / 4 - pad, paint);
                canvas.drawText(text_temp.charAt(1) + "", x2 / 4 * 2 + pad, y2 / 4 * 2 - pad, paint);
                canvas.drawText(text_temp.charAt(2) + "", x2 / 4 * 3 + pad, y2 / 4 * 3 - pad, paint);
                break;
        }
    }
}
