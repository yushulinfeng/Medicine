package org.outing.medicine.fun_remind;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemindMain extends TActivity {
    private final String[] context_items = new String[]{"修改", "删除"};
    private ListView list;
    private ArrayList<AnRemind> array;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private TextView show;
    private File icon_path;
    private int delete_index;
    private AlertDialog method_dialog, delete_dialog;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_main);
        setTitle("用药提醒");
        showBackButton();
        showMenuButton();//test

        initDialog();
        initView();
    }

    private void initDialog() {
        method_dialog = new AlertDialog.Builder(this).setTitle("请选择操作")
                .setItems(context_items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                alterItem();
                                break;
                            case 1:
                                delete_dialog.setMessage("将删除此条提醒：\n" +
                                        array.get(delete_index).getDrugName());
                                delete_dialog.show();
                                break;
                        }
                    }
                }).create();
        delete_dialog = new AlertDialog.Builder(this)
                .setTitle("确认删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                    }
                }).setNegativeButton("取消", null).create();
    }

    private void initView() {
        show = (TextView) findViewById(R.id.fun_remind_tv_show);
        list = (ListView) findViewById(R.id.fun_remind_list);
        Button btn_add = (Button) findViewById(R.id.fun_remind_btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDrug();
            }
        });

        items = new ArrayList<Map<String, Object>>();
        initListMap();
        adapter = new SimpleAdapter(this, items, R.layout.fun_remind_main_item,
                new String[]{"icon", "name", "text"}, new int[]{
                R.id.remind_main_item_icon, R.id.remind_main_item_title,
                R.id.remind_main_item_text});
        adapter.setViewBinder(new AdapterBinder());
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
        array = RemindTool.getDrug(this);
        if (array.size() == 0)
            show.setText("用药提醒为空");
        else
            show.setText("共" + array.size() + "条用药提醒");
        icon_path = RemindTool.getIconSDPath();

        items.clear();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            File icon = null;
            if (icon_path != null)
                icon = new File(icon_path, array.get(i).getDrugId() + ".png");
            if (icon.exists())
                item.put("icon", BitmapFactory.decodeFile(icon.getAbsolutePath()));
            else//加载默认图片
                item.put("icon", BitmapFactory.decodeResource(getResources(),
                        R.drawable.fun_remind_drug_default));
            item.put("name", array.get(i).getDrugName());
            item.put("text", array.get(i).getDrugText());
            items.add(item);
        }
    }

    private void updateList() {
        initListMap();
        adapter.notifyDataSetChanged();
    }

    private void addDrug() {
        Intent intent = new Intent(this, AddRemind.class);
        intent.putExtra("is_alter", false);
        startActivityForResult(intent, 0);//用0就行
    }

    private void alterItem() {
        AnRemind remind_temp = array.get(delete_index);
        Intent intent = new Intent(this, AddRemind.class);
        intent.putExtra("is_alter", true);
        intent.putExtra("drug_id", remind_temp.getDrugId());
        intent.putExtra("drug_name", remind_temp.getDrugName());
        intent.putExtra("drug_text", remind_temp.getDrugText());
        intent.putExtra("icon_path", new File(icon_path,
                remind_temp.getDrugId() + ".png").getAbsolutePath());
        startActivityForResult(intent, 0);//用0就行
    }

    private void deleteItem() {
        RemindTool.deleteDrug(this, array.get(delete_index));
        RemindTool.clearTimer(this, array.get(delete_index).getDrugId());//清空对应timer
        updateList();
        showToast("删除成功");
        // 重置闹钟服务
        RemindTool.refreshTimer(this);
    }

    private void clickItem(int index) {
        // 跳转
        Intent intent = new Intent(this, RemindItem.class);
        intent.putExtra("drug_id",
                array.get(index).getDrugId());//简单小错，改了许久，注意细节
        intent.putExtra("drug_name",
                array.get(index).getDrugName());
        startActivity(intent);
    }

    private void longClickItem() {
        method_dialog.show();
    }

    //建议没有这个菜单，没有清空列表(因为这个清空容易错误操作)
    //本地应该可以看用药记录。建议没有本地缓存，直接网络获取。
    @Override
    public void showContextMenu() {
        startActivityForResult(new Intent(this, RemindTest.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateList();
    }
}
