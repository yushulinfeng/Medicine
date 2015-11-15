package org.outing.medicine.fun_tools;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import org.outing.medicine.R;
import org.outing.medicine.tools.utils.ToastTool;

@SuppressWarnings("deprecation")
public class Flashlight extends Activity {
    private Camera camera = null;
    private Parameters parameters = null;
    private static boolean statusFlag = true;
    private ImageButton btn_switch = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fun_tools_flashlight);

        btn_switch = (ImageButton) findViewById(R.id.tools_flashlight_btn_switch);
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
                camera = Camera.open(0);//后置摄像头
            } catch (Exception e) {// 没有硬件
                camera = null;
                ToastTool.showToast(this,"您的手机不支持手电筒");
                finish();
                return;
            }
            try {
                parameters = camera.getParameters();
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
                camera.setParameters(parameters);
                //尝试初始化摄像头(未开启过摄像头时，必须这样初始化，否则不能打开闪光灯)
                camera.startPreview();
            } catch (Exception e1) {// 不给授权
                camera = null;
                ToastTool.showToast(this,"需要给手电筒授权");
                finish();
                return;
            }
            statusFlag = false;
            btn_switch.setBackgroundResource(R.drawable.tools_flashlight_on);
        } else {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
            camera.setParameters(parameters);
            statusFlag = true;
            camera.stopPreview();//终止预览
            camera.release();
            btn_switch.setBackgroundResource(R.drawable.tools_flashlight_off);
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