package org.outing.medicine.fun_remind;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemindItem extends TActivity {
    private ListView list;
    private ArrayList<AnTimer> array;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private TextView show;
    private String drug_id, drug_name;
    private int delete_index;


    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_drug);
        initMessage();
        setTitle(drug_name);
        showBackButton();

        initView();
    }

    private void initMessage() {
        Intent intent = getIntent();
        try {
            drug_id = intent.getStringExtra("drug_id");
            drug_name = intent.getStringExtra("drug_name");
        } catch (Exception e) {
            drug_id = null;
        }
    }

    private void initView() {
        show = (TextView) findViewById(R.id.remind_drug_tv_show);
        list = (ListView) findViewById(R.id.remind_drug_list);

        Button btn_add = (Button) findViewById(R.id.remind_drug_btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTimer();
            }
        });

        items = new ArrayList<Map<String, Object>>();
        initListMap();
        adapter = new SimpleAdapter(this, items, R.layout.fun_remind_drug_item,
                new String[]{"time", "method", "text"}, new int[]{
                R.id.remind_drug_item_time, R.id.remind_drug_item_method,
                R.id.remind_drug_item_text});
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

    private void initListMap() {
        array = RemindTool.getTimer(this, drug_id);
        if (array.size() == 0)
            show.setText("暂无提醒");
        else
            show.setText("共" + array.size() + "条提醒");

        sortArray();
        items.clear();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            AnTimer timer = array.get(i);
            item.put("time", timer.getTime());
            item.put("method", AddTimer.context_items[timer.getMethod()]);
            item.put("text", timer.getName());
            items.add(item);
        }
    }

    private void updateList() {
        initListMap();
        adapter.notifyDataSetChanged();
    }

    private void AddTimer() {
        Intent intent = new Intent(this, AddTimer.class);
        intent.putExtra("drug_id", drug_id);
        intent.putExtra("is_alter", false);
        startActivityForResult(intent, 0);//用0就行
    }

    private void clickItem(int index) {
        Intent intent = new Intent(this, AddTimer.class);
        intent.putExtra("drug_id", drug_id);
        intent.putExtra("is_alter", true);
        AnTimer timer = array.get(index);
        intent.putExtra("timer_id", timer.getId());
        intent.putExtra("title", timer.getName());
        intent.putExtra("method", timer.getMethod());
        intent.putExtra("hour", timer.getHour());
        intent.putExtra("min", timer.getMinute());
        startActivityForResult(intent, 0);//用0就行
    }

    private void longClickItem() {
        // 确认删除对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("确认删除？")
                .setMessage("将删除此闹钟：\n" + array.get(delete_index).getName())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                    }
                }).setNegativeButton("取消", null);
        builder.create().show();
    }

    private void deleteItem() {
        RemindTool.deleteTimer(this, drug_id, array.get(delete_index).getId());
        updateList();
        showToast("删除成功");
        // 刷新闹钟服务
        RemindTool.refreshTimer(this);
    }

    /**
     * 根据时间进行排序
     */
    public void sortArray() {
        Collections.sort(array, new Comparator<AnTimer>() {
            @Override
            public int compare(AnTimer one1, AnTimer one2) {
                int one1_location = one1.getHour() * 100 + one1.getMinute();
                int one2_location = one2.getHour() * 100 + one2.getMinute();
                if (one1_location < one2_location)
                    return -1;
                else
                    return 1;
            }
        });
    }

    @Override
    public void showContextMenu() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateList();
        RemindTool.refreshTimer(this);
    }

}
