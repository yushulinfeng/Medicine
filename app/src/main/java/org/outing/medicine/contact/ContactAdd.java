package org.outing.medicine.contact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.outing.medicine.R;
import org.outing.medicine.main_main.MainTabContact;
import org.outing.medicine.tools.base.TActivity;
import org.outing.medicine.tools.utils.ToDealBitmap;
import org.outing.medicine.tools.dialog.DialogTitleList;
import org.outing.medicine.tools.file.FileTool;

import java.io.File;

public class ContactAdd extends TActivity implements OnClickListener {
    public static final int CODE_SELECT_START = 2;
    public static final int CODE_SELECT_SURE = 3;
    public static final int CODE_ALBUM_START = 4;
    public static final int CODE_CAMERA_START = 5;
    public static final int CODE_CROP_START = 6;
    private final static String path = "Contact";
    private int index = -1;
    private boolean is_add = true;
    private String icon_path = "";
    private ImageView iv_head;
    private EditText et_name, et_phone, et_relative;
    private Button btn_contact, btn_show, btn_sure;
    private DialogTitleList dialog;
    private boolean is_add_headicon = false;
    private String name = "", phone = "", relative = "";

    @Override
    public void onCreate() {
        setContentView(R.layout.con_add);
        showBackButton();
        getMessage();
        initPath();
        if (is_add)
            setTitle("添加联系人");
        else
            setTitle("修改联系人");
        initView();
        initDialog();
    }

    private void initPath() {
        File file = new File(FileTool.getBaseSDCardPath(), path);
        if (!file.exists())
            file.mkdirs();
        icon_path = new File(file, "/head" + index + ".jpg").getAbsolutePath();
        is_add_headicon = false;
    }

    private void getMessage() {
        Intent intent = getIntent();
        if (intent == null) {
            index = -1;
            return;
        }
        index = intent.getIntExtra("index", -1);
        is_add = intent.getBooleanExtra("is_add", true);

        if (is_add) return;
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        relative = intent.getStringExtra("relative");
    }

    private void initView() {
        iv_head = (ImageView) findViewById(R.id.con_add_iv_head);
        et_name = (EditText) findViewById(R.id.con_add_et_name);
        et_phone = (EditText) findViewById(R.id.con_add_et_number);
        et_relative = (EditText) findViewById(R.id.con_add_et_relative);
        btn_contact = (Button) findViewById(R.id.con_add_btn_contact);
        btn_show = (Button) findViewById(R.id.con_add_btn_head);
        btn_sure = (Button) findViewById(R.id.con_add_btn_sure);
        btn_contact.setOnClickListener(this);
        btn_show.setOnClickListener(this);
        btn_sure.setOnClickListener(this);

        if (is_add || index == -1) return;
        et_name.setText(name);
        et_phone.setText(phone);
        et_relative.setText(relative);
        Bitmap bitmap = BitmapFactory.decodeFile(icon_path);
        iv_head.setImageBitmap(bitmap);
        btn_show.setCompoundDrawables(null, null, null, null);
        btn_show.setText("");
        is_add_headicon = true;
    }

    private void initDialog() {
        dialog = new DialogTitleList(this).
                setListItem(new String[]{"相机拍照", "相册选择", "默认图片"})
                .setListListener(new DialogTitleList.DialogItemListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position) {
                            case 0:
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(new File(icon_path)));
                                startActivityForResult(intent2, CODE_CAMERA_START);
                                break;
                            case 1:
                                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                                intent1.setDataAndType(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent1, CODE_ALBUM_START);
                                break;
                            case 2:
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.contact_default);
                                saveBitmap(bitmap);
                                iv_head.setImageBitmap(bitmap);// 用ImageView显示出来
                                //处理button
                                btn_show.setCompoundDrawables(null, null, null, null);
                                btn_show.setText("");
                                is_add_headicon = true;
                                break;
                        }
                    }
                });
    }

    // 调用系统裁剪
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 宽高的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);// 裁剪图片宽高
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_CROP_START);
    }

    // 保存图片
    private void saveBitmap(Bitmap mBitmap) {
        mBitmap = ToDealBitmap.zipBitmap(mBitmap, 300, 300, true);// 压缩图片使之小于等于300*300
        ToDealBitmap.writeToFile(mBitmap, "png", icon_path);
    }

    @Override
    public void showContextMenu() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.con_add_btn_contact:
                startActivityForResult(new Intent(this, ContactSelect.class),
                        CODE_SELECT_START);
                break;
            case R.id.con_add_btn_head:
                dialog.show();
                break;
            case R.id.con_add_btn_sure:
                if (index == -1) {
                    showToast("系统错误，处理失败");
                    finish();
                    return;
                }
                name = et_name.getText().toString();
                if (name.trim().equals("")) {
                    showToast("请输入联系人姓名");
                    return;
                }
                phone = et_phone.getText().toString();
                if (phone.equals("")) {
                    showToast("请输入联系人电话");
                    return;
                }
                relative = et_relative.getText().toString();
                if (relative.equals("")) {
                    showToast("请输入联系人关系");
                    return;
                }
                if (!is_add_headicon) {//没有添加头像，就用默认的
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.contact_default);
                    saveBitmap(bitmap);
                }
                ContactTool.saveAnContact(this, index, new AnContact(name, phone, relative,
                        icon_path));
                if (is_add)
                    setResult(MainTabContact.CODE_ADD_SURE, getIntent());
                else
                    setResult(MainTabContact.CODE_ALTER_SURE, getIntent());
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_SELECT_START:
                if (resultCode == CODE_SELECT_SURE) {
                    et_name.setText(data.getStringExtra("name"));
                    et_phone.setText(data.getStringExtra("phone"));
                }
                break;
            case CODE_ALBUM_START:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case CODE_CAMERA_START:
                if (resultCode == RESULT_OK) {
                    File temp = new File(icon_path);
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }
                break;
            case CODE_CROP_START:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap head = extras.getParcelable("data");
                    if (head != null) {
                        saveBitmap(head);// 保存在SD卡中，并同步到用户信息
                        iv_head.setImageBitmap(head);// 用ImageView显示出来
                        //处理button
                        btn_show.setCompoundDrawables(null, null, null, null);
                        btn_show.setText("");
                        is_add_headicon = true;
                    }
                }
                break;
        }
    }

}
