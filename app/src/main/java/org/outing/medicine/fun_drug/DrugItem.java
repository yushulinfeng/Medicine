package org.outing.medicine.fun_drug;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.outing.medicine.R;
import org.outing.medicine.tools.NetTActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

@SuppressWarnings("deprecation")
public class DrugItem extends NetTActivity {
    private static final String MEDICINE_SEARCH_URL = "http://a.apix.cn/yi18/drug/search";
    private static final String MEDICINE_SHOW_URL = "http://a.apix.cn/yi18/drug/show";
    private File drug_file;
    private String drug_name, com_name;
    private boolean from_net;
    private String drug_title, drug_text;
    private TextView tv_title, tv_text;
    private Button btn_collect;
    private AnDrug drug;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_drug_item);
        setTitle("药品信息");
        setTitleBackColor(R.color.btn_3_normal);
        showBackButton();

        initMessage();
        initView();
        if (from_net) {
            showProcessDialog("正在查询，请稍候……", "", true);
        } else {
            showProcessDialog("正在加载本地数据……", "", true);
        }
        startNewThread();// 都要用多线程
    }

    private void initMessage() {
        Intent intent = getIntent();
        try {
            drug_name = intent.getStringExtra("drug_name");
            com_name = intent.getStringExtra("com_name");
            from_net = intent.getBooleanExtra("from_net", false);
            if (from_net)
                com_name = "-网络数据-";
            drug = new AnDrug(drug_name, com_name);
        } catch (Exception e) {
            drug = null;
        }
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.drug_item_title);
        tv_text = (TextView) findViewById(R.id.drug_item_text);

        btn_collect = (Button) findViewById(R.id.drug_item_btn_collect);
        btn_collect.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                collectItem();
            }
        });
        if (drug == null)
            btn_collect.setText("返回");
        else if (DrugTool.isCollected(this, drug))
            btn_collect.setText("取消收藏");
        else
            btn_collect.setText("收藏");
    }

    private void getDrugFromLocal() {
        drug_file = DrugTool.getUnzipFile(this, false);
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(drug_file);// 转换为document
        } catch (DocumentException e) {
            drug_title = "暂无数据";
            drug_text = "";
            drug = null;
            btn_collect.setText("返回");
            return;
        }
        Element root = document.getRootElement(); // 获取根元素
        @SuppressWarnings("unchecked")
        List<Element> childList = root.elements();// 获取所有子元素
        for (Element child : childList) {
            if (child.elementText("cnName").equals(drug_name)
                    && child.elementText("commonName").equals(com_name)) {// 完全匹配,肯定就是
                drug_title = drug_name;
                drug_text = getShowText(child);
                break;
            }
        }
    }

    private void showDrugInfo() {
        tv_title.setText(drug_title);
        tv_text.setText(drug_text);
    }

    private String getShowText(Element drug) {
        String show = "";
        show += "【药品名称】:" + getShortText(drug, "cnName");
        show += "【通用名】:" + getShortText(drug, "commonName");
        show += "【功能主治】:" + getLongText(drug, "indication");
        show += "【用法用量】:" + getLongText(drug, "dosage");
        show += "【禁忌】:" + getLongText(drug, "contraindications");
        show += "【注意事项】:" + getLongText(drug, "precautions");
        show += "【不良反应】:" + getLongText(drug, "adverseReactions");
        show += "【药品类型】:" + getShortText(drug, "type");
        show += "【OTC】:" + (drug.elementText("OTC").equals("0") ? "否" : "是");
        show = show.replace("【", "\n\n【").trim();
        return show;
    }

    // deal末尾句号截取,null,<br/>
    private String getLongText(Element element, String tag) {
        String str = element.elementText(tag);
        if (str == null || str.trim().equals(""))
            return "暂无信息";
        str = str.replace("<br/>", "");// 不必\n
        str = str.replace("<b>", "");
        str = str.replace("</b>", "");
        str = str.substring(0, str.lastIndexOf("。") + 1);
        if (str.trim().equals(""))
            return "暂无信息";
        return str;
    }

    // null处理
    private String getShortText(Element element, String tag) {
        String str = element.elementText(tag);
        if (str == null || str.trim().equals(""))
            return "暂无信息";
        return str;
    }

    private void collectItem() {
        if (drug == null) {
            finish();
        } else if (DrugTool.isCollected(this, drug)) {
            DrugNetTool.deleteNetCollect(this, drug);//删除网络收藏
            DrugTool.deleteCollect(this, drug);
            btn_collect.setText("收藏");
            showToast("已取消收藏");
        } else {
            DrugNetTool.addNetCollect(this, drug);//添加到网络收藏
            DrugTool.addCollect(this, drug);
            btn_collect.setText("取消收藏");
            showToast("已加入我的收藏");
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
            request.addHeader("apix-key", "3e49661943e74cb87ef2c71d2c2fd9a9");
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

    private int decodeID(String json_str) {
        try {// json_str是null让它抛出异常即可
            JSONObject json_all = JSONObject.parseObject(json_str);
            JSONArray json_array = json_all.getJSONArray("yi18");
            if (json_array.size() == 0)
                return -1;
            drug_title = json_array.getJSONObject(0).getString("title");
            drug_title = Jsoup.parse(drug_title).text();// jsoup处理
            return json_array.getJSONObject(0).getInteger("id");
        } catch (Exception e) {
            return -1;
        }
    }

    private String decodeText(String json_str) {
        try {// json_str是null让它抛出异常即可
            JSONObject json_all = JSONObject.parseObject(json_str);
            String result = json_all.getJSONObject("yi18").getString("message");
            result = Jsoup.parse(result).text();// 格式化处理
            if (result.endsWith("】"))
                result += "暂无信息";
            result = result.replace("[收起]", "");
            result = result.replace("【", "\n\n【");
            result = result.replace("】", "】\n");
            return result.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private void getDrugFromNet() {
        String url = MEDICINE_SEARCH_URL + "?page=1&limit=1&keyword="
                + drug_name;
        url = MEDICINE_SHOW_URL + "?id=" + decodeID(doGet(url));
        drug_text = decodeText(doGet(url));
    }

    @Override
    public void showContextMenu() {
    }

    @Override
    public void receiveMessage(String what) {
        showDrugInfo();
        if (drug == null)
            btn_collect.setText("返回");
    }

    @Override
    public void newThread() {
        if (from_net) {
            getDrugFromNet();
        } else {
            getDrugFromLocal();
        }
        sendMessage(null);
    }
}
