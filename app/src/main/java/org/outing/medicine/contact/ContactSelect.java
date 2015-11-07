package org.outing.medicine.contact;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.tools.NetTActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人列表，稳定快速加载。
 * 自我感觉良好…………
 */
public class ContactSelect extends NetTActivity {
    private static final int THREAD_HANDLER_INT = 0;
    private TextView tv_load;
    private ListView list;
    private List<Map<String, Object>> listItems;
    private List<Map<String, Object>> listItems_temp;
    private SimpleAdapter simpleAdapter;
    private boolean thread_run = true;
    private Cursor cursor;
    private ContentResolver cr;
    private Handler thrad_handler;
    private boolean need_finish = false;

    @Override
    public void onCreate() {
        setContentView(R.layout.con_contact);
        setTitle("选择联系人");
        showBackButton();
        initView();
        // 直接读取竟然会卡顿
        tv_load.setText("");//加载中\n请稍候……（loading就不必了）
        thread_run = true;
        need_finish = false;
        startNewThread();
    }

    private void updateList() {
        for (int i = 0; i < listItems_temp.size(); i++) {
            listItems.add(listItems_temp.get(i));
        }
        if (listItems.size() == 0)
            tv_load.setText("通讯录\n为空");
        else
            simpleAdapter.notifyDataSetChanged();
        //这里，在更新时，疯狂的快速滑动列表会有不可捕获的运行时异常
        //然后谷歌到，主线程更新界面时，有多线程在添加列表项（相当于多线程也在修改界面）
        //放在主线程中添加列表项即可，但是会界面卡顿，我添加了sleep延时，出错概率降低了很多。
        //但是，刷新时点击依旧报错
        //后来，我使用计时器思路，仍然卡顿。没有搜索到很好的方法。
        //最后的最后，我想起了C++课设，于是使用了双handler控制思路，成功了……
    }

    @Override
    public void receiveMessage(String what) {
        updateList();
        if (what == null)//读取还没有结束
            thrad_handler.sendEmptyMessage(THREAD_HANDLER_INT);
    }

    @Override
    public void newThread() {
        Looper.prepare();
        initContact();
        thrad_handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case THREAD_HANDLER_INT:
                        addConnect();
                        break;
                }
            }
        };
        thrad_handler.sendEmptyMessage(THREAD_HANDLER_INT);
        Looper.loop();
    }

    private void initView() {
        tv_load = (TextView) findViewById(R.id.con_contact_load);
        list = (ListView) findViewById(R.id.con_contact_list);

        listItems = new ArrayList<Map<String, Object>>();
        listItems_temp = new ArrayList<Map<String, Object>>();
        simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.con_contact_item, new String[]{"name", "phone"},
                new int[]{R.id.con_contact_item_name,
                        R.id.con_contact_item_number});
        list.setAdapter(simpleAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dealItemCLick(position);
            }
        });
    }

    private void initContact() {
        // 得到contentresolver对象
        cr = getContentResolver();
        // 取得电话本中开始一项的光标，必须先moveToNext()
        cursor = cr.query(Contacts.CONTENT_URI, null, null, null, Contacts.DISPLAY_NAME);//姓名排序
    }

    private void addConnect() {
        int message_count = 0;
        listItems_temp.clear();
        while (!need_finish && message_count < 10 && cursor.moveToNext()) {//每次加载10条
            message_count++;
            Map<String, Object> listItem = new HashMap<String, Object>();
            // 取得联系人的名字索引
            int nameIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameIndex);
            listItem.put("name", contact);
            // 取得联系人的ID索引值
            String contactId = cursor.getString(cursor
                    .getColumnIndex(Contacts._ID));
            // 查询该位联系人的电话号码，类似的可以查询email，photo
            Cursor phone = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID
                    + " = " + contactId, null, null);
            // 一个人可能有几个号码
            while (phone.moveToNext()) {
                String strPhoneNumber = phone.getString(phone
                        .getColumnIndex(Phone.NUMBER));
                listItem.put("phone", strPhoneNumber);
                break;// 此处只获取第一个号码
            }
            phone.close();
            listItems_temp.add(listItem);
        }
        if (message_count < 10)
            sendMessage("END");//更新界面
        else if (!need_finish)
            sendMessage(null);//更新界面
    }

    private void dealItemCLick(int position) {
        String name = (String) listItems.get(position).get("name");
        String phone = (String) listItems.get(position).get("phone");
        Intent intent = getIntent();
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        setResult(ContactAdd.CODE_SELECT_SURE, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        need_finish = true;
        if (cursor != null)
            cursor.close();
        super.onDestroy();
    }

    @Override
    public void showContextMenu() {
    }

}
