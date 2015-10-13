package org.outing.medicine.start;

import android.content.Context;
import android.os.AsyncTask;

import org.outing.medicine.tools.connect.AnStatus;
import org.outing.medicine.tools.connect.ConnectUser;

/**
 * 自动登录，用于掉线后重新登录
 * 
 * @author Sun Yu Lin
 */
public class AutoLoginTask extends AsyncTask<Void, Void, Boolean> {
	private Context context;
	private String name, pass;

	/** 构造器。 */
	public AutoLoginTask(Context context) {
		this.context = context;
	}

	/** 异步任务类内必须实现的方法。用于多线程运行的代码。 */
	@Override
	protected Boolean doInBackground(Void... params) {
		return autoLogin();
	}

	/** 异步任务类内任务执行完毕后执行的方法 */
	@Override
	protected void onPostExecute(Boolean result) {
		// 暂时不处理登录成败结果
	}

	/**
	 * 自动登录判断
	 */
	private boolean autoLogin() {
		name = UserTool.getUserName(context);
		if (name.equals("")) {
			return false;
		} else {
			pass = UserTool.getUserPass(context);
			return startConnect();
		}
	}

	/**
	 * 建立网络连接
	 */
	private boolean startConnect() {
		ConnectUser conn = new ConnectUser(context, true);
		AnStatus status = conn.autoLogin(name, pass);
		return status.getStatus();
	}
}
