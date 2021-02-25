package com.seraphic.lightapp.home_module.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.home_module.models.RecentSessionAdapter;
import com.seraphic.lightapp.home_module.models.SessionGetter;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.menuprofile.views.MenuFragment;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.RecyclerItemClickListener;
import com.seraphic.lightapp.utilities.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    Context mContext;
    RecentSessionAdapter recentSessionAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    UserDetail mUserDetail;
    PrefsManager prefsManager;
    @BindView(R.id.rvSearchResults)
    RecyclerView rvSearchResults;
    @BindView(R.id.tvReults)
    TextView tvReults;
    @BindView(R.id.svSeaarch)
    EditText edSearch;
    List<SessionGetter> mlist;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.clSearch)
    ConstraintLayout clSearch;
    @OnClick(R.id.ivBack)
    void backk() {
        hideSoftKeyboard(edSearch);

        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @OnClick(R.id.ivSetting)
    void goSett() {
        Intent n = new Intent(mContext, MenuFragment.class);
        startActivity(n);
    }

    @BindView(R.id.lrNoDataFound)
    LinearLayout lrNoDataFound;
    String searchString = null;
    @BindView(R.id.ivCleareSearch)
    ImageView ivCleareSearch;

    @OnClick(R.id.ivCleareSearch)
    void clearSearch() {
        edSearch.setText("");
    }
    @Override
    public void onDestroy() {

        super.onDestroy();

    }
    String strString="";
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        View v = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.bind(this, v);
        Utility.hideKeyboard(getActivity());

        prefsManager = new PrefsManager(mContext);
        mUserDetail = prefsManager.getUserData();
        mlist = new ArrayList<>();
        RecyclerView.LayoutManager lm = new GridLayoutManager(mContext, 2);
        rvSearchResults.setLayoutManager(lm);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null){
                    searchString="";

                    if (s.length()>0){
                        ivCleareSearch.setVisibility(View.VISIBLE);
                        searchString=s.toString();

                    }else {
                        ivCleareSearch.setVisibility(View.INVISIBLE);

                    }
                }
            }
        });

        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    searchString="";
                    if (TextUtils.isEmpty(edSearch.getText())) {

                    } else {
                        Utility.hideKeyboard(getActivity());

                        searchString = edSearch.getText().toString();
                        getSessions();
                    }
                }
                return false;
            }
        });
        if (getArguments()!=null){
            searchString=getArguments().getString("search");
            edSearch.setText(searchString);
            getSessions();
        }

        rvSearchResults.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                 Intent n = new Intent(mContext, MediaPlayFragment.class);
                n.putExtra("session", mlist.get(pos));
                n.putExtra("dailySession",false);
                mContext.startActivity(n);
            }
        }));
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        HomeFragment.svSearch.setText(searchString);
    }

    public void getSessions() {
        rvSearchResults.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        ApiService a = RestClient.intialize();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("text",searchString);
        jsonObject.addProperty("length",20);
        jsonObject.addProperty("index",0);
        Call<ResponseBody> call = a.searchSessions(mUserDetail.token,jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress_bar.setVisibility(View.GONE);
                mlist.clear();
                if (response.code() == 200) {
                    try {
                        Gett gett = new Gson().fromJson(response.body().string(), Gett.class);

                        if (gett.success) {
                            mlist.addAll(gett.data);
                        }
                        showData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);

            }
        });
    }

    void showData() {
        if (mlist.size() > 0) {
            tvReults.setVisibility(View.VISIBLE);

            rvSearchResults.setVisibility(View.VISIBLE);
            lrNoDataFound.setVisibility(View.GONE);
            recentSessionAdapter = new RecentSessionAdapter(mContext, mlist);
            rvSearchResults.setAdapter(recentSessionAdapter);
            tvReults.setText(mlist.size()+" Results Found");
        } else {
            tvReults.setVisibility(View.GONE);
            lrNoDataFound.setVisibility(View.VISIBLE);
        }

    }

    public class Gett {

        List<SessionGetter> data;

        public List<SessionGetter> getData() {
            return data;
        }

        public void setData(List<SessionGetter> data) {
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        boolean success;
    }

}

