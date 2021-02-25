package com.seraphic.lightapp.home_module.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.seraphic.lightapp.App;
import com.seraphic.lightapp.BillingClientLifecycle;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.SplashActivity;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.home_module.models.OnItemClickListner;
import com.seraphic.lightapp.home_module.models.RecentListCategoryAdapter;
import com.seraphic.lightapp.home_module.models.RecentSessionAdapter;
import com.seraphic.lightapp.home_module.models.ScalableVideoView;
import com.seraphic.lightapp.home_module.models.SessionCategories;
import com.seraphic.lightapp.home_module.models.SessionGetter;
import com.seraphic.lightapp.home_module.models.SessionListPojo;
import com.seraphic.lightapp.home_module.models.SessionResponse;
import com.seraphic.lightapp.login_module.models.PurchaseGetter;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.menuprofile.InappFragment;
import com.seraphic.lightapp.menuprofile.views.MenuFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.RecyclerItemClickListener;
import com.seraphic.lightapp.utilities.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnItemClickListner {
    List<SessionCategories> mList;
    SessionGetter dailySessionGetter;
    ProgressDialog progressDialog;
    RecentListCategoryAdapter recentSessionAdapter;
    Context mContext;
    @BindView(R.id.rvRecentList)
    RecyclerView rvRecentList;
    @BindView(R.id.ivSelectSess)
    ImageView ivSelectSess;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvSessionDesc)
    TextView tvSessionDesc;
    @BindView(R.id.mVideoView)
    PlayerView mVideoView;
//    @BindView(R.id.svSeaarch)
public   static EditText svSearch;
    @BindView(R.id.clSearch)
    ConstraintLayout clSearch;
    @BindView(R.id.lrNoDailySession)
    LinearLayout lrNoDailySession;
    @BindView(R.id.sessionLayout)
    ConstraintLayout sessionLayoutDAily;
    private SimpleExoPlayer videoPlayer;
    @BindView(R.id.lrNoConnection)
    LinearLayout lrNoConnection;
    UserDetail mUserDetail;
    PrefsManager prefsManager;
    private BillingClientLifecycle billingClientLifecycle;

    @OnClick(R.id.mediaspace)
    public void gotoplay() {
//        videoPlayer.setVolume(0f);
//        MediaPlayFragment m = new MediaPlayFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("session", mList.get(0));
//        m.setArguments(b);
//        ((HomeBaseActivity) mContext).pushFrgament(m, true, false);
        Intent n = new Intent(mContext, MediaPlayFragment.class);
        n.putExtra("session", dailySessionGetter);
        n.putExtra("dailySession", true);

        startActivity(n);
    }

    @BindView(R.id.ivSearch)
    ImageView ivSearch;

    @BindView(R.id.ivCleareSearch)
    ImageView ivCleareSearch;

    @OnClick(R.id.ivCleareSearch)
    void clearSearch() {
        svSearch.setText("");
    }

    @OnClick(R.id.ivSearch)
    void gosearch() {
        if (clSearch.getVisibility() == View.VISIBLE) {
            clSearch.setVisibility(View.GONE);
            hideSoftKeyboard(svSearch);
            ivSearch.setImageDrawable(getResources().getDrawable(R.mipmap.icn_search_white));
        } else {
            showSoftKeyboard(svSearch);
            ivSearch.setImageDrawable(getResources().getDrawable(R.mipmap.icn_close_white));

            clSearch.setVisibility(View.VISIBLE);

        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocusedView = activity.getCurrentFocus();
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static HomeFragment getmInstance() {
        return mInstance;
    }

    public static void setmInstance(HomeFragment mInstance) {
        HomeFragment.mInstance = mInstance;
    }

    static HomeFragment mInstance;
    @BindView(R.id.nsData)
    NestedScrollView nsData;
    @BindView(R.id.tvHeading)
    TextView tvHeading;

    @OnClick(R.id.ivSetting)
    public void goToMenu() {
        Intent n = new Intent(mContext, MenuFragment.class);
        startActivity(n);
//        ((HomeBaseActivity) mContext).pushFrgament(new MenuFragment(), true, false);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, v);
        mInstance = this;
        mList = new ArrayList<>();
        Utility.hideKeyboard(getActivity());
svSearch=v.findViewById(R.id.svSeaarch);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
        prefsManager = new PrefsManager(mContext);
        mUserDetail = prefsManager.getUserData();
        svSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'

                    if (TextUtils.isEmpty(svSearch.getText())) {

                    } else {
                        Utility.hideKeyboard(getActivity());
                        String s = svSearch.getText().toString();
                        SearchFragment sf = new SearchFragment();
                        Bundle b = new Bundle();
                        b.putString("search", s);
                        sf.setArguments(b);
                        ((HomeBaseActivity) mContext).pushFrgament(sf, true, false);
                    }
                }
                return false;
            }
        });
        svSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
