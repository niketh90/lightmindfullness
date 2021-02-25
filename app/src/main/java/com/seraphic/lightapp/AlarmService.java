package com.seraphic.lightapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.menuprofile.views.ReminderFragment;

import java.io.IOException;

import static com.seraphic.lightapp.AlarmBroadcastReceiver.TITLE;

public class AlarmService extends Service {
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    Uri alarmUri;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        }
//        try {
////mediaPlayer=new MediaPlayer();
////            mediaPlayer.setDataSource(this,alarmUri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
//        mediaPlayer.setLooping(true);

//        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, ReminderFragment.class);
        notificationIntent.putExtra("fromAlarm",true);
        notificationIntent.setAction("alarm");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String alarmTitle = intent.getStringExtra(TITLE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Hey " + alarmTitle)
                .setSmallIcon(R.mipmap.lighticon_bt)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))

//                .setColorized(true)
                .setContentText("Your daily healing session is ready!")
                 .setDefaults(NotificationCompat.DEFAULT_ALL)

                .setContentIntent(pendingIntent).setAutoCancel(true);

//        mediaPlayer.start();

        long[] pattern = {0, 100, 1000};
//        vibrator.vibrate(pattern, 0);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();
        Notification notification1=notification.build();
        notification1.flags=Notification.FLAG_AUTO_CANCEL;
        startForeground(1, notification1);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        mediaPlayer.stop();
//        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}