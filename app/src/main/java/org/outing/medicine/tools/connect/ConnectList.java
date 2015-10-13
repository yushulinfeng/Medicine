package org.outing.medicine.tools.connect;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ConnectList {
    List<NameValuePair> list = null;

    public ConnectList() {
        list = new ArrayList<NameValuePair>();
    }

    public void put(String key, String value) {
        NameValuePair item = new BasicNameValuePair(key, value);
        list.add(item);
    }

    public void put(String key, int value) {
        /*不是骗你，PHP后台，post过去键值对String与int收到的结果是相同的*/
        NameValuePair item = new BasicNameValuePair(key, value + "");
        list.add(item);
    }

    public List<NameValuePair> getList() {
        return list;
    }


}
