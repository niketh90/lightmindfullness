package com.seraphic.lightapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.seraphic.lightapp.login_module.models.UserDetail;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 6/11/17.
 */

public class PrefsManager {
    Context context;

    public PrefsManager(Context context) {
        this.context = context;
    }


    public void setData(String key, String value) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public void setOnBoardData(String key, boolean value) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.OnBoPREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public void setBoolData(String key, boolean value) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }


    public void saveLocalReceipt(JsonObject mPojo) {
        String myjson = new Gson().toJson(mPojo);
        this.setData(Constants.LOCAL_RECEIPT, myjson);

    }


    public JsonObject getSavedLocalReceipt() {
        JsonObject us = null;
        SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        if (pref.getString(Constants.LOCAL_RECEIPT, "").equals("")) {

        } else {
            us = new Gson().fromJson(pref.getString(Constants.LOCAL_RECEIPT, ""), JsonObject.class);

        }
        return us;
    }

    public UserDetail getUserData() {
        UserDetail us = null;
        SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        if (pref.getString(Constants.USER_DATA, "").equals("")) {

        } else {
            us = new Gson().fromJson(pref.getString(Constants.USER_DATA, ""), UserDetail.class);

        }
        return us;
    }


    public void saveUserData(UserDetail mPojo) {
        String myd = new Gson().toJson(mPojo);
//        this.setData(Constants.User_Token, mPojo.getToken());
        this.setData(Constants.USER_DATA, myd);

    }

    public void setDataInt(String key, int value) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public void setDatalong(String key, long value) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();
            editor.putLong(key, value);
            editor.apply();
        }
    }

    public long getDataLong(Context act, String var_name) {
        SharedPreferences pref = act.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        return pref.getLong(var_name, 0);
    }

    public String getData(String key) {

        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            String val1 = prefs.getString(key, "");
            return val1;
        }
        return "";
    }

    public boolean getonbardBoolData(String key) {

        boolean val = false;
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(Constants.OnBoPREFS_NAME, MODE_PRIVATE);
            val = prefs.getBoolean(key, false);
            return val;
        }
        return val;
    }

    public boolean getBoolData(String key) {

        boolean val = false;
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            val = prefs.getBoolean(key, false);
            return val;
        }
        return val;
    }

    public int getIntData(String var_name) {
        SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        return pref.getInt(var_name, 0);
    }

    public void logOut() {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

    }

    public void setDataDouble(String key, double value) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();
            editor.putLong(key, Double.doubleToRawLongBits(value));
            editor.apply();
        }
    }

    public double getDataDouble(String var_name) {
        SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        return Double.longBitsToDouble(pref.getLong(var_name, Double.doubleToLongBits(0)));
    }

    public void setbooldata(String key, boolean value) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public boolean getbooldata(String var_name) {
        SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        return pref.getBoolean(var_name, false);
    }

    public boolean getFirstTime(Context context) {

        SharedPreferences pref = context.getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        return pref.getBoolean(Constants.FIRST_ATTEMPPT, true);
    }


}
