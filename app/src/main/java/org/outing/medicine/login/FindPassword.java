package org.outing.medicine.login;

import org.outing.medicine.MainActivity;
import org.outing.medicine.R;
import org.outing.medicine.Welcome;
import org.outing.medicine.logic.AnStatus;
import org.outing.medicine.tools.Check;
import org.outing.medicine.tools.NetActivity;
import org.outing.medicine.tools.connect.ConnectUser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPassword extends NetActivity implements OnClickListener {

	private EditText userNameEdit = null;
	private EditText verifyCodeEidt = null;
	private Button gainCodeBt = null;
	private Button backToLogin = null;
	private EditText passwordEdit1 = null;
	private EditText passwordEdit2 = null;
	private String password1 = null;
	private String password2 = null;
	private Button confirmBt = null;
	private String username = null;
	private String code = null;
	private static boolean hasGainCode = false;

	private ConnectUser conn = null;

	@Override
	public void onCreate() {
		setContentView(R.layout.find_password);
		userNameEdit = (EditText) findViewById(R.id.findusername);
		verifyCodeEidt = (EditText) findViewById(R.id.find_code);
		gainCodeBt = (Button) findViewById(R.id.find_gain_code);
		backToLogin = (Button) findViewById(R.id.find_back_to_login);
		passwordEdit1 = (EditText) findViewById(R.id.find_password1);
		passwordEdit2 = (EditText) findViewById(R.id.find_password2);
		confirmBt = (Button) findViewById(R.id.find_confirm_bt);
		gainCodeBt.setOnClickListener(this);
		backToLogin.setOnClickListener(this);
		confirmBt.setOnClickListener(this);
		conn = new ConnectUser(this, true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 获取验证码
		case R.id.find_gain_code:
			username = userNameEdit.getText().toString();
			if (Check.verifyUsername(this, username)) {
				gainVerifyCode(username);
			}
			break;
		case R.id.find_confirm_bt:
			code = verifyCodeEidt.getText().toString();
			if (hasGainCode) {
				if (Check.verifyUsername(this, username)
						&& Check.verifyCode(this, code)) {
					Log.e("用户名", username);
					Log.e("验证码", code);
					password1 = passwordEdit1.getText().toString();
					password2 = passwordEdit2.getText().toString();
					if (Check.verifyPassword(FindPassword.this, password1,
							password2)) {
						showProcessDialog("忘记密码", "正在连接后台，请稍候……", false);
						startNewThread();
					}
				}
			} else {
				showToast("未获取验证码");
			}
			break;
		case R.id.find_back_to_login:
			Intent intent = new Intent(FindPassword.this, Login.class);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}
	}

	public void alterSuccess() {
		AlertDialog.Builder builder = new AlertDialog.Builder(FindPassword.this)
				.setTitle("修改密码成功，是否马上登录？")
				.setPositiveButton("登录", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(FindPassword.this,
								Login.class);
						intent.putExtra(Welcome.NAME, username);
						intent.putExtra(Welcome.PASS, "");
						startActivity(intent);
						FindPassword.this.finish();
					}
				})
				.setNegativeButton("先看看",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(FindPassword.this,
										MainActivity.class);
								startActivity(intent);
								FindPassword.this.finish();
							}
						});
		builder.create().show();
	}

	/**
	 * 通知后台发送验证码给邮箱或手机 ;
	 * 
	 * 需要区分用户用来注册的是手机号还是邮箱
	 * 
	 * @param username
	 */
	private void gainVerifyCode(String username) {
		startNewThread();
	}

	private void showToast(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void receiveMessage(String what) {
		if (what.equals("idcode_ok")) {
			showToast("验证码已发送，请查收");
			hasGainCode = true;
		} else if (what.equals("alter_ok")) {
			alterSuccess();
		} else {
			showToast(what);
		}
	}

	@Override
	public void newThread() {
		AnStatus status = null;
		if (!hasGainCode) {
			status = conn.getIDCode(username);
			if (status.getStatus())
				sendMessage("idcode_ok");
			else
				sendMessage(status.getReason());
		} else {
			status = conn.forgetPass(username, code, password1);
			if (status.getStatus())
				sendMessage("alter_ok");
			else
				sendMessage(status.getReason());
		}
	}
}
