package org.outing.medicine.main_main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.outing.medicine.R;
import org.outing.medicine.contact.ContactAdd;
import org.outing.medicine.contact.ContactShow;
import org.outing.medicine.contact.ContactTool;
import org.outing.medicine.tools.ToDealBitmap;

public class MainTabContact extends Fragment implements OnClickListener {
	public Context context;
	public Activity activity;
	public static final int CODE_ADD_START = 0;
	public static final int CODE_ADD_SURE = 1;
	private final int[] btn_ids = { R.id.contact_btn_contact1,
			R.id.contact_btn_contact2, R.id.contact_btn_contact3,
			R.id.contact_btn_contact4, R.id.contact_btn_contact5,
			R.id.contact_btn_contact6 };
	private final int[] head_ids = { R.id.contact_iv_contact1,
			R.id.contact_iv_contact2, R.id.contact_iv_contact3,
			R.id.contact_iv_contact4, R.id.contact_iv_contact5,
			R.id.contact_iv_contact6 };
	private final int[] name_ids = { R.id.contact_tv_contact1,
			R.id.contact_tv_contact2, R.id.contact_tv_contact3,
			R.id.contact_tv_contact4, R.id.contact_tv_contact5,
			R.id.contact_tv_contact6 };
	private final int[] relative_ids={R.id.contact_tv_relative1,
			R.id.contact_tv_relative2,R.id.contact_tv_relative3,
			R.id.contact_tv_relative4,R.id.contact_tv_relative5,
			R.id.contact_tv_relative6};
	private Button[] btns = new Button[6];
	private ImageView[] heads = new ImageView[6];
	private TextView[] names = new TextView[6];
	private TextView[] relatives=new TextView[6];

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context=getActivity();
		activity=getActivity();
		View view = inflater.inflate(R.layout.tab_right_my, container, false);
		initViews(view);
		return view;
	}

	private void initViews(View view) {
		for (int i = 0; i < btns.length; i++) {
			btns[i] = (Button) view.findViewById(btn_ids[i]);
			heads[i] = (ImageView) view.findViewById(head_ids[i]);
			names[i] = (TextView) view.findViewById(name_ids[i]);
			relatives[i]=(TextView)view.findViewById(relative_ids[i]);
			btns[i].setOnClickListener(this);
			registerForContextMenu(btns[i]);

			//设置姓名
			try {
				String name=ContactTool.getAnContact(getActivity(), i).getName();
				if (name.equals("")){
					names[i].setText("添加联系人");
				}else {
					names[i].setText(name);
				}

			} catch (Exception e) {
			}
			//设置联系人关系
			try {
				String relative=ContactTool.getAnContact(getActivity(), i).getRelative();
				if (relative.equals("")){
					relatives[i].setText("");
				}else {
					relatives[i].setText(relative);
				}

			} catch (Exception e) {
			}
			//设置图片
			try {
				String imagePath=ContactTool.getAnContact(getActivity(), i).getIconPath();
				Bitmap bitmap= null;
				bitmap=ToDealBitmap.getFromFile(imagePath);
				if (bitmap==null){
					heads[i].setImageResource(R.drawable.headicon_default);
				}else {
					heads[i].setImageBitmap(bitmap);
				}}catch (Exception e) {
			}

		}
	}

	private int getButtonIndex(int id) {
		int location = -1;
		switch (id) {
		case R.id.contact_btn_contact1:
			location = 0;
			break;
		case R.id.contact_btn_contact2:
			location = 1;
			break;
		case R.id.contact_btn_contact3:
			location = 2;
			break;
		case R.id.contact_btn_contact4:
			location = 3;
			break;
		case R.id.contact_btn_contact5:
			location = 4;
			break;
		case R.id.contact_btn_contact6:
			location = 5;
			break;
		}
		return location;
	}

	// 点击响应
	@Override
	public void onClick(View v) {
		int index = getButtonIndex(v.getId());
		if (index == -1)
			return;
		String number = "";
		try {
			number = ContactTool.getAnContact(getActivity(), index).getPhone();
		} catch (Exception e) {
			number = "";
		}
		if (number.equals("")) {
			Intent con_add = new Intent(getActivity(), ContactAdd.class);
			con_add.putExtra("index", index);
			con_add.putExtra("is_add", true);
			startActivityForResult(con_add, CODE_ADD_START);
		} else {
			Intent con_show = new Intent(getActivity(), ContactShow.class);
			con_show.putExtra("index", index);
			startActivity(con_show);
		}
	}

	// 长按响应，直接拨打电话
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		int index = getButtonIndex(v.getId());
		if (index == -1)
			return;
		String number = "";
		try {
			number = ContactTool.getAnContact(getActivity(), index).getPhone();
		} catch (Exception e) {
			number = "";
		}
		if (number.equals("")) {
			Toast.makeText(getActivity(), "请先设置联系人", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ number));
			startActivity(intent);
		}
	}
}
