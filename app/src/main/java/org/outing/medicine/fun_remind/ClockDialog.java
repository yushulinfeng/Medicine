package org.outing.medicine.fun_remind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.outing.medicine.R;

import java.util.ArrayList;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fun_remind_clock_dialog);

        initMessage();
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
        ClockTool.refuseMedicine(this, array_now);// 记录与上传
        showToast("已拒绝用药");
        finish();
    }

    private void finishTake() {
        ClockTool.takeMedicine(this, array_now);// 完成用药记录与上传
        finish();
    }

    private void stopTake() {
        ClockTool.refuseMedicine(this, array_now);// 记录与上传
        showToast("已拒绝用药");
        finish();
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
                break;
        }
    }

    private void stopRemind() {
        is_stop = true;
        ring.stopRing();
        ring.stopVibrate();
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
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        stopRemind();
        super.onDestroy();
    }
}