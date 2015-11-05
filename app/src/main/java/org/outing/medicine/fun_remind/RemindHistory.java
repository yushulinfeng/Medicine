package org.outing.medicine.fun_remind;

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
import org.outing.medicine.tools.dialog.DialogTitleList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemindHistory extends TActivity {
    public static final String HIS_SPLITE = ",";
    private ListView list;
    private ArrayList<AnHistory> array;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private TextView show, back_show;
    private int delete_index;
    private DialogTitleList delete_dialog, clear_dialog;

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
        delete_dialog = new DialogTitleList(this, "确认删除？")
                .setPositiveButton("确定", new DialogTitleList.DialogButtonListener() {
                    @Override
                    public void onButtonClick() {
                        deleteItem();
                    }
                }).setNegativeButton("取消", null);
        clear_dialog = new DialogTitleList(this, "清空历史记录？")
                .setPositiveButton("确定", new DialogTitleList.DialogButtonListener() {
                    @Override
                    public void onButtonClick() {
                        ClockTool.cleanLog(RemindHistory.this);
                    }
                }).setNegativeButton("取消", null);
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
            show.setText("用药提醒\n历史记录为空");//////////网络历史记录为空，已加载本地历史
        else
            show.setText("");

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
                str_temp = item_temp.getString("data");
                //字符串过渡，因为有""不能直接JSONObject（虽然不用json了，但是保留此句）
                String[] array_temp = str_temp.split(HIS_SPLITE);
                his_temp = new AnHistory(array_temp[2],
                        item_temp.getString("time").substring(0, 10),
                        array_temp[0],
                        array_temp[1].equals("1") ? "完成用药" : "拒绝用药");
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