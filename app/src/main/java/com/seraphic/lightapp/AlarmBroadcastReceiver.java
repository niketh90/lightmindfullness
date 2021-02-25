package com.seraphic.lightapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String TITLE = "TITLE";

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//            String toastText = String.format("Alarm Reboot");
//            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
//            startRescheduleAlarmsService(context);
//        }
//        else {
            String toastText = String.format("Alarm Received");
//            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
//            if (!intent.getBooleanExtra(RECURRING, false)) {
//                startAlarmService(context, intent);
//            } {
                     startAlarmService(context, intent);

//            }
//        }
    }


    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

//    private void startRescheduleAlarmsService(Context context) {
//        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intentService);
//        } else {
//            context.startService(intentService);
//        }
//    }
}