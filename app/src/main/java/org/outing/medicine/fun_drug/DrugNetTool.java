package org.outing.medicine.fun_drug;

import android.content.Context;
import android.widget.Toast;

import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

public class DrugNetTool {
    private static String drug_name = "";
    private static String drug_act = "";

    //添加，删除，清空都要写在这里
    //由于需要更新列表，删除、清空在收藏中也有一份

    public static void addNetCollect(Context context, AnDrug drug) {
        dealNetCollect(context, drug, false);
    }

    public static void deleteNetCollect(Context context, AnDrug drug) {
        dealNetCollect(context, drug, true);
    }

    public static void dealNetCollect(Context context, AnDrug drug, boolean is_delate) {
        String name = drug.getName();
        String com_name = drug.getCommonName();
        drug_name = name + DrugMain.DRUG_SPLIT + com_name;
        if (is_delate) drug_act = "1";
        else drug_act = "0";
        Connect.POST(context, ServerURL.DRUG_PUT_COLLECT, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("medical", drug_name);
                list.put("act", drug_act);
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;
            }

            @Override
            public void onResponse(String response) {
                if (response == null) {//暂不处理
                } else if (response.equals("-4")) {
                } else if (response.equals("-3")) {
                } else if (response.equals("-2")) {
                } else if (response.equals("-1")) {
                } else if (response.equals("0")) {
                    //SUCCESS
                }
            }
        });
    }

    private static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
