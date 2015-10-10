package org.outing.medicine.fun_know;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class KnowList extends TActivity {
	private String file_name;
	private ListView list;
	private ArrayList<String> array;

	@Override
	public void onCreate() {
		setContentView(R.layout.fun_know_list);
		 setTitle("健康知识");

		initMessage();
		initElement();
		initView();
	}

	private void initMessage() {
		Intent intent = getIntent();
		try {
			file_name = intent.getStringExtra("file_name");
		} catch (Exception e) {
			file_name = null;// 出错列表为空，什么都不显示
		}
	}

	private void initElement() {
		array = new ArrayList<String>();
		try {
			// 获取assets文件
			InputStream in = getResources().getAssets().open(file_name);
			// 转换为document
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);
			// 获取根元素
			Element root = document.getRootElement();
			List<Element> childList = root.elements();
			for (Element child : childList) {
				array.add(child.getName());
			}
		} catch (Exception e) {
		}
	}

	private void initView() {
		list = (ListView) findViewById(R.id.know_list_list);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();
		if (array.size() != 0) {
			item.put("name", "--联网获取知识--");
			items.add(item);
		}
		for (int i = 0; i < array.size(); i++) {
			item = new HashMap<String, Object>();
			item.put("name", array.get(i));
			items.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, items,
				R.layout.fun_know_list_item, new String[] { "name" },
				new int[] { R.id.know_list_item_title });
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {// 只添加点击即可
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				clickItem(position);
			}
		});
	}

	private void clickItem(int index) {
		// 跳转
		Intent intent = new Intent(this, KnowSublist.class);
		if (index == 0) {
			intent.putExtra("file_name", file_name);// 联网要获取的类型
			intent.putExtra("sub_name", "");
			intent.putExtra("from_net", true);
		} else {
			intent.putExtra("file_name", file_name);
			intent.putExtra("sub_name", array.get(index - 1));
			intent.putExtra("from_net", false);
		}
		startActivity(intent);
	}

	@Override
	public void showContextMenu() {
	}

}
