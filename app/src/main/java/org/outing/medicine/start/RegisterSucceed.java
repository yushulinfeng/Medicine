package org.outing.medicine.start;

import org.outing.medicine.main_main.MainActivity;
import org.outing.medicine.R;
import org.outing.medicine.tools.connect.ConnectStatus;
import org.outing.medicine.tools.NetActivity;
import org.outing.medicine.tools.connect.ConnectUser;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RegisterSucceed extends NetActivity implements OnClickListener {

	private Button loginNowBt = null;
	private Button loginOtherWayBt = null;
	private String userName = "", password = "";

	private ConnectUser conn = null;

	@Override
	public void onCreate() {
		setContentView(R.layout.register_succeed);
		loginNowBt = (Button) findViewById(R.id.login_now);
		loginOtherWayBt = (Button) findViewById(R.id.login_otherway);
		loginNowBt.setOnClickListener(this);
		loginOtherWayBt.setOnClickListener(this);
		conn = new ConnectUser(this, true);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.login_now:
			try {
				userName = getIntent().getStringExtra(Register.NAMEFLAG);
				password = getIntent().getStringExtra(Register.PASSWORDFLAG);
			} catch (Exception e) {
			}
			Log.e("用户名", userName);
			Log.e("密码", password);
			login();
			break;
		case R.id.login_otherway:
			intent = new Intent(RegisterSucceed.this, Login.class);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 自动登录
	 * 
	 * @param userName
	 * @param password
	 */
	private boolean login() {
		showProcessDialog("登录", "正在连接服务器，请稍候……", false);
		startNewThread();
		return true;
	}

	@Override
	public void receiveMessage(String what) {
		if (what.equals("login_ok")) {
			Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
			UserTool.saveUser(this, userName, password);// 保存
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(this, what, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void newThread() {
		ConnectStatus status = conn.login(userName, password);
		if (status.getStatus())
			sendMessage("login_ok");
		else
			sendMessage(status.getReason());
	}
}
