package com.seraphic.lightapp;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.VideoView;

public class KSplashScreenVideoView extends VideoView {
    public KSplashScreenVideoView(Context context) {
        super(context);
    }

    public KSplashScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KSplashScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth = (screenHeight * 9) / 16;
        setMeasuredDimension(screenWidth, screenHeight);

    }
}