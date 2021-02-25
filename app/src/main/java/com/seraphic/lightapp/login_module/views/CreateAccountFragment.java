package com.seraphic.lightapp.login_module.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountFragment extends Fragment {
    Context mContext;
    ProgressDialog progressDialog;
    PrefsManager prefsManager;
    @BindView(R.id.edFirstName)
    EditText edFirstName;
    @BindView(R.id.edLastName)
    EditText edLastName;
    @BindView(R.id.edEmail)
    EditText edEmail;
    @BindView(R.id.edPassword)
    EditText edPassword;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick(R.id.fbContinue)
    public void goTOHome() {
        if (isDataValid()) {
            signupUser();
        }
    }

    @OnClick(R.id.ivBack)
    public void gobakc() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View v = inflater.inflate(R.layout.create_account_fragment, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    void init() {
        prefsManager = new PrefsManager(mContext);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    public void signupUser() {
        progressDialog.show();

        JsonObject jRequest = new JsonObject();
        jRequest.addProperty("firstName", edFirstName.getText().toString());
        jRequest.addProperty("lastName", edLastName.getText().toString());
        jRequest.addProperty("email", edEmail.getText().toString().trim());
        jRequest.addProperty("password", edPassword.getText().toString());
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.signup(jRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        String mresponse = response.body().string();
                        if (mresponse != null) {
                            JsonObject j = new JsonParser().parse(mresponse).getAsJsonObject();
                            if (j.get("success").isJsonNull()) {

                            } else {
                                if (j.get("success").getAsBoolean()) {
                                    alertDialog(mContext, "", getString(R.string.verification_alert));
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Intent n = new Intent(mContext, HomeBaseActivity.class);
//                    startActivity(n);
//                    getActivity().finish();
                } else if (response.code() == 400) {
                    Utility.alertDialog(mContext, "", getString(R.string.email_already_exsist), false);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    public void alertDialog(Context activity, String title, String message) {
        iOSDialogBuilder bb = new iOSDialogBuilder(activity);
        bb.setTitle(title)
                .setSubtitle(message)

                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener(activity.getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        dialog.dismiss();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();


                    }
                });

        bb.build().show();

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
            if (edPassword.getText().toString().length() < 7) {
                isIt = false;
                edPassword.setError(getString(R.string.password_longer));

            }
        } else {
            isIt = false;
            edPassword.setError(getString(R.string.empty_field));
        }
        if (!TextUtils.isEmpty(edFirstName.getText())) {
        } else {
            isIt = false;
            edFirstName.setError(getString(R.string.empty_field));
        }
        if (!TextUtils.isEmpty(edLastName.getText())) {
        } else {
            isIt = false;
            edLastName.setError(getString(R.string.empty_field));
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

}
