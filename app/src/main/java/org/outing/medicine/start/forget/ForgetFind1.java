package org.outing.medicine.start.forget;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.outing.medicine.R;
import org.outing.medicine.start.login.Login;
import org.outing.medicine.start.tool.SafeCheck;
import org.outing.medicine.tools.base.TActivity;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;


public class ForgetFind1 extends TActivity {
    private EditText et_phone;
    private String phone = "";
    private Button btn_next;

    @Override
    public void onCreate() {
        setContentView(R.layout.start_forget_find1);
        setTitle("密码找回");
        showBackButton();

        initView();
    }

    private void initView() {
        et_phone = (EditText) findViewById(R.id.forget_find1_et_name);
        btn_next = (Button) findViewById(R.id.forget_find1_btn_next);
        btn_next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                gotoNext();
            }
        });
    }

    private void gotoNext() {
        phone = et_phone.getText().toString();
        if (phone.equals("000000")) {// ////测试专用
            Intent intent = new Intent(ForgetFind1.this, ForgetFind2.class);
            intent.putExtra("phone", phone);
            startActivityForResult(intent, 0);
            finish();
            return;
        }
        if (SafeCheck.isPhone(phone)) {
            btn_next.setEnabled(false);
            Connect.POST(this, ServerURL.ID_CODE, new ConnectListener() {
                @Override
                public ConnectDialog showDialog(ConnectDialog dialog) {
                    dialog.config(ForgetFind1.this, "正在连接服务器", "请稍候……", true);
                    return dialog;
                }

                @Override
                public ConnectList setParam(ConnectList list) {
                    list.put("phone", phone);
                    return list;
                }

                @Override
                public void onResponse(String response) {
                    btn_next.setEnabled(true);
                    if (response == null) {
                        showToast("连接服务器失败");
                    } else if (response.equals("1")) {
                        showToast("验证码发送成功");
                        Intent intent = new Intent(ForgetFind1.this,
                                ForgetFind2.class);
                        intent.putExtra("phone", phone);
                        startActivityForResult(intent, 0);
                    } else {
                        showToast("网络错误");
                    }
                }
            });
        } else {
            showToast("请输入正确的号码");
        }
    }

    @Override
    public void showContextMenu() {
    }

    // 返回处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Login.FORGET_JUMP_CODE) {
            // 产生跳转，通知上一页关闭，并就关闭本页
            setResult(Login.FORGET_JUMP_CODE, ForgetFind1.this.getIntent());//通知关闭
            finish();
        }
    }
}
