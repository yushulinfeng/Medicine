package org.outing.medicine.start.login;


import org.outing.medicine.R;

public class LoginTitle extends Login {

	@Override
	public void onCreate() {
		setContentView(R.layout.start_login_withtilte);
		setTitle("登录");
		showBackButton();
		
		initMessage();
		initView();
		initAutoLogin();
	}

	@Override
	public void showContextMenu() {
	}
}
