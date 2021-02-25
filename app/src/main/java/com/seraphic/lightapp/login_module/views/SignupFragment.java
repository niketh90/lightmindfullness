package com.seraphic.lightapp.login_module.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.Purchase;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

public class SignupFragment extends Fragment implements FacebookCallback<LoginResult> {
    Activity mContext;
    PrefsManager prefsManager;
    CallbackManager callbackManager;
    GoogleSignInOptions gso;
    ProgressDialog progressDialog;
    @BindView(R.id.tvterns)
    TextView tvTermsCon;
    private BillingClientLifecycle billingClientLifecycle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @OnClick(R.id.tvlogin)
    public void gotologin() {
        ((LoginBaseActivity) mContext).pushFrgament(new LoginFragment(), true);
    }

    @OnClick(R.id.lrContinueEMail)
    public void gocreate() {
        ((LoginBaseActivity) mContext).pushFrgament(new CreateAccountFragment(), true);
    }

    void facebookLogout(AccessToken accessToken) {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(accessToken, "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();
                callFBLogin();

            }
        }).executeAsync();

    }

    void callFBLogin() {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("email", "public_profile")
        );
    }

    AccessTokenTracker accessTokenTracker;

    @OnClick(R.id.lrContinueFacebbok)
    public void gohomeeate() {

        callFBLogin();
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//        if (isLoggedIn) {
//            facebookLogout(accessToken);
//        } else {
//            callFBLogin();
//        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("onac", "---" + requestCode);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);


        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {

            GoogleSignInAccount account = task.getResult(ApiException.class);

            Log.e("data-----------", " " + account.getDisplayName());

            //            Uri imageUrl = account.getPhotoUrl();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String token = account.getIdToken();
            authGoogle(token, email, name, name);
//            new GetProfileDetails(account, weakAct, TAG).execute();
        } catch (ApiException e) {

//            Toast.makeText(mContext, "Something went wrong, Pleas try again!", Toast.LENGTH_SHORT).show();
            Log.e("_-----------", "signInResult:failed code=" + e.getStatusCode());
        }


    }

    public void authGoogle(String accessToken, String email, String lastnamr, String firstname) {
        progressDialog.show();
        JsonObject j = new JsonObject();
        j.addProperty("access_token", accessToken);
        j.addProperty("deviceType", 2);
        j.addProperty("deviceToken", prefsManager.getData(Constants.DEVICE_TOKEN));
        ApiService apiService = RestClient.intialize();
        Call<ResponseBody> call = apiService.authGoogle(j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    try {
                        UserDetail userDetail = new Gson().fromJson(response.body().string(), UserDetail.class);
//                        userDetail.email = email;
//                        userDetail.firstName = userDetail.firstName;
//                        userDetail.lastName = userDetail.lastName;
                        prefsManager.saveUserData(userDetail);

                        prefsManager.setbooldata(Constants.LOGINSTATUS, true);
                        getdetail();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void authFacebook(String accessToken, String email, String lastnamr, String firstname) {
        progressDialog.show();
        JsonObject j = new JsonObject();
        j.addProperty("access_token", accessToken);
        j.addProperty("deviceType", 2);
        j.addProperty("deviceToken", prefsManager.getData(Constants.DEVICE_TOKEN));
        ApiService apiService = RestClient.intialize();
        Call<ResponseBody> call = apiService.authFacebook(j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        UserDetail userDetail = new Gson().fromJson(response.body().string(), UserDetail.class);
//                        userDetail.email = email;
//                        userDetail.firstName = firstname;
//                        userDetail.lastName = lastnamr;
                        prefsManager.saveUserData(userDetail);
                        prefsManager.setbooldata(Constants.LOGINSTATUS, true);
                        getdetail();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.lrContinuegoogle)
    public void gohomeea2te() {
        if (Utility.isNetworkAvailable(mContext)) {
            if (prefsManager.getData(Constants.DEVICE_TOKEN).equals("")) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("fire", "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                if (token != null) {
                                    if (!token.equals("")) {
                                        prefsManager.setData(Constants.DEVICE_TOKEN, token);

                                    }
                                }


                                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
                                mGoogleSignInClient.revokeAccess();
                                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                                startActivityForResult(signInIntent, 100);
                                // Log and toast
                                Log.e("fire", token);
                            }
                        });
            } else {
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
                mGoogleSignInClient.revokeAccess();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
                Log.e("@@viewmodal", "megamail");

            }

        } else {
            Utility.alertDialog(mContext, "Alert!", "Please check your internet connection.", false);

        }


    }


    public void init() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        prefsManager = new PrefsManager(mContext);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(myScope)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        String b = "By continuing you agree to our <a href=http://ec2-3-21-237-151.us-east-2.compute.amazonaws.com:3000/privacy> Privacy policy</a> and <a href=http://ec2-3-21-237-151.us-east-2.compute.amazonaws.com:3000/terms/>Terms and Conditions.</a>";
        String a = "By continuing you agree to our <a href=http://ec2-3-21-237-151.us-east-2.compute.amazonaws.com:3000/terms/>T&Cs.</a> We use     " +
                "your data to offer you a personalised         \nexperience. <a href=http://ec2-3-21-237-151.us-east-2.compute.amazonaws.com:3000/privacy> Find out more.</a>";
        tvTermsCon.setText(Html.fromHtml(b));
        tvTermsCon.setMovementMethod(LinkMovementMethod.getInstance());

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(mContext);
        View v = inflater.inflate(R.layout.signup_fragment, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();


//        if (accessToken != null && !accessToken.isExpired())

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            Log.e("id", object.getString("id"));
                            String firstName = object.getString("first_name");
                            String lastName = object.getString("last_name");
                            String email = null;
                            if (!object.isNull("email")) {
                                email = object.getString("email");

                            }
                            String fid = object.getString("id");
//                            String image_fb = "https://graph.facebook.com/" + fid + "/picture?type=large";
                            Log.e("facebook", "name: " + firstName + " " + lastName + " \n" + "Email: " + " \n" + "ID: " + fid);
                            prefsManager.setBoolData(Constants.LOGINSTATUS, true);
//                            prefsManager.setData(Constants.LOGIN_Name, firstName + lastName);
                            authFacebook(loginResult.getAccessToken().getToken(), email, lastName, firstName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, gender,email, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    UserDetail mUserdetail;

    void getdetail() {
        mUserdetail = prefsManager.getUserData();
        ApiService w = RestClient.intialize();
        Date currDAte = new Date();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String datem = "";
        datem = sm.format(currDAte);
        JsonObject j = new JsonObject();
        j.addProperty("date", datem);
        Call<ResponseBody> call = w.getSubscriptionDetail(mUserdetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    progressDialog.dismiss();

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
              /*  billingClientLifecycle = ((App) getActivity().getApplication()).getBillingClientLifecycle();
                getLifecycle().addObserver(billingClientLifecycle);*/

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


                if (prefsManager.getonbardBoolData(Constants.OnBoarding)) {
                  /*  if (mUserdetail.isSubscriptionAvaliable) {
                        if (isExpired()) {
                            Intent n = new Intent(mContext, InappFragment.class);
                            startActivity(n);
                            getActivity().finish();
                        } else {
                            Intent n = new Intent(mContext, HomeBaseActivity.class);
                            startActivity(n);
                            getActivity().finish();
                        }

                    } else {
                        Intent n = new Intent(mContext, InappFragment.class);
                        startActivity(n);
                        getActivity().finish();
                    }*/

               /*     billingClientLifecycle = ((App) getActivity().getApplication()).getBillingClientLifecycle();
                    getLifecycle().addObserver(billingClientLifecycle);

                    // Register purchases when they change.
                    billingClientLifecycle.purchaseUpdateEvent.observe(getActivity(), new Observer<List<Purchase>>() {
                        @Override
                        public void onChanged(List<Purchase> purchases) {
                            if (purchases != null) {
                                registerPurchases(purchases);
                            }
                        }
                    });*/
                } else {
                    Intent n = new Intent(mContext, OnboardingScreen.class);
                    startActivity(n);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();

            }
        });


    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    boolean isExpired() {
        TimeZone defTimeZone = TimeZone.getDefault();
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        long exptime = mUserdetail.expiryTimeSubcription;
        Date date = new Date(exptime);
        SimpleDateFormat sm = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm.setTimeZone(utcTimeZone);
        String utcDate = sm.format(date);
        Log.e("##Datee -Signup", "utc t=" + sm.getTimeZone().getDisplayName() + "  " + utcDate);
        SimpleDateFormat sm2 = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm2.setTimeZone(defTimeZone);
        String localeDate = sm2.format(date);
        Date locDate = null;
        try {
            locDate = sm2.parse(localeDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("##Datee-Signup", "defalt t=" + defTimeZone.getDisplayName() + "  " + localeDate);
        Date currDAte = new Date();
        Log.e("##Datee-Signup", "curr t=" + defTimeZone.getDisplayName() + "  " + sm2.format(currDAte));

        if (currDAte.getTime() >= locDate.getTime()) {
            return true;
        } else {
            return false;
        }

    }

    public void registerPurchases(List<Purchase> purchaseList) {
        if (purchaseList.size() > 0) {
            for (Purchase purchase : purchaseList) {
                if (purchase.isAutoRenewing()) {
                  /*  Intent intent = new Intent(getActivity(), HomeBaseActivity.class);
                    startActivity(intent);
                    getActivity().finish();*/


                    JsonObject jsonObLocalReceipt = new PrefsManager(getContext()).getSavedLocalReceipt();

                    if (jsonObLocalReceipt != null) {

                        if (jsonObLocalReceipt.has("autoRenewing")
                                && jsonObLocalReceipt.get("autoRenewing").getAsBoolean()) {

                            // save in local database

                            Intent intent = new Intent(getContext(), HomeBaseActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                        } else {
                            //never genearated or deletd from localdb

                            Intent intent = new Intent(getContext(), InappFragment.class);
                            intent.putExtra("hasPurchase", true);
                            intent.putExtra("hasSubscription", purchase.isAutoRenewing());
                            intent.putExtra("hasSubscriptionCancelled", false);
                            startActivity(intent);
                            getActivity().finish();

                        }


                    } else {

                        //not saved in local db   bcz of cache clear and logout


                        ////////////////////////////////

                        JsonObject j = JsonParser.parseString(purchase.getOriginalJson()).getAsJsonObject();
                        saveLocalRecipt(j);

                        Log.e("--save after relogin", " " + j);


                        Intent n = new Intent(getContext(), HomeBaseActivity.class);
                        startActivity(n);
                        getActivity().finish();

                        getLifecycle().removeObserver(billingClientLifecycle);

/*
                        Intent intent = new Intent(getContext(), InappFragment.class);
//                    Intent intent = new Intent(SplashActivity.this, HomeBaseActivity.class);
                        intent.putExtra("hasSubscription", purchase.isAutoRenewing());
                        startActivity(intent);
                        getActivity().finish();*/
                    }


                } else {

                    Intent n = new Intent(mContext, OnboardingScreen.class);
                    startActivity(n);
                    getActivity().finish();
                }
            }
        } else {
            Intent n = new Intent(mContext, OnboardingScreen.class);
            startActivity(n);
            getActivity().finish();


        }


    }
   /* public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(getActivity());
    }*/



    private void saveLocalRecipt(JsonObject jsonObjectLocal) {

        new PrefsManager(getContext()).saveLocalReceipt(jsonObjectLocal);
        Log.e("Signup--", "jsonObjectLocal ");


    }

}
