package org.outing.medicine.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ContactSelect extends TActivity {
	private TextView tv_load;
	private ListView list;
	private List<Map<String, Object>> listItems;

	@Override
	public void onCreate() {
		setContentView(R.layout.con_contact);
		setTitle("选择联系人");
		// 直接读取竟然会卡顿
		new Handler().postDelayed(new Runnable() {
			public void run() {
				initContact();
				initList();
				tv_load.setText("");
			}
		}, 500);
	}

	private void initContact() {
		listItems = new ArrayList<Map<String, Object>>();
		// 得到contentresolver对象
		ContentResolver cr = getContentResolver();
		// 取得电话本中开始一项的光标，必须先moveToNext()
		Cursor cursor = cr.query(Contacts.CONTENT_URI, null, null, null, null);
		while (cursor.moveToNext()) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			// 取得联系人的名字索引
			int nameIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contact = cursor.getString(nameIndex);
			listItem.put("name", contact);
			// 取得联系人的ID索引值
			String contactId = cursor.getString(cursor
					.getColumnIndex(Contacts._ID));
			// 查询该位联系人的电话号码，类似的可以查询email，photo
			Cursor phone = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID
					+ " = " + contactId, null, null);
			// 一个人可能有几个号码
			while (phone.moveToNext()) {
				String strPhoneNumber = phone.getString(phone
						.getColumnIndex(Phone.NUMBER));
				listItem.put("phone", strPhoneNumber);
				break;// 此处只获取第一个号码
			}
			phone.close();
			listItems.add(listItem);
		}
		cursor.close();
	}

	private void initList() {
		tv_load = (TextView) findViewById(R.id.con_contact_load);
		list = (ListView) findViewById(R.id.con_contact_list);
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.con_contact_item, new String[] { "name", "phone" },
				new int[] { R.id.con_contact_item_name,
						R.id.con_contact_item_number });
		list.setAdapter(simpleAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name = (String) listItems.get(position).get("name");
				String phone = (String) listItems.get(position).get("phone");
				Intent intent = getIntent();
				intent.putExtra("name", name);
				intent.putExtra("phone", phone);
				setResult(ContactAdd.CODE_SELECT_SURE, intent);
				finish();
			}
		});
	}

	@Override
	public void showContextMenu() {
	}

}
