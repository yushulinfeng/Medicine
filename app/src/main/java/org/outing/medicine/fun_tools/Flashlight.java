package org.outing.medicine.fun_tools;

import org.outing.medicine.R;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class Flashlight extends Activity {
	private Camera camera = null;
	private Parameters parameters = null;
	private static boolean statusFlag = true;
	private Button btn_switch = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_tools_flashlight);

		btn_switch = (Button) findViewById(R.id.tools_flashlight_btn_switch);
		btn_switch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switchFlashlight();
			}
		});
		switchFlashlight();
	}

	private void switchFlashlight() {
		if (statusFlag) {
			try {
				camera = Camera.open();
			} catch (Exception e) {// 没有硬件
				camera = null;
				Toast.makeText(this, "您的手机不支持手电筒", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			try {
				parameters = camera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
				camera.setParameters(parameters);
			} catch (Exception e) {// 不给授权
				camera = null;
				Toast.makeText(this, "需要给手电筒授权", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			statusFlag = false;
			btn_switch.setText("关闭");
			btn_switch.setBackgroundColor(Color.WHITE);
			btn_switch.setTextColor(Color.BLACK);
		} else {
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
			camera.setParameters(parameters);
			statusFlag = true;
			camera.release();
			btn_switch.setText("开启");
			btn_switch.setBackgroundColor(Color.BLACK);
			btn_switch.setTextColor(Color.WHITE);
		}
	}

	@Override
	public void onDestroy() {
		if (!statusFlag && camera != null) {// 开关打开时
			camera.release();// 释放硬件资源
			statusFlag = true;// 避免，打开开关后退出程序，再次进入不打开开关直接退出时，程序错误
		}
		super.onDestroy();
	}

}