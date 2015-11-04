package org.outing.medicine.fun_know;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
import org.outing.medicine.tools.xlist.XListView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class KnowList extends TActivity implements XListView.XListViewListener {
    private String file_name;
    private XListView list;
    private ArrayList<String> array;
    private ArrayList<AnNetEssay> net_array;
    private boolean is_net = false;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private int curr_page = 1;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_know_list);
        setTitle("健康知识");
        setTitleBackColor(R.color.btn_4_normal);
        showBackButton();

        initMessage();
        initView();
        if (is_net) {
            setTitle("网络健康知识");
            initNetView();
        } else {
            initElement();
            initLocalView();
        }
    }


    private void initMessage() {
        Intent intent = getIntent();
        try {
            is_net = intent.getBooleanExtra("is_net", false);
            file_name = intent.getStringExtra("file_name");
        } catch (Exception e) {
            is_net = false;
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
        list = (XListView) findViewById(R.id.know_list_list);
        list.setXListViewListener(this);
        list.setOnItemClickListener(new OnItemClickListener() {// 只添加点击即可
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                clickItem(position - 1);//XList
            }
        });
    }

    private void initLocalView() {
        list.setPullRefreshEnable(false);
        if (android.os.Build.VERSION.SDK_INT >= 19) {//受到系统版本影响
            list.setFooterDividersEnabled(false);
            list.setSpring();
        } else {
            list.setSpring();
            list.setPullLoadEnable(false);
        }
        items = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        if (array != null)
            for (int i = 0; i < array.size(); i++) {
                item = new HashMap<String, Object>();
                item.put("name", array.get(i));
                items.add(item);
            }
        adapter = new SimpleAdapter(this, items,
                R.layout.fun_know_list_item, new String[]{"name"},
                new int[]{R.id.know_list_item_title});
        list.setAdapter(adapter);
    }

    private void initNetView() {
        list.setPullLoadEnable(true);
        if (android.os.Build.VERSION.SDK_INT >= 19) {//受到系统版本影响
            list.setFooterDividersEnabled(false);
        }
        net_array = new ArrayList<AnNetEssay>();
        items = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("name", "暂无数据");
        items.add(item);
        adapter = new SimpleAdapter(this, items,
                R.layout.fun_know_list_item, new String[]{"name"},
                new int[]{R.id.know_list_item_title});
        list.setAdapter(adapter);
        getItemFromNet(1);//加载第一页
    }

    private void getItemFromNet(int page) {
        curr_page = page;
        Connect.POST(this, ServerURL.KNOW_GET_ESSAYLIST, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("page", curr_page + "");//区域划分是我的问题，应该注意，可以重载此方法。
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;
            }

            @Override
            public void onResponse(String response) {
                decodeJson(response);
                updateNetView();
                list.stopRefresh();
                list.stopLoadMore();
            }
        });
    }

    private void decodeJson(String json) {
        if (json == null)
            return;
        if (json.equals("") || json.equals("0")) {
            return;
        }
        JSONArray json_array = JSONArray.parseArray(json);
        for (int i = 0; i < json_array.size(); i++) {
            JSONObject json_item = json_array.getJSONObject(i);
            String id = json_item.getString("id");
            String time = json_item.getString("time");
            String title = json_item.getString("title");
            String author = json_item.getString("author");
            AnNetEssay an_essay = new AnNetEssay(id, title, time, author);
            net_array.add(an_essay);
        }
    }

    private void updateNetView() {
        items.clear();
        Map<String, Object> item = new HashMap<String, Object>();
        if (net_array != null) {
            if (net_array.size() == 0) {
                item.put("name", "暂无数据");
                items.add(item);
            }
            for (int i = 0; i < net_array.size(); i++) {
                item = new HashMap<String, Object>();
                item.put("name", net_array.get(i).getTitle());
                items.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void clickItem(int index) {
        // 跳转
        if (is_net) {
            if (net_array.size() == 0)
                return;
            Intent intent = new Intent(this, KnowItem.class);
            intent.putExtra("from_net", is_net);
            intent.putExtra("id", net_array.get(index).getId());
            intent.putExtra("title", net_array.get(index).getTitle());
            intent.putExtra("time", net_array.get(index).getTime());
            intent.putExtra("author", net_array.get(index).getAuthor());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, KnowSublist.class);
            intent.putExtra("file_name", file_name);
            intent.putExtra("sub_name", array.get(index));
            startActivity(intent);
        }
    }

    @Override
    public void showContextMenu() {
    }

    @Override
    public void onRefresh() {
        if (is_net) {
            curr_page = 1;
            net_array.clear();
            getItemFromNet(curr_page);
        } else {
            list.stopRefresh();
        }
    }

    @Override
    public void onLoadMore() {
        if (is_net) {
            curr_page++;
            getItemFromNet(curr_page);
        } else {
            list.stopLoadMore();
        }
    }

}