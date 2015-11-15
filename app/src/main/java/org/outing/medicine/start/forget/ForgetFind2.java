package org.outing.medicine.start.forget;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.outing.medicine.R;
import org.outing.medicine.start.login.Login;
import org.outing.medicine.start.tool.SafeCheck;
import org.outing.medicine.tools.thread.NetTActivity;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

public class ForgetFind2 extends NetTActivity implements View.OnClickListener {
    private static final int CDDE_WAIT_TIME = 60;// 验证码等待时间
    private int wait_time = CDDE_WAIT_TIME;
    private String phone, code, pass1, pass2;
    private EditText et_code, et_pass1, et_pass2;
    private Button btn_code, btn_login;

    @Override
    public void onCreate() {
        setContentView(R.layout.start_forget_find2);
        setTitle("设置登录密码");
        showBackButton();

        initMessage();
        initView();
        waitOneMinute();// 一分钟后才能再次获取验证码
    }

    private void initMessage() {
        Intent intent = getIntent();
        if (intent != null) {
            phone = intent.getStringExtra("phone");
        } else {
            phone = "";
        }
    }

    private void initView() {
        et_code = (EditText) findViewById(R.id.forget_find2_et_code);
        et_pass1 = (EditText) findViewById(R.id.forget_find2_et_pass1);
        et_pass2 = (EditText) findViewById(R.id.forget_find2_et_pass2);
        btn_code = (Button) findViewById(R.id.forget_find2_btn_getcode);
        btn_login = (Button) findViewById(R.id.forget_find2_btn_sure);
        btn_code.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_find2_btn_getcode:
                getCode();
                break;
            case R.id.forget_find2_btn_sure:
                nextStep();
                break;
        }
    }

    private void getCode() {
        waitOneMinute();
        Connect.POST(this, ServerURL.ID_CODE, new ConnectListener() {// code地址要换吗
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;// 不显示对话框
            }

            public ConnectList setParam(ConnectList list) {
                list.put("phone", phone);
                return list;
            }

            public void onResponse(String response) {
                // 这个就不用管了
            }
        });
        showToast("验证码已发送");// 这里就这样了，不能太真实
    }

    private void nextStep() {
        if (phone.equals("")) {
            showToast("系统错误");
            return;
        }
        code = et_code.getText().toString();
        if (!SafeCheck.checkCode(this, code))
            return;
        pass1 = et_pass1.getText().toString();
        pass2 = et_pass2.getText().toString();
        if (!SafeCheck.checkPass(this, pass1, pass2))
            return;

        if (code.equals("000000")) {// ////////测试专用
            alterSuccess();
            return;
        }
        sendToServer();
    }

    private void sendToServer() {
        Connect.POST(this, ServerURL.FORGET_PASS, new ConnectListener() {
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(ForgetFind2.this, "正在连接", "请稍候……", false);
                return dialog;
            }

            public ConnectList setParam(ConnectList list) {
                list.put("phone", phone);
                list.put("code", code);
                list.put("password", pass1);
                return list;
            }

            public void onResponse(String response) {
                try {
                    int result = Integer.parseInt(response);
                    if (result > 0) {
                        alterSuccess();
                    } else if (result == -2) {
                        showToast("手机号错误");
                        finish();//返回上一个界面
                    } else if (result == -3) {
                        showToast("验证码错误");
                    } else {
                        showToast("网络错误");
                    }
                } catch (Exception e) {
                    showToast("网络错误");
                }
            }
        });
    }

    private void alterSuccess() {
        showToast("密码修改成功");
        wait_time = -1;// 终止计时线程
        setResult(Login.FORGET_JUMP_CODE, ForgetFind2.this.getIntent());//通知关闭
        Intent intent = new Intent(ForgetFind2.this, Login.class);//自动登录
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("phone", phone);
        intent.putExtra("password", pass1);
        startActivity(intent);
        finish();
    }

    // ////////计时部分//////////

    private void waitOneMinute() {
        btn_code.setEnabled(false);
        startNewThread();
    }

    @Override
    public void receiveMessage(String what) {
        if (what == null) {// 时钟更新
            if (wait_time > 0) {
                btn_code.setText("" + wait_time + "秒");
            } else {
                wait_time = CDDE_WAIT_TIME;
                btn_code.setEnabled(true);
                btn_code.setText("重新获取");
            }
        }
    }

    @Override
    public void newThread() {// 仅用于验证码倒计时
        while (wait_time > 0) {
            wait_time--;
            if (wait_time == 0) {
                sendMessage(null);
                break;// 否则由于主线程修改而造成死循环
            }
            sendMessage(null);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        wait_time = -1;// 终止计时线程
        super.onDestroy();
    }

    @Override
    public void showContextMenu() {
    }
}
