package com.seraphic.lightapp;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;

import com.android.billingclient.api.BillingClientStateListener;

import com.android.billingclient.api.BillingResult;


import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.println;

public class App extends Application  {
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
    BillingClient billingClient;
    public App context;
    private BillingClient mBillingClient;
    private List<String> skuList = new ArrayList<String>();
    private BillingClientLifecycle billingClientLifecycle;


    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        createNotificationChannnel();
     //  billingClientLifecycle = getBillingClientLifecycle();

    }

   /* public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(context);
    }*/

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
             NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(this);
    }





}