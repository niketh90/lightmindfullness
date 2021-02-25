package com.seraphic.lightapp.menuprofile.views;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.seraphic.lightapp.MainActivity;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.home_module.views.HomeFragment;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Screenshot;
import com.seraphic.lightapp.utilities.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserStatisticsFragment extends AppCompatActivity {
    PrefsManager prefsManager;
    UserDetail mUserDetail;
    private final int CLOSEINTENT = 10;

    @OnClick(R.id.ivHome)
    public void goset() {
        finish();
//        ((HomeBaseActivity) getActivity()).pushFrgament(new MenuFragment(), true,false);
    }

    @OnClick(R.id.ivSettings)
    public void gohome() {
        Intent n = new Intent();
        n.putExtra("finish", true);
        setResult(CLOSEINTENT, n);
        finish();
        //        ((HomeBaseActivity)getActivity()).pushFrgament(new HomeFragment(),false,true);

    }

    @BindView(R.id.clScreenshot)
    ConstraintLayout clScreenshot;
    @BindView(R.id.ivUserImage)
    CircleImageView ivUserImage;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvstreakCount)
    TextView tvstreakCount;
    @BindView(R.id.tvhealminCount)
    TextView tvhealminCount;
    @BindView(R.id.tvdaysCount)
    TextView tvdaysCount;
    @BindView(R.id.lrho)
    ConstraintLayout lrho;

    @OnClick(R.id.lrShareStats)
    public void sharesc() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        } else {
            storeScreenshot(getScreenShot(clScreenshot), "scrennshot" + System.currentTimeMillis() + ".jpg");

        }

    }

    private Bitmap getScreenShot(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(clScreenshot.getWidth(), clScreenshot.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = lrho.getBackground();
        if ((bgDrawable != null)) {
            bgDrawable.draw(canvas);

        } else canvas.drawColor(Color.WHITE);

        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.user_statics);
        ButterKnife.bind(this);
        init();
    }

    void init() {
        prefsManager = new PrefsManager(this);
        mUserDetail = prefsManager.getUserData();
        tvUserName.setText(mUserDetail.firstName + " " + mUserDetail.lastName);
        if (mUserDetail.profileImage != null) {
            Glide.with(this).load(mUserDetail.profileImage).placeholder(R.mipmap.profilepic_placeholder).into(ivUserImage);

        }

        tvdaysCount.setText("" +prefsManager.getIntData("healingDays")+"" );
        tvhealminCount.setText("" + prefsManager.getIntData("healingTime")+"" );
        tvstreakCount.setText("" + prefsManager.getIntData("currentStreak")+"" );
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
//        FileProvider.getUriForFile(
//                this,
//                "com.example.homefolder.example.provider", //(use your app signature + ".provider" )
//                b);
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");

        startActivity(Intent.createChooser(intent, "Share Cover Image"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==123){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            } else {
                storeScreenshot(getScreenShot(clScreenshot), "scrennshot" + System.currentTimeMillis() + ".jpg");

            }
        }
    }
}
