package com.nickelfox.music.networking.repository;


import com.nickelfox.music.ApplicationController;
import com.nickelfox.music.CredentialsHandler;
import com.nickelfox.music.networking.APIService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(new OkHttpClient.Builder().readTimeout(90, TimeUnit.SECONDS)
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor(InterceptorProviders.provideOfflineCacheInterceptor())
                    .addInterceptor(InterceptorProviders.provideHttpHeaderLoggingInterceptor())
                    .addInterceptor(InterceptorProviders.provideHttpBodyLoggingInterceptor()).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();


    private APIService networkApi = retrofit.create(APIService.class);

    public Observable<Pager<PlaylistSimple>> getMyPlaylist() {

        return networkApi.getMyPlaylists("Bearer " + CredentialsHandler.getToken());
    }


    public Observable<Pager<PlaylistTrack>> getTracks(String trackURL) {

        Retrofit trackRetrofit = new Retrofit.Builder()
                .baseUrl(trackURL+"/")
                .client(new OkHttpClient.Builder().readTimeout(90, TimeUnit.SECONDS)
                        .connectTimeout(90, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .addInterceptor(InterceptorProviders.provideOfflineCacheInterceptor())
                        .addInterceptor(InterceptorProviders.provideHttpHeaderLoggingInterceptor())
                        .addInterceptor(InterceptorProviders.provideHttpBodyLoggingInterceptor()).build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        APIService trackAPI = trackRetrofit.create(APIService.class);

        return trackAPI.getTracks(trackURL,"Bearer " + CredentialsHandler.getToken());
    }


}
