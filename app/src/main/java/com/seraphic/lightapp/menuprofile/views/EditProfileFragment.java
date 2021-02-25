package com.seraphic.lightapp.menuprofile.views;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.Purchase;
import com.bumptech.glide.Glide;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seraphic.lightapp.App;
import com.seraphic.lightapp.BaseActivity;
import com.seraphic.lightapp.BillingClientLifecycle;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.SplashActivity;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.home_module.views.HomeFragment;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.menuprofile.InappFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileFragment extends AppCompatActivity {
    UserDetail mUserDetail;
    PrefsManager prefsManager;
    private final int CLOSEINTENT = 10;

    ProgressDialog progressDialog;
    Context mContext;
    @BindView(R.id.v3)
    View v3;
    @BindView(R.id.ivuserimg)
    CircleImageView ivuserimg;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.edUserNmae)
    EditText edUserNmae;
    @BindView(R.id.edUserlastNmae)
    EditText edUserlastNmae;
    @BindView(R.id.edUserpass)
    EditText edUserpass;
    @BindView(R.id.ednewpass)
    EditText ednewpass;
    @BindView(R.id.tiEDUserPass)
    TextInputLayout tiEDUserPass;

    @BindView(R.id.tiEDNewPass)
    TextInputLayout tiEDNewPass;
    @BindView(R.id.edconfnewpass)
    EditText edconfnewpass;
    @BindView(R.id.edUseremail)
    TextView edUseremail;
    @BindView(R.id.clPass)
    ConstraintLayout clPass;

    @BindView(R.id.tvSavePass)
    TextView tvSavePass;
    private BillingClientLifecycle billingClientLifecycle;

    @OnClick(R.id.tvSavePass)
    void savenewPass() {
        clPass.setVisibility(View.VISIBLE);
        tvSavePass.setVisibility(View.INVISIBLE);


    }

    boolean isPAsswordEdit, isProfilePicEdit, isProfileInfoEdit;

    @OnClick(R.id.lrShareStats)
    void verifyEmail() {
        isProfilePicEdit = false;
        isProfileInfoEdit = false;
        isPAsswordEdit = false;
        boolean isAllOk = true;
        if (!TextUtils.isEmpty(ednewpass.getText()) || !TextUtils.isEmpty(edUserpass.getText()) || !TextUtils.isEmpty(edconfnewpass.getText())) {
            isPAsswordEdit = true;
            if (tiEDUserPass.getVisibility() == View.VISIBLE) {
                if (!TextUtils.isEmpty(edUserpass.getText())) {
                    if (edUserpass.getText().length() < 7) {
                        tiEDNewPass.setError(getString(R.string.password_longer));
                        isAllOk = false;
                    }
                } else {
                    isAllOk = false;
                    tiEDNewPass.setError(getString(R.string.please_enter_password));
                }
            }
            if (tiEDNewPass.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(edconfnewpass.getText())) {
                    isAllOk = false;
                }
                if (!TextUtils.isEmpty(ednewpass.getText())) {
                    if (ednewpass.getText().length() < 7) {
                        tiEDNewPass.setError(getString(R.string.password_longer));
                        isAllOk = false;
                    }
                } else {
                    isAllOk = false;
                }
            }
            if (edconfnewpass.getText().toString().equals(ednewpass.getText().toString())) {

            } else {
                isAllOk = false;
                Toast.makeText(mContext, "New password do not match with confirm password", Toast.LENGTH_SHORT).show();

            }

        } else {
            isPAsswordEdit = false;
        }


        if (!TextUtils.isEmpty(edUserNmae.getText())) {
            if (edUserNmae.getText().toString().equals(mUserDetail.firstName)) {

            } else {
                isProfileInfoEdit = true;
            }
        } else {
            isAllOk = false;
            Toast.makeText(mContext, "Please enter your first name", Toast.LENGTH_SHORT).show();

        }
        if (!TextUtils.isEmpty(edUserlastNmae.getText().toString())) {
            if (edUserlastNmae.getText().toString().equals(mUserDetail.lastName)) {
            } else {
                isProfileInfoEdit = true;

            }
        } else {
            Toast.makeText(mContext, "Please enter your last name", Toast.LENGTH_SHORT).show();
        }
        if (file != null) {
            isProfilePicEdit = true;
        }

        if (isAllOk) {
            if (isPAsswordEdit) {
                if (mUserDetail.isPasswordSet) {
                    changePAssword();
                } else {
                    changePAsswordFacebookGoole();
                }
            } else {
                if (isProfilePicEdit && isProfileInfoEdit) {
                    updateProfilePic();
                } else if (isProfileInfoEdit) {
                    updateProfile();
                } else if (isProfilePicEdit) {
                    updateProfilePic();
                } else {

                }
            }
        }


    }

