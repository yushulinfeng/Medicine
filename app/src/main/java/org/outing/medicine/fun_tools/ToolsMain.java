package org.outing.medicine.fun_tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.outing.medicine.R;

//小工具界面  
public class ToolsMain extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_tools_main);

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
}
