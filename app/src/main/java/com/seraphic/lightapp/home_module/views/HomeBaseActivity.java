package com.seraphic.lightapp.home_module.views;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.Purchase;
import com.seraphic.lightapp.App;
import com.seraphic.lightapp.BaseActivity;
import com.seraphic.lightapp.BillingClientLifecycle;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.SplashActivity;
import com.seraphic.lightapp.home_module.models.SessionCategories;
import com.seraphic.lightapp.home_module.models.SessionGetter;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.menuprofile.InappFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.ButterKnife;

public class HomeBaseActivity extends BaseActivity {
    FragmentManager fragmentManager;
    PrefsManager prefsManager;
    public List<SessionCategories> mList;
    public SessionGetter dailySession;
    UserDetail userd;
    private BillingClientLifecycle billingClientLifecycle;
    boolean isStatus=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.login_base_layout);
        ButterKnife.bind(this);
        init();


        // Register purchases when they change.


    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finishAffinity();
        } else {
            super.onBackPressed();
            if (fragmentManager.getBackStackEntryCount() == 1) {
//                HomeFragment.getmInstance().restartVid();
            }

        }
    }

    public void init() {
        mList = new ArrayList<>();
        dailySession = new SessionGetter();
        fragmentManager = getSupportFragmentManager();
        prefsManager = new PrefsManager(this);
        userd = prefsManager.getUserData();
        if (userd.firstName != null) {

            if (userd.firstName.equals("") || userd.firstName.equalsIgnoreCase("null") || userd.firstName.equalsIgnoreCase("Undefined")) {
                Constants.USER_NAME = "";

            }
            {
                Constants.USER_NAME = userd.firstName;

            }
        }
        if (userd != null) {
            if (userd.dailyReminder != null) {
                if (!userd.dailyReminder.equals("")) {
//                    scheduleNotification(getNotification("Complete your sessions"),userd.dailyReminder);
                }
            }
        }


    }

    public void pushFrgament(Fragment fragment, boolean add, boolean isreplace) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //   transaction.setCustomAnimations(R.anim.frag_slide_in_from_bottom, 0);
        if (isreplace) {
            transaction.replace(R.id.mFrameLayout, fragment);
        } else {
            transaction.add(R.id.mFrameLayout, fragment);
        }
        if (add) {
            transaction.addToBackStack(fragment.getTag());
        }
        transaction.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left);

        transaction.commit();
    }




    boolean isExpired() {
        TimeZone defTimeZone = TimeZone.getDefault();
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        long exptime = userd.expiryTimeSubcription;
        Date date = new Date(exptime);
        SimpleDateFormat sm = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm.setTimeZone(utcTimeZone);
        String utcDate = sm.format(date);
        Log.e("##Datee", "utc t=" + sm.getTimeZone().getDisplayName() + "  " + utcDate);
        SimpleDateFormat sm2 = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm2.setTimeZone(defTimeZone);
        String localeDate = sm2.format(date);
        Date locDate = null;
        try {
            locDate = sm2.parse(localeDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("##Datee", "defalt t=" + defTimeZone.getDisplayName() + "  " + localeDate);
        Date currDAte = new Date();
        Log.e("##Datee", "curr t=" + defTimeZone.getDisplayName() + "  " + sm2.format(currDAte));

        if (currDAte.getTime() >= locDate.getTime()) {
            return true;
        } else {
            return false;
        }

    }



    @Override
    protected void onResume() {
        super.onResume();


        // Register purchases when they change.
        pushFrgament(new HomeFragment(), true, false);



    }


    @Override
    protected void onPause() {

        super.onPause();


    }

    @Override
    protected void onDestroy() {
    //    AppCompatActivity)context
        super.onDestroy();


    }

    public void registerPurchases(List<Purchase> purchaseList) {
        if(purchaseList.size()>0)
        {
            for (Purchase purchase : purchaseList) {
                if (!purchase.isAutoRenewing()) {
                    Intent intent = new Intent(HomeBaseActivity.this, InappFragment.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        else
        {
            Intent intent = new Intent(HomeBaseActivity.this, InappFragment.class);
            startActivity(intent);
            finish();


        }




    }
    /*public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(this);
    }*/

}
