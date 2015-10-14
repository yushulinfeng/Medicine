package org.outing.medicine.tools.connect;

import android.app.ProgressDialog;
import android.content.Context;

public class ConnectDialog {
    private ProgressDialog process_dialog = null;
    private Context context;
    private boolean can_cancel;
    private String title, text;

    public ConnectDialog() {
        context = null;
        title = "";
        text = "";
    }

    public void config(Context context, String title, String text,
                       boolean can_cancel) {
        this.context = context;
        this.title = title;
        this.text = text;
        this.can_cancel = can_cancel;
    }

    public void show() {
        if (context != null)
            process_dialog = ProgressDialog.show(context, title, text,
                    true, can_cancel);// 忙碌对话框
    }

    public void hide() {
        if (process_dialog != null)
            process_dialog.dismiss();
    }
}
