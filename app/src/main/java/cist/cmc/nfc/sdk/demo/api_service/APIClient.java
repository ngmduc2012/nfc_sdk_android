package cist.cmc.nfc.sdk.demo.api_service;

import java.util.concurrent.TimeUnit;

import cist.cmc.nfc.sdk.core.constant.SDKConstant;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(9000, TimeUnit.MILLISECONDS)
                .connectTimeout(9000, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder().baseUrl(SDKConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        return retrofit;
    }
}
