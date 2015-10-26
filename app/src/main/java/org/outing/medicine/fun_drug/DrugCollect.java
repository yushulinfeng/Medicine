package org.outing.medicine.fun_drug;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrugCollect extends TActivity {
    private ListView list;
    private TextView show;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private int click_position;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_drug_collect);
        setTitle("药品收藏");
        showBackButton();
        showMenuButton();

        initView();
    }

    private void initView() {
        list = (ListView) findViewById(R.id.drug_collect_list);
        show = (TextView) findViewById(R.id.drug_collect_tv_show);

        ArrayList<AnDrug> array = DrugTool.getCollect(this);
        if (array.size() == 0)
            show.setText("收藏为空");
        else
            show.setText("共" + array.size() + "条收藏");

        items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", array.get(i).getName());
            item.put("com_name", array.get(i).getCommonName());
            items.add(item);
            adapter = new SimpleAdapter(this, items, R.layout.fun_drug_main_item,
                    new String[]{"name", "com_name"}, new int[]{
                    R.id.drug_main_item_title, R.id.drug_main_item_text});
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {// 只添加点击即可
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    clickItem(position);
                }
            });
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    click_position = position;
                    String message = items.get(position).get("name") + "\n"
                            + items.get(position).get("com_name");
                    // 删除收藏对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(DrugCollect.this)
                            .setTitle("删除此条收藏？")
                            .setMessage(message)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    longClickItem(click_position);
                                }
                            }).setNegativeButton("取消", null);
                    builder.create().show();
                    return true;
                }
            });
        }
    }

    private void longClickItem(int index) {
        DrugTool.deleteCollect(this, new AnDrug(items.get(index).get("name") + "",
                items.get(index).get("com_name") + ""));
        items.remove(index);
        adapter.notifyDataSetChanged();
        if (items.size() == 0)
            show.setText("收藏为空");
        else
            show.setText("共" + items.size() + "条收藏");
        showToast("删除成功");
    }

    private void clickItem(int index) {
        // 跳转，返回式子启动，不必去管是否有变化，返回了，就刷新就行了
        Intent intent = new Intent(this, DrugItem.class);
        String com_name_temp = (String) items.get(index).get("com_name");
        intent.putExtra("drug_name",
                (String) items.get(index).get("name"));
        intent.putExtra("com_name", com_name_temp);
        if (com_name_temp.contains("-网络数据-")) {
            intent.putExtra("from_net", true);
        } else {
            intent.putExtra("from_net", false);
        }
        startActivity(intent);
    }

    private void clearCollect() {
        DrugTool.clearCollect(this);
        show.setText("收藏为空");
        items.clear();
        adapter.notifyDataSetChanged();
        showToast("收藏已清空");
    }

    @Override
    public void showContextMenu() {
        // 清空收藏对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("清空收藏？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clearCollect();
                    }
                }).setNegativeButton("取消", null);
        builder.create().show();
    }

}