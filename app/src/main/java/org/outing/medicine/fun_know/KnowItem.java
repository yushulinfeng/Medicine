package org.outing.medicine.fun_know;

import android.content.Intent;
import android.widget.TextView;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

import java.io.InputStream;

public class KnowItem extends TActivity {
    private boolean from_net;
    private String file_name, sub_name, item_name;
    private String title, essay;
    private TextView tv_title, tv_essay;
    private String net_id, net_time, net_author;//暂不处理

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_know_item);
        setTitle("健康知识");
        showBackButton();

        initMessage();
        initView();
        if (from_net) {
            initNetView();
        } else {
            initEssay();
            initLocalView();
        }
    }


    private void initMessage() {
        Intent intent = getIntent();
        if (intent != null)
            from_net = intent.getBooleanExtra("from_net", false);
        try {
            if (from_net) {
                net_id = intent.getStringExtra("id");
                title = intent.getStringExtra("title");
                net_time = intent.getStringExtra("time");
                net_author = intent.getStringExtra("author");
            } else {
                file_name = intent.getStringExtra("file_name");
                sub_name = intent.getStringExtra("sub_name");
                item_name = intent.getStringExtra("item_name");
            }
        } catch (Exception e) {
            net_id = "";
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
    }

    private void initLocalView() {
        tv_title.setText(title);
        tv_essay.setText(essay);
    }

    private void initNetView() {
        tv_title.setText("暂无数据");
        Connect.POST(this, ServerURL.KNOW_GET_ESSAYITEM, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("id", net_id);
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(KnowItem.this, "正在获取数据", "请稍候……", true);
                return dialog;
            }

            @Override
            public void onResponse(String response) {
                if (response == null)
                    tv_title.setText("获取数据失败");
                else if (response.equals("-1") || response.equals("-2"))
                    tv_title.setText("获取数据失败");
                else {
                    tv_title.setText(title);
                    tv_essay.setText(response);
                }
            }
        });
    }

    @Override
    public void showContextMenu() {
    }


}
