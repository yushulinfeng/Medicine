package org.outing.medicine.fun_remind;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.outing.medicine.start.tool.UserTool;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNetLogTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private ArrayList<AnRing> array;
    private ConnectList con_list;
    private boolean is_finish;
    private String[] user;

    private AddNetLogTask(Context context, ArrayList<AnRing> array, boolean is_finish) {
        this.context = context;
        this.array = array;
        this.is_finish = is_finish;
    }

    @Override
    protected Void doInBackground(Void... params) {
        loginAndUpload();
        return null;
    }

    //登录成功就上传数据，失败了就不用管了
    private void loginAndUpload() {
        user = UserTool.getUser(context);
        String name = user[0];
        if (name.equals("")) {// 未登录过
            return;
        }
        Connect.POST(context, ServerURL.LOGIN, new ConnectListener() {
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;
            }

            public ConnectList setParam(ConnectList list) {
                list.put("phone", user[0]);
                list.put("password", user[1]);
                return list;
            }

            public void onResponse(String response) {
                try {
                    int result = Integer.parseInt(response);
                    if (result > 0) {//登录成功，上传数据
                        uploadMedicineState();
                    } else {//登录失败
                    }
                } catch (Exception e) {//错误不必详细处理
                }
            }
        });


    }

    private void uploadMedicineState() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new Date());
        for (int i = 0; i < array.size(); i++) {
            con_list = getHistoryPostText(array.get(i), date, is_finish);
            Connect.POST(context, ServerURL.Post_Body_Message, new ConnectListener() {
                @Override
                public ConnectList setParam(ConnectList list) {
                    return con_list;
                }

                @Override
                public ConnectDialog showDialog(ConnectDialog dialog) {
                    return null;
                }

                @Override
                public void onResponse(String response) {
                    Log.e("EEE", "EEE ------" + "response");
                    //不必处理了
                    if (response == null) {
                    } else if (response.equals("-2")) {
                    } else if (response.equals("-1")) {
                    } else if (response.equals("0")) {
                    }
                }
            });
        }
    }

    private ConnectList getHistoryPostText(AnRing ring, String date, boolean is_finish) {
        ConnectList list = new ConnectList();
        String post_str = ring.remind.getDrugName() + RemindHistory.HIS_SPLITE
                + (is_finish ? 1 : 0) + RemindHistory.HIS_SPLITE + ring.timer.getTime()
                + RemindHistory.HIS_SPLITE + date;
        list.put("type", "4");
        list.put("data", post_str);
        return list;
    }

    ///////////////////////////////////////////

    public static void ADD(Context context, ArrayList<AnRing> array, boolean is_finish) {
        AddNetLogTask task = new AddNetLogTask(context.getApplicationContext(),
                array, is_finish);//因为这个活动会被关闭
        task.execute();
    }

}
