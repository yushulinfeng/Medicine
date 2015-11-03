package org.outing.medicine.tools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.outing.medicine.R;

public abstract class TActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate();
    }

    // ////////允许子类调用的方法
    public void setTitle(String title) {
        ((TextView) findViewById(R.id.top_text)).setText(title);
    }

    public void setTitleBackColor(int color) {
        findViewById(R.id.top_layout).setBackgroundResource(color);
    }

    public void showBackButton() {
        ImageButton top_back = (ImageButton) findViewById(R.id.top_back);
        top_back.setVisibility(ImageButton.VISIBLE);
        top_back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showMenuButton() {
        ImageButton top_menu = (ImageButton) findViewById(R.id.top_menu);
        top_menu.setVisibility(ImageButton.VISIBLE);
        top_menu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showContextMenu();
            }
        });
    }

    // ////////允许子类调用的方法-辅助
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // 子类只需要实现即可的方法/////////////////////////////////////////////////////////////

    /**
     * 相当于原来的OnCreate()，直接从setContentView开始写即可
     */
    public abstract void onCreate();

    /**
     * 显示弹出式菜单
     */
    public abstract void showContextMenu();
}
