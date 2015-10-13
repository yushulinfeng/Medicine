package org.outing.medicine.start;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.outing.medicine.R;
import org.outing.medicine.main_main.MainActivity;
import org.outing.medicine.tools.NetActivity;
import org.outing.medicine.tools.connect.AnStatus;
import org.outing.medicine.tools.connect.ConnectEasy;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectResponseListener;
import org.outing.medicine.tools.connect.ConnectUser;
import org.outing.medicine.tools.connect.ServerURL;

public class Login extends NetActivity implements OnClickListener {

    private EditText userName = null;
    private EditText password = null;
    private Button loginBt = null;
    private Button toRegisterBt = null;
    private Button findPasswordBt = null;
    private Button skipBt = null;
    private String mName = null;
    private String mPassword = null;

    private ConnectUser conn = null;
    private String tempPassword = null;

    @Override
    public void onCreate() {
        setContentView(R.layout.login);
        conn = new ConnectUser(this, true);
        init();
        getNamePass();
    }

    private void init() {
        userName = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        loginBt = (Button) findViewById(R.id.bt_login);
        toRegisterBt = (Button) findViewById(R.id.bt_to_register);
        findPasswordBt = (Button) findViewById(R.id.to_find_password);
        skipBt = (Button) findViewById(R.id.skip_login);
        loginBt.setOnClickListener(this);
        toRegisterBt.setOnClickListener(this);
        findPasswordBt.setOnClickListener(this);
        skipBt.setOnClickListener(this);
    }

    private void getNamePass() {
        Intent intent = getIntent();
        try {
            Bundle bundle = intent.getExtras();
            mName = bundle.getString(Welcome.NAME);
            mPassword = bundle.getString(Welcome.PASS);
        } catch (Exception e) {
            mName = "";
            mPassword = "";
        }
        userName.setText(mName);
        password.setText(mPassword);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.bt_login:
                mName = userName.getText().toString();
                mPassword = password.getText().toString();
                Log.d("登录账号", mName);
                Log.d("登录密码", mPassword);
                tempPassword = UserTool.getTempPass(this);
                if (mPassword.equals(tempPassword)) {
                    mPassword = UserTool.getUserPass(this);
                    tempPassword = null;
                }
                if (UserCheck.verifyUsername(this, mName)) {
                    showProcessDialog("登录", "正在连接服务器，请稍候……", false);
                    startNewThread();
                } else {
                    Toast.makeText(this, "用户名格式不正确", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_to_register:
                intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.to_find_password:
                intent = new Intent(Login.this, FindPassword.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.skip_login:
                intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                Login.this.finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void receiveMessage(String what) {
        if (what.equals("login_ok")) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            UserTool.saveUser(this, mName, mPassword);// 保存
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, what, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void newThread() {
        AnStatus status = null;
        if (tempPassword == null) {
            status = conn.autoLogin(mName, mPassword);
        } else {
            status = conn.login(mName, mPassword);
        }
        if (status.getStatus())
            sendMessage("login_ok");
        else
            sendMessage(status.getReason());
    }

    ////////////基于回调的POST封装使用示例，直接在主线程使用即可////////////////
    private void loginEasy() {
        ConnectEasy.POST(this, ServerURL.LOGIN,
                new ConnectList().put("phone", mName).put("password", mPassword),
                new ConnectResponseListener() {
                    @Override
                    public void onResponse(String response) {//if ERROR,response==null
                        Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
