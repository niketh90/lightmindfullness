package com.seraphic.lightapp.login_module.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.menuprofile.views.WebViewFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import butterknife.BindView;

public class LoginBaseActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    PrefsManager prefsManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);

        Log.e("onnewIntnet","oncreeate");

        setContentView(R.layout.login_base_layout);
        fragmentManager = getSupportFragmentManager();
        prefsManager = new PrefsManager(this);
        pushFrgament(new SignupFragment(), true);
        init();
    }

    public void init() {
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
                        // Log and toast
                        Log.e("fire", token);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finishAffinity();
        } else {
            super.onBackPressed();

        }
    }

    public void pushFrgament(Fragment fragment, boolean add) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //   transaction.setCustomAnimations(R.anim.frag_slide_in_from_bottom, 0);
        if (add) {
            transaction.add(R.id.mFrameLayout, fragment, fragment.getTag());
            transaction.addToBackStack(fragment.getTag());
        } else {
            transaction.replace(R.id.mFrameLayout, fragment);

        }
        transaction.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left);

        transaction.commit();
    }
    @Override
    protected void onNewIntent(Intent intent)
    {

        Log.e("onnewIntnet","fired");
        Intent n=new Intent(this,WebViewFragment.class);

        if (intent.getScheme().startsWith("terms"))
        {
            n.putExtra("mcase",2);
            startActivity(n);
            //handle terms clicked
        }
        else if (intent.getScheme().equalsIgnoreCase("privacy"))
        {
            n.putExtra("mcase",1);
            startActivity(n);
            //handle privacy clicked
        }
        else
        {
            super.onNewIntent(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onac","---"+requestCode);
    }
}
