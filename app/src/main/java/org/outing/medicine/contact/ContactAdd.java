package org.outing.medicine.contact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.outing.medicine.R;
import org.outing.medicine.main_main.MainActivity;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.ToDealBitmap;

import java.io.File;

public class ContactAdd extends TActivity implements OnClickListener {
	public static final int CODE_SELECT_START = 2;
	public static final int CODE_SELECT_SURE = 3;
	public static final int CODE_ALBUM_START = 4;
	public static final int CODE_CAMERA_START = 5;
	public static final int CODE_CROP_START = 6;
	private int index = -1;
	private boolean is_add = true;
	private String icon_path = "";
	private ImageView iv_head;
	private EditText et_name, et_phone,et_relative;
	private Button btn_contact, btn_camera, btn_album, btn_sure, btn_cancel;
	private String path = "Contact";

	@Override
	public void onCreate() {
		setContentView(R.layout.con_add);
		getMessage();
		initPath();
		if (is_add)
			setTitle("添加联系人");
		else
			setTitle("修改联系人");
		initView();
	}

	private void initPath() {
		String base_path = new File(Environment.getExternalStorageDirectory(),
				"Medicine").getAbsolutePath();
		path = base_path + path;
		File file = new File(path);
		if (!file.exists())// 如果不存在
			file.mkdirs();// 创建文件夹
		icon_path = path + "/head" + index + ".jpg";
	}

	private void getMessage() {
		Intent intent = getIntent();
		try {
			index = intent.getIntExtra("index", -1);
			is_add = intent.getBooleanExtra("is_add", true);
		} catch (Exception e) {
		}
	}

	private void initView() {
		iv_head = (ImageView) findViewById(R.id.con_add_iv_head);
		et_name = (EditText) findViewById(R.id.con_add_et_name);
		et_phone = (EditText) findViewById(R.id.con_add_et_number);
		et_relative=(EditText)findViewById(R.id.con_add_et_relative);
		btn_contact = (Button) findViewById(R.id.con_add_btn_contact);
		btn_camera = (Button) findViewById(R.id.con_add_btn_camera);
		btn_album = (Button) findViewById(R.id.con_add_btn_album);
		btn_sure = (Button) findViewById(R.id.con_add_btn_sure);
		btn_cancel = (Button) findViewById(R.id.con_add_btn_cancel);
		btn_contact.setOnClickListener(this);
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
		case R.id.con_add_btn_contact:
			startActivityForResult(new Intent(this, ContactSelect.class),
					CODE_SELECT_START);
			break;
		case R.id.con_add_btn_camera:
			Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent2.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(icon_path)));
			startActivityForResult(intent2, CODE_CAMERA_START);
			break;
		case R.id.con_add_btn_album:
			Intent intent1 = new Intent(Intent.ACTION_PICK, null);
			intent1.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent1, CODE_ALBUM_START);
			break;
		case R.id.con_add_btn_sure:
			String name = et_name.getText().toString();
			String phone = et_phone.getText().toString();
			String relative=et_relative.getText().toString();
			ContactTool.saveAnContact(this, index, new AnContact(name, phone,relative,
					icon_path));
			Intent intent = new Intent(ContactAdd.this,MainActivity.class);
			intent.putExtra("page",1);
			startActivity(intent);
			//删除Mainactivity,ContactShow
			MainActivity.instance.finish();
			ContactShow.instance.finish();
			finish();
			break;
		case R.id.con_add_btn_cancel:
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
				}
			}
			break;
		default:
			break;
		}
	}

}
