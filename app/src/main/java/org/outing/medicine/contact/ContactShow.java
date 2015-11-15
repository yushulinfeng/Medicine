package org.outing.medicine.contact;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.main_main.MainTabContact;
import org.outing.medicine.tools.base.TActivity;
import org.outing.medicine.tools.dialog.DialogTitleList;

public class ContactShow extends TActivity {
    private int index = -1;
    private String phone = "", name = "", relative = "", icon_path = "";
    private Button btn_call, btn_message;
    private ImageView iv_head;
    private TextView tv_name;
    private DialogTitleList dialog;
    private Intent main_tab_intent;

    @Override
    public void onCreate() {
        setContentView(R.layout.con_show);
        showBackButton();
        showMenuButton();// 修改联系人按钮

        getMessage();
        initText();
        initView();
        initDialog();
        showNameIcon();
    }

    private void getMessage() {
        main_tab_intent = getIntent();
        try {
            index = main_tab_intent.getIntExtra("index", -1);
        } catch (Exception e) {
        }
    }

    private void initText() {
        AnContact contact = ContactTool.getAnContact(this, index);
        phone = contact.getPhone();
        name = contact.getName();
        icon_path = contact.getIconPath();
        relative = contact.getRelative();
    }

    private void initView() {
        iv_head = (ImageView) findViewById(R.id.con_show_iv_head);
        tv_name = (TextView) findViewById(R.id.con_show_tv_name);
        btn_call = (Button) findViewById(R.id.con_show_btn_call);
        btn_message = (Button) findViewById(R.id.con_show_btn_message);
        btn_call.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + phone));
                startActivity(intent);
            }
        });
        btn_message.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse("smsto:" + phone));
                intent.putExtra("sms_body", "");
                startActivity(intent);
                finish();
            }
        });
    }

    private void initDialog() {
        dialog = new DialogTitleList(this).
                setListItem(new String[]{"修改", "删除"})
                .setListListener(new DialogTitleList.DialogItemListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position) {
                            case 0:
                                Intent con_add = new Intent(ContactShow.this, ContactAdd.class);
                                con_add.putExtra("index", index);
                                con_add.putExtra("is_add", false);
                                con_add.putExtra("name", name);
                                con_add.putExtra("phone", phone);
                                con_add.putExtra("relative", relative);
                                startActivityForResult(con_add, 0);
                                break;
                            case 1:
                                ContactTool.deleteAnContact(ContactShow.this, index);
                                showToast("删除成功");
                                setResult(MainTabContact.CODE_ALTER_SURE, main_tab_intent);
                                finish();
                                break;
                        }
                    }
                });
    }

    private void showNameIcon() {
        setTitle(name);
        tv_name.setText(name);
        try {
            iv_head.setImageBitmap(BitmapFactory.decodeFile(icon_path));
        } catch (Exception e) {
        }
    }

    @Override
    public void showContextMenu() {
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MainTabContact.CODE_ALTER_SURE) {
            initText();
            showNameIcon();
            setResult(resultCode, main_tab_intent);
            //不必finish
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
