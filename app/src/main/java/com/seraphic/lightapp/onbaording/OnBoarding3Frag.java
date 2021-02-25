package com.seraphic.lightapp.onbaording;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.seraphic.lightapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnBoarding3Frag extends Fragment {
    @BindView(R.id.tvTxt)
    TextView tvTxt;
    @BindView(R.id.ivImg)
    ImageView ivImg;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.onbooarding_frag,container,false);
        ButterKnife.bind(this,v);
        ivImg.setImageDrawable(getResources().getDrawable(R.mipmap.icn_ongoing3));
        tvTxt.setText(getString(R.string.onboarding3));

        return v;
    }
}
