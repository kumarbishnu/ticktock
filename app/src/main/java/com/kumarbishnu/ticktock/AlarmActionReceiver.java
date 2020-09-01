package com.kumarbishnu.ticktock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmActionReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

//        if (intent.hasExtra("snooze")) {
//            snoozeAlarm();
//            context.stopService(new Intent(context, AlarmService.class));
//            context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
//        } else if (intent.hasExtra("stop")) {
//            stopAlarm();
//            context.stopService(new Intent(context, AlarmService.class));
//            context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
//        } else {
//            context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
//            context.stopService(new Intent(context, AlarmService.class));
//        }
    }

    private void snoozeAlarm() {}
    private void stopAlarm() {}
}
