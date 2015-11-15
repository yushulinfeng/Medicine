package org.outing.medicine.tools.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 网络辅助Activity
 * 
 * @author Sun Yu Lin
 */
public abstract class NetActivity extends Activity implements Runnable {
	private String STRING = "string";
	private int HANDLER_INT = 111111;// 建议6位,暂时不用final，可能以后需要修改
	private ProgressDialog process_dialog = null;
	private Handler handler;
	private Message message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initHandler();
		onCreate();
	}

	/** 初始化handler */
	private void initHandler() {
		// 使每个类对应的message的值不同
		String time = new SimpleDateFormat("HHmmss", Locale.CHINA)
				.format(new Date());
		try {
			HANDLER_INT = Integer.parseInt(time);
		} catch (Exception e) {
			HANDLER_INT = 111111;
		}
		// 初始化handler
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == HANDLER_INT) {
					String string = msg.getData().getString(STRING);
					receiveMessage(string);
					// 取消忙碌对话框显示
					try {// 以防万一，如果确保不出错就删去此句。
						if (process_dialog != null)
							process_dialog.dismiss();
					} catch (Exception e) {
					}
				}
				super.handleMessage(msg);
			}
		};
	}

	// 允许子类调用的方法////////////////////////////////////////////////////////////
	/** 默认用null即可 */
	public final void sendMessage(String what) {
		message = new Message();
		message.what = HANDLER_INT;
		Bundle bundle = new Bundle();
		bundle.putString(STRING, what);
		message.setData(bundle);
		handler.sendMessage(message);
	}

	/** 运行新线程 */
	public final void startNewThread() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		newThread();
		// 取消忙碌对话框显示
		try {// 以防万一，如果确保不出错就删去此句。
			if (process_dialog != null) {
				process_dialog.dismiss();
				process_dialog = null;
			}
		} catch (Exception e) {
		}
	}

	/** 显示忙碌对话框，将在多线程结束后消失 */
	public final void showProcessDialog(String title, String text,
			boolean can_cancel) {
		process_dialog = ProgressDialog.show(NetActivity.this, title, text,
				true, can_cancel);// 忙碌对话框
	}

	// 子类只需要实现即可的方法/////////////////////////////////////////////////////////////
	/** 相当于原来的OnCreate()，直接从setContentView开始写即可 */
	public abstract void onCreate();

	/**
	 * 主线程收到message的响应<br>
	 * what只是为了方便字符串信息传输
	 */
	public abstract void receiveMessage(String what);

	/** 新线程的运行项目，其中的代码将在新的线程中执行 */
	public abstract void newThread();
}
