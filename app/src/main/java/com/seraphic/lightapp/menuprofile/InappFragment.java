package com.seraphic.lightapp.menuprofile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.seraphic.lightapp.login_module.views.LoginBaseActivity;
import com.seraphic.lightapp.menuprofile.modals.PurchaseReceiptPojo;
import com.seraphic.lightapp.menuprofile.views.MenuFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.sql.DriverManager.println;

public class InappFragment extends AppCompatActivity implements PurchasesUpdatedListener {
    UserDetail mUserDetail;
    Context context;
    PrefsManager prefsManager;
    BillingClient billingClient;
    private List<String> skuList = new ArrayList<String>();
    LinearLayout cl_monthly, cl_quaterly, cl_yearly;
    @BindView(R.id.tvYearlyPrice)
    TextView tvYearlyPrice;
    @BindView(R.id.tvMonthlyPrice)
    TextView tvMonthlyPrice;
    @BindView(R.id.tvQuartlyPrice)
    TextView tvQuartlyPrice;
    @BindView(R.id.tvRestore)
    TextView tvRestore;
    private BillingClientLifecycle billingClientLifecycle;

    List<Purchase> purchaseList = new ArrayList<>();

    boolean hasPurchase = false;                    //purchase exist
    boolean hasSubscriptionExist = false;                //purchase exist and also autorenew true
    boolean hasSubscriptionCancelled = false;            //purchase cancelled
    String productIdIntent = null;                      //product id use while restore


    @OnClick(R.id.ivBack)
    void bbLogout() {
        logOUt();
        finish();
    }

    void logOUt() {
        JsonObject j = new JsonObject();
        j.addProperty("deviceType", 2);
        j.addProperty("deviceToken", prefsManager.getData(Constants.DEVICE_TOKEN));
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.logOut(mUserDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    prefsManager.logOut();
                    googleLogOUt();
                    facebookLogout();
                    Intent n = new Intent(InappFragment.this, LoginBaseActivity.class);
                    startActivity(n);
                    finish();
                } else {
//                    Toast.makeText(MenuFragment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void googleLogOUt() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(myScope)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        FirebaseAuth.getInstance().signOut();
                        mGoogleSignInClient.revokeAccess();

                        // ...
                    }
                });
    }

    void facebookLogout() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();

    }

