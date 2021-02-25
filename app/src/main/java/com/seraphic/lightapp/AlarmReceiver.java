package com.seraphic.lightapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.seraphic.lightapp.jobscheduling.TestJobService;
import com.seraphic.lightapp.menuprofile.views.ReminderFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "1234";
    private static final int JOB_ID = 0;
    private JobScheduler mScheduler;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    NotificationManager mNotifyManager;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        Log.e("alarm","received");
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
//        }
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//        ringtone.play();

//        Date d=new Date();
//        SimpleDateFormat sm=new SimpleDateFormat("HH:mm");
//        try {
//            Date dv=sm.parse(Constants.HH+":"+Constants.MM);
//            if (d.getTime()-dv.getTime()>180000){
//
//
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        scheduleJob(context);
//        showNotificaion(context);

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            if(Build.VERSION.SDK_INT==Build.VERSION_CODES.P||Build.VERSION.SDK_INT==Build.VERSION_CODES.Q){
////                startWakefulService(context, (intent.setComponent(comp)));
////
////            }else {
////                startWakefulService(context, (intent.setComponent(comp)));
////
//            context.startService(new Intent(context, AlarmService.class));
//
////            context.startForegroundService((intent.setComponent(comp)));
////
////            }
//        } else {
////            startWakefulService(context, (intent.setComponent(comp)));
////
//            context.startService(new Intent(context, AlarmService.class));
//        }
//        setResultCode(Activity.RESULT_OK);

    }

    public void scheduleJob(Context context) {


        int selectedNetworkOption;//= JobInfo.NETWORK_TYPE_NONE;

        selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
        ComponentName serviceName = new ComponentName(context.getPackageName(),
                TestJobService.class.getName());
         TestJobService.UserName=new PrefsManager(context).getUserData().firstName;
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(true)
                .setRequiresCharging(false);

        builder.setOverrideDeadline(1000);
        boolean constraintSet = selectedNetworkOption
                != JobInfo.NETWORK_TYPE_NONE
                || true
                || true
                || false;
        if (constraintSet) {
            JobInfo myJobInfo = builder.build();
            mScheduler.schedule(myJobInfo);
        } else {
        }
    }

    void showNotificaion(Context context) {
        createNotificationChannel(context);

        // Set up the notification content intent to launch the app when
        // clicked.

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, 0, new Intent(context, ReminderFragment.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, PRIMARY_CHANNEL_ID)
                .setContentTitle("Hey " + Constants.USER_NAME)
                .setContentText("Your daily healing session is ready!")
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.mipmap.logo)
                .setColorized(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        mNotifyManager.notify(1, builder.build());


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), alarmUri);
        ringtone.play();
    }

    public void createNotificationChannel(Context context) {

        // Create a notification manager object.
        mNotifyManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID, "Light Chanel",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("mychannel");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

}