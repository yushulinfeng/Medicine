package org.outing.medicine.fun_remind;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.tools.base.TActivity;
import org.outing.medicine.tools.dialog.DialogTitleList;

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
    private DialogTitleList method_dialog, delete_dialog;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_main);
        setTitle("用药提醒");
        setTitleBackColor(R.color.btn_1_normal);
        showBackButton();
        showMenuButton();//test

        initDialog();
        initView();

        initTest();//测试专用，最后注释此行即可
    }


    private void initDialog() {
        method_dialog = new DialogTitleList(this, "请选择操作")
                .setListItem(context_items)
                .setListListener(new DialogTitleList.DialogItemListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position) {
                            case 0:
                                alterItem();
                                break;
                            case 1:
                                delete_dialog.setText("将删除此条提醒：\n" +
                                        array.get(delete_index).getDrugName());
                                delete_dialog.show();
                                break;
                        }
                    }
                });
        delete_dialog = new DialogTitleList(this, "确认删除？")
                .setPositiveButton("确定", new DialogTitleList.DialogButtonListener() {
                    @Override
                    public void onButtonClick() {
                        deleteItem();
                    }
                }).setNegativeButton("取消", null);
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
            show.setText("欢迎\n添加药品");
        else
            show.setText("");
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
        new DialogTitleList(this)
                .setListItem(new String[]{"历史记录"})
                .setListListener(new DialogTitleList.DialogItemListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(RemindMain.this, RemindHistory.class);
                        startActivity(intent);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateList();
    }

    private void initTest() {
        ImageButton top_menu = (ImageButton) findViewById(R.id.top_menu);
        registerForContextMenu(top_menu);
    }

    // 长按响应：测试界面
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.top_menu)
            startActivityForResult(new Intent(this, RemindTest.class), 0);
        return;
    }
}
