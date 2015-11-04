package org.outing.medicine.fun_tools;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

public class Award extends TActivity {
    private EditText edit;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_tools_award);
        setTitle("抽奖啰");
        setTitleBackColor(R.color.btn_5_normal);
        showBackButton();

        initView();
        showKeyboard();
    }

    private void initView() {
        edit = (EditText) findViewById(R.id.tools_award_edit);
        findViewById(R.id.tools_award_btn).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendText();
            }
        });
    }

    private void sendText() {
        String text = edit.getText().toString();
        if (text.equals(""))
            showToast("请输入奖券信息");
        else
            showToast("开发中……");
    }

    private void showKeyboard() {
        //由于某种原因，它不能自动弹出来，只能在此处调用了
        new Handler().postDelayed(new Runnable() {
            public void run() {
                edit.requestFocus();
                InputMethodManager imm = (InputMethodManager) edit.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);//弹出键盘
            }
        }, 100);
    }

    @Override
    public void showContextMenu() {
    }

}
