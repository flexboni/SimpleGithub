package com.android.gubonny.simplegithub.data;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 액세스 토큰을 처리함.
 */
public class AuthTokenProvider {

    private static final String KEY_AUTH_TOKEN = "auth_token";

    private Context context;

    public AuthTokenProvider(@NonNull Context context) {
        this.context = context;
    }

    // SharedPreferences 에 액세스 토큰을 저장함.
    public void updateToken(@NonNull String token) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KEY_AUTH_TOKEN, token)
                .apply();
    }

    // SharedPreferences 에 저장되어 있는 액세스 토큰을 반환함.
    // 저장되어 있는 액세스 토큰이 없는 경우널 값을 반환함.
    @Nullable
    public String getToken() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_AUTH_TOKEN, null);
    }
}
