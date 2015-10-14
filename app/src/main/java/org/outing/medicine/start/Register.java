package org.outing.medicine.start;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.outing.medicine.R;
import org.outing.medicine.tools.NetActivity;
import org.outing.medicine.tools.connect.ConnectStatus;
import org.outing.medicine.tools.connect.ConnectUser;

public class Register extends NetActivity implements OnClickListener {
    private static final int CDDE_WAIT_TIME = 60;//验证码等待时间
    // intent的标签
    public static final String NAMEFLAG = "nameFlag";
    public static final String PASSWORDFLAG = "passwordFlag";

    private EditText userNameEdit = null;
    private EditText verifyEdit = null;
    private EditText passwordEdit = null;
    private EditText passwordConfirmEdit = null;
    private Button gainCode = null;
    private Button registerBt = null;
    private Button backToLoginBt = null;
    private String userName = null;
    private String verifyCode = null;
    private String password = null;
    private String passwordConfirm = null;

    private ConnectUser conn = null;
    private boolean is_idcode = true;
    private int wait_time = CDDE_WAIT_TIME;

    @Override
    public void onCreate() {
        setContentView(R.layout.register);
        conn = new ConnectUser(this, true);
        init();
    }

    /**
     * 初始化组件
     */
    private void init() {
        userNameEdit = (EditText) findViewById(R.id.register_username);
        verifyEdit = (EditText) findViewById(R.id.verify_code);
        passwordEdit = (EditText) findViewById(R.id.register_password);
        passwordConfirmEdit = (EditText) findViewById(R.id.register_password_confirm);
        gainCode = (Button) findViewById(R.id.gain_code);
        registerBt = (Button) findViewById(R.id.register_bt);
        backToLoginBt = (Button) findViewById(R.id.back_to_login);
        gainCode.setOnClickListener(this);
        registerBt.setOnClickListener(this);
        backToLoginBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            // 获取验证码
            case R.id.gain_code:
                userName = userNameEdit.getText().toString();
                if (UserCheck.verifyUsername(this, userName)) {
                    gainVerifyCode(userName);
                    waitOneMinute();
                }
                break;
            // 注册
            case R.id.register_bt:
                userName = userNameEdit.getText().toString();
                verifyCode = verifyEdit.getText().toString();
                password = passwordEdit.getText().toString();
                passwordConfirm = passwordConfirmEdit.getText().toString();

                // 用户名、验证码及两次密码验证成功，发到后台数据库进行存储及跳转到登陆成功界面
                if (UserCheck.verifyUsername(this, userName)
                        && UserCheck.verifyCode(this, verifyCode)
                        && UserCheck.verifyPassword(this, password, passwordConfirm)) {
                    register(userName, password);
                }
                break;
            case R.id.back_to_login:
                intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                this.finish();
            default:
                break;
        }
    }

    /**
     * 将用户的用户名、密码、时间发到后台存储
     *
     * @param username
     * @param password
     */
    private void register(String username, String password) {
        is_idcode = false;
        showProcessDialog("注册", "正在连接服务器，请稍候……", false);
        startNewThread();
    }

    /**
     * 通知后台发送验证码给邮箱或手机 ;
     * <p/>
     * 需要区分用户用来注册的是手机号还是邮箱
     *
     * @param username
     */
    private void gainVerifyCode(String username) {
        is_idcode = true;
        startNewThread();
    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 注册成功，跳转到注册成功界面
     */
    private void startIntent() {
        Intent succeedIntent = new Intent(Register.this, RegisterSucceed.class);
        succeedIntent.putExtra(NAMEFLAG, userName);
        succeedIntent.putExtra(PASSWORDFLAG, password);
        startActivity(succeedIntent);
        this.finish();
    }

    private void waitOneMinute() {
        gainCode.setEnabled(false);
        new Thread() {
            public void run() {
                while (wait_time > 0) {
                    wait_time--;
                    if (wait_time == 0) {
                        sendMessage(null);
                        break;//否则由于主线程修改而造成死循环
                    }
                    sendMessage(null);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    @Override
    public void receiveMessage(String what) {
        if (what == null) {//时钟更新
            if (wait_time > 0) {
                gainCode.setText("（" + wait_time + "秒）");
            } else {
                wait_time = CDDE_WAIT_TIME;
                gainCode.setEnabled(true);
                gainCode.setText("获取验证码");
            }
        } else if (what.equals("idcode_ok")) {
//			showToast("验证码已发送，请查收");
            showToast("请随便输入验证码");
        } else if (what.equals("register_ok")) {
            wait_time = -1;//终止循环
            showToast("注册成功");
            startIntent();
        } else {
            showToast(what);
        }
    }

    @Override
    public void newThread() {
        ConnectStatus status = null;
        if (is_idcode) {
            status = conn.getIDCode(userName);
//			if (status.getStatus())
            sendMessage("idcode_ok");
//			else
//				sendMessage(status.getReason());
        } else {
            status = conn.register(userName, password, verifyCode);
            if (status.getStatus())
                sendMessage("register_ok");
            else
                sendMessage(status.getReason());
        }
    }

}
