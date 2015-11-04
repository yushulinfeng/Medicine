package org.outing.medicine.fun_know;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.xlist.XListView;
import org.outing.medicine.tools.xlist.XListView.XListViewListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnowSublist extends TActivity implements XListViewListener {
    private String file_name;
    private String sub_name;
    private ArrayList<String> array;
    private XListView list;//为了好看

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_know_sublist);

        initMessage();
        setTitle("健康知识");
        setTitleBackColor(R.color.btn_4_normal);
        showBackButton();

        initElement();
        initView();
    }

    private void initMessage() {
        Intent intent = getIntent();
        try {
            file_name = intent.getStringExtra("file_name");
            sub_name = intent.getStringExtra("sub_name");
        } catch (Exception e) {
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
            // 获取指定子元素
            Element sub = root.element(sub_name);
            // 获取所有子元素
            List<Element> childList = sub.elements();
            for (Element child : childList) {
                array.add(child.getName());
            }
        } catch (Exception e) {
        }
    }

    private void initView() {
        list = (XListView) findViewById(R.id.know_sublist_list);
        list.setXListViewListener(this);
        list.setPullRefreshEnable(false);
        if (android.os.Build.VERSION.SDK_INT >= 19) {//受到系统版本影响
            list.setFooterDividersEnabled(false);
            list.setSpring();
        } else {
            list.setSpring();
            //list.setPullLoadEnable(false);//项目多时，注释此行即可（项目过少会出现双线）
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {// 只添加点击即可
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                clickItem(position - 1);//XList
            }
        });
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        for (int i = 0; i < array.size(); i++) {
            item = new HashMap<String, Object>();
            item.put("name", array.get(i));
            items.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, items,
                R.layout.fun_know_list_item, new String[]{"name"},
                new int[]{R.id.know_list_item_title});
        list.setAdapter(adapter);
    }

    private void clickItem(int index) {
        // 跳转
        Intent intent = new Intent(this, KnowItem.class);
        intent.putExtra("file_name", file_name);
        intent.putExtra("sub_name", sub_name);
        intent.putExtra("item_name", array.get(index));
        intent.putExtra("from_net", false);
        startActivity(intent);
    }

    @Override
    public void showContextMenu() {
    }


    @Override
    public void onRefresh() {
        list.stopRefresh();
    }

    @Override
    public void onLoadMore() {
        list.stopLoadMore();
    }

}
