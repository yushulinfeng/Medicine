package org.outing.medicine.fun_remind;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.ToDealBitmap;

import java.io.File;

public class AddDrug extends TActivity implements View.OnClickListener {
    private static final int CODE_ALBUM_START = 1;
    private static final int CODE_CAMERA_START = 2;
    private static final int CODE_CROP_START = 3;
    private EditText et_title, et_text;
    private ImageView iv_show;
    private Button btn_default, btn_camera, btn_album, btn_sure, btn_cancel;
    private String icon_path, drug_id;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_adddrug);
        setTitle("添加用药提醒");
        showBackButton();

        initLogic();
        initView();
    }

    private void initLogic() {
        drug_id = System.currentTimeMillis() + "";
        icon_path = new File(RemindTool.getIconSDPath(), drug_id + ".png").getAbsolutePath();
    }

    private void initView() {
        iv_show = (ImageView) findViewById(R.id.remind_adddrug_iv_drug);
        et_title = (EditText) findViewById(R.id.remind_adddrug_et_name);
        et_text = (EditText) findViewById(R.id.remind_adddrug_et_text);
        btn_default = (Button) findViewById(R.id.remind_adddrug_btn_default);
        btn_camera = (Button) findViewById(R.id.remind_adddrug_btn_camera);
        btn_album = (Button) findViewById(R.id.remind_adddrug_btn_album);
        btn_sure = (Button) findViewById(R.id.remind_adddrug_btn_sure);
        btn_cancel = (Button) findViewById(R.id.remind_adddrug_btn_cancel);
        btn_default.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_album.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    // 调用系统裁剪
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 宽高的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);// 裁剪图片宽高
        intent.putExtra("outputY", 150);
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
            case R.id.remind_adddrug_btn_default:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                ///////////////////////////////////更换图片
                saveBitmap(bitmap);
                iv_show.setImageBitmap(bitmap);
                break;
            case R.id.remind_adddrug_btn_camera:
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(icon_path)));
                startActivityForResult(intent2, CODE_CAMERA_START);
                break;
            case R.id.remind_adddrug_btn_album:
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, CODE_ALBUM_START);
                break;
            case R.id.remind_adddrug_btn_sure:
                String name = et_title.getText().toString();
                String text = et_text.getText().toString();
                if (name.equals("")) {
                    showToast("药品名称不能为空！");
                    return;
                }
                RemindTool.addDrug(this, new AnRemind(drug_id, name, text));
                finish();
                break;
            case R.id.remind_adddrug_btn_cancel:
                finish();
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
                }
                break;
        }
    }

}
