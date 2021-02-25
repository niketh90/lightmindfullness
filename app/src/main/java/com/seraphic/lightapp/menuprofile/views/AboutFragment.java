package com.seraphic.lightapp.menuprofile.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.Purchase;
import com.bumptech.glide.Glide;
import com.seraphic.lightapp.App;
import com.seraphic.lightapp.BillingClientLifecycle;
import com.seraphic.lightapp.BuildConfig;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.home_module.views.HomeFragment;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.menuprofile.InappFragment;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AboutFragment extends AppCompatActivity {
    PrefsManager prefsManager;
    UserDetail mUserDetail;
    private final int CLOSEINTENT = 10;
    @BindView(R.id.ivUserImage)
    CircleImageView ivUserImage;
    Context mCOntext;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    private BillingClientLifecycle billingClientLifecycle;
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mCOntext=context;
//    }

    @OnClick(R.id.tvpricavy)
    public void showPrivay() {
        Intent n = new Intent(this, WebViewFragment.class);
        n.putExtra("mcase", 1);
        startActivity(n);
//        WebViewFragment w=new WebViewFragment();
//                Bundle b=new Bundle();
//        b.putInt("mcase",1);
//        w.setArguments(b);
//        ((HomeBaseActivity)mCOntext).pushFrgament(w,true,false);
    }

    @OnClick(R.id.tvtermsncon)
    public void showterms() {
        Intent n = new Intent(this, WebViewFragment.class);
        n.putExtra("mcase", 2);
        startActivity(n);
//        WebViewFragment w=new WebViewFragment();
//        Bundle b=new Bundle();
//        b.putInt("mcase",2);
//        w.setArguments(b);
//        ((HomeBaseActivity)mCOntext).pushFrgament(w,true,false);
    }

    @OnClick(R.id.ivHome)
    public void gohome() {
        Intent n = new Intent();
        n.putExtra("finish", true);
        setResult(CLOSEINTENT, n);
        finish();
//        ((HomeBaseActivity)getActivity()).pushFrgament(new HomeFragment(),false,true);

    }

    @BindView(R.id.tvversionNumber)
    TextView tvversionNumber;

    @OnClick(R.id.ivBack)
    void cl() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.about_fragment);
        ButterKnife.bind(this);
        prefsManager = new PrefsManager(this);
        mUserDetail = prefsManager.getUserData();
        if (mUserDetail.profileImage != null) {
            Glide.with(this).load(mUserDetail.profileImage).placeholder(R.mipmap.profilepic_placeholder).into(ivUserImage);
        }
        tvUserName.setText(mUserDetail.firstName + " " + mUserDetail.lastName);
        tvversionNumber.setText("" + BuildConfig.VERSION_NAME);
    }







    public void registerPurchases(List<Purchase> purchaseList) {

      /*  getLifecycle().removeObserver(billingClientLifecycle);
        billingClientLifecycle.destroy();*/
        if(purchaseList.size()>0)
        {
            for (Purchase purchase : purchaseList) {
                if (purchase.isAutoRenewing()) {
                    Intent intent = new Intent(AboutFragment.this, HomeBaseActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Intent intent = new Intent(AboutFragment.this, InappFragment.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        else
        {
            Intent intent = new Intent(AboutFragment.this, InappFragment.class);
            startActivity(intent);
            finish();


        }




    }
}
