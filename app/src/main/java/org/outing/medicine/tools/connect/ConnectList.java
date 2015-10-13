package org.outing.medicine.tools.connect;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ConnectList {
    List<NameValuePair> list = null;

    public ConnectList() {
        list = new ArrayList<NameValuePair>();
    }

    public ConnectList put(String key, String value) {
        NameValuePair item = new BasicNameValuePair(key, value);
        list.add(item);
        return this;
    }

    public ConnectList put(String key, int value) {
        /*不是骗你，PHP后台，post过去键值对String与int收到的结果是相同的*/
        NameValuePair item = new BasicNameValuePair(key, value + "");
        list.add(item);
        return this;
    }

    public List<NameValuePair> getList() {
        return list;
    }

    /////////////////////静态方法////////////////////////////////

    /**
     * 直接获取网络数据类
     *
     * @param key_value 键1，值1，键2，值2，……键n,值n
     * @return 网络数据类
     */
    public static ConnectList getSimpleList(String... key_value) {
        if (key_value.length % 2 == 1)
            return null;
        ConnectList list = new ConnectList();
        for (int i = 0; i < key_value.length; i += 2) {
            list.put(key_value[i], key_value[i + 1]);
        }
        return list;
    }

}
