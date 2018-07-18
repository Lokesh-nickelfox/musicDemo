package com.nickelfox.music.networking.repository;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    public AuthenticationInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = null;
        //String token = UserManagerUtil.getToken();


        Response response = chain.proceed(request);
        int code = response.code();
        return response;
    }
}
