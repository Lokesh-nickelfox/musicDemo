package com.nickelfox.music;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

public class CredentialsHandler {

        private static final String ACCESS_TOKEN_NAME = "webapi.credentials.access_token";
        private static final String ACCESS_TOKEN = "access_token";
        private static final String EXPIRES_AT = "expires_at";

        public static void setToken( String token, long expiresIn, TimeUnit unit) {

            long now = System.currentTimeMillis();
            long expiresAt = now + unit.toMillis(expiresIn);

            SharedPreferences.Editor editor = ApplicationController.getInstance().getPrefs().edit();
            editor.putString(ACCESS_TOKEN, token);
            editor.putLong(EXPIRES_AT, expiresAt);
            editor.apply();
        }


        public static String getToken() {


            String token = ApplicationController.getInstance().getPrefs().getString(ACCESS_TOKEN, null);
            long expiresAt = ApplicationController.getInstance().getPrefs().getLong(EXPIRES_AT, 0L);

            if (token == null || expiresAt < System.currentTimeMillis()) {
                return null;
            }

            return token;
        }





    }

