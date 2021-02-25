package com.seraphic.lightapp.apicontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seraphic.lightapp.utilities.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user on 13/6/17.
 */

public class RestClient {

    private RestClient() {
    }
    public static ApiService intialize() {
        return createWithUrl(Constants.BASEURL);
    }

    public static ApiService createWithUrl(String baseUrl){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        builder.connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES);

        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .baseUrl(baseUrl)
                .build();
        return retrofit.create(ApiService.class);

    }
}