//    @OnClick(R.id.tvSaveName)
//    void saveName() {
//        updateProfile();
//
//    }

    @OnClick(R.id.ivuserimg)
    void selset() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 123);
        } else {
            selectImage(this);

        }
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mContext = context;
//    }

    @OnClick(R.id.ivClose)
    public void clodse() {
        finish();
//        getActivity().getSupportFragmentManager().popBackStackImmediate();

    }

    @OnClick(R.id.ivHome)
    public void gohome() {
        Intent n = new Intent();
        n.putExtra("finish", true);
        setResult(CLOSEINTENT, n);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.edit_profile);
        mContext = this;
        ButterKnife.bind(this);
        init();
    }

    void init() {
        prefsManager = new PrefsManager(mContext);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        mUserDetail = prefsManager.getUserData();
        if (mUserDetail.isPasswordSet) {
            tiEDUserPass.setVisibility(View.VISIBLE);
            v3.setVisibility(View.VISIBLE);
        } else {
            tiEDUserPass.setVisibility(View.GONE);
            v3.setVisibility(View.GONE);
        }
        edUserNmae.setText(mUserDetail.firstName);
        edUserlastNmae.setText(mUserDetail.lastName);
        if (mUserDetail.email != null) {
            edUseremail.setText(mUserDetail.email);

        }
        tvUserName.setText(mUserDetail.firstName + " " + mUserDetail.lastName);
        if (mUserDetail.profileImage != null) {
            Glide.with(mContext).load(mUserDetail.profileImage).placeholder(R.mipmap.profilepic_placeholder).into(ivuserimg);
        }
    }

    void changePAsswordFacebookGoole() {
        progressDialog.show();
        JsonObject j = new JsonObject();
        j.addProperty("newPassword", ednewpass.getText().toString());
        j.addProperty("confirmPassword", edconfnewpass.getText().toString());
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.setPass(mUserDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    mUserDetail.isPasswordSet = true;
                    prefsManager.saveUserData(mUserDetail);
                    //                    Toast.makeText(mContext, "Password Sucessfuly changed", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("resp pass", "" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (isProfilePicEdit && isProfileInfoEdit) {
                        updateProfilePic();
                    } else if (isProfileInfoEdit) {
                        updateProfile();
                    } else if (isProfilePicEdit) {
                        updateProfilePic();
                    } else {
                        alertDialog(mContext, "", "Password changed Sucessfully!", false);


                    }
                    //                    getActivity().getSupportFragmentManager().popBackStackImmediate();

                } else {
                    JsonObject j = null;
                    try {
                        j = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (j.get("message") != null) {
                        alertDialog(mContext, getString(R.string.alert), j.get("message").getAsString(), false);
                    }
                    alertDialog(mContext, "Alert!", "Something went wrong, Please try again later!", false);

//                    Toast.makeText(mContext, "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                alertDialog(mContext, "Alert!", "Something went wrong, Please try again later!", false);

            }
        });

    }

    void changePAssword() {
        progressDialog.show();
        JsonObject j = new JsonObject();
        if (mUserDetail.isPasswordSet) {
            j.addProperty("currentPassword", edUserpass.getText().toString());

        }
        j.addProperty("newPassword", ednewpass.getText().toString());
        j.addProperty("confirmPassword", edconfnewpass.getText().toString());
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.changePassword(mUserDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {

//                    Toast.makeText(mContext, "Password Sucessfuly changed", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("resp pass", "" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (isProfilePicEdit && isProfileInfoEdit) {
                        updateProfilePic();
                    } else if (isProfileInfoEdit) {
                        updateProfile();
                    } else if (isProfilePicEdit) {
                        updateProfilePic();
                    } else {
                        alertDialog(mContext, "", "Password changed Sucessfully!", false);
                    }
//                    getActivity().getSupportFragmentManager().popBackStackImmediate();

                } else {
                    JsonObject j = null;
                    try {
                        j = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (j.get("message") != null) {
                        alertDialog(mContext, getString(R.string.alert), j.get("message").getAsString(), false);
                    } else {
                        alertDialog(mContext, "Alert!", "Something went wrong, Please try again later!", false);

                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                alertDialog(mContext, "Alert!", "Something went wrong, Please try again later!", false);

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


    public void updateProfile() {
        progressDialog.show();

        JsonObject j = new JsonObject();

        if (!TextUtils.isEmpty(edUserNmae.getText())) {
            j.addProperty("firstName", edUserNmae.getText().toString());

        }
        if (!TextUtils.isEmpty(edUserlastNmae.getText())) {
            j.addProperty("lastName", edUserlastNmae.getText().toString());
        }
        ApiService a = RestClient.intialize();

        Call<ResponseBody> call = a.updateProfile(mUserDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JsonObject j = new JsonParser().parse(response.body().string()).getAsJsonObject();

                        UserDetail u = new Gson().fromJson(j.toString(), UserDetail.class);
                        mUserDetail.profileImage = u.profileImage;
                        mUserDetail.firstName = u.firstName;
                        mUserDetail.lastName = u.lastName;
                        mUserDetail.email = u.email;
                        prefsManager.saveUserData(mUserDetail);
                        tvUserName.setText(mUserDetail.firstName + " " + mUserDetail.lastName);
//                        if (imageFile != null) {
//                            updateProfilePic();
//                        } else {
//                            if (!TextUtils.isEmpty(ednewpass.getText().toString())) {
//                                validatePassword();
//                            } else {
                        alertDialog(mContext, "", "Profile updated successfully!", false);
                        //                                Toast.makeText(mContext, "Profile updated Successfuly", Toast.LENGTH_SHORT).show();

//                            }

//                        }

                        Log.e("resp prof", "" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        alertDialog(mContext, "Error", response.errorBody().string(), false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                alertDialog(mContext, "Error", t.getMessage().toString(), false);

                progressDialog.dismiss();
            }
        });
    }

    public void updateProfilePic() {
        progressDialog.show();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        File compressedImgFile = null;
        if (imageFile != null) {
            try {
                compressedImgFile = new Compressor(mContext).setQuality(30).compressToFile(file);
                builder.addFormDataPart("profileImage", imageFile, RequestBody.create(MediaType.parse("image/*"), compressedImgFile));
            } catch (IOException e) {

                e.printStackTrace();
            }
        } else {

        }
        MultipartBody requnestBody = builder.build();

        ApiService a = RestClient.intialize();
        Map<String, File> map = new HashMap<>();
        map.put("profileImage", compressedImgFile);
        Call<ResponseBody> call = a.updateProfilePic(mUserDetail.token, requnestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {

                    try {
                        String s = response.body().string();

                        JsonObject j = new JsonParser().parse(s).getAsJsonObject();
                        if (j.get("success").getAsBoolean()) {
                            if (j.get("data").getAsJsonObject() != null) {
                                UserDetail u = new Gson().fromJson(j.get("data").getAsJsonObject().toString(), UserDetail.class);
                                mUserDetail.profileImage = u.profileImage;
                                if (isProfileInfoEdit) {
                                    updateProfile();
                                } else {
                                    alertDialog(mContext, "", "Profile updated Successfully!", false);
                                }

                                prefsManager.saveUserData(mUserDetail);
                            }
                        }
                        Log.e("resp prof", "" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, "Failed to updated Profile info", Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();
            }
        });
    }

    private void selectImage(final Activity activity) {


        final CharSequence[] options = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel)};


        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);

        builder.setTitle(getString(R.string.add_photo));

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(getString(R.string.camera))) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //String [] permissioins = new String[2];
                        ArrayList<String> permissions = new ArrayList<>();
                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.CAMERA);
                        }

                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                        if (permissions.size() > 0) {
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(EditProfileFragment.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                            }
                            return;
                        }

                    }
                    Intent pictureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE
                    );

                    if (pictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                        startActivityForResult(pictureIntent, 101);
                    }

                } else if (options[item].equals(getString(R.string.gallery))) {

//                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    Intent pickPhoto = new Intent();
                    pickPhoto.setAction(Intent.ACTION_PICK);
                    pickPhoto.setType("image/*");
                    startActivityForResult(pickPhoto, 102);


                } else if (options[item].equals(getString(R.string.cancel))) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "profile" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    private File file;
    private String imageEncoded;
    private String imageFile;
    Bitmap mBitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 101:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mBitmap = imageBitmap;
