package org.outing.medicine.fun_remind;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.dialog.DialogTitleList;

public class AddTimer extends TActivity implements View.OnClickListener {
    public static final String[] context_items =
            new String[]{"声音提醒", "震动提醒", "声音与振动", "通知栏提醒"};
    boolean enable_show_note = false;
    //直接修改这个bool值即可，它是不显示context_items的最后一项。
    //因为注释掉最后一项影响的东西太多。
    //通知栏这个，后期可以自己用。它的逻辑麻烦一些，而且并不适合老人。（剩下的不必修改）
    private EditText et_title, et_method;
    private View pickerdialog;
    private TimePicker picker;
    private int hour = -1, min = -1;
    private int remind_method = 2;
    private DialogTitleList method_dialog;
    private String drug_id, title, timer_id;
    private boolean is_alter = false;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_addtimer);
        setTitle("添加闹钟");
        showBackButton();

        initMessage();
        initView();
        initDialog();
        if (is_alter)
            setTitle("修改闹钟");//修改就不用弹键盘了
//        else
//            showKeyboard();//弹出键盘（感觉都不用）
    }

    private void initMessage() {
        Intent intent = getIntent();
        try {
            drug_id = intent.getStringExtra("drug_id");
            is_alter = intent.getBooleanExtra("is_alter", false);
        } catch (Exception e) {
            drug_id = null;//已添加处理
        }
        //不是修改闹钟，直接返回即可
        if (!is_alter)
            return;
        try {
            timer_id = intent.getStringExtra("timer_id");
            title = intent.getStringExtra("title");
            remind_method = intent.getIntExtra("method", 2);
            hour = intent.getIntExtra("hour", -1);
            min = intent.getIntExtra("min", -1);
        } catch (Exception e) {
            is_alter = false;
        }
    }

    private void initView() {
        picker = (TimePicker) findViewById(R.id.fun_remind_addtimer_picker);
        et_title = (EditText) findViewById(R.id.remind_addtimer_et_title);
        et_method = (EditText) findViewById(R.id.remind_addtimer_et_method);
        // et_method.setOnClickListener(this);//这样EditText点击两次才能响应
        // et_time.setOnClickListener(this);
        et_method.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    showMethodDialog();
                return false;
            }
        });
        Button btn_sure = (Button) findViewById(R.id.remind_addtimer_btn_sure);
        Button btn_del = (Button) findViewById(R.id.remind_addtimer_btn_del);
        btn_sure.setOnClickListener(this);
        btn_del.setOnClickListener(this);

        if (is_alter) {//如果是修改闹钟
            btn_del.setVisibility(View.VISIBLE);
            et_title.setText(title);
            et_title.setSelection(title.length());// 定位光标到最后
            et_method.setText(context_items[remind_method]);
            picker.setCurrentHour(hour);
            picker.setCurrentMinute(min);
        }
    }

    private void initDialog() {
        String[] dialog_item = null;
        if (enable_show_note) {
            dialog_item = context_items;//别名
        } else {
            dialog_item = new String[context_items.length - 1];
            for (int i = 0; i < dialog_item.length; i++)
                dialog_item[i] = context_items[i];
        }
        method_dialog = new DialogTitleList(this, "提醒方式")
                .setListItem(dialog_item)
                .setListListener(new DialogTitleList.DialogItemListener() {
                    @Override
                    public void onItemClick(int position) {
                        refreshMethod(position);
                    }
                });
    }

    private void showKeyboard() {
        //由于某种原因，它不能自动弹出来，只能在此处调用了
        new Handler().postDelayed(new Runnable() {
            public void run() {
                et_title.requestFocus();
                InputMethodManager imm = (InputMethodManager) et_title.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);//弹出键盘
            }
        }, 100);
    }

    private void refreshMethod(int index) {
        remind_method = index;
        et_method.setText(context_items[index]);
    }

    private void showMethodDialog() {
        method_dialog.show();
    }


    private void saveTimer() {
        if (drug_id == null) {
            showToast("系统错误");
            finish();
            return;
        }
        title = et_title.getText().toString();
        hour = picker.getCurrentHour();
        min = picker.getCurrentMinute();
        //闹钟的text暂时没有用上，列表中也没有显示，以后可以拓展
        if (is_alter) {//修改
            AnTimer timer = new AnTimer(timer_id, title, "", hour, min, remind_method);
            RemindTool.alterTimer(this, drug_id, timer);
            showToast("修改成功");
        } else {//添加
            AnTimer timer = new AnTimer(title, hour, min, remind_method);
            RemindTool.addTimer(this, drug_id, timer);
            showToast("添加成功");
        }
        //刷新闹钟，在列表中进行
        finish();
    }

    private void deleteTimer() {
        RemindTool.deleteTimer(this, drug_id, timer_id);
        showToast("删除成功");
        // 重置闹钟服务，在列表线程中进行
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remind_addtimer_btn_sure:
                saveTimer();
                break;
            case R.id.remind_addtimer_btn_del:
                deleteTimer();
                break;
        }
    }

    @Override
    public void showContextMenu() {
    }

}
