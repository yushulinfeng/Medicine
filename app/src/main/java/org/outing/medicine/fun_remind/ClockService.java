package org.outing.medicine.fun_remind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//吐槽一句：这个逻辑真的想了好久，写了好久啊
//考虑添加开机启动，log等最后再删除
public class ClockService extends Service implements Runnable {
    public static final int CLOCK = 0, BOOT = 1, REFRESH = 2, STOP = 3;
    private static final int TIMEWAIT = 20;// 闹钟延时20秒
    private static final int ONEDAY = 24 * 60 * 60;// 一天的秒数
    private Context context;
    private int message;
    private ArrayList<AnRing> array;
    private ArrayList<AnRing> array_now;

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("EEE", "EEEE--service  " + "START");
        context = getApplication();// 获取Context
        message = getMessage(intent);// 获取命令
        Log.e("EEE", "EEEE--service  " + "MESSAGE:" + message);
        new Thread(this).start();// 开启线程
        return super.onStartCommand(intent, flags, startId);
    }

    private int getMessage(Intent intent) {
        try {//默认使用CLOCK，不准改成refresh
            int message = intent.getIntExtra("message", CLOCK);
            return message;
        } catch (Exception e) {
            return CLOCK;
        }
    }

    @Override
    public void run() {
        Log.e("EEE", "EEEE--service  " + "RUN");
        cancelTimer();
        // 处理命令
        boolean should_remind = false;
        switch (message) {//偷懒的写法，省空间
            case CLOCK:// CLOCK肯定由时钟启动，需要进行处理
                should_remind = true;
            case BOOT:// 开机启动（并不处理）
            case REFRESH:// 刷新命令
                break;
            case STOP:// 终止命令
                stopSelf();
                return;//这句使之不能写在方法中
        }
        Log.e("EEE", "EEEE--service  " + "SWITCH:remind-" + should_remind);
        //设定新闹钟
        long mill_now = System.currentTimeMillis();// 保存当前时间，使结果更准确
        initArray();//初始化所有提醒的数组
        int second = getNextSecond();// 获取下次的签到时间
        Log.e("EEE", "EEEE--sevice  " + "SECOND:" + second);
        setTimer(mill_now, second);//设定闹钟
        // 如果需要提醒
        if (should_remind) {
            doRemind();//进行提醒
        }
        Log.e("EEE", "EEEE--sevice  " + "BEFOR-END");
        stopSelf();//考虑服务自动重启问题，暂且把它stop了
        Log.e("EEE", "EEEE--service  " + "END");
        return;
    }


    private int getNextSecond() {
        // 获取当前时间
        int hournow, minnow, secnow;
        Time timenow = new Time();
        timenow.setToNow();
        hournow = timenow.hour;
        minnow = timenow.minute;
        secnow = timenow.second;
        // 初始化当前提醒项，获取之后最近的提醒项
        if (array.size() == 0)
            return 0;
        boolean should_temp_clear=false;
        AnRing ring_temp = null, ring_next = null;
        int location_now = hournow * 100 + minnow;
        for (int i = 0; i < array.size(); i++) {
            ring_temp = array.get(i);
            if (ring_temp.getLocation() < location_now) {
                continue;
            } else if (ring_temp.getLocation() == location_now) {
                array_now.add(ring_temp);
                if(ring_temp.timer.getTimes().equals("1"))
                    should_temp_clear=true;
            } else if (ring_temp.getLocation() > location_now) {
                ring_next = ring_temp;//这个肯定是最近的
                break;
            }
        }
        if (ring_next == null) {//最晚的一个闹钟已触发，没有更晚的了
            ring_next = array.get(0);//已确认size != 0。
        }
        if(should_temp_clear){//如果加载了单次闹钟的话，就清空单次闹钟临时列表
            ClockTool.clearTempRing(this);
        }
        //计算下次响铃时间
        int hour = ring_next.timer.getHour(), min = ring_next.timer.getMinute();
        int result = 0;
        result = (hour - hournow) * 60 * 60 + (min - minnow - 1) * 60
                + (60 - secnow);// 借位法精确到秒
        if (result <= 0) // 明天
            result += ONEDAY;
        return result;
    }

    private void doRemind() {
        Log.e("EEE", "EEEE--sevice  " + "REMIND-START");
        if (array_now.size() == 0)
            return;
        List<Integer> method_list = new ArrayList<Integer>();//不能用Integer
        int method_temp = 0;
        String text_temp = "--总计" + array_now.size() + "种药物--";
        for (int i = 0; i < array_now.size(); i++) {//不要引入ringtemp
            //同时响铃，如果提醒方式不同，该怎么办？
            //建议响铃、震动进行合并。其他提醒将覆盖通知栏提醒。
            //即包含通知栏则视为不包含（都响铃了还发什么通知），仅有通知栏则发送通知。
            //个人说一句，通知栏主要是为了我后期自己用的。允许你删去。
            text_temp += "\n";//代码是写给人看的
            text_temp += "\n药品：" + array_now.get(i).remind.getDrugName();
            if (!array_now.get(i).remind.getDrugText().equals(""))//可以没有
                text_temp += "\n备注：" + array_now.get(i).remind.getDrugText();
            text_temp += "\n提醒：" + array_now.get(i).timer.getName();
            method_list.add(array_now.get(i).timer.getMethod());
            Log.e("EEE", "EEEE--sevice  " + "METHOD:LOG:" + array_now.get(i).timer.getMethod());
        }
        //这个逻辑非常清晰，很好，不要乱改
        if (method_list.size() == 1)
            method_temp = method_list.get(0);
        else {
            boolean bool_ring = false, bool_vib = false;
            method_break:
            for (int i = 0; i < method_list.size(); i++) {
                method_temp = method_list.get(i);
                switch (method_temp) {
                    case ClockDialog.METHOD_R_V:
                        method_temp = ClockDialog.METHOD_R_V;
                        break method_break;
                    case ClockDialog.METHOD_NOTE:
                        break;
                    case ClockDialog.METHOD_RING:
                        bool_ring = true;
                        break;
                    case ClockDialog.METHOD_VIBRATE:
                        bool_vib = true;
                        break;
                }
            }
            if (bool_ring && bool_vib) method_temp = ClockDialog.METHOD_R_V;
            else if (bool_ring) method_temp = ClockDialog.METHOD_RING;
            else if (bool_vib) method_temp = ClockDialog.METHOD_VIBRATE;
        }
        Log.e("EEE", "EEEE--sevice  " + "METHOD-COUNT:" + method_list.size());
        Log.e("EEE", "EEEE--sevice  " + "METHOD:" + method_temp);
        Log.e("EEE", "EEEE--sevice  " + "TEXT:" + text_temp.replace("\n", " "));
        //把array_now写入share传递,便于存储用药后的状态
        ClockTool.writeRing(context, array_now);
        //触发提醒
        Intent intent = new Intent(context, ClockDialog.class);
        intent.putExtra("method", method_temp);
        intent.putExtra("text", text_temp);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initArray() {
        //其他列表
        array_now = new ArrayList<AnRing>();
        //将所有提醒添加到列表中
        array = new ArrayList<AnRing>();
        ArrayList<AnRemind> remind_array = RemindTool.getDrug(context);
        ArrayList<AnTimer> timer_array = null;
        AnRemind remind_temp = null;
        AnTimer timer_temp = null;
        AnRing ring_temp = null;
        for (int i = 0; i < remind_array.size(); i++) {
            remind_temp = remind_array.get(i);
            timer_array = RemindTool.getTimer(context, remind_temp.getDrugId());
            for (int j = 0; j < timer_array.size(); j++) {
                timer_temp = timer_array.get(j);
                Log.e("EEE", "EEEE--sevice  " + "METHOD_ADD:" + timer_temp.getMethod());
                ring_temp = new AnRing(remind_temp, timer_temp);
                Log.e("EEE", "EEEE--sevice  " + "METHOD_ADD_ADD:" + ring_temp.timer.getMethod());
                array.add(ring_temp);
            }
        }
        //处理推迟十分钟的闹钟////////////////////////////////////////////////////
        ArrayList<AnRing> array_temp = ClockTool.getTempRing(this);
        for (int i = 0; i < array_temp.size(); i++)
            array.add(array_temp.get(i));
        //按照时间排序从小到大
        if (remind_array.size() != 0)
            Collections.sort(array);
    }

    private void cancelTimer() {
        // 闹钟取消
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent int0 = new Intent(this, ClockRecever.class);
        PendingIntent pend0 = PendingIntent.getBroadcast(this, 0, int0, 0);
        alarm.cancel(pend0);
    }

    private void setTimer(long current_time_millis, int wait_second) {
        if (wait_second == 0) return;//约定等待时间为0，则不设置闹钟
        // 闹钟设定
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent int0 = new Intent(this, ClockRecever.class);
        PendingIntent pend0 = PendingIntent.getBroadcast(this, 0, int0, 0);
        if (current_time_millis + (wait_second + TIMEWAIT) * 1000L
                < System.currentTimeMillis()) {//已经过时，则定明天的闹钟
            wait_second += ONEDAY;
        }
        alarm.set(AlarmManager.RTC_WAKEUP, current_time_millis
                + (wait_second + TIMEWAIT) * 1000L, pend0);// 时间是按毫秒计算的
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
