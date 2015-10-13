package org.outing.medicine.start;

import android.content.Context;
import android.os.AsyncTask;

import org.outing.medicine.tools.connect.ConnectUser;

/**
 * 退出登录
 */
public class LogoutTask extends AsyncTask<Void, Void, Void> {
	private Context context;

	/** 构造器。 */
	public LogoutTask(Context context) {
		this.context = context;
	}

	/** 异步任务类内必须实现的方法。用于多线程运行的代码。 */
	@Override
	protected Void doInBackground(Void... params) {
		ConnectUser conn = new ConnectUser(context);
		conn.Logout();
		return null;
	}

}
