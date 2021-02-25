package com.seraphic.lightapp.apicontroller;


import com.google.gson.JsonObject;
import com.seraphic.lightapp.home_module.models.SessionResponse;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by user on 13/6/17.
 */

public interface ApiService {

    @Headers({"Content-Type: application/json"})
    @POST("savereceipt")
    Call<ResponseBody> saveReceipt(@Header("Authorization") String token,@Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("androidverify")
    Call<ResponseBody> getSubscriptionDetail(@Header("Authorization") String token,@Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("getallsessions")
    Call<ResponseBody> getSession(@Header("Authorization") String token,@Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("getallsessions")
    Call<SessionResponse> getSessionNew(@Header("Authorization") String token, @Body JsonObject jsonObject);


    @Headers({"Content-Type: application/json"})
    @POST("login")
    Call<ResponseBody> login(@Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("signup")
    Call<ResponseBody> signup(@Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("auth/google")
    Call<ResponseBody> authGoogle( @Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("auth/facebook")
    Call<ResponseBody> authFacebook(@Body JsonObject jsonObject);

    @Headers("Accept:application/json")
     @PATCH("updateprofile")
    Call<ResponseBody> updateProfile(@Header("Authorization") String token, @Body JsonObject restDetails);

    @Headers({"Accept:application/json","Cache-Control:no-cache"})
    @POST("updateprofilepic")
    Call<ResponseBody> updateProfilePic(@Header("Authorization") String token, @Body MultipartBody file);


    @Headers("Accept:application/json")
    @PATCH("change-password")
    Call<ResponseBody> changePassword(@Header("Authorization") String token,@Body JsonObject restDetails);

     @Headers({"Content-Type: application/json"})
    @POST("getsessions")
    Call<ResponseBody> searchSessions(@Header("Authorization") String token,@Body JsonObject jsonObject);


    @Headers({"Accept:application/json"})
    @POST("updatestats")
    Call<ResponseBody> updateSTats(@Header("Authorization") String token,@Body JsonObject jsonObject);

    @Headers({"Accept:application/json"})
    @POST("ratesession")
    Call<ResponseBody> updateratings(@Header("Authorization") String token, @Body JsonObject jsonObject);

    @Headers({"Accept:application/json"})
    @POST("signout")
    Call<ResponseBody> logOut(@Header("Authorization") String token, @Body JsonObject json);

    @Headers({"Accept:application/json"})
    @POST("forgot-password")
    Call<ResponseBody> forgotPass( @Body JsonObject jsonObject);


    @Headers({"Accept:application/json"})
    @POST("setpassword")
    Call<ResponseBody> setPass(@Header("Authorization") String token, @Body JsonObject jsonObject);


}
