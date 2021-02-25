package com.seraphic.lightapp.login_module.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.Purchase;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seraphic.lightapp.App;
import com.seraphic.lightapp.BillingClientLifecycle;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.SplashActivity;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.login_module.models.PurchaseGetter;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.menuprofile.InappFragment;
import com.seraphic.lightapp.onbaording.OnboardingScreen;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginFragment extends Fragment {
    Context mContext;
    ProgressDialog progressDialog;
    PrefsManager prefsManager;
    private BillingClientLifecycle billingClientLifecycle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
    @BindView(R.id.edPassword)
    EditText edPassword;
    @BindView(R.id.edEmail)
    EditText edEmail;
    @OnClick(R.id.tvforgotPass)
    public void forg() {
        ((LoginBaseActivity) mContext).pushFrgament(new ForgotPasswordFragment(), true);
    }
    @OnClick(R.id.tvSignUp)
    public void sign() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
    @OnClick(R.id.ivLoginB)
    public void logi() {
        if (isDataValid()) {
            loginUser(); } }

    @OnClick(R.id.ivBack)
    public void gobakc() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View v = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    public void init() {
        prefsManager = new PrefsManager(mContext);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    public void loginUser() {
        progressDialog.show();
        JsonObject jRequest = new JsonObject();
        jRequest.addProperty("email", edEmail.getText().toString().trim());
        jRequest.addProperty("password", edPassword.getText().toString());
        jRequest.addProperty("deviceType", 2);
        jRequest.addProperty("deviceToken", prefsManager.getData(Constants.DEVICE_TOKEN));
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.login(jRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String mRepsonse=response.body().string();
                        if (mRepsonse!=null){
                            UserDetail userDetail=new Gson().fromJson(mRepsonse,UserDetail.class);
                            prefsManager.saveUserData(userDetail);
                            prefsManager.setbooldata(Constants.LOGINSTATUS,true);

                         getdetail();

                        }
                     } catch (IOException e) {
                        e.printStackTrace();
//                        Utility.alertDialog(mContext, getString(R.string.alert),getString(R.string.), false);

                    }
                } else if (response.code() == 401) {
                    progressDialog.dismiss();
                    try {
                        String mError = response.errorBody().string();
                        JsonObject j = new JsonParser().parse(mError).getAsJsonObject();
                        if (!j.get("message").isJsonNull()) {
                            if (j.get("message").getAsString().contains("Authentication failed. Email is not Verified Not Allowed to Login")) {
                                Utility.alertDialog(mContext, "", getString(R.string.unverifyied), false);
                                return;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Utility.alertDialog(mContext, "", getString(R.string.unauthorized), false);
                }else{
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }
    UserDetail mUserdetail;

    void getdetail() {
        Date currDAte = new Date();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String datem = "";
        datem = sm.format(currDAte);
        JsonObject j=new JsonObject();
        j.addProperty("date",datem);
      mUserdetail  = prefsManager.getUserData();
        ApiService w = RestClient.intialize();
        Call<ResponseBody> call = w.getSubscriptionDetail(mUserdetail.token,j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                if (response.code() == 200) {

                    String myresponse = null;
                    try {
                        myresponse = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (myresponse != null) {

                        PurchaseGetter p=new Gson().fromJson(myresponse,PurchaseGetter.class);
                        mUserdetail.isSubscriptionAvaliable=p.success;
                        if (p.androidReceipt!=null){
                            mUserdetail.expiryTimeSubcription =p.androidReceipt.expiryTimeMillis;

                        }
                        if (p.user!=null){
                            mUserdetail.currentStreak=p.user.currentStreak;
                            mUserdetail.healingDays=p.user.healingDays;
                            mUserdetail.healingTime=p.user.healingTime;
                        }
                        prefsManager.saveUserData(mUserdetail);

                    }
                }


        /*        billingClientLifecycle = ((App) getActivity().getApplication()).getBillingClientLifecycle();
                getLifecycle().addObserver(billingClientLifecycle);*/


                billingClientLifecycle = ((App) getActivity().getApplication()).getBillingClientLifecycle();
                getLifecycle().addObserver(billingClientLifecycle);
               // billingClientLifecycle = getBillingClientLifecycle();
                getLifecycle().addObserver(billingClientLifecycle);
                // Register purchases when they change.
                billingClientLifecycle.purchaseUpdateEvent.observe(getActivity(), new Observer<List<Purchase>>() {
                    @Override
                    public void onChanged(List<Purchase> purchases) {
                        if (purchases != null) {
                            registerPurchases(purchases);
                        }
                    }
                });
             /*   if (prefsManager.getonbardBoolData(Constants.OnBoarding)) {
                  *//*  if (mUserdetail.isSubscriptionAvaliable){
                        if (isExpired()){
                            Intent n = new Intent(mContext, InappFragment.class);
                            startActivity(n);
                            getActivity().finish();
                        }else {
                            Intent n = new Intent(mContext, HomeBaseActivity.class);
                            startActivity(n);
                            getActivity().finish();
                        }
                    }else {
                        Intent n = new Intent(mContext, InappFragment.class);
                        startActivity(n);
                        getActivity().finish();
                    }*//*

                    billingClientLifecycle = ((App) getActivity().getApplication()).getBillingClientLifecycle();
                    getLifecycle().addObserver(billingClientLifecycle);

                    // Register purchases when they change.
                    billingClientLifecycle.purchaseUpdateEvent.observe(getActivity(), new Observer<List<Purchase>>() {
                        @Override
                        public void onChanged(List<Purchase> purchases) {
                            if (purchases != null) {
                                registerPurchases(purchases);
                            }
                        }
                    });

                } else {
                    Intent n = new Intent(mContext, OnboardingScreen.class);
                    startActivity(n);
                    getActivity().finish();
                }*/
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();

            }
        });


    }

    boolean isExpired(){
        TimeZone defTimeZone = TimeZone.getDefault();
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        long exptime=mUserdetail.expiryTimeSubcription;
        Date date=new Date(exptime);
        SimpleDateFormat sm=new SimpleDateFormat("yy-MM-dd HH:mm");
        sm.setTimeZone(utcTimeZone);
        String utcDate=sm.format(date);
        Log.e("##Datee","utc t="+sm.getTimeZone().getDisplayName()+"  "+utcDate);
        SimpleDateFormat sm2=new SimpleDateFormat("yy-MM-dd HH:mm");
        sm2.setTimeZone(defTimeZone);
        String localeDate=sm2.format(date);
        Date locDate=null;
        try {
            locDate=sm2.parse(localeDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("##Datee","defalt t="+defTimeZone.getDisplayName()+"  "+localeDate);
        Date currDAte=new Date();
        Log.e("##Datee","curr t="+defTimeZone.getDisplayName()+"  "+sm2.format(currDAte));

        if (currDAte.getTime()>=locDate.getTime()){
            return true;
        }else {
            return false;
        }

    }

    public final boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    boolean isDataValid() {
        boolean isIt = true;
        if (!TextUtils.isEmpty(edPassword.getText())) {
            if (edPassword.getText().length()<7){
                edPassword.setError(getString(R.string.password_longer));
                isIt = false;

            }
         } else {
            isIt = false;
            edPassword.setError(getString(R.string.empty_field));
        }

        if (!TextUtils.isEmpty(edEmail.getText())) {
            if (isValidEmail(edEmail.getText().toString().trim())) {
             } else {
                isIt = false;
                edEmail.setError(getString(R.string.enter_valid_email));
            }
        } else {
            isIt = false;
            edEmail.setError(getString(R.string.empty_field));
        }
        return isIt;
    }

    public void setLeftROundCornerImage() {
        Bitmap mbitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.img_login)).getBitmap();
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//        canvas.drawRoundRect((new RectF(0, -150, mbitmap.getWidth()+150,mbitmap.getHeight()-200)), 250, 250, mpaint); // Round Image Corner 100 100 100 100
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 0, 0, mpaint); // Round Image Corner 100 100 100 100

    }


    public void registerPurchases(List<Purchase> purchaseList) {
        if(purchaseList.size()>0)
        {
            for (Purchase purchase : purchaseList) {
                if (purchase.isAutoRenewing()) {
                    Intent intent = new Intent(getActivity(), HomeBaseActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {

                    Intent n = new Intent(mContext, OnboardingScreen.class);
                    startActivity(n);
                    getActivity().finish();
                }
            }
        }
        else
        {
            Intent n = new Intent(mContext, OnboardingScreen.class);
            startActivity(n);
            getActivity().finish();


        }




    }
   /* public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(getActivity());
    }*/

}
