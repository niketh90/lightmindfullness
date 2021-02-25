package com.seraphic.lightapp.menuprofile.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.seraphic.lightapp.R;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewFragment extends AppCompatActivity {
    String privacy_url = "https://api.lightforcancer.com/privacy";
    String terms_url = "https://api.lightforcancer.com/terms";

    public int PaymentSuccesCode = 213;

    String url = null;
    PrefsManager prefsManager;
    @BindView(R.id.webView)
    WebView mWebview;
    @BindView(R.id.tvTiltle)
    TextView tvTiltle;
    @BindView(R.id.nointenet)
    LinearLayout nointernet;
    String amount = "1";
    Activity mactivity;
    //    ProgressDialog progressBar;
    @BindView(R.id.prProgress)
    ProgressBar progressBar;

    @OnClick(R.id.ivBack)
    public void goback() {
        finish();
//    getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mactivity = getActivity();
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);
        mactivity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.webview_fragment);
        ButterKnife.bind(this);
        prefsManager = new PrefsManager(mactivity);
        init();
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.webview_fragment, container, false);
//        ButterKnife.bind(this, v);
//        prefsManager = new PrefsManager(mactivity);
//        init();
//        return v;
//    }

    public void init() {
        progressBar.setVisibility(View.VISIBLE);
        int typee = 0;

        if (getIntent() != null) {
            typee = getIntent().getExtras().getInt("mcase");

        }
        switch (typee) {
            case 1:
                url = privacy_url;
                tvTiltle.setText(getString(R.string.privacy_policy));
                break;
            case 2:
                url = terms_url;
                tvTiltle.setText(getString(R.string.termsncon));

                break;
            case 3:
                url = getIntent().getExtras().getString("authorpr");
                tvTiltle.setText(getString(R.string.author_profile));

                break;


        }
        if (url.startsWith("http")) {

        } else {
            url = "http://" + url;
        }
        if (Utility.interNetConnection(mactivity)) {
            Log.e("payment", "true");
            nointernet.setVisibility(View.GONE);
            mWebview.setVisibility(View.VISIBLE);
            mWebview.setWebViewClient(new MyCLient());
            mWebview.setWebChromeClient(new mwebviewClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            Log.e("webview", " " + url);
            mWebview.loadUrl(url);

        } else {
            progressBar.setVisibility(View.GONE);
            nointernet.setVisibility(View.VISIBLE);
            mWebview.setVisibility(View.GONE);

        }

    }


    class MyCLient extends WebViewClient {


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            Log.e("webV", "httperror" + request.getUrl() + "  " + errorResponse);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
//            Utility.disimiss_progress();
            // Log.e("webV","pageFinished"+url);

        }

        @Override
        public void onLoadResource(WebView view, String url) {


            super.onLoadResource(view, url);

        }

    }


    class mwebviewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.e("chomeclient", "url " + view.getUrl());
        }


    }


}
