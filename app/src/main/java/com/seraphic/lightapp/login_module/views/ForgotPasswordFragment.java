package com.seraphic.lightapp.login_module.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends Fragment {
    PrefsManager prefsManager;
    UserDetail mUserDetail;
    Context mCOntext;
    ProgressDialog progressDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCOntext = context;
    }

    @OnClick(R.id.fbContinue)
    void goon() {
        if (!TextUtils.isEmpty(edEmail.getText())){
            if (isValidEmail(edEmail.getText().toString().trim())) {
                callForgogtPas();
            }else {
                edEmail.setError(getString(R.string.enter_valid_email));
            }
        }
    }

    @BindView(R.id.edEmail)
    EditText edEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
         View v = inflater.inflate(R.layout.forgot_password_fragmnet, container, false);
        ButterKnife.bind(this, v);
        prefsManager = new PrefsManager(mCOntext);
        mUserDetail = prefsManager.getUserData();
        progressDialog = new ProgressDialog(mCOntext);
        progressDialog.setMessage(getString(R.string.please_wait));
        return v;
    }

    void callForgogtPas() {
        progressDialog.show();
        JsonObject j = new JsonObject();
        j.addProperty("email", edEmail.getText().toString().trim());
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.forgotPass(j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        Utility.alertDialog(getActivity(), "","An email for password change has been sent to your email address", false);
//                        Toast.makeText(mCOntext,"An email is sent to your email id,Please check",Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                        Log.e("resp forgetpas", "" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            JsonObject j = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                            Utility.alertDialog(mCOntext, "", j.get("message").getAsString(), false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        Utility.alertDialog(mCOntext, "", "Something went wrong, Please try again later", false);

                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Utility.alertDialog(mCOntext, "", "Something went wrong, Please try again later", false);

            }
        });
    }

    public final boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}

