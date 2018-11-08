package com.android.gubonny.simplegithub.api.model

import com.google.gson.annotations.SerializedName

/**
 * Retrofit 을 통해 JSON 형태로 받은 응답을
 * Gson 라이브러리를 사용하여 클래스 형태로 변환하며,
 * JSON 응답에 있는 필드와 클래스에 있는 필드 이름이 일치하는 경우
 * 데이터 자동으로 매핑해 줌.
 * (다른 이름으로 매핑을 원하면 SerializedName 에 지정해주면 됨.)
 */
class GithubAccessToken(// SerializedName 에 지정되있는 이름으로 매핑함.
        @field:SerializedName("access_token")
        val accessToken: String, val scope: String, @field:SerializedName("token_type")
        val tokenType: String)
