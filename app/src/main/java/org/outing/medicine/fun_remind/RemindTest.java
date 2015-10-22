package org.outing.medicine.fun_remind;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.outing.medicine.fun_tools.WidgetImage;
import org.outing.medicine.fun_tools.WidgetShow;
import org.outing.medicine.fun_tools.WidgetTool;

public class RemindTest extends Activity {
    private LinearLayout layout;
    private WidgetImage wid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initView());
        wid = new WidgetImage();
        initButton();
    }

    private View initView() {
        layout = new LinearLayout(this);
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        tv.setText("测试界面");
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);
        layout.addView(tv);
        return layout;
    }

    private Button addButton(String text, OnClickListener listener) {
        Button btn = new Button(this);
        btn.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        btn.setText(text);
        btn.setOnClickListener(listener);
        layout.addView(btn);
        return btn;
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void initButton() {
        addButton("显示桌面控件", new OnClickListener() {
            @Override
            public void onClick(View v) {
                WidgetTool.saveWidText(RemindTest.this, "测试\n12345678910");
                WidgetShow.updatewidget(RemindTest.this);
                showToast("TEST-show");
            }
        });
        addButton("隐藏桌面控件", new OnClickListener() {
            @Override
            public void onClick(View v) {
                WidgetTool.saveWidText(RemindTest.this, "老友网");
                WidgetShow.updatewidget(RemindTest.this);
                showToast("TEST-hide");
            }
        });
        addButton("切换壁纸", new OnClickListener() {
            @Override
            public void onClick(View v) {
                wid.showTextOnWallPaper(RemindTest.this, "测试信息", "123456789");
                showToast("success");
            }
        });
        addButton("恢复壁纸", new OnClickListener() {
            @Override
            public void onClick(View v) {
                wid.hideTextOnWallPaper(RemindTest.this);
                showToast("success");
            }
        });
        addButton("清空用药提醒", new OnClickListener() {
            @Override
            public void onClick(View v) {
                RemindTool.clearDrug(RemindTest.this);
            }
        });
        addButton("返回", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
