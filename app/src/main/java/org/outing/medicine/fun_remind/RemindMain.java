package org.outing.medicine.fun_remind;

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
    private ListView list;
    private ArrayList<AnRemind> array;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private TextView show;
    private File icon_path;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_main);
        setTitle("用药提醒");
        showBackButton();
        showMenuButton();//test

        initView();
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
    }

    private void initListMap() {
        array = RemindTool.getDrug(this);
        if (array.size() == 0)
            show.setText("用药提醒为空");
        else
            show.setText("共" + array.size() + "条提醒");
        icon_path = RemindTool.getIconSDPath();

        items.clear();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            File icon = null;
            if (icon_path != null)
                icon = new File(icon_path, array.get(i).getDrugId()+".png");
            if (icon.exists())
                item.put("icon", BitmapFactory.decodeFile(icon.getAbsolutePath()));
            else
                item.put("icon", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            ///////////////////////////////////////上面必须换个图片
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
        Intent intent = new Intent(this, AddDrug.class);
        startActivityForResult(intent, 0);//用0就行
    }

    private void clickItem(int index) {
        // 跳转
        Intent intent = new Intent(this, RemindDrug.class);
        intent.putExtra("drug_id",
                array.get(index).getDrugName());
        intent.putExtra("drug_name",
                array.get(index).getDrugName());
        startActivity(intent);
    }

    //建议没有这个菜单，没有清空列表
    @Override
    public void showContextMenu() {
        startActivityForResult(new Intent(this, RemindTest.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateList();
    }
}
