package org.outing.medicine.contact;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactShow extends TActivity {
	private int index = -1;
	private String phone = "", name = "", icon_path = "";
	private Button btn_call, btn_message;
	private ImageView iv_head;
	private TextView tv_name;
	//设置activity,为了以后在其他activity中关闭
	public static ContactShow instance = null;

	@Override
	public void onCreate() {
		setContentView(R.layout.con_show);
		instance=this;
		getMessage();
		showBackButton();
		showMenuButton();// 修改联系人按钮

		AnContact contact = ContactTool.getAnContact(this, index);
		phone = contact.getPhone();
		if (phone.equals("")) {// 电话为空，跳转到add
			Intent con_add = new Intent(this, ContactAdd.class);
			con_add.putExtra("index", index);
			con_add.putExtra("is_add", true);
			startActivity(con_add);
			finish();
			return;
		}
		name = contact.getName();
		icon_path = contact.getIconPath();
		setTitle(contact.getName());
		initView();
		showNameIcon();
	}

	private void getMessage() {
		Intent intent = getIntent();
		try {
			index = intent.getIntExtra("index", -1);
		} catch (Exception e) {
		}
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

	private void showNameIcon() {
		tv_name.setText(name);
		try {
			iv_head.setImageBitmap(BitmapFactory.decodeFile(icon_path));
		} catch (Exception e) {
		}
	}

	@Override
	public void showContextMenu() {// ////////////////////多层返回,for_resullt
		Intent con_add = new Intent(this, ContactAdd.class);
		con_add.putExtra("index", index);
		con_add.putExtra("is_add", false);
		startActivity(con_add);
	}

}
