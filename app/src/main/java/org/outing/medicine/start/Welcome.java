package org.outing.medicine.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.outing.medicine.R;
import org.outing.medicine.main_main.MainActivity;
import org.outing.medicine.tools.NetActivity;
import org.outing.medicine.tools.connect.AnStatus;
import org.outing.medicine.tools.connect.ConnectUser;

public class Welcome extends NetActivity {
    public static final String NAME = "name";
    public static final String PASS = "pass";
    private String name = "", pass = "";

    @Override
    public void onCreate() {
        setContentView(R.layout.welcome);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (UserTool.isFirstRun(Welcome.this)) {
                    startActivity(new Intent(Welcome.this, FirstRun.class));
                    finish();
                } else {
                    autoLogin();
                }
            }
        }, 500);
    }

    /**
     * 自动登录判断
     */
    private void autoLogin() {
        name = UserTool.getUserName(this);
        if (name.equals("")) {
            toLogin();
        } else {
            pass = UserTool.getUserPass(this);
            startNewThread();
        }
    }

    /**
     * 跳到登录界面
     */
    private void toLogin() {
        Intent intent = new Intent(Welcome.this, Login.class);
        Bundle bundle = new Bundle();
        bundle.putString(NAME, name);
        if (pass != null && !pass.equals(""))
            pass = UserTool.getTempPass(this);
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
