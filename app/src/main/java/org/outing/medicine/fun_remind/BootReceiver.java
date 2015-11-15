package org.outing.medicine.fun_remind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//开机启动
public class BootReceiver extends BroadcastReceiver {
    static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)) {
            Intent start_intent = new Intent(context, ClockService.class);
            start_intent.putExtra("message", ClockService.BOOT);
            context.startService(start_intent);
        }
    }

}