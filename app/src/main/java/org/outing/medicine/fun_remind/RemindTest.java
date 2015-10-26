package org.outing.medicine.fun_remind;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        scroll.addView(layout);
        return scroll;
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
        addButton("闹钟响铃界面", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemindTest.this, ClockDialog.class);
                intent.putExtra("method", 0);
                intent.putExtra("text", "--总计2种药物--\n\n药品：阿莫西林\n备注：可以不填\n提醒：测试闹钟"
                        + "\n\n药品：阿莫西林2\n备注：可以不填2\n提醒：测试闹钟2");
                startActivity(intent);
            }
        });
        addButton("闹钟震动界面", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemindTest.this, ClockDialog.class);
                intent.putExtra("method", 1);
                intent.putExtra("text", "--总计2种药物--\n\n药品：阿莫西林\n备注：可以不填\n提醒：测试闹钟"
                        + "\n\n药品：阿莫西林2\n备注：可以不填2\n提醒：测试闹钟2");
                startActivity(intent);
            }
        });
        addButton("闹钟通知栏界面（建议自用）", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemindTest.this, ClockDialog.class);
                intent.putExtra("method", 2);
                intent.putExtra("text", "--总计2种药物--\n\n药品：阿莫西林\n备注：可以不填\n提醒：测试闹钟"
                        + "\n\n药品：阿莫西林2\n备注：可以不填2\n提醒：测试闹钟2");
                startActivity(intent);
            }
        });
        addButton("闹钟历史记录", new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RemindTest.this)
                        .setMessage(ClockTool.getLog(RemindTest.this))
                        .setPositiveButton("清空", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ClockTool.cleanLog(RemindTest.this);
                            }
                        })
                        .setNegativeButton("返回", null).show();

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
