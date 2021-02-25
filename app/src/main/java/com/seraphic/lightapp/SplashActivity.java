package com.seraphic.lightapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.login_module.models.PurchaseGetter;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.login_module.views.LoginBaseActivity;
import com.seraphic.lightapp.menuprofile.InappFragment;
import com.seraphic.lightapp.onbaording.OnboardingScreen;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {
    PrefsManager prefsManager;
    UserDetail mUserDetail;
    private BillingClientLifecycle billingClientLifecycle;

    public boolean startRegister = false;
    public static SplashActivity mInstance;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);
        setContentView(R.layout.splash_layout);
        mInstance = this;
        prefsManager = new PrefsManager(this);
        mUserDetail = prefsManager.getUserData();
        printHashKey(this);

        //  inBilling();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (prefsManager.getBoolData(Constants.LOGINSTATUS)) {

                    billingClientLifecycle = ((App) getApplication()).getBillingClientLifecycle();
                    getLifecycle().addObserver(billingClientLifecycle);
                    billingClientLifecycle.purchaseUpdateEvent.observe(SplashActivity.this, new Observer<List<Purchase>>() {
                        @Override
                        public void onChanged(List<Purchase> purchases) {

                            if (purchases != null) {
                                registerPurchases(purchases);

                            }


                        }
                    });


                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginBaseActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);


    }

    boolean isExpiredNew(long purchaseTime) {
        TimeZone defTimeZone = TimeZone.getDefault();
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        long exptime = purchaseTime;
//        long exptime = mUserDetail.expiryTimeSubcription;
        Date date = new Date(exptime);
        SimpleDateFormat sm = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm.setTimeZone(utcTimeZone);
        String utcDate = sm.format(date);
        Log.e("##Datee -Splash", "utc t=" + sm.getTimeZone().getDisplayName() + "  " + utcDate);
        SimpleDateFormat sm2 = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm2.setTimeZone(defTimeZone);
        String localeDate = sm2.format(date);
        Date locDate = null;
        try {
            locDate = sm2.parse(localeDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("##Datee-Splash", "defalt t=" + defTimeZone.getDisplayName() + "  " + localeDate);
        Date currDAte = new Date();
        Log.e("##Datee-Splash", "curr t=" + defTimeZone.getDisplayName() + "  " + sm2.format(currDAte));

        if (currDAte.getTime() >= locDate.getTime()) {
            return true;
        } else {
            return false;
        }

    }

    boolean isExpired() {
        TimeZone defTimeZone = TimeZone.getDefault();
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        long exptime = mUserDetail.expiryTimeSubcription;
        Date date = new Date(exptime);
        SimpleDateFormat sm = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm.setTimeZone(utcTimeZone);
        String utcDate = sm.format(date);
        Log.e("##Datee -Splash", "utc t=" + sm.getTimeZone().getDisplayName() + "  " + utcDate);
        SimpleDateFormat sm2 = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm2.setTimeZone(defTimeZone);
        String localeDate = sm2.format(date);
        Date locDate = null;
        try {
            locDate = sm2.parse(localeDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("##Datee-Splash", "defalt t=" + defTimeZone.getDisplayName() + "  " + localeDate);
        Date currDAte = new Date();
        Log.e("##Datee-Splash", "curr t=" + defTimeZone.getDisplayName() + "  " + sm2.format(currDAte));

        if (currDAte.getTime() >= locDate.getTime()) {
            return true;
        } else {
            return false;
        }

    }

    void getdetail() {
        ProgressDialog m = new ProgressDialog(this);
        m.setMessage("Please wait..");
        m.show();
        Date currDAte = new Date();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String datem = "";
        datem = sm.format(currDAte);
        UserDetail mUserdetail = prefsManager.getUserData();
        ApiService w = RestClient.intialize();
        JsonObject j = new JsonObject();
        j.addProperty("date", datem);
        Call<ResponseBody> call = w.getSubscriptionDetail(mUserdetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    String myresponse = null;
                    try {
                        myresponse = response.body().string();
                        Log.e("rrrrr", "" + myresponse);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (myresponse != null) {

                        PurchaseGetter p = new Gson().fromJson(myresponse, PurchaseGetter.class);
                        if (p != null) {
                            mUserdetail.isSubscriptionAvaliable = p.success;
                            if (p.androidReceipt != null) {
                                mUserdetail.expiryTimeSubcription = p.androidReceipt.expiryTimeMillis;
                            }
                            if (p.user != null) {
                                mUserdetail.currentStreak = p.user.currentStreak;
                                mUserdetail.healingDays = p.user.healingDays;
                                mUserdetail.healingTime = p.user.healingTime;
                            }
                            prefsManager.saveUserData(mUserdetail);
                        }

                    }
                }
                m.dismiss();

                if (mUserDetail != null) {
                    if (mUserDetail.isSubscriptionAvaliable) {
                        getLifecycle().removeObserver(billingClientLifecycle);
                        if (isExpired()) {
                            Intent intent = new Intent(SplashActivity.this, InappFragment.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, HomeBaseActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Intent intent = new Intent(SplashActivity.this, InappFragment.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, HomeBaseActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                m.dismiss();
                Intent intent = new Intent(SplashActivity.this, HomeBaseActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("Login", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Login", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("Login", "printHashKey()", e);
        }
    }

    public void registerPurchases(List<Purchase> purchaseList) {

      /*  getLifecycle().removeObserver(billingClientLifecycle);
        billingClientLifecycle.destroy();*/
        if (purchaseList.size() > 0) {
            for (Purchase purchase : purchaseList) {
                Log.e("isExpiredNew", "" + purchase.getPurchaseTime());

                if (isExpiredNew(purchase.getPurchaseTime())) {
                    Log.e("isExpiredNew", "true");

                } else {
                    Log.e("isExpiredNew", "false");

                }


                if (purchase.isAutoRenewing()) {

                    JsonObject jsonObLocalReceipt = new PrefsManager(this).getSavedLocalReceipt();

                    if (jsonObLocalReceipt != null) {

                        if (jsonObLocalReceipt.has("autoRenewing")
                                && jsonObLocalReceipt.get("autoRenewing").getAsBoolean()) {

                            // save in local database

                            Intent intent = new Intent(SplashActivity.this, HomeBaseActivity.class);
                            startActivity(intent);
                            finish();

                        } else {

                            //never genearated or deletd from localdb

                            Intent intent = new Intent(SplashActivity.this, InappFragment.class);
                            intent.putExtra("hasPurchase", true);
                            intent.putExtra("productId", purchase.getSku());
                            intent.putExtra("hasSubscription", purchase.isAutoRenewing());
                            intent.putExtra("hasSubscriptionCancelled", false);

                            startActivity(intent);
                            finish();

                        }


                    } else {

                        //not saved in local db   bcz of cache clear and logout


                        ////////////////////////////////

                        JsonObject j = JsonParser.parseString(purchase.getOriginalJson()).getAsJsonObject();
                        saveLocalRecipt(j);

                        Log.e("--save after relogin", " " + j);


                        Intent n = new Intent(SplashActivity.this, HomeBaseActivity.class);
                        startActivity(n);
                        finish();

                        getLifecycle().removeObserver(billingClientLifecycle);

                        ////////////////////////////////


                       /* Intent intent = new Intent(SplashActivity.this, InappFragment.class);
//                    Intent intent = new Intent(SplashActivity.this, HomeBaseActivity.class);
                        intent.putExtra("hasPurchase", true);
                        intent.putExtra("hasSubscription", purchase.isAutoRenewing());
                        intent.putExtra("hasSubscriptionCancelled", false);

                        startActivity(intent);
                        finish();

                        getLifecycle().removeObserver(billingClientLifecycle);*/

                    }


                } else {

                    // purchase might be cancel from playconsole

                    Intent intent = new Intent(SplashActivity.this, InappFragment.class);
                    intent.putExtra("hasSubscriptionCancelled", true);
                    intent.putExtra("hasPurchase", true);
                    intent.putExtra("productId", purchase.getSku());

                    startActivity(intent);

                    finish();

                    getLifecycle().removeObserver(billingClientLifecycle);

                }

                return;

            }
        } else {

            //no purchase yet

            Intent intent = new Intent(SplashActivity.this, InappFragment.class);
            intent.putExtra("hasPurchase", false);
            intent.putExtra("hasSubscriptionCancelled", false);

            startActivity(intent);
            finish();


        }


    }



    private void saveLocalRecipt(JsonObject jsonObjectLocal) {

        new PrefsManager(SplashActivity.this).saveLocalReceipt(jsonObjectLocal);
        Log.e("SplashActivity--", "jsonObjectLocal ");


    }


    public void registerPurchasesNew(List<Purchase> purchaseList) {
        if (purchaseList.size() > 0) {
            for (Purchase purchase : purchaseList) {
                if (!purchase.isAutoRenewing()) {
                    Intent intent = new Intent(SplashActivity.this, InappFragment.class);
                    startActivity(intent);
                    finish();
                }
            }
        } else {
            Intent intent = new Intent(SplashActivity.this, InappFragment.class);
            startActivity(intent);
            finish();


        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        //  inBilling();

    }


    public static SplashActivity getInstance() {
        return mInstance;
    }


}
