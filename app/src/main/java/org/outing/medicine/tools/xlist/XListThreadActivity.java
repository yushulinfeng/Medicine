package org.outing.medicine.tools.xlist;

import org.outing.medicine.tools.xlist.XListView.XListViewListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 下拉刷新、上拉加载辅助类，继承自Activity。<br>
 * 我实在想不出整合Activity与Fragment的方法，只好分别写了。
 * 
 * @author Sun Yu Lin
 *
 */
public abstract class XListThreadActivity extends Activity implements
		XListViewListener {
	private int HANDLER_INT = 1000000;
	private Handler handler;
	private Message message;
	private boolean thread_running = false;// 保证线程安全

	@Override
	/**实现父类的onCreate方法*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		onCreate();
	}

	@Override
	/** 实现接口中的更新方法 */
	public final void onRefresh() {
		if (!thread_running) {
			thread_running = true;
			new Thread(new Runnable() {
				public void run() {
					updateItem();
				}
			}).start();
		}
	}

	@Override
	/** 实现接口中的加载方法 */
	public final void onLoadMore() {
		if (!thread_running) {
			thread_running = true;
			new Thread(new Runnable() {
				public void run() {
					loadMore();
				}
			}).start();
		}
	}

	// 允许子类调用的方法////////////////////////////////////////////////////////////
	public final void init() {
		// 使每个类对应的message的值不同
		try {
			HANDLER_INT = (int) (Math.random() * 100000);// 确保6位以内
		} catch (Exception e) {
			HANDLER_INT = 1000000;
		}
		// 初始化handler
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == HANDLER_INT) {
					receiveMessage(msg);
					thread_running = false;
				}
				super.handleMessage(msg);
			}
		};
	}

	/** 默认用null即可 */
	public final void sendMessage(Object object) {
		message = new Message();
		message.what = HANDLER_INT;
		message.obj = object;
		handler.sendMessage(message);
	}

	/** 运行新线程 */
	public final void startNewThread() {
		new Thread(new Runnable() {
			public void run() {
				newThread();
			}
		}).start();
	}

	// 子类只需要实现即可的方法/////////////////////////////////////////////////////////////
	/** 相当于父类的onCreate方法 */
	public abstract void onCreate();

	/** 下拉刷新 */
	public abstract void updateItem();

	/** 上拉加载 */
	public abstract void loadMore();

	/** 主线程收到message的响应。message只是为了方便message.obj传输 */
	public abstract void receiveMessage(Message message);

	/** 新线程的运行项目，其中的代码将在新的线程中执行 */
	public abstract void newThread();
}
