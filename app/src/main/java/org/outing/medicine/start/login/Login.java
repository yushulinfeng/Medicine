package org.outing.medicine.start.login;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.outing.medicine.R;
import org.outing.medicine.main_main.MainActivity;
import org.outing.medicine.start.forget.ForgetFind1;
import org.outing.medicine.start.register.Register1;
import org.outing.medicine.start.tool.SafeCheck;
import org.outing.medicine.start.tool.UserTool;
import org.outing.medicine.tools.base.TActivity;
import org.outing.medicine.tools.utils.ToastTool;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

//直接登录就用此类
public class Login extends TActivity implements View.OnClickListener {
    public final static int FORGET_JUMP_CODE = 1;
    public final static int REGISTER_JUMP_CODE = 2;
    private String name = "", pass = "";
    private EditText et_name, et_pass;

    @Override
    public void onCreate() {
        setContentView(R.layout.start_login);
        // 由于此类布局没有使用top，所以TActicity中的方法是留给子类用的

        initMessage();
        initView();
        initAutoLogin();
        initPrivateView();
    }

    protected void initMessage() {
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("phone");
            pass = intent.getStringExtra("password");
        } else {
            name = pass = "";
        }
    }

    protected void initView() {
        et_name = (EditText) findViewById(R.id.login_et_name);
        et_pass = (EditText) findViewById(R.id.login_et_pass);
        Button btn_login = (Button) findViewById(R.id.login_btn_login);
        Button btn_forget = (Button) findViewById(R.id.login_btn_foget);
        Button btn_register = (Button) findViewById(R.id.login_btn_register);
        btn_login.setOnClickListener(this);
        btn_forget.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    protected void initAutoLogin() {
        if (name != null && !name.equals("")) {
            et_name.setText(name);// pass不必设定
            et_name.setSelection(name.length());
            login();// 带参数就直接登录
        } else {
            name = UserTool.getUser(this)[0];
            et_name.setText(name);// 没有风险，pass不能写上
            et_name.setSelection(name.length());
        }
    }

    private void initPrivateView() {// 跳过登录
        Button btn_jump = (Button) findViewById(R.id.login_btn_jump);
        btn_jump.setOnClickListener(this);
    }

    private void login() {
        if (SafeCheck.checkLoginUser(this, name)
                && SafeCheck.checkLoginPass(this, pass)) {
            Connect.POST(this, ServerURL.LOGIN, new ConnectListener() {
                @Override
                public ConnectDialog showDialog(ConnectDialog dialog) {
                    dialog.config(Login.this, "正在登录", "请稍候……", false);// false禁止取消对话框
                    return dialog;
                }

                @Override
                public ConnectList setParam(ConnectList list) {
                    list.put("phone", name);
                    list.put("password", pass);
                    return list;
                }

                @Override
                public void onResponse(String response) {
                    if (response == null)
                        showToast("连接服务器失败");
                    else {
                        try {
                            int result = Integer.parseInt(response);
                            if (result > 0)
                                loginSuccess();//登录成功
                            else if (result == -1)
                                showToast("登录失败");
                            else//-2
                                showToast("用户名或密码错误");
                        } catch (Exception e) {
                            showToast("网络错误");//系统错误
                        }
                    }
                }
            });
        }
    }

    private void loginSuccess() {
        ToastTool.showToast(this, "登录成功");
        UserTool.saveUser(this, name, pass);// 保存
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.login_btn_login:
                name = et_name.getText().toString();
                pass = et_pass.getText().toString();
                login();
                break;
            case R.id.login_btn_foget:
                intent = new Intent(Login.this, ForgetFind1.class);
                startActivityForResult(intent, 0);// 返回式启动
                break;
            case R.id.login_btn_register:
                intent = new Intent(Login.this, Register1.class);
                startActivityForResult(intent, 0);// 返回式启动
                break;
            case R.id.login_btn_jump:
                intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void showContextMenu() {
    }

    // 返回处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == FORGET_JUMP_CODE || resultCode == REGISTER_JUMP_CODE) {
            // 产生跳转，就关闭本页
            finish();
        }
    }

}