/*
    @OnClick(R.id.cl_monthly)
    public void goMonthly() {

        *//*Intent n = new Intent(this, WebViewFragment.class);
        n.putExtra("mcase", 1);
        startActivity(n);*//*

    }

    @OnClick(R.id.cl_quaterly)
    public void goQuaterly() {


    }

    @OnClick(R.id.cl_yearly)
    public void goAnnualy() {


    }*/

    @OnClick(R.id.ivBack)
    void cl() {
        finish();
    }

    @OnClick(R.id.tvRestore)
    void restoreMethod() {

        if (!hasPurchase) {
            Toast.makeText(context, "No Purchase Yet !!!", Toast.LENGTH_SHORT).show();
        } else {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_info);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            String restore_month = "<html><a href=\"https://play.google.com/store/account/subscriptions?sku=monthly_member&amp;package=com.seraphic.lightapp\">Restore</a></html>";
            String restore_quater = "<html><a href=\"https://play.google.com/store/account/subscriptions?sku=quaterly_member&amp;package=com.seraphic.lightapp\">Restore</a></html>";
            String restore_year = "<html><a href=\"https://play.google.com/store/account/subscriptions?sku=annually_member&amp;package=com.seraphic.lightapp\">Restore</a></html>";

            TextView tv_restore = (TextView) dialog.findViewById(R.id.tv_restore);
            TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);

            tv_restore.setMovementMethod(LinkMovementMethod.getInstance());

            if (!productIdIntent.isEmpty()) {

                if (productIdIntent.contains("monthly_member")) {
                    tv_restore.setText(Html.fromHtml(restore_month));
                } else if (productIdIntent.contains("quaterly_member")) {
                    tv_restore.setText(Html.fromHtml(restore_quater));
                } else if (productIdIntent.contains("annually_member")) {
                    tv_restore.setText(Html.fromHtml(restore_year));
                }
            }

            tv_restore.setMovementMethod(LinkMovementMethod.getInstance());

            tv_restore.setLinksClickable(true);

            tv_restore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    finish();
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            dialog.show();
        }

        /*if (hasSubscriptionExist) {

            // old wrong code by nits , as discussed with raman sir

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (prefsManager.getBoolData(Constants.LOGINSTATUS)) {

                        billingClientLifecycle = ((App) getApplication()).getBillingClientLifecycle();
                        getLifecycle().addObserver(billingClientLifecycle);
                        billingClientLifecycle.purchaseUpdateEvent.observe(InappFragment.this, new Observer<List<Purchase>>() {
                            @Override
                            public void onChanged(List<Purchase> purchases) {
                                if (purchases != null) {
                                    purchaseList = purchases;

                                    if (purchaseList.size() > 0) {
                                        registerPurchases(purchaseList);

                                    }
                                }
                            }
                        });


                    } else {
                        Intent intent = new Intent(InappFragment.this, LoginBaseActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 1000);

        } else {
            Toast.makeText(context, "No subscription yet !!!", Toast.LENGTH_SHORT).show();


        }*/


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.inapp_fragment);
        ButterKnife.bind(this);
        prefsManager = new PrefsManager(this);
        mUserDetail = prefsManager.getUserData();
        tvRestore = findViewById(R.id.tvRestore);
        cl_monthly = findViewById(R.id.cl_monthly);
        cl_quaterly = findViewById(R.id.cl_quaterly);
        cl_yearly = findViewById(R.id.cl_yearly);
        context = this;
        skuList.add("monthly_member");
        skuList.add("quaterly_member");
        skuList.add("annually_member");
        setupBillingClient();


        if (getIntent() != null && getIntent().getBooleanExtra("hasSubscription", false)) {
            hasSubscriptionExist = true;
        }
        if (getIntent() != null && getIntent().getStringExtra("productId") != null
                && !getIntent().getStringExtra("productId").isEmpty()) {
            productIdIntent = getIntent().getStringExtra("productId");
        }

        if (getIntent() != null && getIntent().getBooleanExtra("hasSubscriptionCancelled", false)) {
            hasSubscriptionCancelled = true;
        }
        if (getIntent() != null && getIntent().getBooleanExtra("hasPurchase", false)) {
            hasPurchase = true;
        }


        /*if (hasPurchase) {
            if (hasSubscriptionCancelled) {
                Toast.makeText(context, "Has puchase, but cancel before expire", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Has puchase exist without cancel", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(context, "Has no puchase yet", Toast.LENGTH_SHORT).show();

        }*/

       /* billingClientLifecycle = ((App) getApplication()).getBillingClientLifecycle();
        getLifecycle().addObserver(billingClientLifecycle);

        billingClientLifecycle.purchaseUpdateEvent.observe(this, new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                if (purchases != null) {
                    registerPurchases(purchases);
                }
            }
        });
    }
    private void registerPurchases(List<Purchase> purchaseList) {
        for (Purchase purchase : purchaseList) {
            String sku = purchase.getSku();
            String purchaseToken = purchase.getPurchaseToken();
            Log.d("TAG", "Register purchase with sku: " + sku + ", token: " + purchaseToken);
          //  subscriptionViewModel.registerSubscription(sku, purchaseToken);
        }
    }*/


        //  inBilling();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (prefsManager.getBoolData(Constants.LOGINSTATUS)) {

                    billingClientLifecycle = ((App) getApplication()).getBillingClientLifecycle();
                    getLifecycle().addObserver(billingClientLifecycle);
                    billingClientLifecycle.purchaseUpdateEvent.observe(InappFragment.this, new Observer<List<Purchase>>() {
                        @Override
                        public void onChanged(List<Purchase> purchases) {
                            if (purchases != null) {
                                purchaseList = purchases;
                            }
                        }
                    });


                } else {
                    Intent intent = new Intent(InappFragment.this, LoginBaseActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);*/


    }


    public void registerPurchases(List<Purchase> purchaseList) {

      /*  getLifecycle().removeObserver(billingClientLifecycle);
        billingClientLifecycle.destroy();*/

        if (purchaseList.size() > 0) {
            for (Purchase purchase : purchaseList) {

                if (purchase.isAutoRenewing()) {


                    JsonObject j = JsonParser.parseString(purchase.getOriginalJson()).getAsJsonObject();
                    saveLocalRecipt(j);

                    Log.e("--save after relogin", " " + j);


                    Intent n = new Intent(InappFragment.this, HomeBaseActivity.class);
                    startActivity(n);
                    finish();

                    //////////////////////

                    return;

                }
            }
        } else {

            Toast.makeText(context, "no list", Toast.LENGTH_SHORT).show();

        }


    }


    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    loadAllSKUs();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });


       /* billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(BillingResult billingResult, List<PurchaseHistoryRecord> list) {


                if (list != null) {

                }
            }
        });*/
    }

    private void loadAllSKUs() {
        if (billingClient.isReady()) {
            SkuDetailsParams params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.SUBS)
                    .build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !skuDetailsList.isEmpty()) {
                        for (final SkuDetails skuDetails : skuDetailsList) {


                            Log.e("---------", "Found sku: " + skuDetails);
                            if (skuDetails.getSku().equals("monthly_member")) {
                                tvMonthlyPrice.setText(skuDetails.getPrice() + "*");
                                cl_monthly.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        billingClient.endConnection();


                                        if (hasPurchase) {

                                            Toast.makeText(context, "Sorry! You have already subscribed. Please click on Restore to restore your purchase", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("-----onclick----", "monthly_member Found sku: " + skuDetails);
                                            BillingFlowParams params = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
                                            billingClient.launchBillingFlow((Activity) context, params);
                                        }


                                    }
                                });
                            }
                            if (skuDetails.getSku().equals("quaterly_member")) {
                                tvQuartlyPrice.setText(skuDetails.getPrice() + "*");
                                cl_quaterly.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (hasPurchase) {
                                            Toast.makeText(context, "Sorry! You have already subscribed. Please click on Restore to restore your purchase", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Log.e("-----onclick----", "quaterly_member Found sku: " + skuDetails);
                                            BillingFlowParams params = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
                                            billingClient.launchBillingFlow((Activity) context, params);
                                        }


                                    }
                                });
                            }
                            if (skuDetails.getSku().equals("annually_member")) {
                                tvYearlyPrice.setText(skuDetails.getPrice() + "*");
                                cl_yearly.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.e("-----onclick----", "annually_member Found sku: " + skuDetails);

                                        if (hasPurchase) {
                                            Toast.makeText(context, "Sorry! You have already subscribed. Please click on Restore to restore your purchase", Toast.LENGTH_SHORT).show();
                                        } else {
                                            BillingFlowParams params = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
                                            billingClient.launchBillingFlow((Activity) context, params);
                                        }


                                     /*   BillingFlowParams params = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
                                        billingClient.launchBillingFlow((Activity) context, params);*/

                                        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
                                            @Override
                                            public void onPurchaseHistoryResponse(BillingResult billingResult, List<PurchaseHistoryRecord> list) {


                                                if (list != null) {

                                                }
                                            }
                                        });
                                       /* mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                                                new PurchaseHistoryResponseListener() {
                                                    @Override
                                                    public void onPurchaseHistoryResponse(@BillingResponse int responseCode,
                                                                                          List purchasesList) {
                                                        if (responseCode == BillingResponse.OK
                                                                && purchasesList != null) {
                                                            for (Purchase purchase : purchasesList) {
                                                                // Process the result.
                                                            }
                                                        }
                                                    } });*/
                                    }
                                });
                            }
                           /* inList.add(new SkuRowData(details.getSku(), details.getTitle(),
                                    details.getPrice(), details.getDescription(),
                                    details.getType()));*/
                        }
                    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        // Handle an error caused by a user cancelling the purchase flow.
                        Log.e("-----onclick----", "click USER_CANCELED: ");
                    } else {
                        // Handle any other error codes.
                        Log.e("--else---onclick--", " " + billingResult.getDebugMessage());
                    }
                }
            });
        } else {
            println("Billing Client not ready");
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
//                String js=new Gson().toJson(purchase,Purchase.class);

                JsonObject j = JsonParser.parseString(purchase.getOriginalJson()).getAsJsonObject();

                Log.e("JsonObject--", " " + j);
                Log.e("Purchase--", " " + purchase.getOriginalJson());

                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    // Grant entitlement to the user.
                   /* boolean signOk = verifyPurchaseSignature(purchase.getOriginalJson(), purchase.getSignature());
                    if (!signOk) {
                        // Alert the user about wrong signature
                        return;
                    } else */
                    if (!purchase.isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.getPurchaseToken())
                                        .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                                //Give thanks for the purchase

                                Log.e("fgfd", "gdshvg");

                            }
                        });
                    }
                }

                saveLocalRecipt(j);
                saveRecipt(j);
                acknowledgePurchase(purchase.getPurchaseToken());
            }
            Log.e("onPurchasesUpdated--", "OK ");
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.e("onPurchasesUpdated--", "USER_CANCELED ");
        } else {
            Log.e("onPurchasesUpdated--", "else ");
        }
    }

    private void saveLocalRecipt(JsonObject jsonObjectLocal) {

        new PrefsManager(InappFragment.this).saveLocalReceipt(jsonObjectLocal);
        Log.e("jsonObjectLocal--", "jsonObjectLocal ");


    }

    public void saveRecipt(JsonObject jsonObject) {
        ProgressDialog p = new ProgressDialog(this);
        p.setMessage("Please wait..");
        p.show();
        JsonObject mreq = new JsonObject();
        mreq.add("metaData", jsonObject);
        Log.e("rrrree", "" + mreq);
        UserDetail mUserdetail = new PrefsManager(this).getUserData();
        ApiService w = RestClient.intialize();
        Call<ResponseBody> call = w.saveReceipt(mUserdetail.token, mreq);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    // p.dismiss();
                    try {
                        String mres = response.body().string();
                        Log.e("mmmm", "" + mres);
                        if (mres != null) {
                            if (!mres.equals("")) {
                                PurchaseGetter pp = new Gson().fromJson(mres, PurchaseGetter.class);
                                if (pp != null) {
                                    if (pp.success) {
                                        UserDetail use = new PrefsManager(InappFragment.this).getUserData();
                                        use.isSubscriptionAvaliable = pp.success;
                                        use.expiryTimeSubcription = pp.androidReceipt.expiryTimeMillis;
                                        new PrefsManager(InappFragment.this).saveUserData(use);
                                        Log.e("mres", "" + mres);
                                        /*Intent n = new Intent(InappFragment.this, HomeBaseActivity.class);
                                        startActivity(n);
                                        finish();*/
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                p.dismiss();
            }
        });
    }

    private void acknowledgePurchase(String purchaseToken) {

        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build();
        billingClient.acknowledgePurchase(params, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                int responseCode = billingResult.getResponseCode();
                String debugMessage = billingResult.getDebugMessage();
                Intent n = new Intent(InappFragment.this, HomeBaseActivity.class);
                startActivity(n);
                finish();
                Log.e("responseCode---------", " " + responseCode);
                Log.e("----debugMessage-----", "" + debugMessage);

            }
        });

        ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build();

        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String s) {

                int responseCode = billingResult.getResponseCode();
                String debugMessage = billingResult.getDebugMessage();
                Log.e("consumeParams string", " " + s);
                Log.e("responseCode-----c----", " " + responseCode);
                Log.e("----debugMessage---c--", "" + debugMessage);
            }
        });

    }
}
