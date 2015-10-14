package org.outing.medicine.tools.connect;

import android.content.Context;
import android.os.AsyncTask;

public class Connect extends AsyncTask<Void, Void, String> {
    private Context context;
    private String url;
    private ConnectList list;
    private ConnectDialog dialog;
    private ConnectListener listener;

    private Connect(Context context, String url, ConnectListener listener) {
        this.context = context;
        this.url = url;
        this.listener = listener;
        list = new ConnectList();
        dialog = new ConnectDialog();
        if (listener != null) {
            list = listener.setParam(list);
            dialog = listener.showDialog(dialog);
        }
        if (list == null)//防止listener返回错了
            list = new ConnectList();
        if (dialog == null)
            dialog = listener.showDialog(dialog);
    }

    @Override
    protected void onPreExecute() {
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        ConnectBase con = new ConnectBase(context);
        return con.executePost(url, list);
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null)
            listener.onResponse(result);
        if (dialog != null)
            dialog.hide();
    }


    /////////////////////////基于回调的方法///////////////////////

    /**
     * 向指定网址发起post请求
     *
     * @param context  context
     * @param url      网址
     * @param listener 监听回调
     */
    public static void POST(Context context, String url, ConnectListener listener) {
        Connect connect = new Connect(context, url, listener);
        connect.execute();
    }
}