package com.android.gubonny.simplegithub.data

import android.content.Context
import android.preference.PreferenceManager

/**
 * 액세스 토큰을 처리함.
 */
class AuthTokenProvider(private val context: Context) {

    // SharedPreferences 에 저장되어 있는 액세스 토큰을 반환함.
    // 저장되어 있는 액세스 토큰이 없는 경우널 값을 반환함.
    // 읽기 전용 프로퍼티로 액세스 토큰 값을 제공함.
    val token: String?
        get() = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_AUTH_TOKEN, null)

    // SharedPreferences 에 액세스 토큰을 저장함.
    fun updateToken(token: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KEY_AUTH_TOKEN, token)
                .apply()
    }

    // 정적 필드는 동반 객체 내부의 프로퍼티로 변환됨.
    companion object {
        private val KEY_AUTH_TOKEN = "auth_token"
    }
}
