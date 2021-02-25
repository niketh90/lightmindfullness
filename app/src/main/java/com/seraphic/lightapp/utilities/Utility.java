package com.seraphic.lightapp.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.jobscheduling.TestJobService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;



/**
 * Created by user on 13/6/17.
 */

public class Utility {
    private static ProgressDialog kProgressHUD;

public static void show_Progress(Context context,String message){

    Utility.kProgressHUD=new ProgressDialog(context);
    Utility.kProgressHUD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    Utility.kProgressHUD.setMessage(message);
    Utility.kProgressHUD.setCancelable(false);
    Utility.kProgressHUD.show();


 }
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, TestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
    public static   void hideKeyboard(Activity activity) {
        try{
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocusedView = activity.getCurrentFocus();
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
 public static void disimiss_progress(){
    Utility.kProgressHUD.dismiss();
 }


    public static void alertDialog(Context activity,String title,String message,boolean withcancel) {
      iOSDialogBuilder bb=  new iOSDialogBuilder(activity);
                bb.setTitle(title)
                .setSubtitle(message)

                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener(activity.getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {

                        dialog.dismiss();


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

    public static long getRandomNumber(){
        Random r = new Random();
        long numbers = 1000000000 + (long)(r.nextDouble() * 999999999);
        Log.e("relation",""+numbers
        );

        return numbers;
    }

    public static long getTimeInMilis(String date){
        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        long timeDif=0;
        String dayst="",hrst="",mintst="";
    if (date!=null){
    try {

        Date departr=inputdate.parse(date);

        long diff = departr.getTime() ;

        timeDif=diff;
    } catch (ParseException e) {
        e.printStackTrace();
        Log.e("timediffeee",""+timeDif);

    }
}


        return timeDif;

    }

    public static long getTimeDifferenceInMilis(String arrivaldate, String departuredate){
        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        long timeDif=0;
        String dayst="",hrst="",mintst="";

        try {

            Date departr=inputdate.parse(departuredate);
            Date arrivl=inputdate.parse(arrivaldate);

            long diff = arrivl.getTime()-departr.getTime() ;

             timeDif=diff;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("timediffeee",""+timeDif);

        }

        return timeDif;

    }

  public static String getTimeInFormat(String format, String daee){
      SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
      SimpleDateFormat outpuf=new SimpleDateFormat(format);
      String outd="";
      try {
          Date md=inputdate.parse(daee);
         outd=outpuf.format(md);
      } catch (ParseException e) {
          e.printStackTrace();
      }
      return outd;
  }
    public static String getTimefromMilis(long date, String format){
        DateFormat outputFormat = new SimpleDateFormat(format);
        outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datef="";

        Date departr=new Date(date);
        datef=outputFormat.format(departr);



        return datef;

    }
    public static String getTimeforchat(long date){
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy hh:MM a");
        outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datef="";

        Date departr=new Date(date);
        datef=outputFormat.format(departr);

        return datef;

    }
    public static String getTimeDifference(String arrivaldate,String departuredate){
        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        String timeDif="1 hr";
        String dayst="",hrst="",mintst="";

        try {

            Date departr=inputdate.parse(departuredate);
            Date arrivl=inputdate.parse(arrivaldate);

            long diff = arrivl.getTime()-departr.getTime() ;

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays>0){
                dayst=""+diffDays+" days";

            } if (diffHours>0){
                hrst=diffHours+" hr";
            }if(diffMinutes>0){
                mintst=" "+diffMinutes+ " min";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("timediffeee",""+timeDif);

        }
        return dayst+hrst+mintst;
}
    public static String getTimeforHotelx(long hours){
    if (hours==0){
        return "0 H";
    }
         String timeDif="1 hr";
         Log.e("hotelx","time="+hours);
        String dayst="",hrst="",mintst="";
            long milliseconds = hours * 60 * 60 *1000;
//            double diffSeconds = milliseconds / 1000 % 60;
            long diffMinutes = milliseconds / (60 * 1000) % 60;
            long diffHours = milliseconds / (60 * 60 * 1000) % 24;
            long diffDays = milliseconds / (24 * 60 * 60 * 1000);
            if (diffDays>0){
                dayst=""+diffDays+" days";
            } if (diffHours>0){
                hrst=diffHours+" hr";
            }if(diffMinutes>0){
                mintst=" "+diffMinutes+ " min";
            }
        return dayst+hrst+mintst;
    }
    public static String getDateInEMpirent(String format, String date){
//    if (date.equals("")){
//
//        return date;
//
//    }else {
        DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
        DateFormat outputf=new SimpleDateFormat(format);
        String fdate=date;
        try {
            Date mdate = inputFormat.parse(date);
            fdate = outputf.format(mdate);
        }catch (ParseException p){
            Log.e("excc ","oo"+p.getMessage());
            try {
                Date  md=  new SimpleDateFormat("dd MMM yyyy",Locale.US).parse(date);
                fdate=outputf.format(md);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return fdate;
        //}

    }
        public static String getDateInFormat(String format, String date){
//    if (date.equals("")){
//
//        return date;
//
//    }else {
        DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy",Locale.US);
        DateFormat outputf=new SimpleDateFormat(format);
        String fdate=date;
        try {
            Date mdate = inputFormat.parse(date);
            fdate = outputf.format(mdate);
        }catch (ParseException p){
            Log.e("excc ","oo"+p.getMessage());
            try {
                Date  md=  new SimpleDateFormat("dd MMM yyyy",Locale.US).parse(date);
                fdate=outputf.format(md);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return fdate;
    //}

        }
    public static String getCompleteDate(String date){
        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        //DateFormat outputFormat = new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");

        String returnd=date;
        try {
            Date mdate=inputdate.parse(date);
            returnd=outputFormat.format(mdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnd;   }

    public static String getUTCdateInFormat(String date){

        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        String returnd=date;
        try {
            Date mdate=inputdate.parse(date);
            returnd=outputFormat.format(mdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnd;
}
    public static long getPendingTime(String date){

        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd");
         try {
            Date mdate=inputdate.parse(date);
            return mdate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getPendingDate(String date){

        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        String returnd=date;
        try {
            Date mdate=inputdate.parse(date);
            returnd=outputFormat.format(mdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnd;
    }
    public static long getTime_flight_time(String date){

        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        DateFormat outputFormat = new SimpleDateFormat("HH:mm");
        long returnd=0;
        try {
            Date mdate=inputdate.parse(date);
            returnd=mdate.getTime();

        } catch (ParseException e) {

            e.printStackTrace();
        }

        return returnd;
}

    public static String getTime(String date){
        SimpleDateFormat inputdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        DateFormat outputFormat = new SimpleDateFormat("HH:mm");
        String returnd=date;
        try {
            Date mdate=inputdate.parse(date);
            returnd=outputFormat.format(mdate);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        return returnd;   }

    public static String convertMilliesToStringDate(long milliSeconds, String formatNeeded)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(formatNeeded);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }



    private static boolean isReadableASCII(CharSequence string){
        if (TextUtils.isEmpty(string)) return false;
        try {
            Pattern p = Pattern.compile("[\\x20-\\x7E]+");
            return p.matcher(string).matches();
        } catch (Throwable e){
            return true;
        }
    }

/*
    public static String getDeviceId(Context context) {
        return JPushInterface.getUdid(context);
    }
*/
public static void snackBar(CoordinatorLayout cordinateLayout)
    {
        Snackbar snackbar = Snackbar
                .make(cordinateLayout, R.string.internet_connection, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }
    public static void snackBar(View view)
    {
        Snackbar snackbar = Snackbar
                .make(view, R.string.internet_connection, Snackbar.LENGTH_SHORT);
        snackbar.show();
     }
    public static void showAlertDialogType(final Context context, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setCancelable(false)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent startMain = new Intent(Intent.ACTION_MAIN);

                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        context.startActivity(startMain);

                        dialog.dismiss();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        dialog.dismiss();
                    }
                })

                .show();

    }
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean interNetConnection(Context activity) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static void hideKeyboardFrom(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }
public static void makeStatusBarTransparent(Activity activity){
    Window window = activity.getWindow();

    if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
        setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
    }
    if (Build.VERSION.SDK_INT >= 19) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    if (Build.VERSION.SDK_INT >= 21) {
        setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
       window.setStatusBarColor(Color.TRANSPARENT);
    }

}
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    public static void changeStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
             window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorWhite));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    public static void changeStatusBarColortoPrimary(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        }
    }
    private static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                        viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    //   alartDialog(EventDetailActivity.this,event.getMetaData().getDescription());
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false);

                    } else {
                        makeTextViewResizable(tv, 3, "View More", true);
                    }
                  /*  if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false);

                    } else {
                        makeTextViewResizable(tv, 3, "View More", true);
                    }*/
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public static class MySpannable extends ClickableSpan {

        private boolean isUnderline = false;

        /**
         * Constructor
         */
        public MySpannable(boolean isUnderline) {
            this.isUnderline = isUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {

            ds.setUnderlineText(isUnderline);
            ds.setColor(Color.parseColor("#343434"));

        }

        @Override
        public void onClick(View widget) {

        }
    }
    public static boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }


    /*
     * Condition I: Check if the android version in device is greater than
     * Marshmallow, since fingerprint authentication is only supported
     * from Android 6.0.
     * Note: If your project's minSdkversion is 23 or higher,
     * then you won't need to perform this check.
     *
     * */
    public static boolean isSdkVersionSupported() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }



    /*
     * Condition II: Check if the device has fingerprint sensors.
     * Note: If you marked android.hardware.fingerprint as something that
     * your app requires (android:required="true"), then you don't need
     * to perform this check.
     *
     * */
    public static boolean isHardwareSupported(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }



    /*
     * Condition III: Fingerprint authentication can be matched with a
     * registered fingerprint of the user. So we need to perform this check
     * in order to enable fingerprint authentication
     *
     * */
    public static boolean isFingerprintAvailable(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }



    /*
     * Condition IV: Check if the permission has been added to
     * the app. This permission will be granted as soon as the user
     * installs the app on their device.
     *
     * */
    public static boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean neededUpdate(String storeVersion, String current){
        String[] storeParts = storeVersion.split("\\.");
        String[] currentParts = current.split("\\.");

        if(storeParts.length != currentParts.length || storeParts.length!=3){
            return !storeVersion.equals(current);
        }

        int maxLen = 0;
        for (String item:storeParts) {
            if (item.length() > maxLen) maxLen = item.length();
        }
        for (String item:currentParts) {
            if (item.length() > maxLen) maxLen = item.length();
        }

        for (String item:storeParts) {
            while(item.length()<maxLen){
                item = "0" + item;
            }
        }
        for (String item:currentParts) {
            while(item.length()<maxLen){
                item = "0" + item;
            }
        }

        String storeNum = TextUtils.join( "", storeParts);
        String currentNum = TextUtils.join("", currentParts);

        try{
            return Integer.parseInt(storeNum)>Integer.parseInt(currentNum);
        } catch ( NumberFormatException e) {
            return storeVersion.equals(current);
        }
    }

}
