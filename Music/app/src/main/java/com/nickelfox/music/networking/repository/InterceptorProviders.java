package com.nickelfox.music.networking.repository;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl.Builder;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;


public class InterceptorProviders {
    private static final String CACHE_CONTROL = "Cache-Control";

    static class CacheInterceptor implements Interceptor {
        CacheInterceptor() {
        }

        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request()).newBuilder().header(InterceptorProviders.CACHE_CONTROL, new Builder().maxAge(10, TimeUnit.SECONDS).build().toString()).build();
        }
    }

    static class OfflineCacheInterceptor implements Interceptor {
        OfflineCacheInterceptor() {
        }

        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
                request = request.newBuilder().cacheControl(new Builder().maxStale(7, TimeUnit.DAYS).build()).build();
            return chain.proceed(request);
        }
    }

    public static HttpLoggingInterceptor provideHttpHeaderLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(Level.HEADERS);
        return httpLoggingInterceptor;
    }



    public static HttpLoggingInterceptor provideHttpBodyLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(Level.BODY);
        return httpLoggingInterceptor;
    }



    public static Interceptor provideCacheInterceptor() {
        return new CacheInterceptor();
    }

    public static Interceptor provideOfflineCacheInterceptor() {
        return new OfflineCacheInterceptor();
    }

}
