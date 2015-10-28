package org.outing.medicine.fun_remind;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;

import org.outing.medicine.R;


public class ClockRing {
    Vibrator vibrator = null;//振动器
    MediaPlayer player = null;
    private Context context = null;
    private boolean do_vibrate = true;

    public ClockRing(Context context) {
        this.context = context;
    }

    // 标准提醒
    public void startRing() {
        //声音初始化
        player = MediaPlayer.create(context, R.raw.alarm);
        player.setLooping(true);
        try {
            player.prepare();
        } catch (Exception e) {
        }
        player.start();//这句的警告不用管
    }

    public void stopRing() {
        if (player != null)
            try {
                player.pause();
                player.stop();
                player.release();// 释放资源
                player = null;
            } catch (Exception e) {
            }
    }

    // 标准震动
    public void startVibrate() {
        // 震动初始化
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        do_vibrate = true;
        new Thread() {//开多线程
            public void run() {
                while (do_vibrate) {
                    vibrator.vibrate(400);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    public void stopVibrate() {
        do_vibrate = false;
    }

    public void sendNote(String title, String text, String dia_show) {
        // 通知显示
        NotificationManager noteman1 = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification note1 = new Notification();
        note1.icon = R.mipmap.ic_launcher;
        note1.tickerText = title;// 翻动显示
        note1.when = System.currentTimeMillis();
        note1.defaults = Notification.DEFAULT_ALL;// 访问灯光、震动需要权限
        note1.flags = Notification.FLAG_AUTO_CANCEL;// 点击消失
        Intent intent = new Intent(context, ClockDialog.class);// 点击启动主程序
        //添加数据//此处不必理会记录写入等问题，因为我已经取消了它的显示//使用它需要修改AnRing存储的传递模式
        intent.putExtra("method", ClockDialog.METHOD_NOTE_CLICK);
        intent.putExtra("text", dia_show);
        PendingIntent pend = PendingIntent.getActivity(context, 0, intent, 0);
        note1.setLatestEventInfo(context, title, text, pend);// 固定显示
        noteman1.notify(0, note1);
    }

}