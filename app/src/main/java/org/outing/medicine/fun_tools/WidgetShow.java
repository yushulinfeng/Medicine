package org.outing.medicine.fun_tools;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.outing.medicine.R;
import org.outing.medicine.main_main.MainActivity;

public class WidgetShow extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        doUpdate(context, appWidgetManager);
    }

    // 通知更新
    public static void updatewidget(Context context) {
        AppWidgetManager manager0 = AppWidgetManager.getInstance(context);
        doUpdate(context, manager0);
    }

    private static void doUpdate(Context context, AppWidgetManager appWidgetManager) {
        // 获取相关项
        RemoteViews remote0 = new RemoteViews(context.getPackageName(),
                R.layout.wid_show);
        ComponentName name0 = new ComponentName(context, WidgetShow.class);
        // 监听
        Intent int_main = new Intent(context, MainActivity.class);
        PendingIntent pend_main = PendingIntent.getActivity(context, 0,
                int_main, 0);
        remote0.setOnClickPendingIntent(R.id.open_apk_wid_layout, pend_main);
        //设置文字
        String text = WidgetTool.getWidText(context);
        if (text.equals("")) text = "老友网";
        remote0.setTextViewText(R.id.wid_show_tv, text);
        // 注册更新
        appWidgetManager.updateAppWidget(name0, remote0);
    }
}
