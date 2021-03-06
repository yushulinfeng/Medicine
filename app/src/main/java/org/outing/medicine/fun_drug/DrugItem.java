package org.outing.medicine.fun_drug;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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
import org.outing.medicine.tools.thread.NetTActivity;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

@SuppressWarnings("deprecation")
public class DrugItem extends NetTActivity {
    protected static final String MEDICINE_API_KEY = "3e49661943e74cb87ef2c71d2c2fd9a9";
    private static final String MEDICINE_SHOW_URL = "http://a.apix.cn/yi18/drug/show";
    private File drug_file;
    private boolean from_net;
    private String drug_name, com_name, drug_net_name;
    private String drug_title, drug_text, drug_net_id;
    private TextView tv_title, tv_text;
    private Button btn_collect;
    private AnDrug drug;
    private boolean is_collect = false;

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
            if (from_net) {
                com_name = "-网络数据-";
                drug_net_id = intent.getStringExtra("drug_net_id");
                if (drug_net_id == null || drug_net_id.equals(""))
                    from_net = false;
            } else {
                drug_net_id = "";
            }
            drug = new AnDrug(drug_name, com_name, drug_net_id);
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
        if (drug_title == null || drug_title.equals("")) {
            drug_title = "未找到数据";
            drug_text = "";
            drug = null;
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
        } else {
            is_collect = DrugTool.isCollected(this, drug);
            dealNetCollect();
        }
    }

    private void dealNetCollect() {
        String name = drug.getName();
        String com_name = drug.getCommonName();
        String id = drug.getID();
        drug_net_name = name + DrugMain.DRUG_SPLIT + com_name + DrugMain.DRUG_SPLIT + id;////////
        Connect.POST(this, ServerURL.DRUG_PUT_COLLECT, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("medical", drug_net_name);
                if (is_collect)
                    list.put("act", "1");//删除（无此参数是添加）
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(DrugItem.this, "正在处理", "处理中，请稍候……", true);
                return dialog;
            }

            @Override
            public void onResponse(String response) {
                if (response == null) {//暂不处理
                    showToast("网络错误");
                } else if (response.equals("0")) {//SUCCESS
                    //本地同步
                    if (is_collect) {
                        DrugTool.deleteCollect(DrugItem.this, drug);
                        btn_collect.setText("收藏");
                        showToast("已取消收藏");
                    } else {
                        DrugTool.addCollect(DrugItem.this, drug);
                        btn_collect.setText("取消收藏");
                        showToast("已加入我的收藏");
                    }
                } else {
                    showToast("操作失败");
//                    if (response.equals("-4")) {
//                    } else if (response.equals("-3")) {
//                    } else if (response.equals("-2")) {
//                    } else if (response.equals("-1")) {
//                    }
                }
            }
        });
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
            request.addHeader("apix-key", MEDICINE_API_KEY);
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
        drug_title = drug_name;
        String url = MEDICINE_SHOW_URL + "?id=" + drug_net_id;
        drug_text = decodeText(doGet(url));
        if (drug_text.equals(""))
            drug_text = "未找到数据";
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
