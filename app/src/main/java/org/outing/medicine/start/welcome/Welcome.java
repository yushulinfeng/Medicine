package org.outing.medicine.start.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.outing.medicine.R;
import org.outing.medicine.main_main.MainActivity;
import org.outing.medicine.start.login.Login;
import org.outing.medicine.start.tool.UserTool;
import org.outing.medicine.tools.utils.ToastTool;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;


public class Welcome extends Activity {
    private String name = "", pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_welcome);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (UserTool.isFirstRun(Welcome.this)) {
                    startActivity(new Intent(Welcome.this, FirstRun.class));
                    finish();
                } else {
                    autoLogin();
                }
            }
        }, 500);// 时间可以慢慢调整
    }

    public void autoLogin() {
        String[] user = UserTool.getUser(this);
        name = user[0];
        if (name.equals("")) {// 未登录过
            pass = "";
            toLoginActivity();
            return;
        }
        // 有登录记录，到login中自动登录（减少重复代码）
        pass = user[1];
        startLogin();
    }

    public void toLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLogin() {
        Connect.POST(this, ServerURL.LOGIN, new ConnectListener() {
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;
            }

            public ConnectList setParam(ConnectList list) {
                list.put("phone", name);
                list.put("password", pass);
                return list;
            }

            public void onResponse(String response) {
                try {
                    int result = Integer.parseInt(response);
                    if (result > 0) {//登录成功
                        toMainActivity();
                    } else {
                        ToastTool.showToast(Welcome.this, "用户名或密码错误");
                        toLoginActivity();
                    }
                } catch (Exception e) {//错误不必详细处理
                    ToastTool.showToast(Welcome.this, "网络错误");
                    toLoginActivity();
                }
            }
        });
    }

}
