package org.outing.medicine.fun_drug;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.outing.medicine.R;
import org.outing.medicine.tools.NetTActivity;
import org.outing.medicine.tools.dialog.DialogTitleList;
import org.outing.medicine.tools.run.Run;
import org.outing.medicine.tools.run.RunListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrugMain extends NetTActivity implements OnClickListener {
    public static final String DRUG_SPLIT = "~";//一般手机不容易输入，可作为分隔符
    private static final String catalog_path = "drug/drug_index.xml";
    private static final String MEDICINE_SEARCH_URL = "http://a.apix.cn/yi18/drug/search";
    private ListView list;
    private TextView show;
    private EditText edit;
    private List<Map<String, Object>> items;
    private List<AnDrug> items_net;
    private SimpleAdapter adapter;
    private ProgressDialog process_dialog;
    private boolean is_history, is_net, init_success;
    private Element root;
    private String search_name;


    @Override
    public void onCreate() {
        setContentView(R.layout.fun_drug_main);
        setTitle("用药查询");
        setTitleBackColor(R.color.btn_3_normal);
        showBackButton();
        showMenuButton();

        initCatalog();
        initUnzip();
    }

    private void initUnzip() {
        File drug_file = DrugTool.getUnzipFile(this, false);
        if (drug_file == null) {
            init_success = false;
            sendMessage(null);// 通知初始化界面
        } else if (drug_file.exists()) {
            init_success = true;
            sendMessage(null);// 通知初始化界面
        } else {// 在主线程会ANR异常
            showProcessDialog("请稍候", "首次运行，正在初始化数据……", false);
            startNewThread();
        }
    }

    private void initCatalog() {
        try {
            InputStream in = getResources().getAssets().open(catalog_path);// 获取assets文件
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(in);// 转换为document
            root = document.getRootElement();// 获取根元素
        } catch (Exception e) {
            init_success = false;
        }
    }

    private void initView() {
        list = (ListView) findViewById(R.id.fun_drug_list);
        show = (TextView) findViewById(R.id.fun_drug_tv_show);
        edit = (EditText) findViewById(R.id.fun_drug_et_search);

        ImageButton btn_search = (ImageButton) findViewById(R.id.fun_drug_btn_search);
        Button btn_collect = (Button) findViewById(R.id.fun_drug_btn_collect);
        btn_search.setOnClickListener(this);
        btn_collect.setOnClickListener(this);
        //回车事件（隐藏键盘已在xml中写了）
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                prepareAndSearch();
                return false;
            }
        });

        ArrayList<AnDrug> array = DrugTool.getHistory(this);
        if (array.size() == 0)
            show.setText("历史记录为空");// 历史记录为空，就不要显示了，还是显示比较人性化
        is_history = true;
        is_net = false;

        items_net = new ArrayList<AnDrug>();
        items = new ArrayList<Map<String, Object>>();
        if (init_success)// 初始化成功，才添加（否则还要判断列表项点击）
            for (int i = 0; i < array.size(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("name", array.get(i).getName());
                item.put("com_name", array.get(i).getCommonName());
                items.add(item);
            }
        adapter = new SimpleAdapter(this, items, R.layout.fun_drug_main_item,
                new String[]{"name", "com_name"}, new int[]{
                R.id.drug_main_item_title, R.id.drug_main_item_text});
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {// 只添加点击即可
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                clickItem(position);
            }
        });
    }

    private void clickItem(int index) {
        if (is_history) {//历史
            search_name = (String) items.get(index).get("name");
            edit.setText(search_name);
            edit.setSelection(search_name.length());// 定位光标到最后
            search(search_name);// 这里就不必写入历史了，也不调整历史顺序了
            is_history = false;
        } else if (is_net) {//网络
            Intent intent = new Intent(this, DrugItem.class);
            intent.putExtra("drug_name", items_net.get(index).getName());
            intent.putExtra("com_name", "");
            intent.putExtra("drug_net_id", items_net.get(index).getID());
            intent.putExtra("from_net", true);
            startActivity(intent);
        } else {//本地
            // 跳转
            if (index == 0) {
                is_net = true;
                searchFromNet();
            } else {
                Intent intent = new Intent(this, DrugItem.class);
                intent.putExtra("drug_name",
                        (String) items.get(index).get("name"));
                intent.putExtra("com_name",
                        (String) items.get(index).get("com_name"));
                intent.putExtra("from_net", false);
                startActivity(intent);
            }
        }
    }

    private void searchFromNet() {
        process_dialog = ProgressDialog.show(this, "正在搜索，请稍候……", "", true,
                false);// 忙碌对话框
        Run.RUN(new RunListener() {
            @Override
            public void doInThread() {
                String url = MEDICINE_SEARCH_URL + "?page=1&limit=20&keyword="
                        + search_name;
                String response = doGet(url);
                dealMapfromNet(response);
            }

            @Override
            public void afterInMain() {
                show.setText("-联网查询-：" + search_name);
                adapter.notifyDataSetChanged();
                list.setSelection(0);// 回到最前面
                process_dialog.dismiss();// 隐藏忙碌对话框
            }
        });
    }

    private void dealMapfromNet(String response) {
        items_net.clear();
        //解析数据
        try {// json_str是null让它抛出异常即可
            JSONObject json_all = JSONObject.parseObject(response);
            JSONArray json_array = json_all.getJSONArray("yi18");
            String name_temp = "", com_name_temp = "", id_temp = "";
            NET_ALL:
            for (int i = 0; i < json_array.size(); i++) {
                name_temp = json_array.getJSONObject(i).getString("title");
                name_temp = Jsoup.parse(name_temp).text();// jsoup处理
                for (int j = 0; j < items_net.size(); j++) {
                    if (items_net.get(j).getName().equals(name_temp))
                        continue NET_ALL;
                }
                com_name_temp = json_array.getJSONObject(i).getString("content");
                com_name_temp = Jsoup.parse(com_name_temp).text();// jsoup处理
                com_name_temp = com_name_temp.replace("【", "\n【").trim();
                id_temp = json_array.getJSONObject(i).getInteger("id") + "";
                items_net.add(new AnDrug(name_temp, com_name_temp, id_temp));
            }
        } catch (Exception e) {
        }
        //添加到列表
        items.clear();
        for (int i = 0; i < items_net.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", items_net.get(i).getName());
            item.put("com_name", items_net.get(i).getCommonName());
            items.add(item);
        }
    }

    private String doGet(String url) {
        final int COONECT_TIME_OUT = 15000;// 设定连接超时15秒
        final int READ_TIME_OUT = 30000;// 设定读取超时为30秒
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, COONECT_TIME_OUT);
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    READ_TIME_OUT);
            request.addHeader("apix-key", DrugItem.MEDICINE_API_KEY);
            HttpResponse response = client.execute(request);
            // 接收返回
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            return sb.toString();
        } catch (Exception e) {// 很有可能是请求超时了
            return null;
        }
    }

    private void prepareAndSearch() {
        search_name = edit.getText().toString();
        if (search_name.trim().equals("")) {
            showToast("请输入药品名称");
            edit.setText("");
            return;
        }
        if (search_name.contains(DRUG_SPLIT)) {//竖线一般手机不容易输入
            //到时候，就用这个作为分隔符
            search_name = search_name.replace(DRUG_SPLIT, " ");
        }
        DrugTool.addHistory(this, new AnDrug(search_name, "", ""));// 第二位就应该为空
        is_history = false;
        is_net = false;
        search(search_name);
    }

    private void search(String name) {// 想提高用户体验，可以添加忙碌对话框
        process_dialog = ProgressDialog.show(this, "正在搜索，请稍候……", "", true,
                false);// 忙碌对话框
        @SuppressWarnings("unchecked")
        List<Element> childList = root.elements();// 获取所有子元素// 这个定为全局可能内存占用过大
        List<Element> results = new ArrayList<Element>();
        for (Element child : childList) {
            // 包含就添加进列表
            if (child.elementText("cnName").contains(name)
                    || child.elementText("commonName").contains(name)) {
                // 完全匹配位置提前
                if (child.elementText("cnName").equals(name)
                        || child.elementText("commonName").equals(name)) {
                    results.add(0, child);
                } else {// 否则按顺序添加
                    results.add(child);
                }
            }
        }
        show.setText("本地找到" + results.size() + "条：" + name);
        items.clear();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("name", "-联网查询-");
        item.put("com_name", "");
        items.add(item);
        for (int i = 0; i < results.size(); i++) {
            // 必须改成下拉继续加载，否则很麻烦。
            // 但是实践告诉我，加载全部（3000条）一点不卡，very流畅。那就省点力吧。
            item = new HashMap<String, Object>();
            item.put("name", results.get(i).elementText("cnName"));
            item.put("com_name", results.get(i).elementText("commonName"));
            items.add(item);
        }
        adapter.notifyDataSetChanged();
        list.setSelection(0);// 回到最前面
        process_dialog.dismiss();// 隐藏忙碌对话框
    }

    private void collect() {
        // 直接跳转
        Intent intent = new Intent(this, DrugCollect.class);
        startActivity(intent);
    }

    /**
     * 清空历史
     */
    private void clearHistory() {
        DrugTool.clearHistory(this);
        edit.setText("");
        show.setText("历史记录为空");
        items.clear();
        adapter.notifyDataSetChanged();
        showToast("搜索历史已清空");
    }

    @Override
    public void showContextMenu() {
        // 清空历史对话框
        new DialogTitleList(this, "清空历史？")
                .setPositiveButton("确定", new DialogTitleList.DialogButtonListener() {
                    @Override
                    public void onButtonClick() {
                        clearHistory();
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (!init_success) {// 初始化失败，直接返回
            showToast("数据初始化失败，请检查存储卡");
            return;
        }
        switch (v.getId()) {
            case R.id.fun_drug_btn_search:
                prepareAndSearch();
                break;
            case R.id.fun_drug_btn_collect:
                collect();
                break;
        }
    }

    @Override
    public void receiveMessage(String what) {
        // 这个必须在这里处理
        initView();// 可以加个back，menu
    }

    @Override
    public void newThread() {
        init_success = DrugTool.writeUnzip(this);
        sendMessage(null);
    }

}
