package org.outing.medicine.fun_remind;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.ToDealBitmap;
import org.outing.medicine.tools.dialog.DialogTitleList;

import java.io.File;

public class AddRemind extends TActivity implements View.OnClickListener {
    private static final int CODE_ALBUM_START = 1;
    private static final int CODE_CAMERA_START = 2;
    private static final int CODE_CROP_START = 3;
    private EditText et_title, et_text;
    private ImageView iv_show;
    private Button btn_sure, btn_show;
    private String icon_path, drug_id, drug_name, drug_text;
    private boolean is_alter = false;
    private DialogTitleList dialog;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_adddrug);
        setTitle("添加用药提醒");
        showBackButton();

        initMessage();
        initDialog();
        initView();
        if (is_alter) {//修改
            setTitle("修改用药提醒");
        } else {//添加
            initLogic();
            showKeyboard();
        }
    }

    private void initMessage() {
        Intent intent = getIntent();
        try {
            is_alter = intent.getBooleanExtra("is_alter", false);
        } catch (Exception e) {
            is_alter = false;
        }
        //不是修改直接返回即可
        if (!is_alter)
            return;
        try {
            drug_id = intent.getStringExtra("drug_id");
            drug_name = intent.getStringExtra("drug_name");
            drug_text = intent.getStringExtra("drug_text");
            icon_path = intent.getStringExtra("icon_path");
        } catch (Exception e) {
            is_alter = false;
        }
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
                                        R.drawable.fun_remind_drug_default);
                                saveBitmap(bitmap);
                                iv_show.setImageBitmap(bitmap);
                                //处理button
                                btn_show.setCompoundDrawables(null, null, null, null);
                                btn_show.setText("");
                                break;
                        }
                    }
                });
    }

    private void initView() {
        iv_show = (ImageView) findViewById(R.id.remind_adddrug_iv_drug);
        et_title = (EditText) findViewById(R.id.remind_adddrug_et_name);
        et_text = (EditText) findViewById(R.id.remind_adddrug_et_text);
        btn_show = (Button) findViewById(R.id.remind_adddrug_btn_drug);
        btn_sure = (Button) findViewById(R.id.remind_adddrug_btn_sure);
        btn_show.setOnClickListener(this);
        btn_sure.setOnClickListener(this);

        if (is_alter) {
            et_title.setText(drug_name);
            et_title.setSelection(drug_name.length());// 定位光标到最后
            et_text.setText(drug_text);
            if (icon_path == null || icon_path.equals("") || !new File(icon_path).exists())
                iv_show.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.fun_remind_drug_default));//加载默认图片
            else
                iv_show.setImageBitmap(BitmapFactory.decodeFile(icon_path));
            //处理button
            btn_show.setCompoundDrawables(null, null, null, null);
            btn_show.setText("");
        }
    }

    private void initLogic() {
        drug_id = System.currentTimeMillis() + "";
        icon_path = new File(RemindTool.getIconSDPath(), drug_id + ".png").getAbsolutePath();
    }

    private void showKeyboard() {
        //由于某种原因，它不能自动弹出来，只能在此处调用了
        new Handler().postDelayed(new Runnable() {
            public void run() {
                et_title.requestFocus();
                InputMethodManager imm = (InputMethodManager) et_title.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);//弹出键盘
            }
        }, 100);
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
            case R.id.remind_adddrug_btn_sure:
                drug_name = et_title.getText().toString();
                drug_text = et_text.getText().toString();
                if (drug_name.equals("")) {
                    showToast("药品名称不能为空！");
                    return;
                }
                if (drug_name.contains(RemindHistory.HIS_SPLITE)) {//闹钟历史的分隔符
                    drug_name = drug_name.replace(RemindHistory.HIS_SPLITE, " ");
                }
                AnRemind remind_temp = new AnRemind(drug_id, drug_name, drug_text);
                if (is_alter) {
                    RemindTool.alterDrug(this, remind_temp);
                    showToast("修改成功");
                } else {
                    RemindTool.addDrug(this, remind_temp);
                    showToast("添加成功");
                }
                finish();
                break;
            case R.id.remind_adddrug_btn_drug:
                dialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
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
                if (data == null)
                    return;
                Bundle extras = data.getExtras();
                Bitmap head = extras.getParcelable("data");
                if (head != null) {
                    saveBitmap(head);// 保存在SD卡中，并同步到用户信息
                    iv_show.setImageBitmap(head);// 用ImageView显示出来
                    //处理button
                    btn_show.setCompoundDrawables(null, null, null, null);
                    btn_show.setText("");
                }
                break;
        }
    }

}
