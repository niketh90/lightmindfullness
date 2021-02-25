package com.seraphic.lightapp.onbaording;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.seraphic.lightapp.R;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.menuprofile.InappFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingScreen extends AppCompatActivity {
    @BindView(R.id.mViewPAger)
    ViewPager mViewPAger;
    @BindView(R.id.v1)
    View v1;
    @BindView(R.id.v2)
    View v2;
    @BindView(R.id.v3)
    View v3;
    @BindView(R.id.tvSkip)
    TextView tvSkip;
    UserDetail mUserdetail;

    @OnClick(R.id.tvSkip)
    void skip() {
//        if (tvSkip.getText().toString().equals(getString(R.string.skip))) {
//            mViewPAger.setCurrentItem(mViewPAger.getCurrentItem() + 1);
//        } else {
            if (mUserdetail.isSubscriptionAvaliable) {
                if (isExpired()) {
                    Intent n = new Intent(this, InappFragment.class);
                    startActivity(n);
                    finish();
                } else {
                    Intent n = new Intent(this, HomeBaseActivity.class);
                    startActivity(n);
                    finish();
                }

            } else {
                Intent n = new Intent(this, InappFragment.class);
                startActivity(n);
                finish();
            }

//        }
    }

    boolean isExpired() {
        TimeZone defTimeZone = TimeZone.getDefault();
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        long exptime = mUserdetail.expiryTimeSubcription;
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

    ViewPagerADapter viewPagerADapter;
    PrefsManager prefsManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.onboarding_screen);
        ButterKnife.bind(this);
        prefsManager = new PrefsManager(this);
        mUserdetail = prefsManager.getUserData();
        prefsManager.setOnBoardData(Constants.OnBoarding, true);
        viewPagerADapter = new ViewPagerADapter(getSupportFragmentManager());
        mViewPAger.setAdapter(viewPagerADapter);
        mViewPAger.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("pagee", "" + position);
                if (position == 2) {
                    tvSkip.setText(getString(R.string.ge_started));
                } else {
                    tvSkip.setText(getString(R.string.skip));

                }
                switchInd(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    void switchInd(int pos) {
        ColorStateList clPink = ColorStateList.valueOf(getResources().getColor(R.color.baby_pink));
        ColorStateList trans = ColorStateList.valueOf(getResources().getColor(R.color.transLight));
        v1.setBackgroundTintList(trans);
        v2.setBackgroundTintList(trans);
        v3.setBackgroundTintList(trans);
        switch (pos) {

            case 0:
                v1.setBackgroundTintList(clPink);
                break;
            case 1:
                v2.setBackgroundTintList(clPink);
                break;
            case 2:
                v3.setBackgroundTintList(clPink);

                break;
        }

    }
}
