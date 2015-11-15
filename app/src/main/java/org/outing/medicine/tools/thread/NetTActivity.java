package org.outing.medicine.tools.thread;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.tools.utils.ToastTool;

/**
 * 网络辅助TActivity
 *
 * @author Sun Yu Lin
 */
public abstract class NetTActivity extends NetActivity {

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
        ToastTool.showToast(this, text);
    }

    /**
     * 显示弹出式菜单
     */
    public abstract void showContextMenu();
}
