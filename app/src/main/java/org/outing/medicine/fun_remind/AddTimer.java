package org.outing.medicine.fun_remind;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

public class AddTimer extends TActivity {

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_addtimer);
        setTitle("添加闹钟");
        setTitle("开发中……");/////////////////////
        showBackButton();

        initView();
    }

    private void initView() {
        Button btn_sure = (Button) findViewById(R.id.remind_addtimer_btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("开发中……");
            }
        });
        Button btn_cancel = (Button) findViewById(R.id.remind_addtimer_btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void showContextMenu() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //刷新列表
    }

}
