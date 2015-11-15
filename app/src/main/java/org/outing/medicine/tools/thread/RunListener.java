package org.outing.medicine.tools.thread;

public interface RunListener {

	/**
	 * 在多线程中执行的方法<br>
	 * 注意：多线程中不允许Toast,修改界面等
	 */
	public void doInThread();

	/**
	 * 多线程执行完后，在主线程执行的方法
	 */
	public void afterInMain();
}
