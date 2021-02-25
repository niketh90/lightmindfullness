package com.seraphic.lightapp.menuprofile.views;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
import com.google.gson.JsonObject;
import com.seraphic.lightapp.BuildConfig;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.login_module.views.LoginBaseActivity;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends AppCompatActivity {
    Context mContext;
    UserDetail mUserDetail;
    private final int CLOSEINTENT = 10;
    ProgressDialog progressDialog;
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mContext = context;
//    }

    @OnClick(R.id.ivClose)
    public void closeit() {
        Intent n = new Intent();
        n.putExtra("finish", true);
        setResult(CLOSEINTENT, n);
        finish();
        //        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @OnClick(R.id.ivback)
    void goBack() {
        finish();

    }

    @BindView(R.id.tvVersion)
    TextView tvVersion;

    @OnClick(R.id.tvUserName)
    public void goedit() {
        Intent n = new Intent(this, EditProfileFragment.class);
        startActivityForResult(n, CLOSEINTENT);

//        ((HomeBaseActivity) mContext).pushFrgament(new EditProfileFragment(), true, false);
    }

    @OnClick(R.id.clEditProfile)
    public void editp() {
        Intent n = new Intent(this, EditProfileFragment.class);
        startActivityForResult(n, CLOSEINTENT);
//        ((HomeBaseActivity) mContext).pushFrgament(new EditProfileFragment(), true, false);
    }

    @OnClick(R.id.tvUserStats)
    public void goStatitcs() {
        Intent n = new Intent(this, UserStatisticsFragment.class);
        startActivityForResult(n, CLOSEINTENT);
//        ((HomeBaseActivity) mContext).pushFrgament(new UserStatisticsFragment(), true, false);
    }

    @OnClick(R.id.tvReminders)
    public void goReminder() {
        Intent n = new Intent(this, ReminderFragment.class);
        startActivityForResult(n, CLOSEINTENT);
//        ((HomeBaseActivity) mContext).pushFrgament(new ReminderFragment(), true, false);

    }

    @OnClick(R.id.tvHelp)
    public void goSupport() {
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//
//                "mailto", "info@lightforcancer.com", null));
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
//        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email) + "..."));

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@lightforcancer.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT, "body of email");
        List<Intent> targetShareIntents = new ArrayList<Intent>();


        List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(i, 0);
        if (!resInfos.isEmpty()) {
            System.out.println("Have package");
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);
                if (packageName.contains("android.email") || packageName.contains("android.gm")) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "info@lightforcancer.com", null));
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
//                startActivity(chooserIntent);
            }
        }
        Intent ii = new Intent(Intent.ACTION_SEND);
        ii.setType("*/*");
        ii.putExtra(Intent.EXTRA_EMAIL, new String[] {
                "info@lightforcancer.com"
        });
        ii.putExtra(Intent.EXTRA_SUBJECT, "");
        ii.putExtra(Intent.EXTRA_TEXT, "");
     startActivity(createEmailOnlyChooserIntent(ii, "Send via email"));

    }

    @OnClick(R.id.tvAbout)
    public void goabout() {
        Intent n = new Intent(this, AboutFragment.class);
        startActivityForResult(n, CLOSEINTENT);
//        ((HomeBaseActivity) mContext).pushFrgament(new AboutFragment(), true, false);
    }

    @OnClick(R.id.tvLogout)
    public void goLogout() {
        go_Logout();

    }

    public void googleLogOUt() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(myScope)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
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


    PrefsManager prefsManager;
    @BindView(R.id.ivUserProfile)
    CircleImageView ivUserProfile;
    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utility.changeStatusBarColor(this);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.menu_layout);
        ButterKnife.bind(this);
        mContext = this;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        prefsManager = new PrefsManager(mContext);
        tvVersion.setText("" + BuildConfig.VERSION_NAME);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserDetail = prefsManager.getUserData();
        if (mUserDetail.firstName!=null) {

            if (mUserDetail.firstName.equals("") || mUserDetail.firstName.equalsIgnoreCase("null") || mUserDetail.firstName.equalsIgnoreCase("Undefined")) {
                Constants.USER_NAME = "";

            }
            {
                Constants.USER_NAME = mUserDetail.firstName;

            }
        }
        tvUserName.setText(mUserDetail.firstName +" "+mUserDetail.lastName);
        if (mUserDetail.profileImage != null) {
            Glide.with(mContext).load(mUserDetail.profileImage).placeholder(R.mipmap.profilepic_placeholder).into(ivUserProfile);
        }
    }
    public Intent createEmailOnlyChooserIntent(Intent source,
                                               CharSequence chooserTitle) {
        Stack<Intent> intents = new Stack<Intent>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                "info@domain.com", null));
        List<ResolveInfo> activities = getPackageManager()
                .queryIntentActivities(i, 0);
        for(ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }
        if(!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));
            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CLOSEINTENT) {
            if (data != null) {
                Intent n = new Intent();
                n.putExtra("finish", true);
                setResult(CLOSEINTENT, n);
                finish();

            }


        }
    }

    void logOUt() {
        progressDialog.show();
        JsonObject j = new JsonObject();
        j.addProperty("deviceType", 2);
        j.addProperty("deviceToken", prefsManager.getData(Constants.DEVICE_TOKEN));
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.logOut(mUserDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    prefsManager.logOut();
                    googleLogOUt();
                    facebookLogout();
                    Intent n = new Intent(mContext, LoginBaseActivity.class);
                    startActivity(n);
                    finish();
                } else {
                    Toast.makeText(MenuFragment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MenuFragment.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }

    void go_Logout() {
        iOSDialogBuilder bb = new iOSDialogBuilder(this);
        bb
                .setSubtitle("Are your sure you want to logout?")

                .setBoldPositiveLabel(false)
                .setCancelable(false)
                .setPositiveListener(getString(R.string.no), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        dialog.dismiss();


                    }
                });
        bb.setNegativeListener(getString(
                R.string.yes), new iOSDialogClickListener() {
            @Override
            public void onClick(iOSDialog dialog) {
                dialog.dismiss();
                logOUt();
            }
        });

        bb.build().show();

    }
}
