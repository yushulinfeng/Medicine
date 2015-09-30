package org.outing.medicine;

import org.outing.medicine.logic.AnStatus;
import org.outing.medicine.login.Login;
import org.outing.medicine.tools.NetActivity;
import org.outing.medicine.tools.ToDealUser;
import org.outing.medicine.tools.connect.ConnectUser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Welcome extends NetActivity {
	public static String NAME = "name";
	public static String PASS = "pass";
	private String name = "", pass = "";

	@Override
	public void onCreate() {
		setContentView(R.layout.welcome);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				// autoLogin();
				toLogin();// /////////////////////////////////////////test
			}
		}, 500);
	}

	/**
	 * 自动登录判断
	 */
	public void autoLogin() {
		name = ToDealUser.getUserName(this);
		if (name.equals("")) {
			toLogin();
		} else {
			pass = ToDealUser.getUserPass(this);
			startNewThread();
		}
	}

	/**
	 * 跳到登录界面
	 */
	public void toLogin() {
		Intent intent = new Intent(Welcome.this, Login.class);
		Bundle bundle = new Bundle();
		bundle.putString(NAME, name);
		if (pass != null && !pass.equals(""))
			pass = ToDealUser.getTempPass(this);
		bundle.putString(PASS, pass);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 跳到主界面
	 */
	public void toMain() {
		Intent intent = new Intent(Welcome.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void receiveMessage(String what) {
		if (what.equals("OK")) {
			toMain();
		} else {
			toLogin();
		}
	}

	@Override
	public void newThread() {
		ConnectUser conn = new ConnectUser(this, true);
		AnStatus status = conn.autoLogin(name, pass);
		if (status.getStatus())
			sendMessage("OK");
		else
			sendMessage("NO");
	}
}
