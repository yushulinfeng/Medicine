package org.outing.medicine.tools.thread;

import android.os.AsyncTask;

public class Run extends AsyncTask<Void, Void, Void> {
	private RunListener listener;

	public Run(RunListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (listener != null)
			listener.doInThread();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (listener != null)
			listener.afterInMain();
	}

	// ///////////////////////基于回调的方法///////////////////////
	/**
	 * 轻量级多线程封装，比自己写Handler与Async省力
	 *
	 * @param listener
	 *            线程监听
	 */
	public static void RUN(RunListener listener) {
		Run connect = new Run(listener);
		connect.execute();
	}
}