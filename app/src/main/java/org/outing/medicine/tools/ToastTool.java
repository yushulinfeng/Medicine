package org.outing.medicine.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.outing.medicine.R;

public class ToastTool {

    public static void showToast(Context context, String text) {
        if (context == null || text == null) return;
        //使用自定义布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast_show, null);
        TextView message = (TextView) toastRoot.findViewById(R.id.toast_show_tv);
        message.setText(text);
        //显示
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }

}
