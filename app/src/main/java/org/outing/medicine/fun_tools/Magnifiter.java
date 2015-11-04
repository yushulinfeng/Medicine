package org.outing.medicine.fun_tools;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;

import org.outing.medicine.R;

@SuppressWarnings("deprecation")
public class Magnifiter extends Activity implements SurfaceHolder.Callback {
	private static final int BACK_CAMERA = 0;// 前置是1
	private static final int PREVIEW_WIDTH = 800;// 经测试，16:9的比例倾斜时不会失真
	private static final int PREVIEW_HEIGHT = 480;// 一般手机屏幕是16:9，符合比例就好，view会将其fixCenter
	private SurfaceView surface;
	private Camera.Parameters parameters;
	private Camera camera;
	private SurfaceHolder holder;
	private boolean flag = false;// 用于pause、resume防止重复初始化
	private int zoom_value, zoom_step;
	private ImageButton btn_sub, btn_add;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_tools_magnifiter);

		// 屏幕常亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 让一个activity浮在锁屏界面的上方，返回即进入解锁界面（////以后可能有用）
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		surface = (SurfaceView) findViewById(R.id.tools_magnifiter_surface);
		btn_sub = (ImageButton) findViewById(R.id.tools_magnifiter_btn_sub);
		btn_add = (ImageButton) findViewById(R.id.tools_magnifiter_btn_add);

	}

	private void initListener() {
		btn_sub.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (camera != null) {
					if (zoom_value - zoom_step > 0) {
						zoom_value -= zoom_step;
					} else {
						zoom_value = 0;
					}
					parameters.setZoom(zoom_value);
					camera.setParameters(parameters);
					camera.startPreview();
					camera.autoFocus(null);// 对焦
				}
			}
		});
		btn_add.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (camera != null) {
					if (zoom_value + zoom_step < parameters.getMaxZoom()) {
						zoom_value += zoom_step;
					} else {
						zoom_value = parameters.getMaxZoom();
					}
					parameters.setZoom(zoom_value);// 越界会报错
					camera.setParameters(parameters);
					camera.startPreview();
					camera.autoFocus(null);// 对焦
				}
			}
		});
		surface.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {// 触碰对焦，非常好
				if (camera != null)
					camera.autoFocus(null);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (camera != null) {
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		holder = surface.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		startCamera();

		initListener();// 必须在startCamera后调用，使全局量完成初始化
	}

	private void startCamera() {
		if (flag) {
			if (camera != null) {
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		}
		try {
			camera = Camera.open(BACK_CAMERA);// 没有摄像头，在这里报错
		} catch (Exception e) {
			camera = null;
		}
		try {// 有摄像头，但是拒绝授权，在这里报错
			if (camera != null) {
				camera.setDisplayOrientation(90);// 旋转矫正
				parameters = camera.getParameters();
				parameters.setPictureFormat(PixelFormat.JPEG);
				parameters.set("orientation", "portrait");
				parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
				parameters.setRotation(90);// 若为前置，此处应为270（上面还是90）
				zoom_step = parameters.getMaxZoom() / 5;// 5级缩放
				zoom_value = zoom_step * 2;// 已第二级缩放初始化
				parameters.setZoom(zoom_value);
				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);
				camera.startPreview();
				flag = true;
			}
		} catch (Exception e) {
			camera = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {// create之后会调用一次
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		startCamera();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

}
