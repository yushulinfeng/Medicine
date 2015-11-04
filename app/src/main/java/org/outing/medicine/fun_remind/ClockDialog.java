package org.outing.medicine.fun_remind;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.outing.medicine.R;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class ClockDialog extends Activity implements OnClickListener {
    private static ConnectList con_list = null;//过渡专用
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
            takeMedicine();
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
                    refuseMedicine();
            }
        }, CLOCK_TIME_OUT);
    }


    private void takeMedicine() {
        stopRemind();

        tv_title.setText("请参考列表用药");
        btn_sure.setText("完成用药");
        btn_cancel.setText("拒绝用药");
        tv_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        is_taken = true;
    }

    private void refuseMedicine() {
        stopRemind();
        refuseMedicine(this, array_now);// 记录与上传
        showToast("已拒绝用药");
//        finish();
    }

    private void finishTake() {
        takeMedicine(this, array_now);// 完成用药记录与上传
//        finish();
    }

    private void stopTake() {
        refuseMedicine(this, array_now);// 记录与上传
        showToast("已拒绝用药");
//        finish();
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

    @Override
    public void onClick(View v) {//已设置点击外部不能退出
        switch (v.getId()) {
            case R.id.remind_clock_dia_sure:
                if (is_taken)
                    finishTake();
                else
                    takeMedicine();
                break;
            case R.id.remind_clock_dia_cancel:
                if (is_taken)
                    stopTake();
                else
                    refuseMedicine();
                break;
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // 屏蔽按键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //当做拒绝处理
            refuseMedicine();
            ////////////////////// home不处理，不过有空的话，建议直接禁用。
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

    //

    /**
     * 完成用药
     */
    public void takeMedicine(Context context, ArrayList<AnRing> array) {
        uploadMedicineState(context, array, true);
        String log = "用户完成用药\n" + getRingLogText(array);
        ClockTool.saveLog(context, log);
        finish();///////////
    }

    /**
     * 拒绝用药
     */
    public void refuseMedicine(Context context, ArrayList<AnRing> array) {
        uploadMedicineState(context, array, false);
        String log = "用户拒绝用药\n" + getRingLogText(array);
        ClockTool.saveLog(context, log);
        finish();///////////////////////
    }

    private void uploadMedicineState(Context context,
                                     ArrayList<AnRing> array, boolean is_finish) {
        Log.e("EEE", "EEE ------" + "start");
        for (int i = 0; i < array.size(); i++) {
            Log.e("EEE", "EEE ------" + "for");
            con_list = getHistoryPostText(array.get(i), is_finish);
            Log.e("EEE", "EEE ------" + "list");
            Connect.POST(context, ServerURL.Post_Body_Message, new ConnectListener() {
                @Override
                public ConnectList setParam(ConnectList list) {
                    return con_list;
                }

                @Override
                public ConnectDialog showDialog(ConnectDialog dialog) {
                    return null;
                }

                @Override
                public void onResponse(String response) {
                    Log.e("EEE", "EEE ------" + "response");
                    if (response == null) {//暂不处理
                    } else if (response.equals("-2")) {
                    } else if (response.equals("-1")) {
                    } else if (response.equals("0")) {
                    }
                    finish();
                }
            });
            Log.e("EEE", "EEE ------" + "post");
        }
    }

    private ConnectList getHistoryPostText(AnRing ring, boolean is_finish) {
        ConnectList list = new ConnectList();
        String post_str = ring.remind.getDrugName() + RemindHistory.HIS_SPLITE
                + (is_finish ? 1 : 0) + RemindHistory.HIS_SPLITE + ring.timer.getTime();
        list.put("type", "4");
        list.put("data", post_str);
        return list;
    }

    private static String getRingLogText(ArrayList<AnRing> array_now) {
        if (array_now.size() == 0)
            return "";
        String text_temp = "--总计" + array_now.size() + "种药物--";
        for (int i = 0; i < array_now.size(); i++) {//不要引入ringtemp
            text_temp += "\n";
            text_temp += "\n药品：" + array_now.get(i).remind.getDrugName();
            if (!array_now.get(i).remind.getDrugText().equals(""))//可以没有
                text_temp += "\n备注：" + array_now.get(i).remind.getDrugText();
            text_temp += "\n提醒：" + array_now.get(i).timer.getName();
            text_temp += "\n\n";//好看
        }
        return text_temp;
    }

}