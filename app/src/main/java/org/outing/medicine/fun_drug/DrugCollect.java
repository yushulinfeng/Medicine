package org.outing.medicine.fun_drug;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;

import org.outing.medicine.R;
import org.outing.medicine.tools.base.TActivity;
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

//本地收藏只用于同步判断，不显示给用户
public class DrugCollect extends TActivity {
    private ListView list;
    private TextView show;
    ArrayList<AnDrug> array;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private int click_position;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_drug_collect);
        setTitle("药品收藏");
        setTitleBackColor(R.color.btn_3_normal);
        showBackButton();
        showMenuButton();

        initView();
        initNetData();
    }

    private void initView() {
        list = (ListView) findViewById(R.id.drug_collect_list);
        show = (TextView) findViewById(R.id.drug_collect_tv_show);

        items = new ArrayList<Map<String, Object>>();
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
                new DialogTitleList(DrugCollect.this, "删除此条收藏？")
                        .setText(message)
                        .setPositiveButton("确定", new DialogTitleList.DialogButtonListener() {
                            @Override
                            public void onButtonClick() {
                                longClickItem();
                            }
                        }).setNegativeButton("取消", null).show();
                return true;
            }
        });
    }

    private void initLocalData() {
        array = DrugTool.getCollect(this);
        initListMap();
        updateList();
    }

    private void initNetData() {
        array = new ArrayList<AnDrug>();
        getNetList();
    }

    private void initListMap() {
        if (array.size() == 0)
            show.setText("您尚未\n收藏药品");
        else
            show.setText("");

        items.clear();
        for (int i = 0; i < array.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", array.get(i).getName());
            item.put("com_name", array.get(i).getCommonName());
            items.add(item);
        }
    }

    private void longClickItem() {
        Connect.POST(this, ServerURL.DRUG_PUT_COLLECT, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("medical", array.get(click_position).getName() +
                        DrugMain.DRUG_SPLIT + array.get(click_position).getCommonName() +
                        DrugMain.DRUG_SPLIT + array.get(click_position).getID());
                list.put("act", "1");
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;
            }

            @Override
            public void onResponse(String response) {
                if (response == null) {//暂不处理
                    showToast("网络错误");
                } else if (response.equals("0")) {
                    //SUCCESS
                    items.remove(click_position);
                    adapter.notifyDataSetChanged();
                    if (items.size() == 0) {
                        show.setText("您尚未\n收藏药品");
                    } else {
                        show.setText("");
                    }
                    showToast("删除成功");
                    //本地
                    DrugTool.deleteCollect(DrugCollect.this, new AnDrug(array.get(click_position).getName(),
                            array.get(click_position).getCommonName(), array.get(click_position).getID()));

                } else {
                    showToast("删除失败");
//                    if (response.equals("-4")) {
//                    } else if (response.equals("-3")) {
//                    } else if (response.equals("-2")) {
//                    } else if (response.equals("-1")) {
//                    }
                }
            }
        });
    }

    private void clickItem(int index) {
        // 跳转，返回式子启动，不必去管是否有变化，返回了，就刷新就行了
        Intent intent = new Intent(this, DrugItem.class);
        String com_name_temp = (String) items.get(index).get("com_name");
        intent.putExtra("drug_name", array.get(index).getName());
        intent.putExtra("com_name", array.get(index).getCommonName());
        intent.putExtra("drug_net_id", array.get(index).getID());
        if (com_name_temp.contains("-网络数据-")) {
            intent.putExtra("from_net", true);
        } else {
            intent.putExtra("from_net", false);
        }
        startActivity(intent);
    }

    private void clearCollect() {
        Connect.POST(this, ServerURL.DRUG_CLEAR_COLLECT, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                return null;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(DrugCollect.this, "正在处理", "请稍候……", true);
                return dialog;
            }

            @Override
            public void onResponse(String response) {
                if (response == null) {//暂不处理
                    showToast("网络错误，清空失败");//这个本地也不能清空
                } else if (response.equals("-2")) {//身份过期与未登录
//        //local（弹个对话框就好了）
//        DrugTool.clearCollect(DrugCollect.this);
//        show.setText("收藏为空");
//        items.clear();
//        adapter.notifyDataSetChanged();
//        showToast("收藏已清空");
                } else if (response.equals("-1")) {//不可能
                } else if (response.equals("0")) {
                    show.setText("您尚未\n收藏药品");
                    items.clear();
                    adapter.notifyDataSetChanged();
                    showToast("收藏已清空");
                    //本地
                    DrugTool.clearCollect(DrugCollect.this);
                }
            }
        });
    }

    private void updateList() {
        initListMap();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showContextMenu() {
        // 清空收藏对话框
        new DialogTitleList(this, "清空收藏？")
                .setPositiveButton("确定", new DialogTitleList.DialogButtonListener() {
                    @Override
                    public void onButtonClick() {
                        clearCollect();
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    private void getNetList() {
        Connect.POST(this, ServerURL.DRUG_GET_COLLECT, new ConnectListener() {
            public ConnectList setParam(ConnectList list) {
                return list;
            }

            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(DrugCollect.this, "正在获取", "请稍候……", true);
                return dialog;
            }

            public void onResponse(String response) {
                dealResponse(response);
            }
        });
    }

    private void dealResponse(String response) {
        if (response == null) {//暂不处理
        } else if (response.equals("-2")) {
        } else if (response.equals("-1")) {
        } else if (response.equals("0")) {
        } else {
            JSONArray json_array = JSONArray.parseArray(response);
            String item_temp = "", name_temp = "", com_temp = "", id_temp = "";
            String[] all_temp = null;
            AnDrug drug_temp = null;
            for (int i = 0; i < json_array.size(); i++) {
                item_temp = json_array.getString(i);
                Log.e("EEE", "EEE " + item_temp);
                try {
                    all_temp = item_temp.split(DrugMain.DRUG_SPLIT);
                    name_temp = all_temp[0];
                    com_temp = all_temp[1];
                    if (all_temp.length == 3)
                        id_temp = all_temp[2];
                    else
                        id_temp = "";
                } catch (Exception e) {
                    int str_end = item_temp.indexOf(DrugMain.DRUG_SPLIT);
                    if (str_end != -1)
                        name_temp = item_temp.substring(0, str_end);
                    else
                        name_temp = item_temp;
                    com_temp = "";
                    id_temp = "";
                }
                Log.e("EEE", "EEE-1 " + name_temp);
                Log.e("EEE", "EEE-2 " + com_temp);
                drug_temp = new AnDrug(name_temp, com_temp, id_temp);
                array.add(drug_temp);
            }
            updateList();
        }
//        /////////////////////////////////////////是否有必要（暂时不要）
//        if (array.size() == 0) {
//            initLocalData();
//            show.setText("网络收藏为空，已加载本地收藏");
//        }
    }

}