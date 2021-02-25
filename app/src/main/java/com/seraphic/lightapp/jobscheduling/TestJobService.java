package com.seraphic.lightapp.jobscheduling;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.seraphic.lightapp.R;
import com.seraphic.lightapp.menuprofile.views.ReminderFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;

public class TestJobService extends JobService {

    // Notification channel ID.
    public static   String UserName =  "";

    private static final String PRIMARY_CHANNEL_ID =  "primary_notification_channel";
    // Notification manager.
    NotificationManager mNotifyManager;

    /**
     * Called by the system once it determines it is time to run the job.
     *
     * @param jobParameters Contains the information about the job.
     * @return Boolean indicating whether or not the job was offloaded to a
     * separate thread.
     * In this case, it is false since the notification can be posted on
     * the main thread.
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        // Create the notification channel.
        createNotificationChannel();
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.round_icon);
        // Set up the notification content intent to launch the app when
        // clicked.

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, ReminderFragment.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Hey " + UserName)
                .setContentText("Your daily healing session is ready!")
                .setContentIntent(contentPendingIntent)
//                .setSmallIcon(R.mipmap.round_icon)

         .setOngoing(false) // Cant cancel your notification (except NotificationManger.cancel(); )

//.setLargeIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                 .setAutoCancel(true);
              builder.setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                    .setColorized(true);
            builder.setSmallIcon(R.mipmap.lighticon_bt);

        mNotifyManager.notify(1, builder.build());
//startForegroundService(contentPendingIntent)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();
        stopSelf();
          return true;
    }

    /**
     * Called by the system when the job is running but the conditions are no
     * longer met.
     * In this example it is never called since the job is not offloaded to a
     * different thread.
     *
     * @param jobParameters Contains the information about the job.
     * @return Boolean indicating whether the job needs rescheduling.
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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