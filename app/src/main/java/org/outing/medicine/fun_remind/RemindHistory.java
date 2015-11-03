package org.outing.medicine.fun_remind;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemindHistory extends TActivity {
    private ListView list;
    private ArrayList<AnHistory> array;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private TextView show, back_show;
    private int delete_index;
    private AlertDialog delete_dialog, clear_dialog;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_history);
        setTitle("用药提醒历史");
        setTitleBackColor(R.color.btn_1_normal);
        showBackButton();

        initDialog();
        initView();
        initNetList();
    }

    private void initDialog() {
        delete_dialog = new AlertDialog.Builder(this)
                .setTitle("确认删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                    }
                }).setNegativeButton("取消", null).create();
        clear_dialog = new AlertDialog.Builder(this)
                .setTitle("清空历史记录？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClockTool.cleanLog(RemindHistory.this);
                    }
                }).setNegativeButton("取消", null).create();
    }

    private void initView() {
        show = (TextView) findViewById(R.id.remind_history_tv_show);
        back_show = (TextView) findViewById(R.id.remind_history_tv_back);//背景层
        list = (ListView) findViewById(R.id.remind_history_list);
        Button btn_add = (Button) findViewById(R.id.remind_history_btn_clear);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });

        array = new ArrayList<AnHistory>();
        items = new ArrayList<Map<String, Object>>();

        adapter = new SimpleAdapter(this, items, R.layout.fun_remind_history_item,
                new String[]{"time", "date", "text", "state"}, new int[]{
                R.id.remind_history_item_time, R.id.remind_history_item_date,
                R.id.remind_history_item_text, R.id.remind_history_item_state});
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {// 只添加点击即可
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                clickItem(position);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                delete_index = position;
                longClickItem();
                return true;
            }
        });
    }

    private void clearHistory() {
        clear_dialog.show();
    }

    private void initListMap() {
        if (array.size() == 0)
            show.setText("网络历史记录为空，已加载本地历史");//////////
        else
            show.setText("共" + array.size() + "条历史记录");

        items.clear();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("time", array.get(i).getTime());
            item.put("date", array.get(i).getDate());
            item.put("text", array.get(i).getText());
            item.put("state", array.get(i).getState());
            items.add(item);
        }
    }

    private void updateList() {
        initListMap();
        adapter.notifyDataSetChanged();
    }


    private void deleteItem() {

    }

    private void clickItem(int index) {

    }

    private void longClickItem() {
        delete_dialog.show();
    }

    @Override
    public void showContextMenu() {
    }

    //
    private void initNetList() {
        Connect.POST(this, ServerURL.Get_Body_Message, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("type", 4);
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(RemindHistory.this, "正在获取", "请稍候……", true);
                return dialog;
            }

            @Override
            public void onResponse(String response) {
                if (response == null) {//暂不处理
                } else if (response.equals("-2")) {
                } else if (response.equals("-1")) {
                } else if (response.equals("0")) {
                } else {
                    loadNetItem(response);
                }
            }
        });
    }

    private void loadNetItem(String response) {
        Log.e("EEE", "EEE-history:" + response);
        JSONArray json_array = JSONArray.parseArray(response);
        JSONObject item_temp = null;
        String str_temp = "";
        AnHistory his_temp = null;
        for (int i = 0; i < json_array.size(); i++) {
            try {
                item_temp = json_array.getJSONObject(i);
                Log.e("EEE", "EEE-history-temp1:" + item_temp.toString());
                str_temp = item_temp.getString("data");//字符串过渡，因为有""不能直接JSONObject
                Log.e("EEE", "EEE-history-temp-str:" + str_temp);
                item_temp = JSONObject.parseObject(str_temp);
                Log.e("EEE", "EEE-history-temp1:" + item_temp.toString());
                his_temp = new AnHistory(item_temp.getString("time"),
                        item_temp.getString("date"),
                        item_temp.getString("text"),
                        item_temp.getBoolean("state") ? "完成用药" : "拒绝用药");
                array.add(his_temp);
            } catch (Exception e) {
                Log.e("EEE", "EEE-history-ERROR:" + e.getMessage());
            }
        }
        updateList();
        //////////
        if (array.size() == 0) {
            back_show.setText(ClockTool.getLog(this));
            list.setVisibility(View.GONE);
        } else {
            list.setVisibility(View.VISIBLE);
        }
    }

}