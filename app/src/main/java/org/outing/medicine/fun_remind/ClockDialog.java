package org.outing.medicine.fun_remind;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.tools.utils.ToastTool;

import java.util.ArrayList;
import java.util.Calendar;

//////////推迟用药不存log
@SuppressWarnings("deprecation")
public class ClockDialog extends Activity implements OnClickListener {
    public static final int METHOD_NOTE_CLICK = -2;//通知栏触发
    public static final int METHOD_RING = 0, METHOD_VIBRATE = 1, METHOD_R_V = 2, METHOD_NOTE = 3;
    private static final int CLOCK_TIME_OUT = 60 * 1000;//一分钟提醒时间，超时视为拒绝
    private TextView tv_title, tv_text;
    private ClockRing ring;
    private Button btn_sure, btn_cancel;
    private boolean is_taken = false, is_stop = false;
    private String text;
    private int method = -1;
    private ArrayList<AnRing> array_now;
    private KeyguardLock key_guard;
    private WakeLock wake_lock;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fun_remind_clock_dialog);

        initMessage();
        initUnlockBright();
        initView();
        initRing();
        if (method >= 0) {
            startRemind();
        } else if (method == METHOD_NOTE_CLICK) {
            startTake();
        }
    }

    private void initMessage() {
        Intent intent = getIntent();
        try {//应该有一个array记录所有的提醒信息，随用随加
            // title = intent.getStringExtra("title");//暂时不用
            method = intent.getIntExtra("method", -1);
            text = intent.getStringExtra("text");
        } catch (Exception e) {
            method = -1;
        }
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.remind_clock_dia_title);
        tv_text = (TextView) findViewById(R.id.remind_clock_dia_text);
        initText();
        btn_sure = (Button) findViewById(R.id.remind_clock_dia_sure);
        btn_cancel = (Button) findViewById(R.id.remind_clock_dia_cancel);
        btn_sure.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void initRing() {
        ring = new ClockRing(this);
        if (method != -1) {
            array_now = ClockTool.getRing(this);

            //如果在静音状态，就取消静音并用最小音量响铃
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);// Music我的ZTE的Max是13
            if (volume == 0)
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
        }
    }

    private void initText() {//已引入并测试scrollview
        tv_text.setText(text);
    }

    private void waitOneMinute() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //一分钟之内没有操作，视为拒绝
                if (!is_stop)
                    laterRemind();
            }
        }, CLOCK_TIME_OUT);
    }

    private void startRemind() {
        switch (method) {
            case METHOD_RING://响铃
                ring.startRing();
                waitOneMinute();
                break;
            case METHOD_VIBRATE://震动
                ring.startVibrate();
                waitOneMinute();
                break;
            case METHOD_R_V://响铃和震动
                ring.startRing();
                ring.startVibrate();
                waitOneMinute();
                break;
            case METHOD_NOTE://通知栏
                ring.sendNote("用药提醒", "点击查看相关药物", text);
                finish();//退出即可
                return;
        }
        unlockAndBright();
    }

    private void stopRemind() {
        is_stop = true;
        ring.stopRing();
        ring.stopVibrate();
    }

    private void startTake() {
        stopRemind();
        tv_title.setText("请参考列表用药");
        btn_sure.setText("吃完了");
        btn_cancel.setVisibility(View.GONE);
        tv_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        is_taken = true;
    }

    private void finishTake() {
        AddNetLogTask.ADD(this, array_now, true);// 完成用药记录与上传
//        saveLogText(true);//暂不存储本地记录
        //处理10分钟后提醒的项目
        ArrayList<String> array_temp = new ArrayList<String>();
        AnTimer timer_temp = null;
        for (int i = 0; i < array_now.size(); i++) {
            timer_temp = array_now.get(i).timer;
            if (timer_temp.getTimes().equals("1"))
                array_temp.add(timer_temp.getId());
        }
        if (array_temp.size() != 0)
            ClockTool.deleteTempRing(this, array_temp);
        //关闭
        finish();
    }

    private void laterRemind() {//////////////////////////////////////////////测试阶段
        stopRemind();
//        saveLogText(false);//暂不存储本地记录
        showToast("将在10分钟后提醒");
        //推迟提醒处理
        AnTimer timer_temp = null;
        int hour_temp = 0, min_temp = 0;
        Calendar calendar = Calendar.getInstance();// 时间计算
        calendar.add(Calendar.MINUTE, 10);//当前时间+10分钟
        hour_temp = calendar.get(Calendar.HOUR_OF_DAY);
        min_temp = calendar.get(Calendar.MINUTE);
        for (int i = 0; i < array_now.size(); i++) {
            timer_temp = array_now.get(i).timer;
            timer_temp.setTimes("1");
            timer_temp.setHour(hour_temp);
            timer_temp.setMinute(min_temp);
        }
        ClockTool.writeTempRing(this, array_now);
        RemindTool.refreshTimer(this);
        finish();
    }
//
//    private void saveLogText(boolean is_finish_take) {
//        if (array_now.size() == 0)
//            return;
//        String text_temp = "--总计" + array_now.size() + "种药物--";
//        for (int i = 0; i < array_now.size(); i++) {//不要引入ringtemp
//            text_temp += "\n";
//            text_temp += "\n药品：" + array_now.get(i).remind.getDrugName();
//            if (!array_now.get(i).remind.getDrugText().equals(""))//可以没有
//                text_temp += "\n备注：" + array_now.get(i).remind.getDrugText();
//            text_temp += "\n提醒：" + array_now.get(i).timer.getName();
//            text_temp += "\n\n";//好看
//        }
//        if (is_finish_take)
//            text_temp = "用户完成用药\n" + text_temp;
//        else
//            text_temp = "用户推迟用药\n" + text_temp;
//        ClockTool.saveLog(this, text_temp);
//    }

    private void initUnlockBright() {
        // 禁用锁屏
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);// 得到键盘锁管理器对象
        key_guard = km.newKeyguardLock("unLock");
        // 获取电源管理器对象（允许点亮屏幕）
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wake_lock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
    }

    private void unlockAndBright() {
        wake_lock.acquire();// 点亮屏幕
        wake_lock.release();// 释放
        key_guard.disableKeyguard();// 解锁
    }

    private void showToast(String text) {
        ToastTool.showToast(this, text);
    }

    @Override
    public void onClick(View v) {//已设置点击外部不能退出
        switch (v.getId()) {
            case R.id.remind_clock_dia_sure:
                if (is_taken)
                    finishTake();
                else
                    startTake();
                break;
            case R.id.remind_clock_dia_cancel:
                laterRemind();
                break;
        }
    }

    // 屏蔽按键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //当做拒绝处理
            laterRemind();
            //home不处理，不过有空的话，建议直接禁用。
            //后来发现，home不能禁用，否则用户遇到问题没有后路。
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        stopRemind();
        key_guard.reenableKeyguard();// 重新启用自动加锁
        super.onDestroy();
    }

}