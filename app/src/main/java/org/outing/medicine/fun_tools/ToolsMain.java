package org.outing.medicine.fun_tools;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

//小工具界面  
public class ToolsMain extends TActivity {
    @Override
    public void onCreate() {
        setContentView(R.layout.fun_tools_main);
        setTitle("常用工具");
        showBackButton();

        initView();
    }

    private void initView() {
        Button btn_award = (Button) findViewById(R.id.fun_tools_award);
        btn_award.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ToolsMain.this, Award.class);
                startActivity(intent);
            }
        });
        Button btn_calculator = (Button) findViewById(R.id.fun_tools_calculator);
        btn_calculator.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ToolsMain.this, Calculator.class);
                startActivity(intent);
            }
        });
        Button btn_flashlight = (Button) findViewById(R.id.fun_tools_flashlight);
        btn_flashlight.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ToolsMain.this, Flashlight.class);
                startActivity(intent);
            }
        });
        Button btn_magnifiter = (Button) findViewById(R.id.fun_tools_magnifiter);
        btn_magnifiter.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ToolsMain.this, Magnifiter.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void showContextMenu() {
    }
}