//                    ivuserimg.setImageBitmap(imageBitmap);
                    Uri uriData = getImageUri(mContext, imageBitmap);
                    Glide.with(mContext).load(uriData).into(ivuserimg);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mContext.getContentResolver().query(uriData, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    file = new File(imageEncoded);
                    imageFile = file.getName();


                }

                break;
            case 102:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage;
                      selectedImage = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                          selectedImage = getImageUri(mContext, bitmap);
                        Glide.with(mContext).load(selectedImage).into(ivuserimg);

                    }

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    file = new File(imageEncoded);
                    imageFile = file.getName();
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case 111:
                Intent pictureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );
                if (pictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                    startActivityForResult(pictureIntent,
                            1);
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                selectImage(this);
//                ActivityCompat.requestPermissions(
//                        this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
//                        123);


            }

        }
    }

    public void alertDialog(Context activity, String title, String message, boolean withcancel) {
        iOSDialogBuilder bb = new iOSDialogBuilder(activity);
        bb.setTitle(title)
                .setSubtitle(message)

                .setBoldPositiveLabel(true)
                .setCancelable(false)

                .setPositiveListener(activity.getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        dialog.dismiss();
                        finish();
                    }
                });
        if (withcancel) {
            bb.setNegativeListener(activity.getString(
                    R.string.cancel), new iOSDialogClickListener() {
                @Override
                public void onClick(iOSDialog dialog) {
                    dialog.dismiss();
                }
            });
        }

        bb.build().show();


    }

    @Override
    protected void onResume() {


        super.onResume();
    //    startRegister=false;

    }


}
