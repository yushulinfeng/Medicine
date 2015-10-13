package org.outing.medicine.tools.connect;

import android.content.Context;
import android.os.AsyncTask;

public class ConnectEasy extends AsyncTask<Void, Void, String> {
    private Context context;
    private String url;
    private ConnectList list;
    private ConnectResponseListener listener;

    private ConnectEasy(Context context, String url, ConnectList list, ConnectResponseListener listener) {
        this.context = context;
        this.url = url;
        this.list = list;
        this.listener = listener;
    }


    @Override
    protected String doInBackground(Void... params) {
        Connect con = new Connect(context);
        return con.executePost(url, list);
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onResponse(result);
    }


    /////////////////////////基于回调的方法///////////////////////

    /**
     * 向指定网址发起post请求
     *
     * @param context context
     * @param url 网址
     * @param list 数据
     * @param listener 监听回调
     */
    public static void POST(Context context, String url, ConnectList list, ConnectResponseListener listener) {
        ConnectEasy easy = new ConnectEasy(context, url, list, listener);
        easy.execute();
    }
}