package com.seraphic.lightapp.pushnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;


public class MyFirebaseMessagingService extends  FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e("mes","hhhhhhhhh");

        super.onMessageReceived(remoteMessage);
        Log.e("mes",""+remoteMessage.getNotification().getBody());
//        addNotification(remoteMessage.getNotification().getBody());
//        Intent intent = new Intent(this, HomeBaseActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        String channelId = getString(R.string.default_notification_channel_id);
//        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.mipmap.logo)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setColor(getResources().getColor(R.color.colorPrimary))
//                .setColorized(true)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setWhen(System.currentTimeMillis())
//
//                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "Default channel",  NotificationManager.IMPORTANCE_HIGH);
//            manager.createNotificationChannel(channel);
//        }
//        manager.notify(0, builder.build());

    }

      @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("toke",""+s);
//        sendRegistrationToServer(token);

    }
    NotificationManager manager;
//    private void showNotification(boolean isFirstShow, String albumName, String songName, boolean isPlay,int titleCase) {
//// Using RemoteViews to bind custom layouts into Notification
////        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
////        notificationManager.cancel(NOTIFICATION_ID);
////        notificationManager.cancelAll();
//
//
////        MyApplication.getInstance().changeLanguage(getApplicationContext());
//        String subtitle = "Artist";
//
//        RemoteViews bigViews = null;
//        NotificationCompat.Builder notificationBuilder = null;
//        Notification notification = null;
//        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        bigViews = new RemoteViews(getPackageName(), R.layout.status_bar_expansion);
//
//        Intent notificationIntent = new Intent(getApplicationContext(), NavigationActivityBase.class);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent intent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//        Intent previousIntent = new Intent(this, MediaPlayerService.class);
//        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
//        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
//                previousIntent, 0);
//
//        Intent playIntent = new Intent(this, MediaPlayerService.class);
//        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
//        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
//                playIntent, 0);
//
//        Intent nextIntent = new Intent(this, MediaPlayerService.class);
//        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
//        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
//                nextIntent, 0);
//
//        Intent closeIntent = new Intent(this, MediaPlayerService.class);
//        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
//        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
//                closeIntent, 0);
//        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
//        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
//        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
//        bigViews.setOnClickPendingIntent(R.id.ivAlbumImage, intent);
//        bigViews.setTextViewText(R.id.status_bar_track_name, songName);
//        bigViews.setTextViewText(R.id.status_bar_album_name, subtitle);
//
////        Drawable d = null;
//        int d;
//        NotificationCompat.Action playAction = new NotificationCompat.Action(R.drawable.icn_pause, "play ", pplayIntent);
//        NotificationCompat.Action pauseAction = new NotificationCompat.Action(R.drawable.icn_play, "pause ", pplayIntent);
//        NotificationCompat.Action mAction = null;
//
//        Log.e("actions", ""+subtitleCase);
//
//        if (isPlay) {
//            mAction = playAction;
//            d = R.drawable.icn_pause;
////         d=   AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.icn_pause);
//            bigViews.setImageViewResource(R.id.status_bar_play, R.mipmap.icn_pause);
//        } else {
//            mAction = pauseAction;
//            d = R.drawable.icn_play;
//            bigViews.setImageViewResource(R.id.status_bar_play, R.mipmap.icn_play);
//            Log.e("actions", "play icon");
////            d=   AppCompatDrawableManager.get().getDrawable(getApplicationContext(), R.drawable.icn_play);
//        }
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
//            chan.setLightColor(Color.BLUE);
//            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            assert manager != null;
//            manager.createNotificationChannel(chan);
//
//            NOTIFICATION_ID = 2;
//            notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//            notificationBuilder.setOngoing(true)
//                    .setSmallIcon(R.drawable.notification_icon)
//
//                    .setContentTitle(subtitle)
//                    //                     .addAction(R.drawable.icn_back,"play prev",ppreviousIntent)
////
////                    .addAction(mAction)
////                     .addAction(R.drawable.icn_next,"play prev",pnextIntent)
////
//////                     .addAction(R.drawable.stop_songs,"",pplayIntent)
//                    .setPriority(NotificationManager.IMPORTANCE_MIN)
////             .setStyle(new NotificationCompat.BigPictureStyle())
//
//                    .setCategory(Notification.CATEGORY_SERVICE)
//                    .setContent(bigViews);
//        } else {
//            NOTIFICATION_ID = 1;
//            notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//
//            Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
//                    R.drawable.ic_launcher);
//            notificationBuilder.setOngoing(true)
//                    .setSmallIcon(R.drawable.notification_icon)
//                    .setColorized(true)
//                    .setColor(getResources().getColor(R.color.blue))
//
//                    .setContentTitle(subtitle)
//                    .setCategory(Notification.CATEGORY_SERVICE)
//                    .setCustomBigContentView(bigViews);
//        }
//
//        if (isFirstShow) {
//            startForeground(NOTIFICATION_ID, notificationBuilder.build());
//        } else {
//            startForeground(NOTIFICATION_ID, notificationBuilder.build());
//
////            manager.notify(NOTIFICATION_ID, notificationBuilder.build());
//        }
////        Picasso.get().load(d).into(bigViews, R.id.status_bar_play, NOTIFICATION_ID, notification);
//
//
//
//
//    }

}
