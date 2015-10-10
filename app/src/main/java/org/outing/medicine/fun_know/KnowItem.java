package org.outing.medicine.fun_know;

import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.outing.medicine.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class KnowItem extends Activity {
	private String file_name, sub_name, item_name;
	private String title, essay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.fun_know_item);

	}

	private void initMessage() {
		Intent intent = getIntent();
		try {
			file_name = intent.getStringExtra("file_name");
			sub_name = intent.getStringExtra("sub_name");
			item_name = intent.getStringExtra("item_name");
		} catch (Exception e) {
			file_name = null;
			sub_name = null;
			item_name = null;
			// /////////////////// 后期处理
		}
	}

	private void initEssay() {
		Document document = null;
		try {
			// 获取assets文件
			InputStream in = getResources().getAssets().open(file_name);
			// 转换为document
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(in);
		} catch (Exception e) {
			document = null;// ///////其他反馈，toast，finish等
		}
		// 获取根元素
		Element root = document.getRootElement();
		// 获取指定子元素
		Element child = root.element(sub_name).element(item_name);
		// 获取属性
		title = child.elementText("title");
		essay = child.elementText("essay");
	}

	private void initView() {

	}

}
