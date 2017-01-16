package com.natavit.cooklycook.manager;

import android.content.Context;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.manager.http.ApiService;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Natavit on 2/12/2016 AD.
 */
public class HttpManager {

    private static final String API_URL = "https://api.edamam.com";

    private static HttpManager instance;

    public static HttpManager getInstance() {
        if (instance == null)
            instance = new HttpManager();
        return instance;
    }

    private Context mContext;
    private ApiService service;

    private HttpManager() {
        mContext = Contextor.getInstance().getContext();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request origin = chain.request();

                HttpUrl url = origin.url()
                        .newBuilder()
                        .addQueryParameter("app_id", mContext.getString(R.string.edamam_app_id))
                        .addQueryParameter("app_key", mContext.getString(R.string.edamam_app_key))
                        .build();

                Request.Builder requestBuilder = origin.newBuilder()
                        .addHeader("Accept-Encoding", "gzip")
                        .url(url);

                Request request = requestBuilder.build();

                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    public ApiService getService() {
        return service;
    }

}
