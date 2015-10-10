package org.outing.medicine.fun_know;

import android.content.Intent;
import android.widget.TextView;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.outing.medicine.R;
import org.outing.medicine.tools.NetTActivity;

import java.io.InputStream;

public class KnowItem extends NetTActivity {
    private String file_name, sub_name, item_name;
    private boolean from_net;
    private String title, essay;
    private TextView tv_title, tv_essay;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_know_item);
        setTitle("健康知识");

        initMessage();
        if (from_net) {
            setTitle("开发中……");//////////
        } else {
            initEssay();
            initView();
        }
    }


    private void initMessage() {
        Intent intent = getIntent();
        try {
            file_name = intent.getStringExtra("file_name");
            sub_name = intent.getStringExtra("sub_name");
            item_name = intent.getStringExtra("item_name");
            from_net = intent.getBooleanExtra("from_net", false);
        } catch (Exception e) {
            file_name = null;//将异常交给InputStream处理
            sub_name = null;
            item_name = null;
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
            title = "获取数据失败";
            essay = "";
            return;
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
        tv_title = (TextView) findViewById(R.id.know_item_title);
        tv_essay = (TextView) findViewById(R.id.know_item_text);
        tv_title.setText(title);
        tv_essay.setText(essay);
    }

    @Override
    public void showContextMenu() {
    }


    @Override
    public void receiveMessage(String what) {

    }

    @Override
    public void newThread() {

    }
}
