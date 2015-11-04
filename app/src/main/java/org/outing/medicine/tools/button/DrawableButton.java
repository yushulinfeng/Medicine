package org.outing.medicine.tools.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * drawable与文本一起居中显示
 */
public class DrawableButton extends Button {

    public DrawableButton(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableButton(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            Drawable drawableTop = drawables[1];
            Drawable drawableRight = drawables[2];
            Drawable drawableBottom = drawables[3];
            if (drawableLeft != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableLeft.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
            if (drawableRight != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableRight.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                setPadding(0, 0, (int) (getWidth() - bodyWidth), 0);
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
            if (drawableTop != null) {
                float textHight = getPaint().measureText("　");//默认单行
                int drawablePadding = getCompoundDrawablePadding();
                int drawableHight = 0;
                drawableHight = drawableTop.getIntrinsicHeight();
                float bodyHight = textHight + drawableHight + drawablePadding;
                setPadding(0, 0, 0, (int) (getHeight() - bodyHight));
                canvas.translate(0, (getHeight() - bodyHight) / 2);
            }
            if (drawableBottom != null) {
                float textHight = getPaint().measureText("　");//默认单行
                int drawablePadding = getCompoundDrawablePadding();
                int drawableHight = 0;
                drawableHight = drawableBottom.getIntrinsicHeight();
                float bodyHight = textHight + drawableHight + drawablePadding;
                setPadding(0, 0, 0, (int) (getHeight() - bodyHight));
                canvas.translate(0, (getHeight() - bodyHight) / 2);
            }
        }
        super.onDraw(canvas);
    }
}