if(s!=null){
    if (s.length()>0){
        ivCleareSearch.setVisibility(View.VISIBLE);
    }else {
        ivCleareSearch.setVisibility(View.INVISIBLE);

    }
}
            }
        });
        RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        rvRecentList.setLayoutManager(lm);

        if (((HomeBaseActivity) mContext).mList.size() == 0) {
            if (Utility.interNetConnection(mContext)) {
                lrNoConnection.setVisibility(View.GONE);
                nsData.setVisibility(View.VISIBLE);
                getSessionListNew();
                getSessionList();

//                getdetail();

            } else {
                lrNoConnection.setVisibility(View.VISIBLE);
                nsData.setVisibility(View.GONE);
            }

        } else {
            mList.clear();
            mList.addAll(((HomeBaseActivity) mContext).mList);
            dailySessionGetter = ((HomeBaseActivity) mContext).dailySession;
            showData();
            showSelectedSession();
        }


        return v;
    }

   /* @Override
    public void onResume() {
        super.onResume();
        if (mList.size() > 0) {
            if (isExpired()) {
                Intent intent = new Intent(mContext, InappFragment.class);
                mContext.startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }

        }
        inBillingNew();
//        setupVideo();


    }
*/
    boolean isExpired() {
        TimeZone defTimeZone = TimeZone.getDefault();
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        long exptime = mUserDetail.expiryTimeSubcription;
        Date date = new Date(exptime);
        SimpleDateFormat sm = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm.setTimeZone(utcTimeZone);
        String utcDate = sm.format(date);
        Log.e("##Datee -Home", "utc t=" + sm.getTimeZone().getDisplayName() + "  " + utcDate);
        SimpleDateFormat sm2 = new SimpleDateFormat("yy-MM-dd HH:mm");
        sm2.setTimeZone(defTimeZone);
        String localeDate = sm2.format(date);
        Date locDate = null;
        try {
            locDate = sm2.parse(localeDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("##Datee-Home", "defalt t=" + defTimeZone.getDisplayName() + "  " + localeDate);
        Date currDAte = new Date();
        Log.e("##Datee-Home", "curr t=" + defTimeZone.getDisplayName() + "  " + sm2.format(currDAte));

        if (currDAte.getTime() >= locDate.getTime()) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoPlayer != null) {
            videoPlayer.stop();

        }
    }

    void getdetail() {
        progressDialog.show();
        Date currDAte = new Date();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String datem = "";
        datem = sm.format(currDAte);
        ApiService w = RestClient.intialize();
        JsonObject j = new JsonObject();
        j.addProperty("date", datem);
        Call<ResponseBody> call = w.getSubscriptionDetail(mUserDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    String myresponse = null;
                    try {
                        myresponse = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (myresponse != null) {

                        PurchaseGetter p = new Gson().fromJson(myresponse, PurchaseGetter.class);
                        if (p != null) {
                            mUserDetail.isSubscriptionAvaliable = p.success;
                            if (p.androidReceipt != null) {
                                mUserDetail.expiryTimeSubcription = p.androidReceipt.expiryTimeMillis;

                            }
                            if (p.user != null) {
                                mUserDetail.currentStreak = p.user.currentStreak;
                                mUserDetail.healingDays = p.user.healingDays;
                                mUserDetail.healingTime = p.user.healingTime;
                            }
                            prefsManager.saveUserData(mUserDetail);
                        }

                    }
                }

                if (mUserDetail.isSubscriptionAvaliable) {
                    if (isExpired()) {
                        Intent intent = new Intent(mContext, InappFragment.class);
                        mContext.startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finish();

                        }
                    } else {
                        getSessionList();
                        getSessionListNew();
                    }

                } else {
                    Intent intent = new Intent(mContext, InappFragment.class);
                    mContext.startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    public void getSessionList() {
        progressDialog.show();
//        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd");

        Date d = new Date();
        String currentDate = inputdate.format(d);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sessionDate", currentDate);
        Log.e("curr_date", currentDate);
        ApiService a = RestClient.intialize();
        Call<ResponseBody> call = a.getSession(mUserDetail.token, jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("respnse session", "res" + response.isSuccessful());
                progressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        SessionListPojo s = new Gson().fromJson(response.body().string(), SessionListPojo.class);
                        ((HomeBaseActivity) mContext).dailySession = s.getDailySession();

                        dailySessionGetter = s.getDailySession();
                        showSelectedSession();
                        ((HomeBaseActivity) mContext).mList.addAll(s.getCategories());
                        mList.addAll(s.getCategories());
                        showData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void getSessionListNew() {

//        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd");

        Date d = new Date();
        String currentDate = inputdate.format(d);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sessionDate", currentDate);
        Log.e("curr_date", currentDate);
        ApiService a = RestClient.intialize();
        Call<SessionResponse> call = a.getSessionNew(mUserDetail.token, jsonObject);
        call.enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                Log.e("respnse session", "res" + response.isSuccessful());

                if (response.code() == 200) {

                    prefsManager.setDataInt("currentStreak",response.body().getUser().getCurrentStreak());
                    prefsManager.setDataInt("healingTime",response.body().getUser().getHealingTime());
                    prefsManager.setDataInt("healingDays",response.body().getUser().getHealingDays());


                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {

            }
        });
    }

    public void showData() {
        recentSessionAdapter = new RecentListCategoryAdapter(mContext, mList, this);
        rvRecentList.setAdapter(recentSessionAdapter);
    }


    public void showSelectedSession() {
        if (dailySessionGetter.sessionName != null) {
            lrNoDailySession.setVisibility(View.GONE);
            sessionLayoutDAily.setVisibility(View.VISIBLE);
            tvHeading.setText(dailySessionGetter.sessionName);
//            tvSessionDesc.setText(dailySessionGetter.sessionDescription);
            tvTime.setText("" + dailySessionGetter.sessionTime + " Min");

            Glide.with(mContext).load(dailySessionGetter.sessionThumbNail).centerCrop().into(ivSelectSess);
        } else {
            lrNoDailySession.setVisibility(View.VISIBLE);
            sessionLayoutDAily.setVisibility(View.GONE);

        }


    }

    public void restartVid() {
        if (videoPlayer != null) {
            videoPlayer.setVolume(1f);

        }

    }

    public void setupVideo() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        videoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);


//        Uri uri = RawResourceDataSource.buildRawResourceUri(R.raw.bg_video);


        mVideoView.setPlayer(videoPlayer);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                mContext, Util.getUserAgent(mContext, "RecyclerView VideoPlayer"));
//        String mediaUrl = "android.resource://" + mContext.getPackageName() + "/" + R.raw.bg_video;
//        if (mediaUrl != null) {
//            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(uri);
//            mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//
//            if (videoPlayer != null) {
//                videoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
//                videoPlayer.prepare(videoSource);
//                videoPlayer.setPlayWhenReady(true);
//            }
//
//        }

    }

    @Override
    public void onClickSession(int pos) {
//        videoPlayer.setVolume(0f);

//                MediaPlayFragment m = new MediaPlayFragment();
//                Bundle b = new Bundle();
//                b.putSerializable("session", mList.get(position));
//                m.setArguments(b);
//                ((HomeBaseActivity) mContext).pushFrgament(m, true, false);

    }

    public void registerPurchasesNew(List<Purchase> purchaseList) {
        if(purchaseList.size()>0)
        {
            for (Purchase purchase : purchaseList) {
                if (!purchase.isAutoRenewing()) {
                    Intent intent = new Intent(getActivity(), InappFragment.class);
                    startActivity(intent);

                }
            }
        }
        else
        {
            Intent intent = new Intent(getActivity(), InappFragment.class);
            startActivity(intent);



        }




    }




    @Override
    public void onStart() {
        super.onStart();

    }
}
