package com.seraphic.lightapp.menuprofile.views;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seraphic.lightapp.AlarmBroadcastReceiver;
import com.seraphic.lightapp.AlarmService;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.apicontroller.ApiService;
import com.seraphic.lightapp.apicontroller.RestClient;
import com.seraphic.lightapp.login_module.models.UserDetail;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.PrefsManager;
import com.seraphic.lightapp.utilities.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderFragment extends AppCompatActivity {
    private static final int JOB_ID = 0;
    private JobScheduler mScheduler;
    ProgressDialog progressDialog;
    private final int CLOSEINTENT = 10;

    @OnClick(R.id.ivBack)
    public void goset() {
//        Intent n=new Intent(this, MenuFragment.class);
//        startActivity(n);
        finish();
//        ((HomeBaseActivity) getActivity()).pushFrgament(new MenuFragment(), true,false);
    }

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    @BindView(R.id.clReminder)
    ConstraintLayout clReminder;

    @OnClick(R.id.ivHome)
    public void gohome() {
        Intent n = new Intent();
        n.putExtra("finish", true);
        setResult(CLOSEINTENT, n);
        finish();
//        ((HomeBaseActivity)getActivity()).pushFrgament(new HomeFragment(),false,true);

    }

    String pickedTIme = "";
    int pickedHour, pickedMin;
    final Calendar myCalendar = Calendar.getInstance();

    @OnClick(R.id.lrSetReminder)
    public void setReminder() {

        if (!pickedTIme.equals("")) {
            if (pickedTIme.equals(mUserDetail.dailyReminder)) {
                //notupdated
            } else {
                setRemind(true);

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.tvDone)
    public void pickTime() {
        clTimelayout.setVisibility(View.GONE);
        if (pickedTIme.equals("")) {
            String mi = String.valueOf(timePicker.getMinute());
            if (mi.length() == 1) {
                mi = 0 + "" + timePicker.getMinute();
            }
            String hr = String.valueOf(timePicker.getHour());
            if (hr.length() == 1) {
                hr = 0 + "" + timePicker.getHour();
            }
            pickedTIme = hr + ":" + mi;
            pickedHour = timePicker.getHour();
            pickedMin = timePicker.getMinute();

        }
        remindTINE.setText("" + pickedTIme);
        clReminder.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.clReminder)
    void setrm() {
        clTimelayout.setVisibility(View.VISIBLE);

    }

    @BindView(R.id.clTimelayout)
    ConstraintLayout clTimelayout;
    @BindView(R.id.sWitch)
    SwitchCompat sWitch;
    @BindView(R.id.tpTimePicker)
    TimePicker timePicker;
    UserDetail mUserDetail;
    PrefsManager prefsManager;
    @BindView(R.id.remindTINE)
    TextView remindTINE;
    boolean isremind = false;
    boolean isReminderSett = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.makeStatusBarTransparent(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.reminder_fragment);
        ButterKnife.bind(this);
        timePicker.setIs24HourView(true);
        prefsManager = new PrefsManager(this);
        mUserDetail = prefsManager.getUserData();
        if (mUserDetail.firstName != null) {
            if (mUserDetail.firstName.equals("") || mUserDetail.firstName.equalsIgnoreCase("null") || mUserDetail.firstName.equalsIgnoreCase("Undefined")) {
                Constants.USER_NAME = "";
            }
            {
                Constants.USER_NAME = mUserDetail.firstName;
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        progressDialog.setCancelable(false);
        ColorStateList cl = getResources().getColorStateList(R.color.colorWhite);
        ColorStateList cllightgrey = getResources().getColorStateList(R.color.grey);
        ColorStateList cldarkgrey = getResources().getColorStateList(R.color.hintText);
        ColorStateList clpinkgrey = getResources().getColorStateList(R.color.baby_pink);
        if (mUserDetail.dailyReminder != null) {
            if (!mUserDetail.dailyReminder.equals("")) {
                isremind = true;
            } else {
                isremind = false;
            }
        }
        if (isremind) {
            isReminderSett = true;
            clReminder.setVisibility(View.VISIBLE);
            remindTINE.setText(mUserDetail.dailyReminder + "");
            sWitch.setChecked(true);
            sWitch.setTrackTintList(cl);
            sWitch.setThumbTintList(clpinkgrey);
        } else {
            isReminderSett = false;
            sWitch.setTrackTintList(cllightgrey);
            sWitch.setThumbTintList(cldarkgrey);
            clReminder.setVisibility(View.GONE);
        }

        if (getIntent() != null) {
Log.e("nnn","uuu"+getIntent().getAction());
            if (getIntent().getAction()!=null) {
                String[] tt= mUserDetail.dailyReminder.split(":");
                Log.e("nnn","uuu"+tt.toString());

                if (tt.length==2){
                    pickedHour=Integer.valueOf(tt[0]);
                    pickedMin=Integer.valueOf(tt[1]);


                }
                setReminderON(true);

                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
            }
        }


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                pickedHour = hourOfDay;
                pickedMin = minute;
                myCalendar.set(Calendar.YEAR, 2020);
                myCalendar.set(Calendar.MONTH, 4);
                myCalendar.set(Calendar.DAY_OF_MONTH, 7);
                Date date = myCalendar.getTime();
                String myFormat = "dd/MM/yy hh:mm"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                String s = sdf.format(date);
                Log.e("timmm", "" + s);

//                scheduleNotification(getNotification( s ), date.getTime()) ;


                String mi = String.valueOf(minute);
                if (mi.length() == 1) {
                    mi = 0 + "" + minute;
                }
                String hr = String.valueOf(hourOfDay);
                if (hr.length() == 1) {
                    hr = 0 + "" + hourOfDay;
                }
                pickedTIme = hr + ":" + mi;
            }
        });

        sWitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sWitch.setTrackTintList(cl);
                    sWitch.setThumbTintList(clpinkgrey);

                    clTimelayout.setVisibility(View.VISIBLE);
                    clReminder.setVisibility(View.VISIBLE);
                } else {
                    pickedTIme = "";
                    if (isReminderSett) {
                        setRemind(false);
                    }
                    sWitch.setTrackTintList(cllightgrey);
                    sWitch.setThumbTintList(cldarkgrey);
                    clTimelayout.setVisibility(View.GONE);
                    clReminder.setVisibility(View.GONE);

                }
            }
        });
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v=inflater.inflate(R.layout.reminder_fragment,container,false);
//        ButterKnife.bind(this,v);
//        return v;
//    }

    public void setRemind(boolean set) {
        progressDialog.show();

        JsonObject j = new JsonObject();
        j.addProperty("dailyReminder", pickedTIme);

        ApiService a = RestClient.intialize();

        Call<ResponseBody> call = a.updateProfile(mUserDetail.token, j);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                isReminderSett = set;
                progressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        if (set) {
                            JsonObject j = new JsonParser().parse(response.body().string()).getAsJsonObject();

                            UserDetail u = new Gson().fromJson(j.toString(), UserDetail.class);
                            mUserDetail.healingTime = u.healingTime;
                            mUserDetail.currentStreak = u.currentStreak;
                            mUserDetail.healingDays = u.healingDays;
                            mUserDetail.dailyReminder = u.dailyReminder;
                            remindTINE.setText("" + u.dailyReminder);
                            clReminder.setVisibility(View.VISIBLE);
                            Toast.makeText(ReminderFragment.this, "Reminder updated successfuly", Toast.LENGTH_SHORT).show();
                            prefsManager.saveUserData(mUserDetail);
                            setReminderON(true);
//                            scheduleNotification(getNotification("Complete your session."), u.dailyReminder);

                        } else {
                            setReminderON(false);

//                            JsonObject j = new JsonParser().parse(response.body().string()).getAsJsonObject();
//
//                            UserDetail u = new Gson().fromJson(j.toString(), UserDetail.class);
//                            mUserDetail.healingTime = u.healingTime;
//                            mUserDetail.currentStreak = u.currentStreak;
//                            mUserDetail.healingDays = u.healingDays;
                            mUserDetail.dailyReminder = null;
                            clReminder.setVisibility(View.GONE);
                            isremind = false;
                            Toast.makeText(ReminderFragment.this, "Reminder Removed successfuly", Toast.LENGTH_SHORT).show();
                            prefsManager.saveUserData(mUserDetail);

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
                progressDialog.dismiss();
            }
        });
    }


    private void setReminderON(boolean On) {

//        int alarmId = new Random().nextInt(Integer.MAX_VALUE);
        Intent intent = new Intent(ReminderFragment.this, AlarmBroadcastReceiver.class);
        intent.putExtra("TITLE", mUserDetail.firstName);
        pendingIntent = PendingIntent.getBroadcast(ReminderFragment.this, 0, intent, 0);

        if (On) {
            alarmManager.cancel(pendingIntent);
            Calendar calendar = Calendar.getInstance();
            Log.e("alarmtime", "" + pickedHour + " " + pickedMin);
            Date currTime = new Date();
            calendar.set(Calendar.HOUR_OF_DAY, pickedHour);
            calendar.set(Calendar.MINUTE, pickedMin);
            calendar.set(Calendar.SECOND, 0);
            if (calendar.getTimeInMillis() < currTime.getTime()) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            }

            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), RUN_DAILY, pendingIntent);
            Log.e("alarmtime--", " " + calendar.getTime());

        } else {
            if (pendingIntent != null)
                alarmManager.cancel(pendingIntent);

        }

    }


    private Notification getNotification(String content) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder builder = new Notification.Builder(this);
//        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setColor(getResources().getColor(R.color.colorPrimary));

        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setSound(alarmSound);
        builder.setSmallIcon(R.mipmap.logo);

        return builder.build();
    }

    public void cancelJobs(View view) {

        if (mScheduler != null) {
            mScheduler.cancelAll();
            mScheduler = null;
//            Toast.makeText(this, R.string.jobs_canceled, Toast.LENGTH_SHORT).show();
        }
    }

}

