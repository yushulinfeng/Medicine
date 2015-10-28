package org.outing.medicine.fun_tools;

import android.content.Context;

import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

public class AddScore {
    private static int score_temp = 0;

    public static void addScore(Context context, int score) {
        score_temp = score;
        Connect.POST(context, ServerURL.SCORE_ADD, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("score", score_temp);
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;
            }

            @Override
            public void onResponse(String response) {
                int get = 0;
                try {
                    get = Integer.parseInt(response);//包含response=null
                } catch (Exception e) {
                    //ERROR
                    return;
                }
                if (get >= 0) {
                    //SUCCESS
                } else {
                    //FAIL
                }
            }
        });
    }

}
