package org.outing.medicine.start.register;


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

public class Register2 extends NetTActivity implements View.OnClickListener {
    private static final int CDDE_WAIT_TIME = 60;// 验证码等待时间
    private int wait_time = CDDE_WAIT_TIME;
    private EditText et_code, et_pass1, et_pass2, et_codecode;
    private Button btn_code, btn_next;
    private String phone, code, pass1, pass2, codecode;

    @Override
    public void onCreate() {
        setContentView(R.layout.start_register2);
        setTitle("设置密码");
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
        et_code = (EditText) findViewById(R.id.register2_et_code);
        et_pass1 = (EditText) findViewById(R.id.register2_et_pass1);
        et_pass2 = (EditText) findViewById(R.id.register2_et_pass2);
        et_codecode = (EditText) findViewById(R.id.register2_et_codecode);
        btn_code = (Button) findViewById(R.id.register2_btn_getcode);
        btn_next = (Button) findViewById(R.id.register2_btn_next);
        btn_code.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register2_btn_getcode:
                getCode();
                break;
            case R.id.register2_btn_next:
                nextStep();
                break;
        }
    }

    private void getCode() {
        waitOneMinute();
        Connect.POST(this, ServerURL.ID_CODE, new ConnectListener() {
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;// 不显示对话框
            }

            public ConnectList setParam(ConnectList list) {
                list.put("username", phone);
                return list;
            }

            public void onResponse(String response) {
                // 这个就不用管了
            }
        });
        showToast("验证码已发送");//这里就这样了，不能太真实
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
        codecode = et_codecode.getText().toString();//优惠码，不必判断了

        if (code.equals("000000")) {// ////////测试专用
            registerSuccess();
            return;
        }
        sendToServer();
    }

    private void sendToServer() {
        Connect.POST(this, ServerURL.REGISTER, new ConnectListener() {
            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(Register2.this, "正在注册", "请稍候……", false);
                return dialog;
            }

            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("phone", phone);
                list.put("password", pass1);
                list.put("coupon", codecode);
                list.put("code", code);
                return list;
            }

            @Override
            public void onResponse(String response) {
                if (response == null)
                    showToast("连接服务器失败");
                else {
                    try {
                        int result = Integer.parseInt(response);
                        if (result > 0) {
                            registerSuccess();
                        } else if (result == -1) {
                            showToast("注册失败");
                        } else if (result == -2) {//这样弥补一下比较合适吧
                            showToast("手机号已被注册");
                            finish();//返回上一个界面
                        } else {//-3
                            showToast("密码过短");
                        }
                    } catch (Exception e) {
                        showToast("系统错误");
                    }
                }
            }
        });
    }

    private void registerSuccess() {
        showToast("注册成功");
        wait_time = -1;// 终止计时线程
        setResult(Login.REGISTER_JUMP_CODE, Register2.this.getIntent());//通知关闭
        Intent intent = new Intent(this, Login.class);
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
