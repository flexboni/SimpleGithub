package com.android.gubonny.simplegithub.api

import com.android.gubonny.simplegithub.api.model.GithubAccessToken
import com.android.gubonny.simplegithub.api.model.RepoSearchResponse

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.*

import io.reactivex.Observable

/**
 * 사용자 액세스 토큰 받는 API 선언.
 */
interface AuthApi {

    @FormUrlEncoded
    // POST 방식으로 정보 전송.
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("code") code: String):
    // 반환 타입 변경
    Observable<GithubAccessToken>

//    // GithubAccessToken 에 정의된 데이터 형식으로 응답 받음.
//    // 필드 인자로 데이터 넣어 전송
//            Call<GithubAccessToken>
}
