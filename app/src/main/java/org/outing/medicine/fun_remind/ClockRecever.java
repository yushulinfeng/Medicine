package org.outing.medicine.fun_remind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//启动响铃服务
public class ClockRecever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent0=new Intent(context, ClockService.class);
        intent0.putExtra("message", ClockService.CLOCK);
        context.startService(intent0);
    }

}
