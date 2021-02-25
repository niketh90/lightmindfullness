package com.seraphic.lightapp.menuprofile.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.home_module.models.SessionGetter;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.home_module.views.HomeFragment;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsFragment extends AppCompatActivity {
    Context mContext;
    PrefsManager prefsManager;
    UserDetail userDetail;
    private final int CLOSEINTENT = 10;

    //    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mContext=context;
//    }
    @OnClick(R.id.ivHome)
    public void gohome() {
        Intent n = new Intent();
        n.putExtra("finish", true);
        setResult(CLOSEINTENT, n);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(100);
        finish();
    }

    @OnClick(R.id.rvAuthorLayout)
    void gotoProFile() {
        if (sessionGetter.sessionAuthor.authorWebsite != null) {
            Intent n = new Intent(this, WebViewFragment.class);
            n.putExtra("mcase", 3);
            n.putExtra("authorpr", sessionGetter.sessionAuthor.authorWebsite);
            startActivity(n);
        }

    }

    @BindView(R.id.ivUserImgae)
    CircleImageView ivUserImgae;
    @BindView(R.id.tvname)
    TextView tvname;
    @BindView(R.id.tvHealingMin)
    TextView tvHealingMin;
    @BindView(R.id.tvHealingDays)
    TextView tvHealingDays;
    @BindView(R.id.tvCurrentStreak)
    TextView tvCurrentStreak;
    @BindView(R.id.tvdescrip)
    TextView tvdescrip;
    @BindView(R.id.ivs1)
    ImageView ivs1;
    @BindView(R.id.ivs2)
    ImageView ivs2;
    @BindView(R.id.ivs3)
    ImageView ivs3;
    @BindView(R.id.ivs4)
    ImageView ivs4;
    @BindView(R.id.ivs5)
    ImageView ivs5;
    int rating = 0;
    @BindView(R.id.clScreen)
    ConstraintLayout clScreen;
    @BindView(R.id.mhome)
    ConstraintLayout mhome;

    @OnClick(R.id.ivshare)
    void share() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        } else {
            storeScreenshot(getScreenShot(clScreen), "scrennshot" + System.currentTimeMillis() + ".jpg");

        }
    }

    @OnClick(R.id.ivHome)
    void goHome() {

    }

    @OnClick(R.id.ivs5)
    void cla1() {
        updateStar(5, true);
    }

    @OnClick(R.id.ivs4)
    void cla4() {
        updateStar(4, true);
    }

    @OnClick(R.id.ivs3)
    void cla3() {
        updateStar(3, true);
    }

    @OnClick(R.id.ivs2)
    void cla2() {
        updateStar(2, true);
    }

    @OnClick(R.id.ivs1)
    void cla11() {
        updateStar(1, true);
    }

    void updateStar(int mcase, boolean rated) {
        if (rated) {
            ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            ivs3.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            ivs4.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            ivs5.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            switch (mcase) {
                case 1:
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    rating = 1;
                    break;
                case 2:
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    rating = 2;
                    break;
                case 3:
                    rating = 3;
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs3.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));

                    break;
                case 4:
                    rating = 4;
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs3.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs4.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));

                    break;
                case 5:
                    rating = 5;
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs3.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs4.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs5.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));

                    break;
            }
        } else {
            ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            ivs3.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            ivs4.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            ivs5.setImageDrawable(getDrawable(R.mipmap.icn_star_grey));
            switch (mcase) {
                case 1:
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));

                    break;
                case 2:
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));

                    break;
                case 3:
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs3.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));

                    break;
                case 4:
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs3.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs4.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));

                    break;
                case 5:
                    ivs1.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs2.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs3.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs4.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));
                    ivs5.setImageDrawable(getDrawable(R.mipmap.icn_star_gold));

                    break;
            }
        }
        updateRatings();
    }

    SessionGetter sessionGetter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Utility.makeStatusBarTransparent(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.stats_fragment);
        ButterKnife.bind(this);
        prefsManager = new PrefsManager(this);
        userDetail = prefsManager.getUserData();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        if (getIntent() != null) {
            sessionGetter = (SessionGetter) getIntent().getSerializableExtra("session");
            Glide.with(mContext).load(sessionGetter.sessionAuthor.authorImage).into(ivUserImgae);

            tvname.setText("" + sessionGetter.sessionAuthor.authorName);
            tvdescrip.setText("" + sessionGetter.ratingMessage);
            updateStats();
        }
    }

    //    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v=inflater.inflate(R.layout.stats_fragment,container,false);
//        ButterKnife.bind(this,v);
//        return v;
//
//    }
    void updateStats() {
        Date currDAte = new Date();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String datem = "";
        datem = sm.format(currDAte);
        JsonObject j = new JsonObject();
        j.addProperty("sessionId", sessionGetter._id);
        j.addProperty("date", datem);
        progressDialog.show();
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.updateSTats(userDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        UserDetail muserDetail = new Gson().fromJson(response.body().string(), UserDetail.class);
                        userDetail.healingTime = muserDetail.healingTime;
                        userDetail.dailyReminder = muserDetail.dailyReminder;
                        userDetail.healingDays = muserDetail.healingDays;
                        userDetail.currentStreak = muserDetail.currentStreak;
                        prefsManager.saveUserData(userDetail);
                        tvCurrentStreak.setText("" + userDetail.currentStreak);
                        tvHealingDays.setText("" + userDetail.healingDays);
                        tvHealingMin.setText("" + userDetail.healingTime);
                        Log.e("resp stats", "" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();

            }
        });
    }

    void updateRatings() {

        JsonObject j = new JsonObject();
        j.addProperty("sessionId", sessionGetter._id);
        j.addProperty("rating", rating);
        progressDialog.show();
        ApiService a = RestClient.intialize();

        Call<ResponseBody> call = a.updateratings(userDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        Toast.makeText(mContext, "Ratings updated", Toast.LENGTH_SHORT).show();
                        Log.e("resp stats", "" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();

            }
        });
    }

    public void storeScreenshot(Bitmap bitmap, String filename) {
        String path = Environment.getExternalStorageDirectory().toString() + "/" + filename;
        OutputStream out = null;
        File imageFile = new File(path);

        try {
            out = new FileOutputStream(imageFile);
            // choose JPEG format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            // manage exception ...
        } catch (IOException e) {
            // manage exception ...
        } finally {

            try {
                if (out != null) {
                    out.close();
                }

            } catch (Exception exc) {
            }

            shareImage(imageFile);
        }
    }

    public void shareImage(File b) {
        Uri uri = Uri.fromFile(b);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");

        startActivity(Intent.createChooser(intent, "Share Cover Image"));

    }

    private Bitmap getScreenShot(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(clScreen.getWidth(), clScreen.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = mhome.getBackground();
        if ((bgDrawable != null)) {
            bgDrawable.draw(canvas);

        } else canvas.drawColor(Color.WHITE);

        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            } else {
                storeScreenshot(getScreenShot(clScreen), "scrennshot" + System.currentTimeMillis() + ".jpg");

            }
        }
    }
}
